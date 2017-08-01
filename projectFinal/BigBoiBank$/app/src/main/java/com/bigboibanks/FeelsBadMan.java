package com.bigboibanks;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
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

        setContentView(R.layout.mr_krabz);
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mr_krabs);
        mediaPlayer.start();
    }
}
