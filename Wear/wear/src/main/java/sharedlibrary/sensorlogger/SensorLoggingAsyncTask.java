package sharedlibrary.sensorlogger;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Set;

public class SensorLoggingAsyncTask extends AsyncTask<Object, Long, Boolean> {
    float[] mRotationMatrix = new float[9];
    float[] mGameRotationMatrix = new float[9];
    float quat[] = new float[4];
    float gamequat[] =new float[4];
    //boolean first=true;
    //long startTime;

    @SuppressWarnings("deprecation")
    @Override
    protected Boolean doInBackground(Object... params) {
        File file = (File) params[1];
        //@SuppressWarnings("unchecked")
        Set<ComparableSensorEvent> sensorEventSet = (Set<ComparableSensorEvent>) params[0];
        try {
            PrintWriter printWriter = new PrintWriter(file);
            for (ComparableSensorEvent event : sensorEventSet) {
                //if(first){
                //    startTime=event.timestamp;
                //    first=false;
                //}
                StringBuilder sb = new StringBuilder();
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        sb.append("acc").append(" ");
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        sb.append("mag").append(" ");
                        break;
                    case Sensor.TYPE_ROTATION_VECTOR:
                        sb.append("quat").append(" ");

                        SensorManager.getQuaternionFromVector(quat, event.values);
                        SensorManager.getRotationMatrixFromVector(mRotationMatrix, quat);
//                        for (int i = 0;i < 4;i++){
//                            sb.append(quat[i]).append(" ");
//                        }
                        //sb.append(quat).append(" ");
                        break;
                    case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                        sb.append("gyro").append(" ");
                        break;
                    case Sensor.TYPE_ORIENTATION:
                        sb.append("ori").append(" ");
                        break;
                    case Sensor.TYPE_GRAVITY:
                        sb.append("earth").append(" ");
                        break;
                    case Sensor.TYPE_GAME_ROTATION_VECTOR:
                        sb.append("gquat").append(" ");
                        //Log.v("debug_","lala");
                        SensorManager.getQuaternionFromVector(gamequat, event.values);
                        SensorManager.getRotationMatrixFromVector(mGameRotationMatrix,gamequat);
                }
                // sb.append(event.sensor.getName().replaceAll(" ",
                // "")).append(" ");
                sb.append(event.timestamp).append(" ");
                 if(event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR){
                     for (int i = 0;i < 4;i++){
                         sb.append(quat[i]).append(" ");
                       }
                     /*for(int i=0;i<3;i++){
                         sb.append
                     }*/
                 }else {
                     if(event.sensor.getType()==Sensor.TYPE_GAME_ROTATION_VECTOR){
                         //Log.v("debug_","lala");
                         for(int i=0;i<4;i++){
                             sb.append(gamequat[i]).append(" ");
                         }
                     }else {
                         for (int i = 0; i < 3; i++) {
                             if (i < event.values.length) {
                                 sb.append(event.values[i]).append(" ");
                             } else {
                                 sb.append(0).append(" ");
                             }
                         }
                     }
                 }
                printWriter.println(sb.toString());
            }
            printWriter.flush();
            printWriter.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }
}
