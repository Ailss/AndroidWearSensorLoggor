package com.example.lap.wear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.os.PowerManager;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.Date;

import sharedlibrary.audiologger.AudioRecordorService;
import sharedlibrary.sensorlogger.SensorLoggingService;


public class MainActivity extends Activity implements
        DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener{

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yy_MM_dd_HH_mm_ss");

    GoogleApiClient mGoogleApiClient;
    private final String TAG="googel client";
    TextView mTextView;
    Handler mHandler;
    private static final int REC_MESSAGE=1;
    private static final int STOP_MESSAGE=2;
    protected PowerManager.WakeLock mWakeLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();



        mTextView=(TextView)findViewById(R.id.textview);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case REC_MESSAGE:
                        startAcc((String)msg.obj);
//                        mTextView.setText((String)msg.obj);
                        break;
                    case STOP_MESSAGE:
                        stopAcc((String) msg.obj);
//                        mTextView.setText((String)msg.obj);
                        break;
                }
                super.handleMessage(msg);
            }
        };

        Intent loggingIntent = new Intent(this, SensorLoggingService.class);
        startService(loggingIntent);
        Log.i("start audio recorder", " ...");
        Intent audioRecordingIntent = new Intent(this, AudioRecordorService.class);
        startService(audioRecordingIntent);

    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {

        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            Wearable.DataApi.removeListener(mGoogleApiClient, this);
            Wearable.MessageApi.removeListener(mGoogleApiClient,this);
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Wearable.DataApi.addListener(mGoogleApiClient, MainActivity.this);
        Wearable.MessageApi.addListener(mGoogleApiClient,MainActivity.this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals("/sendmessage")) {
            final String message = new String(messageEvent.getData());

            Log.e("接收到message",message);
            Message re_message = new Message();
            re_message.what = REC_MESSAGE;
            re_message.obj = message;
            mHandler.sendMessage(re_message);

        }
        else if(messageEvent.getPath().equals("/stopmessage")){
            final String message = new String(messageEvent.getData());

            Log.e("接收到message",message);
            Message re_message = new Message();
            re_message.what = STOP_MESSAGE;
            re_message.obj = message;
            mHandler.sendMessage(re_message);

        }
    }

    public void startAcc(String text) {
        String timeString = sdf.format(new Date());
        Toast.makeText(this, "start sensor/audio logging", Toast.LENGTH_SHORT).show();
        SensorLoggingService.instance().startLogging(timeString);
        AudioRecordorService.instance().startRecording(timeString);
        mTextView.setText(text);

    }

    public void stopAcc(String text) {
        Toast.makeText(this, "stop sensor/audio logging", Toast.LENGTH_SHORT).show();

        SensorLoggingService.instance().stopLogging();

        new Thread(new Runnable() {
            @Override
            public void run() {
                AudioRecordorService.instance().stopRecording();

            }
        }).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mTextView.setText(text);

    }

    public void onDestroy() {
        this.mWakeLock.release();
        super.onDestroy();
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
}
