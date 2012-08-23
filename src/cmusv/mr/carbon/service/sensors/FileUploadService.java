package cmusv.mr.carbon.service.sensors;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import cmusv.mr.carbon.io.sendToServer.ClientHelper;
import cmusv.mr.carbon.utils.ShareTools;
import cmusv.mr.carbon.utils.SharepreferenceHelper;

public class FileUploadService extends Service{
	private final String TAG = FileUploadService.class.getSimpleName();
	private ClientHelper clientHelper;
	private Handler handler = new Handler();
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.d("Service", "onBind called");
		
		return null;
	}
	public void onStart(Intent intent, int startId) {
		clientHelper = new ClientHelper();
		handler.postDelayed(checkFilesToBeUpload, 1000);
		super.onStart(intent, startId);
	}
	private Runnable checkFilesToBeUpload = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (!ShareTools.isInternetConnected(getApplicationContext()))
				handler.postDelayed(checkFilesToBeUpload, 10000);
			File dir = getExternalCacheDir();
			final File filelist[] = dir.listFiles();
			if (filelist != null){
				for (File file : filelist) {
					Log.d(TAG,"file" + file.getName());
					SharedPreferences settings = getSharedPreferences("account", MODE_PRIVATE);
					SharepreferenceHelper preferenceHelper = new SharepreferenceHelper(settings);

					try {
						clientHelper.uploadFile(preferenceHelper.getUserToken(), file);
						Log.d("upload", file.getName() + " uploaded");
						file.delete();
					} catch (Exception e) {
						Log.d("upload", "upload fail :(");
						e.printStackTrace();
					}
				}
			
			}
			Log.d("Runnable", "run called");
			
		}};
}
