package cmusv.mr.carbon.service.sensors;

import java.io.File;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import cmusv.mr.carbon.R;
import cmusv.mr.carbon.TrafficLog;
import cmusv.mr.carbon.data.DataCollector;


public class SensorLogService extends Service {
	private DataCollector dataCollector;
	private String TAG = "SensorLogService";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private boolean isRecording = false;
	private NotificationManager mNotificationManager;
	

	@Override
	public void onStart(Intent intent, int startId) {

		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		dataCollector = new DataCollector(this);

		/*
		 * String logDateTimeString = new SimpleDateFormat("yyyyMMdd_HHmmss")
		 * .format(new Date());
		 */
		
		Log.d(TAG, "onStart");
		dataCollector.startRecording();
		isRecording = true;
		
		showNotification();

		super.onStart(intent, startId);
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
