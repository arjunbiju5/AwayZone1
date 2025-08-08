package com.awayzone.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ExcuseGeneratorActivity extends Activity {

    TextView tvExcuse;
    Button btnCopyExcuse; // New button for copying

    // Predefined list of excuses
    private List<String> excuses = new ArrayList<>(Arrays.asList(
            "I’m on a strict ‘do nothing and chill’ diet today.",
            "I've accidentally glued myself to the couch in an intense board game marathon.",
            "My phone battery is at 1%, so I’m officially off the grid.",
            "I'm currently in a staring contest with my reflection, and I'm losing.",
            "The laundry monster has taken over my room; I’m in battle mode." ,
            "I’m attending an urgent virtual conference with my blanket and pillow." ,
            "I’m quarantining with my thoughts—no visitors allowed." ,
            "I promised my plants I’d check on them—they get clingy." ,
            "My cat just informed me she's planning world domination, and I'm needed for strategic meow-tings."
    ));

    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excuse_generator);

        tvExcuse = findViewById(R.id.tvExcuse);
        Button btnGenerate = findViewById(R.id.btnGenerate);
        btnCopyExcuse = findViewById(R.id.btnCopyExcuse); // Initialize the copy button

        // Display an initial excuse
        generateExcuse();

        btnGenerate.setOnClickListener(v -> {
            generateExcuse();
        });

        btnCopyExcuse.setOnClickListener(v -> {
            copyExcuseToClipboard();
        });
    }

    private void generateExcuse() {
        if (excuses.isEmpty()) {
            tvExcuse.setText("No excuses available!");
            return;
        }
        // Get a random excuse from the list
        String randomExcuse = excuses.get(random.nextInt(excuses.size()));
        tvExcuse.setText(randomExcuse);
    }

    private void copyExcuseToClipboard() {
        String excuseToCopy = tvExcuse.getText().toString();
        if (excuseToCopy.isEmpty() || excuseToCopy.equals("No excuses available!")) {
            Toast.makeText(this, "Nothing to copy!", Toast.LENGTH_SHORT).show();
            return;
        }

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Excuse", excuseToCopy);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, "Excuse copied to clipboard!", Toast.LENGTH_SHORT).show();
    }
}