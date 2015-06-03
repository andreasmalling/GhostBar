package f2015.itsmap.ghostbar;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by AikMarco on 03-06-2015.
 */
public class HTTPHandler extends AsyncTask<String, Void, String> {

    private Context context;
    String line;
    HTTPHandler(Context c)
    {
        context = c;
    }
    protected String doInBackground(String... URL){
        String returnString = "";
        HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;


        String jsonString = "{\"transactionId\":\"1\",\"price\":\"2.5\",\"amount\":\"150\"}";

        try{
            HttpPost post = new HttpPost(URL[0]);

            // Create a NameValuePair out of the JSONObject + a name
            //List<NameValuePair> nVP = new ArrayList<NameValuePair>(2);
            //nVP.add(new BasicNameValuePair("json", json.toString()));

            // Hand the NVP to the POST

            post.setEntity(new StringEntity(jsonString));
            Log.i("main", "TestPOST - nVP = " + jsonString);

            try {

                // Collect the response
                response = client.execute(post);

            /*Checking response */
                if (response != null) {
                    InputStream in = response.getEntity().getContent(); //Get the data in the entity
                    BufferedReader r = new BufferedReader(new InputStreamReader(in));
                    StringBuilder total = new StringBuilder();
                    //line = "";

                    while ((line = r.readLine()) != null) {
                        total.append(line);
                        //Toast.makeText(context, line, Toast.LENGTH_SHORT).show();
                    }
                    returnString = total.toString();

                    //Toast.makeText(context, line, Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e) {
                //Toast.makeText(context, e.toString(),Toast.LENGTH_LONG).show();
            }
        }
        catch(Exception e){
            e.printStackTrace();
            //createDialog("Error", "Cannot Establish Connection");
        }

        return returnString;
    }

    protected void onPostExecute(String s) {
        // TODO: check this.exception
        // TODO: do something with the feed
        //Toast.makeText(context, line, Toast.LENGTH_SHORT).show();
        String[] splitString = s.split(":");
        String BeerId = splitString[3].replaceAll("\\p{P}","");
        Toast.makeText(context, "Your beer ID is: " + BeerId, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, SuccesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("BeerId", BeerId);
        context.startActivity(intent);
    }
}
