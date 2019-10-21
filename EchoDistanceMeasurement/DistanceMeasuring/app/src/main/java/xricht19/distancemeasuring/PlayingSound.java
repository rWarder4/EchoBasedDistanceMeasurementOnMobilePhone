package xricht19.distancemeasuring;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;

/**
 * Created by Jirka on 5. 4. 2015.
 */
public class PlayingSound implements Runnable {
    private UserInterface parent;
    private int soundToPlay;

    public PlayingSound(UserInterface parent, int soundName){
        this.parent = parent;
        this.soundToPlay = soundName;
    }

    public void run() {
        // checking if the thread starts
        Log.i("Playing Sound thread Started. Try write some variables:", String.valueOf(parent.playingFinished));

        synchronized (parent.lock) {
            while (!parent.notifyPlaying) {
                try {
                    parent.lock.wait();
                } catch (Exception e) {
                    Log.e("Waiting for notifyPlaying!", e.getMessage());
                }
            }
        }

        // playing the sound
        parent.mp = MediaPlayer.create(parent.getApplicationContext(), soundToPlay);
        parent.mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i("Main", "Prehravani ukonceno, uvolnuji media player.");
                mp.stop();
                mp.release();
                // push parent to next step
                parent.waitForPlayingFinishedCountDown();
            }
        });
        parent.mp.start();

        /*synchronized (parent.lock) {
            parent.playingFinished = true;
            parent.lock.notifyAll();
        }*/

        // checking if the thread starts
        Log.i("Just playing the sound. Try write some variables:", String.valueOf(parent.recorderReady));
    }
}
