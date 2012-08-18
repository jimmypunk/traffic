package cmusv.mr.carbon.data.algorithm;

import java.util.ArrayList;

import android.location.Location;

public class DataAnalystTool {
	public DataAnalystTool(){
		
	}
	
	public double averageSpeed(ArrayList<Location> data) {
		double ret = 0;
		for (int i = 0; i < data.size() - 1; i++) {
			double distance = data.get(i).distanceTo(data.get(i + 1));
			ret += distance;
		}
		Long srcTime = data.get(0).getTime();
		Long desTime = data.get(data.size() - 1).getTime();
		double timediff = (desTime - srcTime) / 1000;
		return ret / (timediff);
	}

	public double maxSpeed(ArrayList<Location> data) {
		double ret = 0;
		for (int i = 0; i < data.size() - 1; i++) {
			if (data.get(i).getSpeed() > ret) {
				ret = data.get(i).getSpeed();
			}
		}
		return ret;
	}

	public double averageAccuracy(ArrayList<Location> data) {
		double ret = 0;
		for (int i = 0; i < data.size(); i++) {
			ret += data.get(i).getAccuracy();
		}
		return ret / (double) data.size();
	}

	public double averageBearingChange(ArrayList<Location> data) {
		double ret = 0;
		for (int i = 0; i < data.size() - 1; i++) {
			ret += Math.abs(data.get(i + 1).getBearing()
					- data.get(i).getBearing());
		}
		return ret / (double) data.size();
	}

	public double varianceSpeed(ArrayList<Location> data) {
		double ret = 0;
		double averageSpeed = averageSpeed(data);
		for (int i = 0; i < data.size(); i++) {
			ret += Math.abs(data.get(i).getSpeed() - averageSpeed);
		}
		return ret / (double) data.size();
	}

	public double averageAcceleration(ArrayList<Location> data) {
		double ret = 0;
		int validateDataCount = 0;
		for (int i = 0; i < data.size() - 1; i++) {
			double timediff = data.get(i + 1).getTime() - data.get(i).getTime();
			double speeddiff = data.get(i + 1).getSpeed()
					- data.get(i).getSpeed();
			if (timediff != 0 && speeddiff != 0) {
				validateDataCount++;
				ret += Math.abs(speeddiff) / timediff;
			}
		}
		return ret / (double) validateDataCount;
	}
}
