package cmusv.mr.carbon.data.algorithm;

import java.util.ArrayList;

import android.location.Location;

public class DataAnalyst {
	/*
	 *  Define all the transportation Mode in Integer
	 */
	private static int ERROR = -1;
	private static int WALKING = 0;
	private static int BIKING = 1;
	private static int TRAIN = 2;
	private static int DRIVING = 3;
	
	/*
	 *  Variable needed for Rule-based algorithm...
	 */
	private static double SPEED_LIMIT = 50;
	private static double SPEED_CAR = 70;
	private static double SPEED_MAX_WALK = 15;
	
	/* 
	 *  What this analyst have ....
	 */
	private DataAnalystTool mTools = null;
	private ArrayList<Location> mKnowledgeOfLocations = null;
	

	public DataAnalyst(ArrayList<Location> data) {
		mTools = new DataAnalystTool();
		mKnowledgeOfLocations = data;
	}

	public int getAnalysisResult() {
		if (mKnowledgeOfLocations != null) {
			double averageSpeed = mTools.averageSpeed(mKnowledgeOfLocations);
			double maxSpeed = mTools.maxSpeed(mKnowledgeOfLocations);
			double averageAccuracy = mTools.averageAccuracy(mKnowledgeOfLocations);
			double averageBearingChange = mTools.averageBearingChange(mKnowledgeOfLocations);
			double varianceSpeed = mTools.varianceSpeed(mKnowledgeOfLocations);
			double averageAcceleration = mTools.averageAcceleration(mKnowledgeOfLocations);
			
			return this.analyze(averageSpeed, maxSpeed, averageAccuracy, averageBearingChange, varianceSpeed, averageAcceleration);
		} 
		return ERROR;
	}
	
	public void setAnotherTripData(ArrayList<Location> l){
		mKnowledgeOfLocations = l;
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

	
}
