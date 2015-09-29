package sharedlibrary.sensorlogger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SensorLoggingService extends Service implements SensorEventListener {
	private SensorManager manager;
	private static boolean loggingEnabled;
	private static List<ComparableSensorEvent> sensorEventList = new ArrayList<ComparableSensorEvent>(10000);
	private static String timeString;
	public static int currentOrientation = -1;

	private static SensorLoggingService instance;

	public static SensorLoggingService instance() {
		return instance;
	}

	public void startLogging(String timestr) {
		sensorEventList.clear();
		timeString = timestr;
		loggingEnabled = true;
	}

	public void stopLogging() {
		loggingEnabled = false;
		logToFile(timeString);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		instance = this;
		manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		List<Integer> candiateSensorList = new ArrayList<Integer>();
		candiateSensorList.add(Sensor.TYPE_ACCELEROMETER);
		candiateSensorList.add(Sensor.TYPE_GYROSCOPE);
		candiateSensorList.add(Sensor.TYPE_MAGNETIC_FIELD);
		candiateSensorList.add(Sensor.TYPE_ORIENTATION);
		for (Integer i : candiateSensorList) {
			manager.registerListener(this, manager.getDefaultSensor(i), SensorManager.SENSOR_DELAY_FASTEST);
		}

		loggingEnabled = false;
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		// Toast.makeText(getApplicationContext(),
		// "Sensor Loggging Service Stopped!", Toast.LENGTH_SHORT).show();
		loggingEnabled = false;
		// manager.unregisterListener(this);
		super.onDestroy();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (loggingEnabled) {
			sensorLogging(event);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	private void sensorLogging(SensorEvent event) {
		sensorEventList.add(new ComparableSensorEvent(event));
	}

	public static void logToFile(String string) {

		Set<ComparableSensorEvent> eventSet = new LinkedHashSet<ComparableSensorEvent>();
		eventSet.addAll(sensorEventList);
		String machineName = android.os.Build.MODEL.replace(" ", "");
		File outputFile = new File(Environment.getExternalStorageDirectory()+"/watch_mobile/", string + "_sensor_" + machineName + ".txt");
        new SensorLoggingAsyncTask().execute(eventSet, outputFile);
	}

}
