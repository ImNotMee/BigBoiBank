package com.bigboibanks;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class FeelsBadMan extends AppCompatActivity {

    /**
     * A method that plays sad music with mr.krabs picture when logging in with wrong user type.
     * @param savedInstanceState, the Bundle used in every Android activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // picture taken from tenor.com
        /*
        Exact URL for picture
        https://tenor.com/view/tiny-violin-mr-krabs-spongebob-gif-3533818
         */

        // end time in milliseconds
        int end = 20000;

        // stop player when run
        final MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mr_krabs);
        Runnable stopPlayer = new Runnable() {
            @Override
            public void run() {
                mediaPlayer.stop();
            }
        };

        setContentView(R.layout.mr_krabz);
        // start the player
        mediaPlayer.start();

        // stop player after given time in milliseconds pass
        Handler handler = new Handler();
        handler.postDelayed(stopPlayer, end);

    }
}
