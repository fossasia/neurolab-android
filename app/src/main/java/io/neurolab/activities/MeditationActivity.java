package io.neurolab.activities;

import android.annotation.TargetApi;
import android.os.Bundle;
import android.support.v4.media.session.PlaybackStateCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import io.neurolab.R;
import io.neurolab.main.output.audio.MediaPlayerHolder;
import io.neurolab.main.output.audio.PlaybackInfoListener;
import io.neurolab.main.output.audio.PlayerAdapter;

import static io.neurolab.activities.MeditationHome.MEDITATION_DIR_KEY;

public final class MeditationActivity extends AppCompatActivity {

    public static final String TAG = MeditationActivity.class.getCanonicalName();   // TAG for debugging purposes.
    public static int MEDIA_RES_ID;

    // Necessary view references.
    private SeekBar seekbarAudio;
    private TextView progressTimeView;
    private TextView durationView;
    private TextView trackNameView;

    // the interface reference which would be used to control the media session from this UI client.
    private PlayerAdapter playerAdapter;

    private boolean isUserSeeking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MEDIA_RES_ID = getIntent().getIntExtra(MEDITATION_DIR_KEY, R.raw.soften_and_relax);

        grabNecessaryReferencesAndSetListeners();

        setTrackName(MEDIA_RES_ID);

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
        CircleImageView playButton = findViewById(R.id.button_play);
        CircleImageView pauseButton = findViewById(R.id.button_pause);
        CircleImageView resetButton = findViewById(R.id.button_reset);
        seekbarAudio = findViewById(R.id.seekbar_audio);
        progressTimeView = findViewById(R.id.progress_time);
        durationView = findViewById(R.id.duration_view);
        trackNameView = findViewById(R.id.track_name);
        pauseButton.setVisibility(View.GONE); // The pause button should be invisible initially

        //Handling Play-Pause-Reset buttons according to the player state.
        pauseButton.setOnClickListener(
                view -> {
                    playerAdapter.pause();
                    pauseButton.setVisibility(View.GONE);
                    playButton.setVisibility(View.VISIBLE);
                });

        playButton.setOnClickListener(
                view -> {
                    playerAdapter.play();
                    pauseButton.setVisibility(View.VISIBLE);
                    playButton.setVisibility(View.GONE);
                });
        resetButton.setOnClickListener(
                view -> {
                    playerAdapter.reset();
                    playButton.setVisibility(View.VISIBLE);
                    pauseButton.setVisibility(View.GONE);
                });

        setSeekbarListener();
    }

    private void setTrackName(int MEDIA_RES_ID) {
        TypedValue value = new TypedValue();
        getResources().getValue(MEDIA_RES_ID, value, true);
        String trackName = getMeditationName(getName(value.string.toString()));
        trackNameView.setText(trackName);
    }

    private String getName(String path) {
        String[] token = path.split("/raw/", 2);
        String[] nextToken = token[1].split(".mp3", 2);
        String finalName = nextToken[0];
        return finalName;
    }

    private String getMeditationName(String rawName) {
        String name = "" + rawName.charAt(0);
        name = name.toUpperCase();
        for (int i = 1; i < rawName.length(); i++) {
            if (rawName.charAt(i) == '_') {
                name += ' ';
                continue;
            }
            name += rawName.charAt(i);
        }
        return name;
    }

    private void initializePlaybackController() {
        MediaPlayerHolder mediaPlayerHolder = new MediaPlayerHolder(this);
        Log.d(TAG, "Inside initializePlaybackController method: MediaPlayerHolder Created");
        mediaPlayerHolder.setPlaybackInfoListener(new PlaybackListener());
        playerAdapter = mediaPlayerHolder;
        Log.d(TAG, "Inside initializePlaybackController: MediaPlayerHolder progress callback set");
    }

    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        int minutes = (int) (millis % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000;

        buf.append(String.format("%02d", minutes)).append(":").append(String.format("%02d", seconds));

        return buf.toString();
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
                        progressTimeView.setText(getTimeString((long) progress));
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    // Defining inner class for getting the references of the OUTER class conveniently
    public class PlaybackListener extends PlaybackInfoListener {

        public void onDurationChanged(int duration) {
            seekbarAudio.setMax(duration);
            durationView.setText(getTimeString((long) duration));
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
        public void onPlaybackCompleted() {
            Log.i(TAG, "onPlaybackCompleted");
        }

        public void onLogUpdated(String message) {
        }
    }
}
