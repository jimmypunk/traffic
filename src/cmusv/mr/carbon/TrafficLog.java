package cmusv.mr.carbon;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cmusv.mr.carbon.data.algorithm.DataAnalyst.DataType;
import cmusv.mr.carbon.service.sensors.SensorLogService;
import cmusv.mr.carbon.utils.ShareTools;
import cmusv.mr.carbon.utils.SharepreferenceHelper;

public class TrafficLog extends Activity {
	private Button startButton;
	private Button stopButton;
	// private Spinner trafficSpinner;
	// private String[] trafficModeList = { "walk", "bike", "car", "light rail"
	// };
	private String useChoice = null;
	private BroadcastReceiver receiver;
	private ImageView statusImage;
	private ImageView bgImage;
	private TextView statusText;
	public static final String ACTION = "android.intent.action.cmusv.mr.carbon.dataTransmit";
	private final String TAG = TrafficLog.class.getSimpleName();
	private String movingStatus = "moving:false";
	private String activityStatus = "dataType:unknown";
	private String activityLevelStatus = "activityLevel:0";
	private SharepreferenceHelper preferenceHelper;
	private ImageAnimation animation;
	private WakeLock wakeLock;
	public static final long NO_TRACK = -2;
	private long recordingTrackId = NO_TRACK;
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);

		SharedPreferences settings = getSharedPreferences("account",
				MODE_PRIVATE);
		preferenceHelper = new SharepreferenceHelper(settings);
		String account = preferenceHelper.getUserAccount();
		if (account == null) {
			Intent i = new Intent(getApplicationContext(), LoginActivity.class);
			startActivity(i);
		}
		
		startButton = (Button) findViewById(R.id.startButton);
		stopButton = (Button) findViewById(R.id.stopButton);
		startButton.setOnClickListener(startClickListener);
		stopButton.setOnClickListener(stopClickListener);
		statusText = (TextView) findViewById(R.id.status_text);
		
		serviceStateSetting();
		if (!ShareTools.isSDCardExist()) {
			Toast.makeText(this, "SD card is not mounted!", Toast.LENGTH_SHORT)
					.show();
			finish();
		}

		statusImage = (ImageView) findViewById(R.id.status_img);
		bgImage = (ImageView) findViewById(R.id.bg_img);
		animation = new ImageAnimation(this, bgImage);
		animation.setAnimation(animation.bg, 1000);

		
		setupBroadcastReceiver();
		ShareTools.checkFilesToBeUpload(this);
		

	}

	void popOutRewardDialog(DataType dataType) {
		AlertDialog.Builder badgeDialog = new AlertDialog.Builder(
				TrafficLog.this);
		badgeDialog.setTitle("Badge notification");

		LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.mylayout, null);
		
		TextView text = (TextView) view.findViewById(R.id.reward_message);
		text.setText("Congrats, you just got a new badge");
		ImageView badge = (ImageView) view.findViewById(R.id.badge);
		switch(dataType){
		case WALKING:
			badge.setImageDrawable(getResources().getDrawable( R.drawable.walkman_badge ));
			break;
		case BIKING:
			badge.setImageDrawable(getResources().getDrawable( R.drawable.bikeman_badge ));
			break;
		case DRIVING:
			badge.setImageDrawable(getResources().getDrawable( R.drawable.carman_badge ));
			break;
		case TRAIN:
			badge.setImageDrawable(getResources().getDrawable( R.drawable.vtaman_badge ));
			break;
		}
		
		badgeDialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						

					}
				});


		badgeDialog.setView(view);
		badgeDialog.show();
	}

	public void onResume() {
		acquireWakeLock();
		super.onResume();
	}

	@Override
	public void onPause() {
		releaseWakeLock();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (receiver != null)
			unregisterReceiver(receiver);
		super.onDestroy();
	}

	private void acquireWakeLock() {
		if (wakeLock == null) {

			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, this
					.getClass().getCanonicalName());
			wakeLock.acquire();
		}

	}

	private void releaseWakeLock() {
		if (wakeLock != null && wakeLock.isHeld()) {
			wakeLock.release();
			wakeLock = null;
		}

	}

	private void setupBroadcastReceiver() {
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				if(intent.hasExtra("rewardMessage")){
					DataType dataType = (DataType) intent
							.getSerializableExtra("rewardMessage");
					popOutRewardDialog(dataType);
				}
				if(intent.hasExtra("trackId")){
					recordingTrackId = intent.getLongExtra("trackId", NO_TRACK);
					Log.d(TAG,"receive trackId"+recordingTrackId);
				}
				if (intent.hasExtra("isMoving")) {

					boolean isMoving = intent
							.getBooleanExtra("isMoving", false);
					movingStatus = "ismoving:" + isMoving;
					if (isMoving) {
						animation.startAnimation();
					} else {
						animation.stopAnimation();
					}

				}
				if (intent.hasExtra("dataType")) {
					DataType dataType = (DataType) intent
							.getSerializableExtra("dataType");

					activityStatus = "dataType:" + dataType.toString();
					switch (dataType) {
					case WALKING:
						statusImage.setImageResource(R.drawable.mrc_walk);
						break;
					case BIKING:
						statusImage.setImageResource(R.drawable.mrc_bicycle);
						break;
					case TRAIN:
						statusImage.setImageResource(R.drawable.mrc_train);
						break;
					case DRIVING:
						statusImage.setImageResource(R.drawable.mrc_car);
						break;
					case ERROR:
						// it should not happened...
						Log.e(TAG, "error, wrong dataType");
						break;
					}

				}
				if (intent.hasExtra("activityLevel")) {
					float activityLevel = intent.getFloatExtra("activityLevel",
							-1);
					if (activityLevel < 0) {
						activityLevelStatus = "activityLevel:" + "missing data";
					} else {
						activityLevelStatus = "activityLevel:" + activityLevel;
					}

				}

				statusText.setText(movingStatus + "\n" + activityStatus + "\n"
						+ activityLevelStatus);
			}

		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION);
		registerReceiver(receiver, filter);
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
			if ("cmusv.mr.carbon.service.sensors.SensorLogService"
					.equals(service.service.getClassName())) {
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
			statusText.setText("Mr.carbon");
			movingStatus = "moving:false";
			activityStatus = "dataType:unknown";
			activityLevelStatus = "activityLevel:0";
			animation.stopAnimation();
			stopService(intent);
			startButton.setEnabled(true);
			stopButton.setEnabled(false);
			intent = new Intent(getApplicationContext(),TrackSummaryPage.class);
			intent.putExtra("trackId",recordingTrackId);
			startActivity(intent);

		}
	};
}
