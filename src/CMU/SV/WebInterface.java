package CMU.SV;

import java.io.File;
import java.io.FileInputStream;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;


import android.util.Log;

public class WebInterface {

	private static String username = "";
	private static String password = "";
	private static String hostname = "";
	private static String directory = "";
    public static boolean SFTPupload(String filepath,String filename)
    {
    	JSch jsch = new JSch();
    	Session session =null;
    	if(username == "" || password =="" || hostname == "" ||directory == ""){
    		Log.e("WebInterface","missing username, password, hostname, or directory");
    		return false;
    	}
		try {
			//
			session = jsch.getSession(username, hostname);
			session.setPassword(password);
	        java.util.Properties config = new java.util.Properties();
	        config.put("StrictHostKeyChecking", "no");
	        session.setConfig(config);
	        session.connect();
	        Channel channel = session.openChannel( "sftp" );
	    	channel.connect();
	    	ChannelSftp sftpChannel = (ChannelSftp) channel;
	    	Log.d("upload",filepath);
	    	
	    	
	    	File file = new File(filepath);
	  	  	if (!file.exists())
	  	  		Log.d("upload","file open faild");
	  	  	else
	  	  		Log.d("upload","file open sucess");
	  	  	FileInputStream aInputStream=new FileInputStream(file); 
	  	  	sftpChannel.put( aInputStream, directory + filename);
	  	  	aInputStream.close();
	  	  	
    	  // process inputstream as needed

	  	  	sftpChannel.exit();
	  	  	session.disconnect();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    	

  	  	
  	  	
    	
    	
    	return true;
    }
}