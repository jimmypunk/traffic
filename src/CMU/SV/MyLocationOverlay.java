package CMU.SV;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Looper;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class MyLocationOverlay extends ItemizedOverlay {
	private OverlayItem mOverlays = null;

	private Context mctx;

	public MyLocationOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		mctx = context;
		// EventHandler handler = new EventHandler(Looper.getMainLooper());

		// TODO Auto-generated constructor stub
	}

	// void boundCenter(android.graphics.drawable.Drawable balloon)
	public void addOverlay(OverlayItem overlay) {
		mOverlays =overlay;
		populate();
	}

	public void addOverlay(OverlayItem overlay, Drawable marker) {
		boundCenterBottom(marker);
		overlay.setMarker(marker);
		mOverlays = overlay;
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		OverlayItem item = mOverlays;

		return item;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		if(mOverlays!=null)
		return 1;
		else
		return 0;
	}

}
