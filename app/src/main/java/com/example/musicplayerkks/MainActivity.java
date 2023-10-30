
package com.example.musicplayerkks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private SeekBar seekBar;
    private Button playButton, pauseButton, previousButton, nextButton;
    private int currentSongIndex = 0;
    private List<String> songsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private boolean isPlaying = false;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = new MediaPlayer();
        seekBar = findViewById(R.id.seekbar);
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        recyclerView = findViewById(R.id.recyclerView);

//         Request the necessary permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 1);
        } else {
            // Permission is already granted, so fetch and play songs
            fetchSongsAndDisplay();
        }


        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playSong();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseSong();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPreviousSong();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playNextSong();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playNextSong();
            }
        });
    }

    private void fetchSongsAndDisplay() {
        // Specify the directory where your songs are stored
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music";
        File directory = new File(path);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {

                songsList.add(file.getAbsolutePath());
            }
        }

        if (!songsList.isEmpty()) {
            songAdapter = new SongAdapter(songsList, new SongAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(String songPath) {

                    playSelectedSong(songPath);
                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(songAdapter);
        } else {
            Toast.makeText(this, "No songs found on your device.", Toast.LENGTH_SHORT).show();
        }
    }


    private void playSelectedSong(String songPath) {
        currentSongIndex = songsList.indexOf(songPath);
        playSong();
    }

    private void playSong() {
        if (!isPlaying) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());
                mediaPlayer.setDataSource(songsList.get(currentSongIndex));
                mediaPlayer.prepare();
                mediaPlayer.start();
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
                isPlaying = true;

                // Start updating the SeekBar progress
                updateSeekBar();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void pauseSong() {
        if (isPlaying) {
            mediaPlayer.pause();
            playButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.GONE);
            isPlaying = false;
        }
    }

    private void playPreviousSong() {
        isPlaying = false;
        if (currentSongIndex > 0) {
            currentSongIndex--;
        } else {
            // If it's the first song, play the last song
            currentSongIndex = songsList.size() - 1;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        playSong();
    }

    private void playNextSong() {
        isPlaying = false;
        if (currentSongIndex < songsList.size() - 1) {
            currentSongIndex++;
        } else {
            // If it's the last song, play the first song
            currentSongIndex = 0;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        playSong();
    }

    private void updateSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setProgress(mediaPlayer.getCurrentPosition());
        handler.postDelayed(updateSeekBarRunnable, 1000);
    }

    private Runnable updateSeekBarRunnable = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        // Remove callbacks to prevent memory leaks
        handler.removeCallbacks(updateSeekBarRunnable);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch and play songs
                fetchSongsAndDisplay();
            } else {
                // Permission denied, show a message or handle accordingly
                Toast.makeText(this, "Permission denied. Cannot access songs.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


