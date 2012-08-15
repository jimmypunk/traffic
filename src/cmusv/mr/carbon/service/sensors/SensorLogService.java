package cmusv.mr.carbon.service.sensors;

import java.text.SimpleDateFormat;
import java.util.Date;

import cmusv.mr.carbon.data.DataUpdate;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class SensorLogService extends Service {
	private DataUpdate dataUpdate;
	private String userTrafficeMode = null;
	private String TAG = "SensorLogService";
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {

		Bundle bundle = intent.getExtras();
		userTrafficeMode = bundle.getString("position");
		Log.d("traffic", userTrafficeMode);
		dataUpdate = new DataUpdate(this);
		
		/*String logDateTimeString = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());*/
		Log.d(TAG,"onStart");
		dataUpdate.startRecording();
		super.onStart(intent, startId);
	}



	@Override
	public void onDestroy() {
		dataUpdate.stopRecording();
		super.onDestroy();

	}

	
}
