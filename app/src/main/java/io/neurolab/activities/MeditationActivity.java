package io.neurolab.activities;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import io.neurolab.R;
import io.neurolab.main.output.audio.MediaPlayerHolder;
import io.neurolab.main.output.audio.PlaybackInfoListener;
import io.neurolab.main.output.audio.PlayerAdapter;

public final class MeditationActivity extends AppCompatActivity {

    public static final String TAG = MeditationActivity.class.getCanonicalName();   // TAG for debugging purposes.
    public static final int MEDIA_RES_ID = R.raw.delta_waves;

    // Necessary view references.
    private TextView textDebug;
    private SeekBar seekbarAudio;
    private ScrollView scrollContainer;

    // the interface reference which would be used to control the media session from this UI client.
    private PlayerAdapter playerAdapter;

    private boolean isUserSeeking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation);

        grabNecessaryReferencesAndSetListeners();

        initializePlaybackController();
    }

    @Override
    protected void onStart() {
        super.onStart();
        playerAdapter.loadMedia(MEDIA_RES_ID);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // not releasing the media player resources during auto-rotation. I believe it won't make it logical for the user to listen to the audio again from the start if he rotates his phone.
        if (isChangingConfigurations() && playerAdapter.isPlaying()) {
            Log.d(TAG, "onStop: don't release MediaPlayer as screen is rotating & playing");
        } else {
            playerAdapter.release();
            Log.d(TAG, "onStop: release MediaPlayer");
        }
    }

    // UI initialization part is taken care by this method
    private void grabNecessaryReferencesAndSetListeners() {
        textDebug = findViewById(R.id.text_debug);
        Button playButton = findViewById(R.id.button_play);
        Button pauseButton = findViewById(R.id.button_pause);
        Button resetButton = findViewById(R.id.button_reset);
        seekbarAudio = findViewById(R.id.seekbar_audio);
        scrollContainer = findViewById(R.id.scroll_container);

        pauseButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playerAdapter.pause();
                    }
                });
        playButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playerAdapter.play();
                    }
                });
        resetButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playerAdapter.reset();
                    }
                });

        setSeekbarListener();
    }

    private void initializePlaybackController() {
        MediaPlayerHolder mediaPlayerHolder = new MediaPlayerHolder(this);
        Log.d(TAG, "Inside initializePlaybackController method: MediaPlayerHolder Created");
        mediaPlayerHolder.setPlaybackInfoListener(new PlaybackListener());
        playerAdapter = mediaPlayerHolder;
        Log.d(TAG, "Inside initializePlaybackController: MediaPlayerHolder progress callback set");
    }

    // Sets the seekbar's listener. Based on UI change the media session gets changed (correspondingly).
    private void setSeekbarListener() {
        seekbarAudio.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    int userSelectedPosition = 0;

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        isUserSeeking = true;
                    }

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            userSelectedPosition = progress;
                        }
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        isUserSeeking = false;
                        playerAdapter.seekTo(userSelectedPosition);
                    }
                });
    }

    // Defining inner class for getting the references of the OUTER class conveniently
    public class PlaybackListener extends PlaybackInfoListener {

        public void onDurationChanged(int duration) {
            seekbarAudio.setMax(duration);
            Log.d(TAG, String.format("setPlaybackDuration: setMax(%d)", duration));
        }

        @TargetApi(24)
        public void onPositionChanged(int position) {
            if (!isUserSeeking) {
                seekbarAudio.setProgress(position, true);
                Log.d(TAG, String.format("setPlaybackPosition: setProgress(%d)", position));
            }
        }

        public void onStateChanged(@PlaybackStateCompat.State int state) {
            String stateToString = PlaybackInfoListener.convertStateToString(state);
            onLogUpdated(String.format("onStateChanged(%s)", stateToString));
        }

        // TODO: Implementation will be carried out in the future when required.
        public void onPlaybackCompleted(){
            Log.i(TAG, "onPlaybackCompleted");
        }

        public void onLogUpdated(String message) {
            if (textDebug != null) {
                textDebug.append(message);
                textDebug.append("\n");
                // Moves the scrollContainer focus to the end.
                scrollContainer.post(
                        new Runnable() {
                            @Override
                            public void run() {
                                scrollContainer.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
            }
        }
    }

}
