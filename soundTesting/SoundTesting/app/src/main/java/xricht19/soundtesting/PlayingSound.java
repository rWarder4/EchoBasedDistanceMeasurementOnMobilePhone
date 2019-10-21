package xricht19.soundtesting;

/**
 * Created by Jirka on 2. 5. 2015.
 */
import android.media.MediaPlayer;
import android.util.Log;

public class PlayingSound implements Runnable {
    private UserInterface parent;
    private int soundToPlay;


    public PlayingSound(UserInterface parent, int soundName){
        this.parent = parent;
        this.soundToPlay = soundName;
    }

    public void run() {

        // playing the sound
        parent.mp = MediaPlayer.create(parent.getApplicationContext(), soundToPlay);
        parent.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i("Main", "Prehravani ukonceno, uvolnuji media player.");
                mp.stop();
                mp.release();
                // push parent to next step
                parent.enablePlayButton();
            }
        });
        parent.mp.start();
    }
}
