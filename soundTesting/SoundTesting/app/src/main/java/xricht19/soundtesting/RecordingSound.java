package xricht19.soundtesting;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jirka on 1. 4. 2015.
 */
public class RecordingSound implements Runnable {
    private UserInterface parent;
    int SamplingFreq = 48000;
    int recLength = 1;
    short[] recordedSoundShorts;
    String fileName;
    int recordedSound = AudioRecord.getMinBufferSize(SamplingFreq, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)*125;
    final AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SamplingFreq, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, recordedSound);

    //msg type
    public static final int ENABLE_REC_BUTTON = 1;

    public RecordingSound(UserInterface parent, int length, String fileName){
        this.parent = parent;
        this.recLength = length;
        this.fileName = fileName;
    }

    public void run() {

        try {
            Log.e("Buffer size>>>>>>",String.valueOf(recordedSound));
            recorder.startRecording();
            Log.e("Recording started","");
        } catch (Exception x) {
            Log.e("Audio", x.getMessage());
            x.printStackTrace();
        }
        Log.e("Timer set","");
        int time = 500+1000*recLength;
        try{ Thread.sleep(time); }catch(InterruptedException e){ }
        stopRecording();
    }

    public void stopRecording(){
        Log.e("Recording stopping","");
        //ending recording, than notify parent
            // create byte array for rec sound
            recordedSoundShorts = new short[recordedSound/2];
            // read recorded data
            recorder.read(recordedSoundShorts,0,recordedSound/2);
            // stop recording
            recorder.stop();
            //release recorder
            recorder.release();
        // write to file
        saveSignal(recordedSoundShorts);

        // create message for handler
        Message message = Message.obtain();
        message.what = ENABLE_REC_BUTTON;
        parent.handler.sendMessage(message);
    }

    // Saving recording sound to file.
    public void saveSignal(short[] signal){
        // save loaded audio into file
        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        //add audio name
        mFileName += "/"+fileName;

        // create output stream
        BufferedWriter fos = null;
        try {
            fos = new BufferedWriter(new FileWriter(mFileName, false));
        } catch (IOException e) {
            Log.e("Output stream doesn't create.", e.getMessage());
        }
        try {
            // write recorded signal
            for (short item : signal){
                //Log.i("item written to file.", String.valueOf(item));
                fos.write(String.valueOf(item));
                fos.write("\r\n");
            }
            fos.close();
        } catch (NullPointerException e) {
            Log.e("Null Pointer Exception while writing to file.", e.getMessage());
        } catch (IOException e) {
            Log.e("Cannot write or close the file!", e.getMessage());
        } catch (Exception e) {
            Log.e("Unknown error!", e.getMessage());
        }
    }
}
