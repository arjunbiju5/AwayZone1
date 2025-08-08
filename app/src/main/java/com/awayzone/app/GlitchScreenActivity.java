package com.awayzone.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class GlitchScreenActivity extends Activity {

    View root;
    TextView glitchText;
    Handler handler = new Handler();

    Runnable glitchEffect = new Runnable() {
        @Override
        public void run() {
            float randX = (float) (Math.random() * 2);
            float randY = (float) (Math.random() * 2);
            root.setScaleX(randX);
            root.setScaleY(randY);
            root.setRotation((float) (Math.random() * 360));
            handler.postDelayed(() -> {
                root.setScaleX(1f);
                root.setScaleY(1f);
                root.setRotation(0);
            }, 100);
            handler.postDelayed(this, 400);
        }
    };

    Runnable showCrash = () -> glitchText.setText("Phone Not Responding...");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glitch_screen);

        root = findViewById(android.R.id.content);
        glitchText = findViewById(R.id.tvGlitch);

        handler.post(glitchEffect);
        handler.postDelayed(showCrash, 5000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
