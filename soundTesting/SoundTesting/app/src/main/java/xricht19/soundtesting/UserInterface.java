package xricht19.soundtesting;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;


public class UserInterface extends ActionBarActivity {

    // variables
    MediaPlayer mp;
    Button playButton;
    Button recButton;
    RadioGroup sounds;
    RadioGroup recLength;
    TextView fileName;
    Handler handler;

    //msg type
    public static final int ENABLE_PLAY_BUTTON = 0;
    public static final int ENABLE_REC_BUTTON = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_interface);

        playButton = (Button) findViewById(R.id.buttonPlay);
        recButton = (Button) findViewById(R.id.buttonRec);
        sounds = (RadioGroup) findViewById(R.id.radioGroup);
        recLength = (RadioGroup) findViewById(R.id.radioGroup3);
        fileName = (TextView) findViewById(R.id.editText);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                //reaction on message
                switch (msg.what){
                    case ENABLE_PLAY_BUTTON:
                        enablePlayButton();
                    case ENABLE_REC_BUTTON:
                        enableRecButton();
                }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startPlayingButton(View view){
        playButton.setEnabled(false);
        switch (sounds.getCheckedRadioButtonId()){
            case R.id.radioButton55:
                (new Thread(new PlayingSound(this, R.raw.sine55hz))).start();
                break;
            case R.id.radioButton110:
                (new Thread(new PlayingSound(this, R.raw.sine110hz))).start();
                break;
            case R.id.radioButton220:
                (new Thread(new PlayingSound(this, R.raw.sine220hz))).start();
                break;
            case R.id.radioButton440:
                (new Thread(new PlayingSound(this, R.raw.sine440hz))).start();
                break;
            case R.id.radioButton880:
                (new Thread(new PlayingSound(this, R.raw.sine880hz))).start();
                break;
        }
    }

    public void startRecordingButton(View view){
        recButton.setEnabled(false);
        String name = String.valueOf(fileName.getText());
        Log.i("RADIO ID:", String.valueOf(recLength.getCheckedRadioButtonId()));
        Log.i("RADIO ID5s:", String.valueOf(R.id.radioButton5s));
        Log.i("RADIO ID10s:", String.valueOf(R.id.radioButton10s));
        Log.i("RADIO ID15s:", String.valueOf(R.id.radioButton15s));
        switch (recLength.getCheckedRadioButtonId()){
            case R.id.radioButton5s:
                (new Thread(new RecordingSound(this, 5, name))).start();
                Log.i("5Sec", "");
                break;
            case R.id.radioButton10s:
                (new Thread(new RecordingSound(this, 10, name))).start();
                Log.i("10Sec", "");
                break;
            case R.id.radioButton15s:
                (new Thread(new RecordingSound(this, 15, name))).start();
                Log.i("15Sec", "");
                break;
        }
    }

    public void enablePlayButton(){
        playButton.setEnabled(true);
    }
    public void enableRecButton(){
        recButton.setEnabled(true);
    }

}
