package cmusv.mr.carbon.map;

import java.util.List;
import java.util.Locale;

import cmusv.mr.carbon.utils.ShareTools;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import cmusv.mr.carbon.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MapPage extends MapActivity {

	private Location current_location = null;

	private MapView mapview;
	private GeoPoint currentGeoPoint = null;
	private int intZoomLevel = 17;
	private List<Overlay> mapOverlays;
	private GeoPoint centerGeoPoint = null;
	private int latitude_span = 4000000; // default
	public AsyncLocationRunner runner;
	private MyLocationOverlay myoverlay;
	private String TAG = "MapPage";
	@Override
	protected void onCreate(Bundle icicle) {
		// TODO Auto-generated method stub
		super.onCreate(icicle);
		setContentView(R.layout.map);
		if (!ShareTools.isInternetConnected(this)) {
			Toast.makeText(this, "No internet connection. Can not update map!",
					Toast.LENGTH_LONG).show();
		}
		setupMapView();
		initial_mapOverlays();

		EventHandler handler = new EventHandler(this.getMainLooper());
		runner = new AsyncLocationRunner(this, handler);

	}
	void setupMapView(){
		mapview = (MapView) findViewById(R.id.mapview);
		mapview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				centerGeoPoint = mapview.getMapCenter();
				latitude_span = mapview.getLatitudeSpan();
				Log.d(TAG, "center:" + centerGeoPoint.getLatitudeE6()
						+ " " + centerGeoPoint.getLongitudeE6());
				Log.d(TAG, "latitude_span:" + latitude_span);
			}

		});
	}
	class EventHandler extends Handler {
		private MapController mc;

		public EventHandler(Looper looper) {

			super(looper);
			intZoomLevel = mapview.getZoomLevel();
			mc = mapview.getController();
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AsyncLocationRunner.LOCATION_MSG:
				current_location = (Location) msg.obj;
				currentGeoPoint = getGeoByLocation(current_location);
				// mTextView01.setText("Update:\n"+"Latitude: "+"\nLongitude: "+current_location.getLongitude());
				mc.setZoom(intZoomLevel);
				mc.animateTo(currentGeoPoint);
				Toast.makeText(MapPage.this, "Update location",
						Toast.LENGTH_SHORT);
				drawCurentLocationOnMap();

				break;
			case AsyncLocationRunner.LAST_LOCATION_MSG:
				current_location = (Location) msg.obj;
				currentGeoPoint = getGeoByLocation(current_location);
				// mTextView01.setText("Last location\n"+"Latitude: "+current_location.getLatitude()+"\nLongitude: "+current_location.getLongitude());

				Toast.makeText(MapPage.this, "Last location",
						Toast.LENGTH_SHORT);
				mc.setZoom(intZoomLevel);
				mc.animateTo(currentGeoPoint);
				drawCurentLocationOnMap();

				break;
			case AsyncLocationRunner.HAVENT_GET_LOCATION_MSG:
				Toast.makeText(MapPage.this, (String) msg.obj,
						Toast.LENGTH_SHORT);
				// mTextView01.setText((String)msg.obj);
				break;
			case AsyncLocationRunner.GET_LOCATION_FAIL_MSG:
				Toast.makeText(MapPage.this, (String) msg.obj,
						Toast.LENGTH_SHORT);
				// mTextView01.setText((String)msg.obj);
				break;
			case AsyncLocationRunner.LOCATION_ERROR_MSG:
				Toast.makeText(MapPage.this, (String) msg.obj,
						Toast.LENGTH_SHORT);
				// mTextView01.setText((String)msg.obj);
				break;
			}
		}

	}

	public void onRestart() {
		super.onRestart();
		// runner.restart();
		Log.v(TAG, "onReStart");
	}

	public void onStop() {
		super.onStop();
		runner.stop();
		Log.v(TAG, "onReStart");
	}

	private void drawCurentLocationOnMap() {
		mapOverlays.remove(myoverlay);

		Log.d(TAG, "here" + currentGeoPoint.getLatitudeE6() + " "
				+ currentGeoPoint.getLongitudeE6());
		Drawable marker;
		marker = this.getResources().getDrawable(R.drawable.icon_green);
		OverlayItem overlayitem;
		overlayitem = new OverlayItem(currentGeoPoint, "fuck", "wtf!");

		myoverlay.addOverlay(overlayitem, marker);
		mapOverlays.add(myoverlay);

	}

	private void initial_mapOverlays() {
		mapOverlays = mapview.getOverlays();

		Drawable marker = this.getResources()
				.getDrawable(R.drawable.icon_green);// default marker
		myoverlay = new MyLocationOverlay(marker, this);
	}

	public String getAddressbyGeoPoint(GeoPoint gp) {
		String strReturn = "";
		try {

			if (gp != null) {

				Geocoder gc = new Geocoder(MapPage.this, Locale.getDefault());

				double geoLatitude = (int) gp.getLatitudeE6() / 1E6;
				double geoLongitude = (int) gp.getLongitudeE6() / 1E6;

				List<Address> lstAddress = gc.getFromLocation(geoLatitude,
						geoLongitude, 1);
				StringBuilder sb = new StringBuilder();

				if (lstAddress.size() > 0) {
					Address adsLocation = lstAddress.get(0);

					for (int i = 0; i < adsLocation.getMaxAddressLineIndex(); i++) {
						sb.append(adsLocation.getAddressLine(i)).append("\n");
					}
					sb.append(adsLocation.getLocality()).append("\n");
					sb.append(adsLocation.getPostalCode()).append("\n");
					sb.append(adsLocation.getCountryName());
				}

				strReturn = sb.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return strReturn;
	}

	private GeoPoint getGeoByLocation(Location location) {
		GeoPoint gp = null;
		try {
			if (location != null) {
				double geoLatitude = location.getLatitude() * 1E6;
				double geoLongitude = location.getLongitude() * 1E6;
				gp = new GeoPoint((int) geoLatitude, (int) geoLongitude);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gp;
	}

	public static void refreshMapViewByGeoPoint(GeoPoint gp, MapView mv,
			int zoomLevel, boolean bIfSatellite) {
		try {
			mv.displayZoomControls(true);

			MapController mc = mv.getController();

			mc.animateTo(gp);

			mc.setZoom(zoomLevel);

			// mv.getMaxZoomLevel()

			if (bIfSatellite) {
				mv.setSatellite(true);
			} else {
				mv.setSatellite(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processLocationUpdated(Location location) {

		currentGeoPoint = getGeoByLocation(location);

		refreshMapViewByGeoPoint(currentGeoPoint, mapview, intZoomLevel, false);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "Zoom to current position");
		// return super.onCreateOptionsMenu(menu);
		return true;

	}

	public boolean onOptionsItemSelected(MenuItem item) {

		MapController mc = mapview.getController();

		if (item.getItemId() == 0) {

			mc.animateTo(currentGeoPoint);
			return true;
		}
		return false;
	}

}
