package xricht19.distancemeasuring;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Jirka on 1. 4. 2015.
 */
public class RecordingSound implements Runnable {
    private UserInterface parent;

    public RecordingSound(UserInterface parent){
        this.parent = parent;
    }

    public void run() {
        // checking if the thread starts
        Log.i("Recording Sound thread Started. Try write some variables:", String.valueOf(parent.recorderReady));

        // initialize the recorder class
        // create variables
        AudioRecord recorder = null;

        try {

            parent.recordedSound = AudioRecord.getMinBufferSize(parent.SamplingFreq, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            Log.i("Size of array N: ", String.valueOf(parent.recordedSound));
            parent.recordedSound = parent.recordedSound * 24;
            // create byte array for rec sound
            parent.recordedSoundShorts = new short[parent.recordedSound/2];
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, parent.SamplingFreq, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, parent.recordedSound);
            recorder.startRecording();

        } catch (Exception x) {
            recorder = null;
            Log.e("Audio", x.getMessage());
            x.printStackTrace();
        }

        // recording started inform parent that he can notify playing thread after 100 ms
        synchronized (parent.lock) {
            parent.recorderReady = true;
            parent.lock.notifyAll();
        }

        // recording has been finished, inform the parent
        synchronized (parent.lock) {
            while (!parent.notifyRecording) {
                try {
                    parent.lock.wait();
                } catch (InterruptedException e) {
                    Log.e("Waiting for notifyRecording!", e.getMessage());
                }
            }
        }

        //ending recording, than notify parent
        if (recorder !=  null) {
            // read recorded data
            recorder.read(parent.recordedSoundShorts,0,parent.recordedSound/2);
            // stop recording
            recorder.stop();
            //release recorder
            recorder.release();
        }

        synchronized (parent.lock) {
            parent.recordingFinished = true;
            parent.lock.notifyAll();
        }

    }
}
