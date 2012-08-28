package cmusv.mr.carbon.io.sendToServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import cmusv.mr.carbon.utils.StringUtils;

import android.util.Log;

public class ClientHelper {
    private DefaultHttpClient mHttpClient;
    private String mToken;
    private String mUsername;
    private String mPassword;
    
    private String TAG ="cool";
    private String API_HOST_SERVER = "http://sweetfeedback.csie.org/louis/GumballServer/php";
    
    public ClientHelper(){
        mHttpClient = new DefaultHttpClient();
    }
    
    
    public ClientHelper(String account, String password){
        mUsername = account;
        mPassword = password;
        mHttpClient = new DefaultHttpClient();
    }    
    
    /* register for a new user */
    public JSONObject register(String username, String password) throws Exception{
        HttpGet get = new HttpGet(API_HOST_SERVER + "/mobile/createNewUser.php?account=" + username + "&password=" + password);
        get.setHeader("Accept-Encoding", "gzip");

        HttpResponse response = mHttpClient.execute(get);
        JSONObject json = parseResponseToJson(response);
        return json;
    }
    
    public String sendCurrentTripToServer(String token, String type, String trip_id, double aver_speed, double max_speed, double total_distance, double total_time, long time_start, long time_end, float walking_percentage, float biking_percentage, float driving_percentage, float train_percentage) throws Exception{
    	HttpPost post = new HttpPost(API_HOST_SERVER + "/mobile/getUploadActivity.php");
    	post.setHeader("Accept-Encoding", "gzip");
    	MultipartEntity mEntity = new MultipartEntity();
    	mEntity.addPart("token", new StringBody(token));
    	mEntity.addPart("trip_id", new StringBody(trip_id));
    	mEntity.addPart("type", new StringBody(type));
    	mEntity.addPart("average_speed", new StringBody(Double.toString(aver_speed)));
    	mEntity.addPart("max_speed", new StringBody(Double.toString(max_speed)));
    	mEntity.addPart("total_distance", new StringBody(Double.toString(total_distance)));
    	mEntity.addPart("total_time", new StringBody(Double.toString(total_time)));
    	mEntity.addPart("start_time", new StringBody(StringUtils.formatDateTimeIso8601(time_start)));
    	mEntity.addPart("end_time", new StringBody(StringUtils.formatDateTimeIso8601(time_end)));
    	mEntity.addPart("walking_percentage", new StringBody(Float.toString(walking_percentage)));
    	mEntity.addPart("biking_percentage", new StringBody(Float.toString(biking_percentage)));
    	mEntity.addPart("driving_percentage", new StringBody(Float.toString(driving_percentage)));
    	mEntity.addPart("train_percentage", new StringBody(Float.toString(train_percentage)));
    	
    	post.setEntity(mEntity);
    	HttpResponse response = mHttpClient.execute(post);
    	String ret = parseResponseToString(response);
    	return ret;
    }
    
    /* use for upload something to server 
     *         need to use token
     *  
     */ 
    public JSONObject uploadFile(String token, String filepath) throws Exception{
        
        File f = new File(filepath);
        return uploadFile(token, f);
    }
    public JSONObject uploadFile(String token, File f) throws Exception{
    	HttpPost post = new HttpPost(API_HOST_SERVER + "/getUploadFile.php");
        post.setHeader("Accept-Encoding", "gzip");
        MultipartEntity mEntity = new MultipartEntity();
        ContentBody body = new FileBody(f, "text/csv");
        mEntity.addPart("file", body);
        mEntity.addPart("token", new StringBody(token));
        post.setEntity(mEntity);
        HttpResponse response = mHttpClient.execute(post);
        JSONObject ret = parseResponseToJson(response);
        return ret;
    }
    private JSONObject parseResponseToJson(HttpResponse response) throws IOException, JSONException {
        String result = streamToString(getInputStream(response));
        Log.d(TAG, "Parsing: " + result);
        
        JSONObject json = new JSONObject(result);
        return json;
    }
    
    private String parseResponseToString(HttpResponse response) throws IOException {
    	String result = streamToString(getInputStream(response));
    	Log.d(TAG, "Getting: " + result);
    	
    	return result;
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

