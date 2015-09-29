package sharedlibrary.sensorlogger;


import android.hardware.Sensor;
import android.hardware.SensorEvent;

public class ComparableSensorEvent {

	public float[] values ;
	public Sensor sensor;
	public long timestamp;
	public int accuracy;

	public ComparableSensorEvent(SensorEvent se) {
		this.values  = se.values.clone();
		this.sensor = se.sensor;
		this.timestamp = se.timestamp;
		this.accuracy = se.accuracy;
        
	}

//	@Override
//	public boolean equals(Object o) {
//		// TODO Auto-generated method stub
//		return org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals(this, o, false);
//	}
//
//	@Override
//	public int hashCode() {
//		// TODO Auto-generated method stub
//		return org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode(this, false);
//	}
	
	

}
