package cmusv.mr.carbon.data;

import java.util.ArrayList;
import java.util.Iterator;
import android.util.Log;

public class AccelerDataWindow {
	private ArrayList<AccelerData> accelerWindowData;
	private long timeWindow;

	public AccelerDataWindow(long timeWindow) {

		accelerWindowData = new ArrayList<AccelerData>();
		this.timeWindow = timeWindow;
	}

	public void addAccelerToWindow(float[] values, long timestamp) {
		assert (values.length == 3);
		accelerWindowData.add(new AccelerData(values, timestamp));
	}

	/*
	 * public ArrayList<AccelerData> getCurrentAccelerWindow(long currentTime){
	 * slideWindow(currentTime); return accelerWindowData; }
	 */
	public float getCurrentActivityLevel(long currentTime) {
		slideWindow(currentTime);
		Iterator<AccelerData> accelerIt = accelerWindowData.iterator();
		float sum = 0;
		float squareSum = 0;
		if (accelerWindowData.size() == 0) {
			Log.d("ActivityLevel", "no data");
			return 0;
		}
		// calculate variance in the time window
		while (accelerIt.hasNext()) {
			AccelerData accelerdata = accelerIt.next();
			sum += accelerdata.getMagnitude();
			squareSum += Math.pow(accelerdata.getMagnitude(), 2);
		}

		float average = sum / accelerWindowData.size();

		return (float) (squareSum/ accelerWindowData.size() - Math.pow(average, 2));
	}

	private void slideWindow(long currentTime) {
		Iterator<AccelerData> accelerIt = accelerWindowData.iterator();
		while (accelerIt.hasNext()) {
			AccelerData accelerdata = accelerIt.next();

			if (currentTime - accelerdata.getTime() > timeWindow) {
				Log.d("ActivityLevel", "Delete:" + accelerdata.getTime() + ","
						+ currentTime);
				accelerIt.remove();
			}

		}

	}

	public void clear() {
		accelerWindowData.clear();
	}
}
