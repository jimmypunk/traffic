package CMU.SV;

import java.util.Arrays;

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

public class DataUpdate implements LocationListener, SensorEventListener{
	private static final int TWO_MINUTES = 1000 * 60 * 2;
	private Location currentBestLocation;
	private Location previousBestLocation;
	private LocationManager mLocationManager;
	private SensorManager mSensorManager;
	private int sensorTypes[] = new int[] { Sensor.TYPE_ACCELEROMETER};
	private static final float NS2S = 1.0f / 1000000000.0f;
	private float[] acceleration = new float[] {0,0,0};
	private float mDeltaAccelerometer = 0;
	private String TAG = DataUpdate.class.getSimpleName();
	
    private float deltaAccelerometerReading(float[] oldReading, float[] newReading){
    	float delta = 0;
    	for(int i = 0; i < 3; i ++){
    		delta += Math.abs(oldReading[i] - newReading[i]); 
    	}
    	return delta;
    }
	
	
	public DataUpdate(Context context){
		mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		Vibrator myVibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
	}
	public void startRecording(){
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		for (int i = 0; i < sensorTypes.length; i++) {
			Sensor mSensor = mSensorManager.getDefaultSensor(sensorTypes[i]);
			if (mSensor != null) {
				mSensorManager.registerListener(this,
						mSensor, SensorManager.SENSOR_DELAY_NORMAL);
			}

		}
	}
	public void stopRecording(){
		mLocationManager.removeUpdates(this);
		mSensorManager.unregisterListener(this);
	}
	/* Location parts */
	@Override
	public void onLocationChanged(Location location) {

		if (isBetterLocation(location, currentBestLocation)) {
			currentBestLocation = location;
			//send_location_msg(location);
			Log.d("onLocationChanged","location:"+currentBestLocation.toString());
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
			
			float newDeltaAccelerometer = deltaAccelerometerReading(acceleration, event.values);
			acceleration = event.values;
			mDeltaAccelerometer = lowpassFilter(newDeltaAccelerometer,mDeltaAccelerometer,0.6f);
			if( isMoving(mDeltaAccelerometer) ){		
				Log.d(TAG,"is moving");
			}
			else{
				Log.d(TAG,"is not moving");
			}
		}
		
	}
	private boolean isMoving(float deltaAccelerometer){
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
