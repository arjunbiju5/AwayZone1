package com.awayzone.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnFakeCall = findViewById(R.id.btnFakeCall);
        Button btnGlitchScreen = findViewById(R.id.btnGlitchScreen);
        Button btnExcuseGenerator = findViewById(R.id.btnExcuseGenerator);
        Button btnGhostMode = findViewById(R.id.btnGhostMode);
        Button btnNoiseSimulator = findViewById(R.id.btnNoiseSimulator);
        Button btnVoiceBreak = findViewById(R.id.btnVoiceBreak);

        btnFakeCall.setOnClickListener(v -> startActivity(new Intent(this, FakeCallActivity.class)));
        btnGlitchScreen.setOnClickListener(v -> startActivity(new Intent(this, GlitchScreenActivity.class)));
        btnExcuseGenerator.setOnClickListener(v -> startActivity(new Intent(this, ExcuseGeneratorActivity.class)));
        btnGhostMode.setOnClickListener(v -> startActivity(new Intent(this, GhostModeActivity.class)));
        btnNoiseSimulator.setOnClickListener(v -> startActivity(new Intent(this, NoiseSimulatorActivity.class)));
        btnVoiceBreak.setOnClickListener(v -> startActivity(new Intent(this, VoiceBreakActivity.class)));
    }
}
