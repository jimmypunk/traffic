package cmusv.mr.carbon.data;

import java.util.ArrayList;
import java.util.Iterator;

import android.location.Location;

public class DataWindow {
	private ArrayList <Location> windowOfData;
	private long timeWindow;
	public DataWindow(long timeWindow){
		windowOfData = new ArrayList<Location>();
		this.timeWindow = timeWindow;
	}
	public void addDataToWindow(Location loc){
		windowOfData.add(loc);
	}
	public ArrayList<Location> getCurrentWindow(long currentTime){
		slideWindow(currentTime);
		return windowOfData;
	}
	public void clear(){
		windowOfData.clear();
	}
	private void slideWindow(long currentTime){
		Iterator<Location> it = windowOfData.iterator();
		while(it.hasNext()){
			Location location = it.next();
			if(currentTime - location.getTime()>timeWindow){
				it.remove();
			} 
		}
			 
		 
	}
}
