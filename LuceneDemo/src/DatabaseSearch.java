import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class DatabaseSearch {
	
	protected IndexSearcher searcher = null;
	protected QueryParser parser = null;
	
	public DatabaseSearch(String dir, String field) throws IOException {
		FSDirectory directory = FSDirectory.open(new File(dir));
		IndexReader reader = DirectoryReader.open(directory);
		searcher = new IndexSearcher(reader);
		parser = new QueryParser(Version.LUCENE_47, field, new StandardAnalyzer(Version.LUCENE_47));
	}
	
	public DatabaseSearch(Directory dir, String field) throws IOException {
		IndexReader reader = DirectoryReader.open(dir);
		searcher = new IndexSearcher(reader);
		parser = new QueryParser(Version.LUCENE_47, field, new StandardAnalyzer(Version.LUCENE_47));
	}
	
	public TopDocs performSearch(String queryString, int n) throws ParseException, IOException{
		Query query = parser.parse(queryString);
		TopDocs hits = searcher.search(query, n);
		return hits;
	}
	
	public Document getDocument(int n) throws IOException{
		return searcher.doc(n);
	}
}
