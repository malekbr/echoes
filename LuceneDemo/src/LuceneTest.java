import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
//import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
//import org.apache.lucene.index.DirectoryReader;
//import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.index.IndexWriterConfig;
//import org.apache.lucene.queryparser.classic.QueryParser;
//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.Query;
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.TopScoreDocCollector;
//import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.RAMDirectory;
//import org.apache.lucene.util.Version;

import org.echoes.backend.LuceneDoc;
import org.echoes.backend.MessageDoc;
import org.echoes.backend.PostDoc;
import com.restfb.*;
import com.restfb.types.*;
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
		c.set(2013, 5, 3);
		FacebookClient facebookClient = new DefaultFacebookClient(ACCESS_TOKEN);
		Connection<Post> myFeed = facebookClient.fetchConnection("me/feed", Post.class, Parameter.with("since", c.getTime()));
		myFeed.setSince(c.getTime());
		System.out.println(myFeed.getData().size());
		for (List<Post> myFeedConnectionPage : myFeed){
			for (Post post : myFeedConnectionPage){
				//System.out.println("Post: " + post);
				System.out.println("Date: " + post.getCreatedTime());
			}
		}
		
//Lucene stuff that makes our index:
		LuceneDoc pd = new PostDoc(123, 5403, 14314, 13413414, "Hello world",
				"He shared a picture");
		LuceneDoc md = new MessageDoc(123, 1434, 13212341, 3424, "Hi there");

		IndexWriter indexWriter = new IndexWriter(null, null);
		LuceneTest.buildIndex(indexWriter, pd);
		LuceneTest.buildIndex(indexWriter, md);
		indexWriter.close();

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
			doc.add(new LongField(LuceneDoc.ID, ld.getId(), Field.Store.YES));
			doc.add(new LongField(LuceneDoc.FROM, ld.getFrom(), Field.Store.YES));
			doc.add(new LongField(LuceneDoc.TO, ld.getTo(), Field.Store.YES));
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