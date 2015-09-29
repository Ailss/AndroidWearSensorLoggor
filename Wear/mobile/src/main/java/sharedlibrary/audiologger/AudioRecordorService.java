package sharedlibrary.audiologger;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder.AudioSource;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioRecordorService extends Service {

    private static AudioRecordorService instance;
    private final static int SAMPLING_RATE = 44100;
    private AudioRecord audioRecoder;
    private boolean isRecording;
    private boolean loggingEnabled;
    private String timeString;
    private int bufferSize;
    private String machineName = android.os.Build.MODEL.replace(" ", "");
    private File tempFile;
    private File outputFile;

    public static AudioRecordorService instance() {
        return instance;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = this;
        bufferSize = AudioRecord.getMinBufferSize(SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        audioRecoder = new AudioRecord(AudioSource.CAMCORDER, SAMPLING_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        Log.i("audio recorder", "initialized");
        tempFile = new File(Environment.getExternalStorageDirectory(), "_audio_" + machineName + "_.raw");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        audioRecoder.startRecording();
        isRecording = true;
        new Thread(new AudioRecordThread()).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        loggingEnabled = false;
        isRecording = false;
        audioRecoder.stop();
        audioRecoder.release();
        audioRecoder = null;
        super.onDestroy();
    }

    public void startRecording(String timeStr) {
        timeString = timeStr;
        loggingEnabled = true;
    }

    public void stopRecording() {
        audioRecoder.stop();
        loggingEnabled = false;
        isRecording = false;
        outputFile = new File(Environment.getExternalStorageDirectory()+"/watch_mobile/", timeString + "_audio_" + machineName + ".wav");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        copyWaveFile(tempFile, outputFile);// �������ݼ���ͷ�ļ�

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                audioRecoder.startRecording();
                isRecording = true;
                new Thread(new AudioRecordThread()).start();
            }
        }).start();
    }

    class AudioRecordThread implements Runnable {

        @Override
        public void run() {
            writeDateTOFile(tempFile);// ���ļ���д��������
        }
    }

    private void writeDateTOFile(File file) {
        // newһ��byte����������һЩ�ֽ����ݣ���СΪ��������С
        byte[] audiodata = new byte[bufferSize];
        FileOutputStream fos = null;
        int readsize = 0;
        try {
            if (file.exists()) {
                file.delete();
            }
            fos = new FileOutputStream(file);// ����һ���ɴ�ȡ�ֽڵ��ļ�
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (isRecording) {
            readsize = audioRecoder.read(audiodata, 0, bufferSize);
            if (loggingEnabled && AudioRecord.ERROR_INVALID_OPERATION != readsize) {
                // Log.v("audio source", "logging");
                try {
                    fos.write(audiodata);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            fos.close();// �ر�д����
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyWaveFile(File inFile, File outFile) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = SAMPLING_RATE;
        int channels = 1;
        long byteRate = 16 * SAMPLING_RATE * channels / 8;
        byte[] data = new byte[bufferSize];
        try {
            in = new FileInputStream(inFile);
            out = new FileOutputStream(outFile);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate, int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }
}
