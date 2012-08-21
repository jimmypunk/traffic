package cmusv.mr.carbon.service.sensors;

import java.io.File;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import cmusv.mr.carbon.R;
import cmusv.mr.carbon.TrafficLog;
import cmusv.mr.carbon.data.DataCollector;
import cmusv.mr.carbon.io.sendToServer.ClientHelper;
import cmusv.mr.carbon.utils.ShareTools;

public class SensorLogService extends Service {
	private DataCollector dataCollector;
	private String TAG = "SensorLogService";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private boolean isRecording = false;
	private NotificationManager mNotificationManager;
	private ClientHelper clientHelper;

	@Override
	public void onStart(Intent intent, int startId) {

		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		dataCollector = new DataCollector(this);

		/*
		 * String logDateTimeString = new SimpleDateFormat("yyyyMMdd_HHmmss")
		 * .format(new Date());
		 */
		checkFilesToBeUpload();
		Log.d(TAG, "onStart");
		dataCollector.startRecording();
		isRecording = true;
		clientHelper = new ClientHelper();
		showNotification();
		
		super.onStart(intent, startId);
	}

	private void checkFilesToBeUpload() {
		if (!ShareTools.isInternetConnected(this))
			return;
		File dir = getExternalFilesDir(null);
		final File filelist[] = dir.listFiles();
		if (filelist != null)
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (File file : filelist) {

						try {
							clientHelper.uploadFile(
									"d35528c14af11f08881c8b924de396e4", file);
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

	@Override
	public void onDestroy() {
		dataCollector.stopRecording();
		isRecording = false;
		showNotification();
		super.onDestroy();

	}

	private void showNotification() {
		if (isRecording) {
			Intent intent = new Intent(this, TrafficLog.class);
			int icon = R.drawable.notification_icon;
			CharSequence tickerText = "";
			long when = System.currentTimeMillis();

			Notification notification = new Notification(icon, tickerText, when);

			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					intent, 0);
			CharSequence contentTitle = "Mr.carbon";
			CharSequence contentText = "Recording your track...";
			notification.setLatestEventInfo(this.getApplicationContext(),
					contentTitle, contentText, contentIntent);
			startForeground(1, notification);
		} else {
			stopForeground(true);
		}

	}

}
