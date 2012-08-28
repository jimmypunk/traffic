package cmusv.mr.carbon.data;

import java.util.ArrayList;
import java.util.Iterator;
import android.location.Location;
import android.util.Log;

public class DataWindow {
	private ArrayList <Location> locWindowData;
	private ArrayList <AccelerData> accelerWindowData;
	private long timeWindow;
	
	public DataWindow(long timeWindow){
		locWindowData = new ArrayList<Location>();
		accelerWindowData = new ArrayList<AccelerData>();
		this.timeWindow = timeWindow;
	}
	
	public void addLocationToWindow(Location loc){
		locWindowData.add(loc);
	}
	
	public void addAccelerToWindow(float[] values, long timestamp){
		assert(values.length == 3);
		accelerWindowData.add(new AccelerData(values, timestamp));
	}
	
	public ArrayList<Location> getCurrentLocationWindow(long currentTime){
		slideWindow(currentTime);
		return locWindowData;
	}
	public ArrayList<AccelerData> getCurrentAccelerWindow(long currentTime){
		slideWindow(currentTime);
		return accelerWindowData;
	}
	
	public void clear(){
		locWindowData.clear();
		accelerWindowData.clear();
	}
	
	private void slideWindow(long currentTime){
		Iterator<Location> locIt = locWindowData.iterator();
		while(locIt.hasNext()){
			Location location = locIt.next();
			if(currentTime - location.getTime()>timeWindow){
				locIt.remove();
			} 
		}
		Iterator<AccelerData> accelerIt = accelerWindowData.iterator();
		while(accelerIt.hasNext()){
			AccelerData accelerdata = accelerIt.next();
			
			if(currentTime - accelerdata.getTime()>timeWindow){
				Log.d("ActivityLevel","Delete:"+accelerdata.getTime() + "," + currentTime);
				accelerIt.remove();
			} 
			
		}
			 
		 
	}
	public float getCurrentActivityLevel(long currentTime){
		slideWindow(currentTime);
		Iterator<AccelerData> accelerIt = accelerWindowData.iterator();
		float sum = 0;
		float sumSqrt = 0;	
		if(accelerWindowData.size() == 0){ 
			Log.d("ActivityLevel","no data");
			return 0;}
		while(accelerIt.hasNext()){
			AccelerData accelerdata = accelerIt.next();
			sum += accelerdata.getMagnitude();
			sumSqrt += Math.pow(accelerdata.getMagnitude(),2);
		}
		
		float average = sum/accelerWindowData.size();
		
		return (float) (sumSqrt/accelerWindowData.size() - Math.pow(average,2));
	}


	
}
