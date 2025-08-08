package com.awayzone.app;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class GlitchScreenActivity extends Activity {

    private ImageView glitchOverlay;
    private TextView tvStatus;
    private Handler handler = new Handler();
    private Runnable glitchRunnable;
    private boolean isGlitching = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glitch_screen);

        glitchOverlay = findViewById(R.id.imgGlitch);
        tvStatus = findViewById(R.id.tvStatus);
        tvStatus.setVisibility(View.GONE);

        startGlitchEffect();

        // After 1 minute, stop glitch and show "Phone not responding"
        handler.postDelayed(() -> {
            isGlitching = false;
            glitchOverlay.setVisibility(View.GONE);
            tvStatus.setVisibility(View.VISIBLE);
            tvStatus.setText("Phone Not Responding...");
            tvStatus.setTextColor(Color.RED);

            // Close activity after a few seconds
            handler.postDelayed(this::finish, 5000);

        }, 60000); // 60 seconds
    }

    private void startGlitchEffect() {
        glitchRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isGlitching) return;

                // Random flicker or show/hide glitch image
                glitchOverlay.setAlpha(new Random().nextFloat());
                handler.postDelayed(this, 100 + new Random().nextInt(200));
            }
        };
        handler.post(glitchRunnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
