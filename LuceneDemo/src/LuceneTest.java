import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.echoes.backend.LuceneDoc;
import org.echoes.backend.MessageDoc;
import org.echoes.backend.PostDoc;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.types.Conversation;
import com.restfb.types.FacebookType;
import com.restfb.types.Message;
import com.restfb.types.NamedFacebookType;
import com.restfb.types.Post;
import com.restfb.types.User;

public class LuceneTest {
	private static final String ACCESS_TOKEN = PrivateResource.accessToken;
	private static Document doc;

	public static void main(String[] args) throws IOException {
		long start = System.currentTimeMillis();
		Date lastSync;
		Calendar c = Calendar.getInstance();
		
		c.set(2014, 2, 3);
		//Last time we got data:
		try{
			BufferedReader br = new BufferedReader(new FileReader("lastSync.data"));
			lastSync = new Date(Long.parseLong(br.readLine()));
			br.close();
		} catch(FileNotFoundException e){
			lastSync = c.getTime();
		}
		
		//Facebook stuff that gets our data:

		FacebookClient facebookClient = new DefaultFacebookClient(ACCESS_TOKEN);
		User user = null;
		try{
		user = facebookClient.fetchObject("me", User.class);
		} catch(Exception e){
			System.err.println("Get a new Access Token");
			System.err.println("https://developers.facebook.com/tools/explorer/");
			System.exit(0);
		}
		System.out.println(user.getId());
		Connection<Post> myFeed = facebookClient.fetchConnection(Connection.feedUrl, Post.class, Parameter.with("since", lastSync));
		//Connection<FacebookType> myOutbox = facebookClient.fetchConnection("me/outbox", FacebookType.class, Parameter.with("since", lastSync), Parameter.with("limit", 100));
		String query = "SELECT created_time, attachment, body, author_id, created_time FROM message WHERE thread_id IN  (SELECT thread_id FROM thread WHERE folder_id = 1) AND author_id = 100003555168803 ORDER BY created_time";
		List<Message> myOutbox = facebookClient.executeFqlQuery(query, Message.class);
		System.out.println(myOutbox);
		myFeed.setSince(lastSync);
		//myOutbox.setSince(lastSync);
		
		FSDirectory postDir = FSDirectory.open(new File("test"));
		FSDirectory messageDir = FSDirectory.open(new File("messagetest"));
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
		IndexWriterConfig config1 = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		IndexWriterConfig config2 = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		IndexWriter postSegmentWriter = new IndexWriter(postDir, config1);
		IndexWriter messageSegmentWriter = new IndexWriter(messageDir, config2);

		long lastPost = lastSync.getTime();
		long timestamp = 0;
		for (List<Post> myFeedConnectionPage : myFeed){
			for (Post post : myFeedConnectionPage){
				String message = post.getMessage();
				message = message != null?message:"";
				String description = post.getDescription();
				description = description != null?description:"";
				timestamp = post.getCreatedTime().getTime();
				if (timestamp>lastPost)
					lastPost = timestamp;
				buildIndex(postSegmentWriter, new PostDoc(post.getId(), post.getFrom().getId(), user.getId(), timestamp, message, description));
			}
		}
		for (Message m : myOutbox){
				//System.out.println("Post: " + post);
				//System.out.println("Date: " + post.getCreatedTime());
				
				String message =  m.getMessage();
				message = message != null?message:"";
				System.out.println(message);
				if(message.equals(""))
					continue;
				timestamp = m.getCreatedTime().getTime();
				if (timestamp>lastPost)
					lastPost = timestamp;
				LuceneDoc md = new MessageDoc(m.getId(), m.getFrom().getId(), user.getId(), timestamp, message);
				
				buildIndex(messageSegmentWriter, md);
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter("lastSync.data"));
		bw.write(Long.toString(lastPost).toString());
		bw.flush();
		bw.close();
//Lucene stuff that searches our index:
		
		messageSegmentWriter.close();
		postSegmentWriter.close();
		DatabaseSearch db = new DatabaseSearch(postDir, PostDoc.MESSAGE);
		try {
			TopDocs res = db.performSearch("message:'finished semester mit'~5", 20);
			for (ScoreDoc doc : res.scoreDocs){
				System.out.println(db.getDocument(doc.doc).getField(PostDoc.MESSAGE));
				System.out.println(db.getDocument(doc.doc).getField(PostDoc.FROM));
				System.out.println(db.getDocument(doc.doc).getField(PostDoc.ID));
			}
			System.out.println(db.performSearch("'mit'", 20).totalHits);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println(System.currentTimeMillis() - start);
	}

	/**
	 * @param w the IndexWriter
	 * @param ld the LuceneDoc to write to the index. It can be either a MessageDoc or a PostDoc.
	 * A PostDoc will be written to one segment.
	 * A MessageDoc will be written to another segment.
	 */
	public static void buildIndex(IndexWriter w, LuceneDoc ld) {
		try {
			doc = new Document();
			doc.add(new StringField(LuceneDoc.ID, ld.getId(), Field.Store.YES));
			doc.add(new StringField(LuceneDoc.FROM, ld.getFrom(), Field.Store.YES));
			doc.add(new StringField(LuceneDoc.TO, ld.getTo(), Field.Store.YES));
			doc.add(new LongField(LuceneDoc.CREATED_TIME, ld.getCreated_time(),
					Field.Store.YES));
			doc.add(new TextField(LuceneDoc.MESSAGE, ld.getMessage(), Field.Store.YES));
			if (ld instanceof PostDoc){
				doc.add(new StringField(PostDoc.STORY, ((PostDoc)ld).getStory(), Field.Store.YES));
				//Put in appropriate segment
			}else if (ld instanceof MessageDoc){
				//Put in appropriate segment
			}
			w.addDocument(doc);
		} catch (IOException e) {e.printStackTrace();}
	}
}