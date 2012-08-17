package cmusv.mr.carbon.data.algorithm;

import java.util.ArrayList;

import android.location.Location;

public class DataAnalyst {
	private static int ERROR = -1;
	private static int WALKING = 0;
	private static int BIKING = 1;
	private static int TRAIN = 2;
	private static int DRIVING = 3;
	
	
	private static double SPEED_LIMIT = 50;
	private static double SPEED_CAR = 70;
	private static double SPEED_MAX_WALK = 15;
	
	private ArrayList<Location> mKnowledge = null;
	

	public DataAnalyst(ArrayList<Location> data) {
		mKnowledge = data;
	}

	public int extractFeature() {
		if (mKnowledge != null) {
			double averageSpeed = this.averageSpeed(mKnowledge);
			double maxSpeed = this.maxSpeed(mKnowledge);
			double averageAccuracy = this.averageAccuracy(mKnowledge);
			double averageBearingChange = this.averageBearingChange(mKnowledge);
			double varianceSpeed = this.varianceSpeed(mKnowledge);
			double averageAcceleration = this.averageAcceleration(mKnowledge);
			
			return this.analyze(averageSpeed, maxSpeed, averageAccuracy, averageBearingChange, varianceSpeed, averageAcceleration);
		} 
		return ERROR;
	}
	
	public void setAnotherTripData(ArrayList<Location> l){
		mKnowledge = l;
	}
	
	public int analyze(double averageSpeed, double maxSpeed, double averageAccuracy, double averageBearingChange, double varianceSpeed, double averageAcceleration){
		if(maxSpeed > SPEED_LIMIT){
			/*
			 *  possibly drive or train
			 */
			if(averageSpeed > SPEED_CAR){
				return DRIVING;
			}
			else{
				return TRAIN;
			}
		}
		else{
			/*
			 *  possibly walk or bike 
			 */ 
			if(maxSpeed > SPEED_MAX_WALK){
				return BIKING;
			}
			else {
				return WALKING;
			}
		}
		//return ERROR;
	}

	private double averageSpeed(ArrayList<Location> data) {
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

	private double maxSpeed(ArrayList<Location> data) {
		double ret = 0;
		for (int i = 0; i < data.size() - 1; i++) {
			if (data.get(i).getSpeed() > ret) {
				ret = data.get(i).getSpeed();
			}
		}
		return ret;
	}

	private double averageAccuracy(ArrayList<Location> data) {
		double ret = 0;
		for (int i = 0; i < data.size(); i++) {
			ret += data.get(i).getAccuracy();
		}
		return ret / (double) data.size();
	}

	private double averageBearingChange(ArrayList<Location> data) {
		double ret = 0;
		for (int i = 0; i < data.size() - 1; i++) {
			ret += Math.abs(data.get(i + 1).getBearing()
					- data.get(i).getBearing());
		}
		return ret / (double) data.size();
	}

	private double varianceSpeed(ArrayList<Location> data) {
		double ret = 0;
		double averageSpeed = averageSpeed(data);
		for (int i = 0; i < data.size(); i++) {
			ret += Math.abs(data.get(i).getSpeed() - averageSpeed);
		}
		return ret / (double) data.size();
	}

	private double averageAcceleration(ArrayList<Location> data) {
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
