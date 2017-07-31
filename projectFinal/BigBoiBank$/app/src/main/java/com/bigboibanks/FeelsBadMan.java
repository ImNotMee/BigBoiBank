package com.bigboibanks;

import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class FeelsBadMan extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.mr_krabz);
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.mr_krabs);
        mediaPlayer.start();
    }
}
