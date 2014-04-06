import java.io.File;
import java.io.IOException;
import java.util.Calendar;
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
import com.restfb.types.Post;
import com.restfb.types.User;

public class LuceneTest {
	private static final String ACCESS_TOKEN = PrivateResource.accessToken;
	private static Document doc;

	public static void main(String[] args) throws IOException {
//Facebook stuff that gets our data:
		Calendar c = Calendar.getInstance();
		c.set(2013, 2, 3);
		FacebookClient facebookClient = new DefaultFacebookClient(ACCESS_TOKEN);
		User user = facebookClient.fetchObject("me", User.class);
		Connection<Post> myFeed = facebookClient.fetchConnection("me/feed", Post.class, Parameter.with("since", c.getTime()));
		myFeed.setSince(c.getTime());
		
		FSDirectory dir = FSDirectory.open(new File("test"));
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_47);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_47, analyzer);
		IndexWriter indexWriter = new IndexWriter(dir, config);
		
		for (List<Post> myFeedConnectionPage : myFeed){
			for (Post post : myFeedConnectionPage){
				//System.out.println("Post: " + post);
				//System.out.println("Date: " + post.getCreatedTime());
				String message = post.getMessage();
				message = message != null?message:"";
				String description = post.getDescription();
				description = description != null?description:"";
				/*System.out.println(post.getId());
				System.out.println(message);
				System.out.println(post.getFrom().getId());
				System.out.println(user.getId());
				System.out.println(description);
				System.out.println(post.getCreatedTime());
				System.out.println();*/
				buildIndex(indexWriter, new PostDoc(post.getId(), post.getFrom().getId(), user.getId(), post.getCreatedTime().getTime(), message, description));
			}
		}
		
//Lucene stuff that makes our index:
		/*LuceneDoc pd = new PostDoc(123, 5403, 14314, 13413414, "Hello world",
				"He shared a picture");
		LuceneDoc md = new MessageDoc(123, 1434, 13212341, 3424, "Hi there");
		LuceneTest.buildIndex(indexWriter, pd);
		LuceneTest.buildIndex(indexWriter, md);*/
		indexWriter.close();
		DatabaseSearch db = new DatabaseSearch(dir, PostDoc.MESSAGE);
		try {
			TopDocs res = db.performSearch("message:congratulations AND mit", 20);
			for (ScoreDoc doc : res.scoreDocs){
				System.out.println(db.getDocument(doc.doc).getField(PostDoc.MESSAGE));
				System.out.println(db.getDocument(doc.doc).getField(PostDoc.FROM));
				System.out.println(db.getDocument(doc.doc).getField(PostDoc.ID));
			}
			System.out.println(db.performSearch("'mit'", 20).totalHits);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
			if (ld.getType().equals(PostDoc.TYPE)){
				doc.add(new StringField(PostDoc.STORY, ((PostDoc)ld).getStory(), Field.Store.YES));
				//Put in appropriate segment
			}else if (ld.getType().equals(MessageDoc.TYPE)){
				//Put in appropriate segment
			}
			w.addDocument(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}