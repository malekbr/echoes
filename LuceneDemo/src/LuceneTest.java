import java.io.IOException;




//import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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


import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;


public class LuceneTest 
{
	public static void main(String[] args)
	{
		String ACCESS_TOKEN = "CAACEdEose0cBAFjzGVYvTmm3xjLgu8rpPGI3BTccZB3ZCdyzNKJutRbvquoZAJprcQ7XronnKRGIHR1dc1SMZAw9LyrknGQOlYqup1fln1fwvzxssjuaepRKZBznRoMiYzYzuWZAqT1uyikUTmMKFs1wAsd6exglelSzNDFi66gKEfD7uuyZCpOTFAeL50LYtwsaE3QimKVzgZDZD";
		FacebookClient facebookClient = new DefaultFacebookClient(ACCESS_TOKEN);
//		try
//		{
//			//	Specify the analyzer for tokenizing text.
//		    //	The same analyzer should be used for indexing and searching
//			StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_44);
//			
//			//	Code to create the index
//			Directory index = new RAMDirectory();
//			
//			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_44, analyzer);
//			
//			IndexWriter w = new IndexWriter(index, config);
//			addDoc(w, "Lucene in Action", "193398817");
//			addDoc(w, "Lucene for Dummies", "55320055Z");
//			addDoc(w, "Managing Gigabytes", "55063554A");
//			addDoc(w, "The Art of Computer Science", "9900333X");
//			addDoc(w, "My name is teja", "12842d99");
//			addDoc(w, "Lucene demo by teja", "23k43413");
//			w.close();
//			
//			//	Text to search
//			String querystr = args.length > 0 ? args[0] : "teja";
//			
//			//	The "title" arg specifies the default field to use when no field is explicitly specified in the query
//			Query q = new QueryParser(Version.LUCENE_44, "title", analyzer).parse(querystr);
//			
//			// Searching code
//			int hitsPerPage = 10;
//		    IndexReader reader = DirectoryReader.open(index);
//		    IndexSearcher searcher = new IndexSearcher(reader);
//		    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
//		    searcher.search(q, collector);
//		    ScoreDoc[] hits = collector.topDocs().scoreDocs;
//		    
//		    //	Code to display the results of search
//		    System.out.println("Found " + hits.length + " hits.");
//		    for(int i=0;i<hits.length;++i) 
//		    {
//		      int docId = hits[i].doc;
//		      Document d = searcher.doc(docId);
//		      System.out.println((i + 1) + ". " + d.get("isbn") + "\\t" + d.get("title"));
//		    }
//		    
//		    // reader can only be closed when there is no need to access the documents any more
//		    reader.close();
//		}
//		catch(Exception e)
//		{
//			System.out.println(e.getMessage());
//		}
	}
	private static void addDoc(IndexWriter w, String title, String isbn) throws IOException 
	{
		  Document doc = new Document();
		  // A text field will be tokenized
		  doc.add(new TextField("title", title, Field.Store.YES));
		  // We use a string field for isbn because we don\'t want it tokenized
		  doc.add(new StringField("isbn", isbn, Field.Store.YES));
		  w.addDocument(doc);
	}
}