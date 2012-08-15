package cmusv.mr.carbon.map;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class AsyncLocationRunner {
	public static final int LOCATION_MSG = 9000;
	public static final int HAVENT_GET_LOCATION_MSG = 9001;
	public static final int GET_LOCATION_FAIL_MSG = 9003;
	public static final int LAST_LOCATION_MSG = 9004;
	public static final int LOCATION_ERROR_MSG = 9005;
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	private Handler _handler;
	private Context _context;
	private Location currentBestLocation = null;
	private Location locationFromNetwork = null;
	private Location locationFromGPS = null;
	private LocationManager mLocationManager;
	private String TAG = "AsyncLocationRunner";

	public AsyncLocationRunner(final Context context,
			final Handler handler) {
		_handler = handler;
		_context = context;
		new Thread() {
			public void run() {
				Log.d(TAG, "Thread is running");
				mLocationManager = (LocationManager) _context
						.getSystemService(Context.LOCATION_SERVICE);



				try {
					locationFromGPS = mLocationManager
							.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					locationFromNetwork = mLocationManager
							.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

					if (locationFromGPS != null) {
						send_last_location_msg(locationFromGPS);
						currentBestLocation = locationFromGPS;
					}

					else if (locationFromNetwork != null) {
						Log.d("here", "network provider work");
						send_last_location_msg(locationFromNetwork);
						currentBestLocation = locationFromNetwork;
					} else if (locationFromNetwork == null
							&& locationFromGPS == null) {
						send_havent_get_location_msg();
					}
				} catch (Exception e) {
					send_error_msg();
				}
				// Register the listener with the Location Manager to receive
				// location updates
				try {
					
					mLocationManager.requestLocationUpdates(
							LocationManager.GPS_PROVIDER, 5000, 20,
							locationListener);
					mLocationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER, 5000, 20,
							locationListener);

					

				} catch (Exception e) {
					send_error_msg();

				}
			}

		}.start();


	}
	public void restart(){
		mLocationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 5000, 20,
				locationListener);
		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 5000, 20,
				locationListener);
		
	}
	public void stop(){
		mLocationManager.removeUpdates(locationListener);
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

	private void send_error_msg() {
		String notification = "Error occur when retreiving current location";
		Message msg = _handler.obtainMessage(LOCATION_ERROR_MSG, notification);
		_handler.sendMessage(msg);
	}

	private void send_no_provider_msg() {
		String notification = "No location provider now\nPlease check your Location Setting";
		Message msg = _handler
				.obtainMessage(GET_LOCATION_FAIL_MSG, notification);
		_handler.sendMessage(msg);
	}

	private void send_havent_get_location_msg() {
		String notification = "trying to get location";
		Message msg = _handler.obtainMessage(HAVENT_GET_LOCATION_MSG,
				notification);
		_handler.sendMessage(msg);
	}

	private void send_location_msg(Location location) {
		Message msg = _handler.obtainMessage(LOCATION_MSG, location);
		_handler.sendMessage(msg);
	}

	private void send_last_location_msg(Location location) {
		Message msg = _handler.obtainMessage(LAST_LOCATION_MSG, location);
		_handler.sendMessage(msg);
	}


	public LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {

			if (isBetterLocation(location, currentBestLocation)) {
				currentBestLocation = location;
				send_location_msg(location);
			}

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	};

}
