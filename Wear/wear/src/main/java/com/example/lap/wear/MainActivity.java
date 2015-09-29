package com.example.lap.wear;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.wearable.view.CardScrollView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Date;

import sharedlibrary.sensorlogger.SensorLoggingService;

public class MainActivity extends Activity {


    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yy_MM_dd_HH_mm_ss");

    private Button startButton, stopButton;
    private Button show;
    private TextView text;

    private double markDuartion=100;
    private double spaceDuration=100;
    private int repeatTimes=1;

    private final String TAG="google api services";
    Activity MainActivity;

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        CardScrollView cardScrollView =
                (CardScrollView) findViewById(R.id.card_scroll_view);
        cardScrollView.setCardGravity(Gravity.BOTTOM);

        /*查看本机所有传感器，输出传感器列表
        // 获取传感器管理器
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // 获取全部传感器列表
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // 打印每个传感器信息
        StringBuilder strLog = new StringBuilder();
        int iIndex = 1;
        for (Sensor item : sensors) {
            strLog.append(iIndex + ".");
            strLog.append(" Sensor Type - " + item.getType() + "\r\n");
            strLog.append(" Sensor Name - " + item.getName() + "\r\n");
            strLog.append(" Sensor Version - " + item.getVersion() + "\r\n");
            strLog.append(" Sensor Vendor - " + item.getVendor() + "\r\n");
            strLog.append(" Maximum Range - " + item.getMaximumRange() + "\r\n");
            strLog.append(" Minimum Delay - " + item.getMinDelay() + "\r\n");
            strLog.append(" Power - " + item.getPower() + "\r\n");
            strLog.append(" Resolution - " + item.getResolution() + "\r\n");
            strLog.append("\r\n");
            iIndex++;
        }
        System.out.println(strLog.toString());

        //
        */


        startButton = (Button) findViewById(R.id.startAccSensorButton);
        stopButton = (Button) findViewById(R.id.stopAccSensorButton);

        text = (TextView)findViewById(R.id.textview);

        stopButton.setEnabled(false);

        Intent loggingIntent = new Intent(this, SensorLoggingService.class);
        startService(loggingIntent);
        Log.i("start audio recorder", " ...");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // Now you can use the Data Layer API
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("onResume", ".asdfasdf");
    }

    public void startAcc(View view) {

        try {
            Thread.currentThread().sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //首次震动
        Vibrator vib = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(100);

        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    Vibrator vibb = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                    vibb.vibrate(100);
                    Thread.sleep(5000);
                    vibb.vibrate(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        String timeString = sdf.format(new Date());
        Toast.makeText(this, "start sensor/audio logging", Toast.LENGTH_SHORT).show();
        SensorLoggingService.instance().startLogging(timeString);
        startButton.setEnabled(false);
        stopButton.setEnabled(true);

        String path="/sendmessage";
        String message="start recording";
        sendmessage(path,message);
    }

    public void stopAcc(View view) {
        Toast.makeText(this, "stop sensor/audio logging", Toast.LENGTH_SHORT).show();

        SensorLoggingService.instance().stopLogging();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        startButton.setEnabled(true);
        stopButton.setEnabled(false);

        String path="/stopmessage";
        String message="stop recording";
        sendmessage(path,message);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Intent loggingIntent = new Intent(this, SensorLoggingService.class);
//        stopService(loggingIntent);
//        Log.i("stop audio recorder", " ...");
//        Intent audioRecordingIntent = new Intent(this, AudioRecordorService.class);
//        stopService(audioRecordingIntent);
//        System.exit(0);
    }

    public void sendmessage(final String p, final String m){
        Thread s = new Thread(){
            @Override
            public void run() {
                super.run();
                String path=p;
                String message=m;
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                for(Node node:nodes.getNodes())
                {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, message.getBytes()).await();
                    if (result.getStatus().isSuccess()) {
                        Log.v("myTag", "Message: {" + message + "} sent to: " + node.getDisplayName());
                    } else {

                        Log.v("myTag", "ERROR: failed to send Message");
                    }
                }
            }
        };
        s.start();
    }
}
