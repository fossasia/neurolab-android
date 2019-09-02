package io.neurolab.main.output.visual;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import io.neurolab.R;
import io.neurolab.gui.GraphicBgRenderer;

public class SpaceAnimationVisuals {

    private View parentView;
    private ImageView travellingRocket;
    private GraphicBgRenderer focusBg;
    private View scrim;
    private View rocketFocusScreen;
    private SeekBar animSeekbar;
    private TextView timerView;

    public static int count = 0;
    private boolean running;
    private static Timer timer;
    private static TimerTask timerTask;

    private final ValueAnimator[] valueAnimator = new ValueAnimator[3];
    private ValueAnimator currentAnimator = ValueAnimator.ofFloat(0, 0);

    public SpaceAnimationVisuals(View view) {
        this.parentView = view;
        travellingRocket = view.findViewById(R.id.indicator);
        scrim = view.findViewById(R.id.scrim_focus_screen);
        focusBg = view.findViewById(R.id.focus_bg);
        rocketFocusScreen = view.findViewById(R.id.rocket_focus_screen);
        animSeekbar = view.findViewById(R.id.anim_seekbar);
        timerView = view.findViewById(R.id.anim_timer);
    }

    public void playRocketAnim(View view) {
        Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
        animation.setDuration(300); //1 second duration for each animation cycle
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
        animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
        travellingRocket.startAnimation(animation);
        scrim.startAnimation(AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_out));
        scrim.setVisibility(View.GONE);

        focusBg.setVideoURI(Uri.parse("android.resource://" + view.getContext().getPackageName() + "/" + R.raw.focus_screen_bg));
        focusBg.setOnPreparedListener(mp -> {
                    mp.setLooping(true);
                    running = true;
                    animSeekbar.setMax(focusBg.getDuration());
                    final int duration = focusBg.getDuration();
                    animSeekbar.postDelayed(onEverySecond, 1000);
                    new Thread(new Runnable() {
                        public void run() {
                            do {
                                timerView.post(new Runnable() {
                                    public void run() {
                                        int time = (focusBg.getCurrentPosition()) / 1000;
                                        timerView.setText("00:" + String.format("%02d", time));
                                    }
                                });
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!running) break;
                            }
                            while (focusBg.getCurrentPosition() < duration);
                        }
                    }).start();
                }
        );
        focusBg.setOnCompletionListener(mediaPlayer -> focusBg.startAnimation(AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_out)));
        animSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                if (fromUser) {
                    // this is when actually seekbar has been seeked to a new position
                    focusBg.seekTo(progress);
                }
            }
        });
        focusBg.start();
    }

    private Runnable onEverySecond = new Runnable() {

        @Override
        public void run() {

            if (animSeekbar != null) {
                animSeekbar.setProgress(focusBg.getCurrentPosition());
            }

            if (focusBg.isPlaying()) {
                animSeekbar.postDelayed(onEverySecond, 1000);
            }

        }
    };

    public void pauseRocketAnim(View view) {

        travellingRocket.clearAnimation();
        focusBg.pause();
        scrim.startAnimation(AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_in));
        scrim.setVisibility(View.VISIBLE);
        if (timer != null)
            stop();
    }

    public void animateRocket(double[] data, Activity activity) {

        if (count < data.length) {
            double value = Double.parseDouble(Double.toString(data[count] * 100));
            valueAnimator[0] = ValueAnimator.ofFloat(0f, (float) -value);
            valueAnimator[0].setInterpolator(new AccelerateInterpolator());
            valueAnimator[0].setDuration(600);
            valueAnimator[0].addUpdateListener(valueAnimator1 -> {
                float value1 = (float) valueAnimator1.getAnimatedValue();
                rocketFocusScreen.setTranslationY(value1);
            });
            valueAnimator[0].addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    currentAnimator = valueAnimator[1];
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            valueAnimator[1] = ValueAnimator.ofFloat((float) -value, (float) -value);
            valueAnimator[1].setDuration(200);
            valueAnimator[1].addUpdateListener(valueAnimator1 -> {
                float value1 = (float) valueAnimator1.getAnimatedValue();
                rocketFocusScreen.setTranslationY(value1);
            });
            valueAnimator[1].addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    currentAnimator = valueAnimator[2];
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            valueAnimator[2] = ValueAnimator.ofFloat((float) -value, 0);
            valueAnimator[2].setInterpolator(new LinearInterpolator());
            valueAnimator[2].setDuration(1000);
            valueAnimator[2].addUpdateListener(valueAnimator1 -> {
                float value1 = (float) valueAnimator1.getAnimatedValue();
                rocketFocusScreen.setTranslationY(value1);
            });
            valueAnimator[2].addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    currentAnimator = valueAnimator[0];
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
        } else {
            count = 0;
        }

        currentAnimator = valueAnimator[0];

        timerTask = new TimerTask() {

            @Override
            public void run() {
                activity.runOnUiThread(() -> {
                    if (count <= data.length) {

                        if (currentAnimator.isPaused()) {
                            currentAnimator.resume();
                        } else {
                            currentAnimator.start();
                        }
                        count++;
                    } else {
                        travellingRocket.clearAnimation();
                        focusBg.stopPlayback();
                        scrim.startAnimation(AnimationUtils.loadAnimation(parentView.getContext(), android.R.anim.fade_in));
                        scrim.setVisibility(View.VISIBLE);
                        stop();
                    }
                });
            }
        };
        start();
    }

    public void start() {
        if (timer != null) {
            return;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 100, 2000);
    }

    public void stop() {
        if (timer != null) {
            currentAnimator.pause();
            timer.cancel();
            timer = null;
        }
    }
}
