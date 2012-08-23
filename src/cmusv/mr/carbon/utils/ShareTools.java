package cmusv.mr.carbon.utils;

import java.io.File;

import org.json.JSONObject;

import cmusv.mr.carbon.io.sendToServer.ClientHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class ShareTools {
	public static String TAG = ShareTools.class.getSimpleName();

	public static boolean isInternetConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isConnected() && ni.isAvailable())
			return true;
		return false;
	}

	public static boolean isSDCardExist() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;

		return false;

	}

	public static void checkFilesToBeUpload(final Context context) {
		if (!ShareTools.isInternetConnected(context))
			return;
		File dir = context.getExternalCacheDir();
		final File filelist[] = dir.listFiles();
		final ClientHelper clientHelper = new ClientHelper();
		Log.d(TAG, "fileList" + filelist.toString());
		if (filelist != null)
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (File file : filelist) {
						Log.d(TAG, "file" + file.getName());
						SharedPreferences settings = context
								.getSharedPreferences("account",
										Context.MODE_PRIVATE);
						SharepreferenceHelper preferenceHelper = new SharepreferenceHelper(
								settings);

						try {
							clientHelper.uploadFile(
									preferenceHelper.getUserToken(), file);
							Log.d("upload", file.getName() + " uploaded");
							file.delete();
						} catch (Exception e) {
							Log.d("upload", "upload fail :(");
							e.printStackTrace();
						}

					}

				}
			}).start();
	}

}
