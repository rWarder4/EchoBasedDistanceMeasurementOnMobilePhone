package xricht19.distancemeasuring;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.Math;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;


public class UserInterface extends ActionBarActivity {
    // declare of variables
    // variables for UI
    private Button startMeasuringButton;
    private TextView manualStatusText;
    private TextView resultsView;
    private EditText distanceText;
    private Context ctx;

    // variables for synchronization of threads
    protected boolean recorderReady = false;
    protected boolean notifyPlaying = false;
    protected boolean playingFinished = false;
    protected boolean notifyRecording = false;
    protected boolean recordingFinished = false;
    protected boolean notifyPostProcess = false;
    protected boolean buttonEnabled = true;
    final protected Object lock = new Object();

    // variables for playing sound
    protected int soundName = R.raw.wn50x_2_7khz;
    protected MediaPlayer mp;
    protected int soundVolume = 17;
    private AudioManager audioManager;
    int originalVolume;

    // variables for recording sound
    final protected int SamplingFreq = 48000;
    protected int recordedSound;
    protected short[] recordedSoundShorts;

    //variables for postprocessing
    private PostProcessing processRecSound;
    public String fileName = "recordedFinal";
    public String distanceToName = "10";
    public int originalFileName = R.raw.wn50x_2_7khz_pieces;
    public String measuringSignal = "ws50Filtr";
    private int status = 5;
    private int[] distChange = new int[]{10,50,60,70,80,90,100,125};
    private int distChangePos = 0;
    public double finalDistance = -1.0;

    private int repeate = 0;

    // enumeration of types
    public enum TypesOfSignals {
        CONTINUOUS,
        SWITCHINGSILENT,
    }

    // communication between threads
    Handler handler;

