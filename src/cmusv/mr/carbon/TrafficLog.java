package cmusv.mr.carbon;
import java.io.File;

import cmusv.mr.carbon.io.sendToServer.ClientHelper;
import cmusv.mr.carbon.service.sensors.SensorLogService;
import cmusv.mr.carbon.utils.ShareTools;
import CMU.SV.R;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;


public class TrafficLog extends Activity {
	private Button startButton;
	private Button stopButton;
	private Spinner trafficSpinner;
	private String[] trafficModeList = { "walk", "bike",
			"car", "light rail" };
	private String useChoice = null;
	
	private ClientHelper mHelper;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
		trafficSpinner = (Spinner) findViewById(R.id.position);
		startButton = (Button) findViewById(R.id.startButton);
		stopButton = (Button) findViewById(R.id.stopButton);
		startButton.setOnClickListener(startClickListener);
		stopButton.setOnClickListener(stopClickListener);
		setPlaceAdaper();
		serviceStateSetting();
		if(!ShareTools.isSDCardExist()){
			finish();
		}
		mHelper = new ClientHelper();
		/*
		 * server upload file template 
		 * 
		 * Thread t = new Thread(){
			@Override
			public void run(){
				try{
					mHelper.uploadFile("d35528c14af11f08881c8b924de396e4", "/sdcard/MyTracks/csv/louis.csv");
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		};
		t.start();
		*/
		
	}


	private void setPlaceAdaper() {
		ArrayAdapter<String> positionAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, trafficModeList);
		positionAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		trafficSpinner.setAdapter(positionAdapter);
		trafficSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
					public void onItemSelected(AdapterView adapterView,
							View view, int position, long id) {
						useChoice = trafficModeList[position];
					}

					public void onNothingSelected(AdapterView adapterView) {

					}
				});
	}

	private void serviceStateSetting() {
		if (isMyServiceRunning()) {
			startButton.setEnabled(false);
			stopButton.setEnabled(true);
		} else {
			startButton.setEnabled(true);
			stopButton.setEnabled(false);
		}
	}

	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {
			if ("cmusv.mr.carbon.service.sensors.SensorLogService".equals(service.service
					.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private Button.OnClickListener startClickListener = new Button.OnClickListener() {
		public void onClick(View arg0) {
			
			Bundle bundle = new Bundle();
			bundle.putString("position", useChoice);
			Intent intent = new Intent(TrafficLog.this, SensorLogService.class);
			intent.putExtras(bundle);
			startService(intent);
			startButton.setEnabled(false);
			stopButton.setEnabled(true);
		}
	};

	private Button.OnClickListener stopClickListener = new Button.OnClickListener() {
		public void onClick(View arg0) {

			
			Intent intent = new Intent(TrafficLog.this, SensorLogService.class);
			stopService(intent);
			startButton.setEnabled(true);
			stopButton.setEnabled(false);

		}
	};
}
