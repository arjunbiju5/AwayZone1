package com.awayzone.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class VoiceBreakerActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1000;
    private static final int SAMPLE_RATE = 44100;
    private boolean isRecording = false;
    private Thread recordingThread;

    private Button btnRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_breaker);

        btnRecord = findViewById(R.id.btnRecord);

        if (!checkPermissions()) {
            requestPermissions();
        }

        btnRecord.setOnClickListener(v -> {
            if (isRecording) {
                stopRecording();
                btnRecord.setText("Start Recording");
            } else {
                startRecording();
                btnRecord.setText("Stop Recording");
            }
        });
    }

    private boolean checkPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, REQUEST_PERMISSION_CODE);
    }

    private void startRecording() {
        isRecording = true;

        recordingThread = new Thread(() -> {
            AudioRecord recorder = null;
            FileOutputStream fos = null;
            BufferedOutputStream bos = null;

            try {
                int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize);

                byte[] buffer = new byte[bufferSize];

                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "AwayZone");
                if (!dir.exists()) dir.mkdirs();

                File file = new File(dir, "distorted_" + System.currentTimeMillis() + ".wav");
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);

                // Write placeholder WAV header (we'll fix later)
                writeWavHeader(bos, SAMPLE_RATE, 1, 16);

                recorder.startRecording();

                while (isRecording) {
                    int read = recorder.read(buffer, 0, buffer.length);
                    if (read > 0) {
                        distortAudio(buffer, read); // Apply distortion
                        bos.write(buffer, 0, read);
                    }
                }

                recorder.stop();

                // Fix WAV header with correct sizes
                bos.flush();
                fos.close();
                fixWavHeader(file);

                runOnUiThread(() -> Toast.makeText(this, "Saved: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show());

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (recorder != null) {
                    recorder.release();
                }
                try {
                    if (bos != null) bos.close();
                    if (fos != null) fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        recordingThread.start();
    }

    private void stopRecording() {
        isRecording = false;
    }

    // Distortion effect: reduce bit depth + add noise
    // Glitchy distortion: cut chunks + insert silence + heavy bit crush
    // Distortion effect: focus on silent breaks + mild glitches
    // State tracking for mute timing
    private long lastMuteTime = 0;
    private boolean isMutePhase = false;

    private void distortAudio(byte[] buffer, int read) {
        long now = System.currentTimeMillis();

        // Toggle mute phase every 1.5s
        if (now - lastMuteTime > 1500) {
            isMutePhase = true;
            lastMuteTime = now;
        }

        for (int i = 0; i < read; i += 2) {
            short sample = (short) ((buffer[i] & 0xFF) | (buffer[i + 1] << 8));

            // If in mute phase, silence this part
            if (isMutePhase) {
                sample = 0;
            } else {
                // Mild distortion while preserving voice
                sample = (short) (sample >> 1 << 1); // light bit crush
                if (Math.random() < 0.05) { // occasional noise
                    sample += (short) (Math.random() * 500 - 250);
                }
            }

            buffer[i] = (byte) (sample & 0xFF);
            buffer[i + 1] = (byte) ((sample >> 8) & 0xFF);
        }

        // End mute phase quickly (~300ms silence)
        if (isMutePhase && now - lastMuteTime > 300) {
            isMutePhase = false;
        }
    }





    // WAV header helpers
    private void writeWavHeader(FileOutputStream out, int sampleRate, int channels, int bitsPerSample) throws IOException {
        byte[] header = new byte[44];
        out.write(header);
    }

    private void writeWavHeader(BufferedOutputStream out, int sampleRate, int channels, int bitsPerSample) throws IOException {
        byte[] header = new byte[44];
        out.write(header);
    }

    private void fixWavHeader(File file) throws IOException {
        byte[] audioData = java.nio.file.Files.readAllBytes(file.toPath());
        int totalDataLen = audioData.length - 8;
        int totalAudioLen = audioData.length - 44;
        int sampleRate = SAMPLE_RATE;
        int channels = 1;
        int byteRate = sampleRate * channels * 16 / 8;

        ByteBuffer header = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN);
        header.put("RIFF".getBytes());
        header.putInt(totalDataLen);
        header.put("WAVE".getBytes());
        header.put("fmt ".getBytes());
        header.putInt(16);
        header.putShort((short) 1);
        header.putShort((short) channels);
        header.putInt(sampleRate);
        header.putInt(byteRate);
        header.putShort((short) (channels * 16 / 8));
        header.putShort((short) 16);
        header.put("data".getBytes());
        header.putInt(totalAudioLen);

        byte[] headerBytes = header.array();
        System.arraycopy(headerBytes, 0, audioData, 0, 44);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(audioData);
        }
    }
}
