package com.awayzone.app;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

public class FakeCallActivity extends Activity {

    TextView tvCaller, tvTimer;
    MediaPlayer ringtone;
    Handler timerHandler = new Handler();
    int secondsElapsed = 0;
    boolean callActive = false;

    String[] callers = {
            "Mom", "Dad", "Delivery Guy", "Unknown Number", "Bestie", "Boss"
    };

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            int minutes = secondsElapsed / 60;
            int seconds = secondsElapsed % 60;
            tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            secondsElapsed++;
            timerHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_call);

        tvCaller = findViewById(R.id.tvCallerName);
        tvTimer = findViewById(R.id.tvTimer);
        Button btnAccept = findViewById(R.id.btnAccept);
        Button btnDecline = findViewById(R.id.btnDecline);
        ImageButton btnHangup = findViewById(R.id.btnHangup);

        // Pick random caller
        Random rand = new Random();
        String caller = callers[rand.nextInt(callers.length)];
        tvCaller.setText(caller + " is calling...");

        // Play ringtone
        ringtone = MediaPlayer.create(this, R.raw.ringtone);
        if (ringtone != null) {
            ringtone.setLooping(true);
            ringtone.start();
        }

        btnAccept.setOnClickListener(v -> {
            callActive = true;
            tvCaller.setText(caller);
            tvTimer.setVisibility(View.VISIBLE);
            btnAccept.setVisibility(View.GONE);
            btnDecline.setVisibility(View.GONE);
            btnHangup.setVisibility(View.VISIBLE); // show hang up button

            if (ringtone != null && ringtone.isPlaying()) {
                ringtone.stop();
                ringtone.release();
            }

            secondsElapsed = 0;
            timerHandler.postDelayed(timerRunnable, 1000);
        });

        btnDecline.setOnClickListener(v -> {
            if (ringtone != null && ringtone.isPlaying()) {
                ringtone.stop();
                ringtone.release();
            }
            finish(); // Close screen
        });

        btnHangup.setOnClickListener(v -> {
            if (callActive) {
                timerHandler.removeCallbacks(timerRunnable);
            }
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
            ringtone.release();
        }
        if (callActive) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }
}
