package io.neurolab.main.output.audio;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class MediaPlayerHolder implements PlayerAdapter {

    public static final int PLAYBACK_POSITION_REFRESH_INTERVAL_MS = 1000;

    private final Context context;
    private MediaPlayer mediaPlayer;
    private int resourceId;
    private PlaybackInfoListener playbackInfoListener;
    private ScheduledExecutorService executor;
    private Runnable seekbarPositionUpdateTask;

    public MediaPlayerHolder(Context context) {
        this.context = context.getApplicationContext();
    }

    // initializes the media player. uses the same instance if one exists already instead of creating new ones.
    private void initializeMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopUpdatingCallbackWithPosition(true);
                    logDisplay("MediaPlayer playback completed");   // They are just used for debugging and validation by me and so hardcoded
                    if (playbackInfoListener != null) {
                        playbackInfoListener.onStateChanged(PlaybackInfoListener.State.COMPLETED);
                        playbackInfoListener.onPlaybackCompleted();
                    }
                }
            });
            logDisplay("Media Player object constructed");
        }
    }

    public void setPlaybackInfoListener(PlaybackInfoListener listener) {
        playbackInfoListener = listener;
    }

    @TargetApi(24)
    // This function loads the media (pointed out by resourceId) onto the media player after first initializing the media player
    // Implements PlaybackControl.
    @Override
    public void loadMedia(int resourceId) {
        this.resourceId = resourceId;

        initializeMediaPlayer();

        AssetFileDescriptor assetFileDescriptor =
                context.getResources().openRawResourceFd(this.resourceId);
        try {
            logDisplay("LoadingMedia process {1. setDataSource}");
            mediaPlayer.setDataSource(assetFileDescriptor);
        } catch (Exception e) {
            logDisplay(e.toString());
        }

        try {
            logDisplay("LoadingMedia process {2. prepare}");
            mediaPlayer.prepare();
        } catch (Exception e) {
            logDisplay(e.toString());
        }

        initializeProgressCallback();
        logDisplay("initializeProgressCallback() is called");
    }

    // Necessary for releasing the resources and doing the required clean up.
    @Override
    public void release() {
        if (mediaPlayer != null) {
            logDisplay("released the media player resources and mediaPlayer is set to null");
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void play() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            logDisplay(String.format("Playback has started for %s",
                    context.getResources().getResourceEntryName(resourceId)));
            mediaPlayer.start();
            if (playbackInfoListener != null) {
                playbackInfoListener.onStateChanged(PlaybackInfoListener.State.PLAYING);
            }
            startUpdatingCallbackWithPosition();
        }
    }

    @Override
    public void reset() {
        if (mediaPlayer != null) {
            logDisplay("playback is reset");
            mediaPlayer.reset();
            loadMedia(resourceId);
            if (playbackInfoListener != null) {
                playbackInfoListener.onStateChanged(PlaybackInfoListener.State.RESET);
            }
            stopUpdatingCallbackWithPosition(true);
        }
    }

    @Override
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            if (playbackInfoListener != null) {
                playbackInfoListener.onStateChanged(PlaybackInfoListener.State.PAUSED);
            }
            logDisplay("play back is paused");
        }
    }

    @Override
    public void seekTo(int position) {
        if (mediaPlayer != null) {
            logDisplay(String.format("seek to %d ms", position));
            mediaPlayer.seekTo(position);
        }
    }

    // Syncs the mediaPlayer position with playbackProgressCallback via recurring task.
    private void startUpdatingCallbackWithPosition() {
        if (executor == null) {
            executor = Executors.newSingleThreadScheduledExecutor();
        }
        if (seekbarPositionUpdateTask == null) {
            seekbarPositionUpdateTask = new Runnable() {
                @Override
                public void run() {
                    updateProgressCallbackTask();
                }
            };
        }
        // for syncing at a fixed predefined rate(PLAYBACK_POSITION_REFRESH_INTERVAL_MS.
        // NOTE: the same thing can be achieved through a handler but I think executor has more readability.
        executor.scheduleAtFixedRate(
                seekbarPositionUpdateTask,
                0,
                PLAYBACK_POSITION_REFRESH_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );
    }

    // Reports media playback position to playbackProgressCallback.
    private void stopUpdatingCallbackWithPosition(boolean resetUIPlaybackPosition) {
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
            seekbarPositionUpdateTask = null;
            if (resetUIPlaybackPosition && playbackInfoListener != null) {
                playbackInfoListener.onPositionChanged(0);
            }
        }
    }

    // Informs the playback info listener(which then is responsible for carrying out changes in the User Interface) about the current media player progress(update)
    private void updateProgressCallbackTask() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            if (playbackInfoListener != null) {
                playbackInfoListener.onPositionChanged(currentPosition);
            }
        }
    }

    // informs the playback info listener (which then does the corresponding UI changes) about the initial progress
    @Override
    public void initializeProgressCallback() {
        final int duration = mediaPlayer.getDuration();
        if (playbackInfoListener != null) {
            playbackInfoListener.onDurationChanged(duration);
            playbackInfoListener.onPositionChanged(0);
            logDisplay(String.format("duration of the playback is (%d sec)",
                    TimeUnit.MILLISECONDS.toSeconds(duration)));
            logDisplay("setting the Playback Position to 0");
        }
    }

    // For Debugging and Validation
    // informs the playback info listener about the log messages which then displays them in a UI to the user.
    private void logDisplay(String message) {
        if (playbackInfoListener != null) {
            playbackInfoListener.onLogUpdated(message);
        }
    }

}