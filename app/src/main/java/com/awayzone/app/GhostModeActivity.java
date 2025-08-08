package com.awayzone.app;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GhostModeActivity extends Activity {

    private TextView tvGhostMessage;
    private Button btnCloseApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost_mode);

        tvGhostMessage = findViewById(R.id.tvGhostMessage);
        btnCloseApp = findViewById(R.id.btnCloseApp);

        // Simulate app crash screen delay
        new Handler().postDelayed(() -> {
            tvGhostMessage.setVisibility(View.VISIBLE);
            btnCloseApp.setVisibility(View.VISIBLE);
        }, 3000); // show after 3 seconds

        btnCloseApp.setOnClickListener(v -> finish());
    }
}
