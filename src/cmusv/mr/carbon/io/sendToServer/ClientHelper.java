package cmusv.mr.carbon.io.sendToServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ClientHelper {
    private DefaultHttpClient mHttpClient;
    private String mToken;
    private String mUsername;
    private String mPassword;
    
    private String API_HOST_SERVER = "http://209.129.244.24/louis/GumballServer/php";
    
    public ClientHelper(){
        
    }
    public ClientHelper(String account, String password){
        mUsername = account;
        mPassword = password;
    }    
    
    public JSONObject register(String username, String password) throws Exception{
        mHttpClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(API_HOST_SERVER + "/moblie/createNewUser.php?account=" + username + "&password=" + password);
        get.setHeader("Accept-Encoding", "gzip");

        HttpResponse response = mHttpClient.execute(get);
        JSONObject json = parseResponseToJson(response);
        return json;
    }
    
    public void uploadFile(String filepath) throws Exception{
        mHttpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(API_HOST_SERVER + "/getUploadFile.php");
        post.setHeader("Accept-Encoding", "gzip");
        
        
    }
    
    private JSONObject parseResponseToJson(HttpResponse response) throws IOException, JSONException {
        String result = streamToString(getInputStream(response));
        Log.d("cool", "Parsing: " + result);
        
        JSONObject json = new JSONObject(result);
        return json;
    }
    private String streamToString(InputStream is) {
        if (is != null) {
            Writer w = new StringWriter();
            char[] buffer = new char[1024];
            
            Reader r;
            try {
                r = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8192);
                int n;
                while((n = r.read(buffer)) != -1) {
                    w.write(buffer,0,n);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            return w.toString();
        } else {
            return null;
        }
    }
    private InputStream getInputStream(HttpResponse response) {
        InputStream is = null;
        try {
             is = response.getEntity().getContent();
            
             Header ce = response.getFirstHeader("Content-Encoding");
             if (ce != null && ce.getValue().equalsIgnoreCase("gzip"));
                is = new GZIPInputStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return is;
    }
}

