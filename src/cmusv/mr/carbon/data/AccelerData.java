package cmusv.mr.carbon.data;

public class AccelerData{
	private long timestamp;
	private float magnitude;
	public AccelerData(float[] values, long timestamp){
		assert(values.length == 3);
		this.timestamp = timestamp;
		magnitude = vectorMagnitude(values[0],values[1],values[2]);
	}
	public long getTime(){
		return timestamp;
	}
	public float getMagnitude(){
		return magnitude;			
	}
	public float vectorMagnitude(float x, float y, float z) {
		return (float)Math.sqrt(x * x + y * y + z * z);
	}
}