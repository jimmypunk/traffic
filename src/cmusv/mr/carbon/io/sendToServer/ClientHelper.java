package cmusv.mr.carbon.io.sendToServer;

import org.apache.http.impl.client.DefaultHttpClient;

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
    
    public void register(String username, String password){
        
    }
}
