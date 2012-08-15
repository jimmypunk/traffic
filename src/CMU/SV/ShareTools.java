package CMU.SV;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class ShareTools {
	//public static WebInterface webInterface;
	public static void initialize(Context context) {
	}
	
	
    public static boolean isConnectInternet(Context context) {
    	ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo ni = cm.getActiveNetworkInfo();
    	if(ni != null && ni.isConnected() && ni.isAvailable())
    		return true;
    	return false;
    }
	public static boolean isSDCardExist() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;

		return false;

	}

}
