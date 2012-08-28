package cmusv.mr.carbon.data.algorithm;

import java.util.ArrayList;

import cmusv.mr.carbon.data.AccelerData;
import android.location.Location;

public class DataAnalyst {
	/*
	 *  Define all the transportation Mode in Integer
	 */

	public static enum DataType{
		ERROR, WALKING, BIKING, TRAIN, DRIVING
	};
	/*
	 *  Variable needed for Rule-based algorithm...
	 */
	private static double SPEED_LIMIT = 50;
	private static double SPEED_CAR = 70;
	private static double SPEED_MAX_WALK = 15;
	private static double mps2kmph = 3.6;
	/* 
	 *  What this analyst have ....
	 */
	private DataAnalystTool mTools = null;
	private ArrayList<Location> mKnowledgeOfLocations = null;
	private ArrayList<AccelerData> mKnowledgeOfAccelerations = null;
	

	public DataAnalyst(ArrayList<Location> data) {
		mTools = new DataAnalystTool();
		mKnowledgeOfLocations = data;
	}

	public DataType getAnalysisResult() {
		if (mKnowledgeOfLocations != null) {
			double averageSpeed = mTools.averageSpeed(mKnowledgeOfLocations) * mps2kmph;
			double maxSpeed = mTools.maxSpeed(mKnowledgeOfLocations) * mps2kmph;
			double averageAccuracy = mTools.averageAccuracy(mKnowledgeOfLocations) * mps2kmph;
			double averageBearingChange = mTools.averageBearingChange(mKnowledgeOfLocations);
			double varianceSpeed = mTools.varianceSpeed(mKnowledgeOfLocations) * mps2kmph * mps2kmph;
			double averageAcceleration = mTools.averageAcceleration(mKnowledgeOfLocations);
			
			return this.analyze(averageSpeed, maxSpeed, averageAccuracy, averageBearingChange, varianceSpeed, averageAcceleration);
		} 
		return DataType.ERROR;
	}
	
	public void setAnotherTripData(ArrayList<Location> l){
		mKnowledgeOfLocations = l;
	}
	
	public DataType analyze(double averageSpeed, double maxSpeed, double averageAccuracy, double averageBearingChange, double varianceSpeed, double averageAcceleration){
		if(maxSpeed > SPEED_LIMIT){
			/*
			 *  possibly drive or train
			 */
			if(averageSpeed > SPEED_CAR){
				return DataType.DRIVING;
			}
			else{
				return DataType.TRAIN;
			}
		}
		else{
			/*
			 *  possibly walk or bike 
			 */ 
			if(maxSpeed > SPEED_MAX_WALK){
				return DataType.BIKING;
			}
			else {
				return DataType.WALKING;
			}
		}
		//return ERROR;
	}

	public void setAnotherAccelerData(ArrayList <AccelerData> accelerWindowData) {
		mKnowledgeOfAccelerations = accelerWindowData;
	}
	

	
}
