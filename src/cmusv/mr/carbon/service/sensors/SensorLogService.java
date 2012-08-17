package cmusv.mr.carbon.service.sensors;

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
import cmusv.mr.carbon.data.DataUpdate;

public class SensorLogService extends Service {
	private DataUpdate dataUpdate;
	private String userTrafficeMode = null;
	private String TAG = "SensorLogService";
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	private boolean isRecording = false;
	private NotificationManager mNotificationManager;
	@Override
	public void onStart(Intent intent, int startId) {

		Bundle bundle = intent.getExtras();
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		userTrafficeMode = bundle.getString("position");
		Log.d("traffic", userTrafficeMode);
		dataUpdate = new DataUpdate(this);
		
		/*String logDateTimeString = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());*/
		Log.d(TAG,"onStart");
		dataUpdate.startRecording();
		isRecording = true;
		showNotification();
		super.onStart(intent, startId);
	}



	@Override
	public void onDestroy() {
		dataUpdate.stopRecording();
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
			notification.setLatestEventInfo(this.getApplicationContext(), contentTitle,
					contentText, contentIntent);
			startForeground(1, notification);
		}
		else{
			 stopForeground(true);
		}

	}

	
}