    private TypesOfSignals typeOfSignalUsing = TypesOfSignals.SWITCHINGSILENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interface);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ctx = getApplicationContext();
        // get result view
        resultsView = (TextView) findViewById(R.id.resultText);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                // set distance from previous measuring
                if ((Double) msg.obj == 0.0){
                    resultsView.setText("\n Invalid recorded signal!");
                } else {
                    DecimalFormat myFormatter = new DecimalFormat("#0.000");
                    String formattedValue = myFormatter.format(msg.obj);
                    resultsView.setText("\r" + formattedValue + " m");
                }
                // change back user device volume
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
                initializeForNext();
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_interface, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_help) {
            openHelp();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openHelp(){
        // display help
        Intent intent = new Intent(this, Help.class);
        startActivity(intent);
    }

    private void disableEnableButton(){
        // Function disable startMeasuringButton if it's enabled and conversely.
        synchronized (lock) {
            startMeasuringButton = (Button) findViewById(R.id.buttonStartMeasure);
            if (startMeasuringButton.isEnabled()){
                startMeasuringButton.setEnabled(false);
                startMeasuringButton.setBackgroundColor(getResources().getColor(R.color.disabled_button));
                buttonEnabled = false;
            }else {
                startMeasuringButton.setEnabled(true);
                startMeasuringButton.setBackgroundColor(getResources().getColor(R.color.enabled_button));
                buttonEnabled = true;
            }
            lock.notifyAll();
        }

    }
    //----------------------------------------------------------------------------------------------
    // Function is making delay after user click on start Measuring button.
    public void startMeasuringDistance(View view){
        //disable button
        disableEnableButton();

        // after 100 ms notify playing thread, to play the sound
        new CountDownTimer(500, 500) {
            public void onTick(long millisUntilFinished){}
            public void onFinish() {
                // notify Playing to play a sound
                startMeasuringDistanceDelayed();
            }
        }.start();
    }
    //----------------------------------------------------------------------------------------------
    // Function is called after user click on start Measuring button.
    public void startMeasuringDistanceDelayed(){
        // Function start two new threads:
        //      - recording sound -> this thread recording the sound into vector variables
        //      - playing sound -> this thread playing sound from speaker

        // change volume of application
        audioManager = (AudioManager)getSystemService(ctx.AUDIO_SERVICE);
        originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, soundVolume, 0);

        // start thread of postprocessing
        (new Thread(new PostProcessing(this,typeOfSignalUsing))).start();
        // starting the threads
        (new Thread(new RecordingSound(this))).start();
        (new Thread(new PlayingSound(this, soundName))).start();


        // wait until recorder is initialized ------------------------------------------------------
        synchronized (lock) {
            while (!recorderReady) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Log.e("Waiting for recorderReady!", e.getMessage());
                }
            }
        }        // after 100 ms notify playing thread, to play the sound
        new CountDownTimer(100, 100) {
            public void onTick(long millisUntilFinished) {            }
            public void onFinish() {
                // notify Playing to play a sound
                synchronized (lock) {
                    notifyPlaying = true;
                    lock.notifyAll();
                }
            }
        }.start();
    }

    //----------------------------------------------------------------------------------------------
    // wait until playing is finished --------------------------------------------------------------
    public void waitForPlayingFinishedCountDown(){
        // after 100 ms notify recording thread, to finish the recording
        new CountDownTimer(100, 100) {
            public void onTick(long millisUntilFinished) {}
            public void onFinish() {
                waitForRecordingFinishedCountDown();
            }
        }.start();
    }

    //----------------------------------------------------------------------------------------------
    // wait until recording sound is finished ------------------------------------------------------
    public void waitForRecordingFinishedCountDown(){
        // notify Recording to finish
        synchronized (lock) {
            notifyRecording = true;
            lock.notifyAll();
            while (!recordingFinished) try {
                lock.wait();
            } catch (InterruptedException e) {
                Log.e("Reading:", e.getMessage());
            }
        }

        // processing of recorded sound, calculating of distance
        postprocessing();

    }
    //--------------------------------------------------------------------------------------------//
    public void initializeForNext(){
        // enable button and reset variables
        disableEnableButton();
        synchronized (lock) {
            recorderReady = false;
            notifyPlaying = false;
            playingFinished = false;
            notifyRecording = false;
            recordingFinished = false;
            notifyPostProcess = false;
        }

        // next type of signal
        Log.i("Can play another sound.", " Calling another round.");
        //measuringDifferentTypesButton(getWindow().getDecorView().findViewById(android.R.id.content));
    }



    //----------------------------------------------------------------------------------------------
    // postprocessing of recorded sounds
    public void postprocessing(){
        // notify postProcess to Calculate distance
        synchronized (lock) {
            notifyPostProcess = true;
            lock.notifyAll();
        }
    }
    //----------------------------------------------------------------------------------------------
    // For measuring testing method to switch over different type of signals.
    public void measuringDifferentTypesButton(View view){
        //  -> get distance
        if (status == 5) {
            //distanceText = (EditText) findViewById(R.id.distanceEditText);
            distanceToName = String.valueOf(distChange[distChangePos]);
            //distChangePos += 1;
            // set text in edit text

            //distanceText.setText(distanceToName);
            // reset view with results
            resultsView.setText("");
        }

        if (status < 8){
            measuringDifferentTypes(view);
            // write distance to UI

        } else  if ( status == 8 && repeate < 10) {
            status = 5;
            repeate+=1;
        } else{
            status = 5;
            repeate = 0;
        }
    }
    //----------------------------------------------------------------------------------------------
    // For measuring testing method to switch over different type of signals.
    public void measuringDifferentTypes(View view){
        //  -> get distance
        //distanceToName = distanceText.getText().toString();

        if (status == 0) {
            // start with first type - cosines
            //  -> set variables of sound files
            /*soundName = R.raw.cos1sec_con_4500hz;
            originalFileName = R.raw.cos1sec_con_4500hz_pieces;
            typeOfSignalUsing = TypesOfSignals.CONTINUOUS;
            measuringSignal = "cos1s";
            // call ordinary function for pressing button
            startMeasuringDistance(view);
            status +=1;*/
        } else if (status == 1){
            // next type of signal
            Log.i("Can play another sound.", " And start measuring with different sound.");
        } else if (status == 2){

        } else if (status == 3){

        } else if (status == 4){
            // start with first type - cosines
            //  -> set variables of sound files
            soundName = R.raw.cos50x_4500hz;
            originalFileName = R.raw.cos50x_4500hz_pieces;
            typeOfSignalUsing = TypesOfSignals.SWITCHINGSILENT;
            measuringSignal = "cos50";
            // call ordinary function for pressing button
            startMeasuringDistance(view);
            status +=1;
        } else if (status == 5){
            // start with first type - cosines
            //  -> set variables of sound files
            soundName = R.raw.chirp50x2_7khz;
            originalFileName = R.raw.chirp50x2_7khz_pieces;
            typeOfSignalUsing = TypesOfSignals.SWITCHINGSILENT;
            measuringSignal = "chirp50";
            // call ordinary function for pressing button
            startMeasuringDistance(view);
            status +=1;
        } else if (status == 6){
            // start with first type - cosines
            //  -> set variables of sound files
            soundName = R.raw.wn50x_2_7khz;
            originalFileName = R.raw.wn50x_2_7khz_pieces;
            typeOfSignalUsing = TypesOfSignals.SWITCHINGSILENT;
            measuringSignal = "ws50Filtr";
            // call ordinary function for pressing button
            startMeasuringDistance(view);
            status +=1;
        } else if (status == 7){
            // start with first type - cosines
            //  -> set variables of sound files
            soundName = R.raw.wn50x_allfreq;
            originalFileName = R.raw.wn50x_allfreq_pieces;
            typeOfSignalUsing = TypesOfSignals.SWITCHINGSILENT;
            measuringSignal = "wn50NotF";
            // call ordinary function for pressing button
            startMeasuringDistance(view);
            status +=1;
        }

    }



}


