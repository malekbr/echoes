import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class MultiQuery {

public static void main(String[] args) {
	String multiquery = "SELECT created_time, body, author_id, created_time FROM message WHERE thread_id IN  (SELECT thread_id FROM thread WHERE folder_id = 1) AND author_id = me() ORDER BY created_time";
	String s = executeFQL(multiquery, PrivateResource.accessToken);
	System.out.println(s);
}

private static String executeFQL(String query, String accessToken) {
        
        String res = null;
        
        try 
        {
            //Compose request URL
        	String url = "https://api.facebook.com/method/fql.query?query=" + query.replace(" ", "%20").replace(",","%2C").replace("=", "%3D")+ "&format=json";
        	//Append access token in available (If not available, non-public information is not returned)
            if(accessToken != null)
                url = url + "&access_token=" + accessToken;

            //Use buffered reader to read result from submitting query
            URL u = new URL(url);
            URLConnection yc = u.openConnection();
            BufferedReader in = new BufferedReader(
                                            new InputStreamReader(
                                            yc.getInputStream()));
            String inputLine;
            res = "";

            while ((inputLine = in.readLine()) != null)  res += inputLine;
            in.close();
        }
        catch (Exception e) 
        {
            System.out.println("FB_ERROR: "+e);
        }
        
        //Return query result
        return res;
 }



}
