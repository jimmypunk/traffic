package cmusv.mr.carbon.data;

import java.util.ArrayList;
import java.util.Iterator;
import android.location.Location;

public class LocationDataWindow {
	private ArrayList <Location> locWindowData;
	private long timeWindow;
	public LocationDataWindow(long timeWindow){
		locWindowData = new ArrayList<Location>();
		this.timeWindow = timeWindow;
	}
	
	public void addLocationToWindow(Location loc){
		locWindowData.add(loc);
	}
	
	
	public ArrayList<Location> getCurrentLocationWindow(long currentTime){
		slideWindow(currentTime);
		return locWindowData;
	}
	public void clear(){
		locWindowData.clear();		
	}
	
	private void slideWindow(long currentTime){
		Iterator<Location> locIt = locWindowData.iterator();
		while(locIt.hasNext()){
			Location location = locIt.next();
			if(currentTime - location.getTime()>timeWindow){
				locIt.remove();
			} 
		}
	}

}
