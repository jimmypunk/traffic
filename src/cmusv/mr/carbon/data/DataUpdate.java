package cmusv.mr.carbon.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;

import cmusv.mr.carbon.data.stats.TripStatistics;
import cmusv.mr.carbon.db.DatabaseHelper;
import cmusv.mr.carbon.io.file.CsvTrackWriter;
import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class DataUpdate implements LocationListener, SensorEventListener{
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private Location currentBestLocation;
	private Location previousBestLocation;
	private LocationManager mLocationManager;
	private SensorManager mSensorManager;
	private Vibrator mVibrator;
	private int sensorTypes[] = new int[] { Sensor.TYPE_ACCELEROMETER};
	private static final float NS2S = 1.0f / 1000000000.0f;
	private float[] previousEventValue = new float[]{0,0,0};
	private float mDeltaAccelerometer = 0;
	private String TAG = DataUpdate.class.getSimpleName();
	private DatabaseHelper dbHelper;
	private long recordingTrackId = -1L;
	private boolean isRecording = false;
	
	

    private float deltaAccelerometerReading(float[] oldReading, float[] newReading){
    	float delta = 0;
    	for(int i = 0; i < 3; i ++){
    		delta += Math.abs(oldReading[i] - newReading[i]); 
    	}
    	Log.d(TAG,"delta:"+delta);
    	
    	return delta;
    }
	
	private Context mContext;
	public DataUpdate(Context context){
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		mVibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
		dbHelper = new DatabaseHelper(context);
		mContext = context;
	}
	public void startNewTrack(){
		long startTime = System.currentTimeMillis();
		Track recordingTrack = new Track();
		cmusv.mr.carbon.data.stats.TripStatistics trackStats = recordingTrack.getTripStatistics();
	    trackStats.setStartTime(startTime);
	    recordingTrackId = dbHelper.insertTrack(recordingTrack);
	    Toast.makeText(mContext,"new track id:"+recordingTrackId, Toast.LENGTH_SHORT).show();
	     
	}
	private boolean isTrackInProgress() {
	    return recordingTrackId != -1 || isRecording;
	  }
	  private void endCurrentTrack() {
		    if (!isTrackInProgress()) {
		      return;
		    }
		    isRecording = false;
		    Track recordedTrack = dbHelper.getTrack(recordingTrackId);
		    if (recordedTrack != null) {
		      long lastRecordedLocationId = dbHelper.getLastLocationId(recordingTrackId);
		      if (lastRecordedLocationId >= 0 && recordedTrack.getStopId() >= 0) {
		        recordedTrack.setStopId(lastRecordedLocationId);
		      }
		      TripStatistics tripStatistics = recordedTrack.getTripStatistics();
		      tripStatistics.setStopTime(System.currentTimeMillis());
		      tripStatistics.setTotalTime(tripStatistics.getStopTime() - tripStatistics.getStartTime());
		      dbHelper.updateTrack(recordedTrack);
		    }
		    //showNotification();
		    writeTrack2File(recordedTrack);
		    recordingTrackId = -1L;
		    
		  }
	public void writeTrack2File(Track track){
		CsvTrackWriter writer = new CsvTrackWriter(mContext);
		File file = new File(mContext.getExternalCacheDir(), System.currentTimeMillis()+".csv");
		Log.d(TAG,file.getAbsolutePath());
		OutputStream out;
		try {
			out = new FileOutputStream(file);
			writer.prepare(track,out);
			writer.writeHeader();
			writer.writeBeginTrack();
			writer.writeLocations();
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	public void startRecording(){
		startNewTrack();
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		for (int i = 0; i < sensorTypes.length; i++) {
			Sensor mSensor = mSensorManager.getDefaultSensor(sensorTypes[i]);
			if (mSensor != null) {
				mSensorManager.registerListener(this,
						mSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}

		}
		isRecording = true;
	}
	public void stopRecording(){
		endCurrentTrack();
		mLocationManager.removeUpdates(this);
		mSensorManager.unregisterListener(this);
	}
	/* Location parts */
	@Override
	public void onLocationChanged(Location location) {

		if (isBetterLocation(location, currentBestLocation)) {
			currentBestLocation = location;
			//send_location_msg(location);
			//Log.d("onLocationChanged","location:"+currentBestLocation.toString());
			if(isRecording){
				long rowId = dbHelper.insertTrackPoint(currentBestLocation, recordingTrackId);
				Toast.makeText(mContext,"rowId:"+rowId+" location:"+currentBestLocation,Toast.LENGTH_SHORT).show();
			}
		}

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onProviderDisabled(String provider) {
	}
	
	protected boolean isBetterLocation(Location location,
			Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
				.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(),
				currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate
				&& isFromSameProvider) {
			return true;
		}
		return false;
	}
	
	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}
	


	/* ==================================================================== 
	 * Sensor part
	 * */
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		Log.d("SensorEventListener", "onSensorChanged called");
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER ) {
			Log.d("onSensorChanged",Arrays.toString(event.values));
			float newDeltaAccelerometer = deltaAccelerometerReading(previousEventValue, event.values);
			previousEventValue = event.values.clone();
			mDeltaAccelerometer = lowpassFilter(newDeltaAccelerometer,mDeltaAccelerometer,0.6f);
			if( isMoving(mDeltaAccelerometer) ){		
				Log.d(TAG,"is moving");
				PhoneStatus.isMoving = PhoneStatus.moveStatus.move;
				
			}
			else{
				Log.d(TAG,"is not moving");
				PhoneStatus.isMoving = PhoneStatus.moveStatus.still;
				
			}
		}
		
	}
	private boolean isMoving(float deltaAccelerometer){
		Log.d(TAG,""+deltaAccelerometer);
		return (deltaAccelerometer > 1);
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		Log.d("SensorEventListener", "onAccuracyChanged called");
		
	}
	private float lowpassFilter(float newValue, float oldValue, float alpha) {
		return alpha * newValue + (1 - alpha) * oldValue;
	}

}
