package io.neurolab.main.output.visual;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

import java.util.Timer;
import java.util.TimerTask;

import io.neurolab.R;
import io.neurolab.gui.GraphicBgRenderer;

public class SpaceAnimationVisuals {

    private ImageView travellingRocket;
    private GraphicBgRenderer focusBg;
    private View scrim;
    private View rocketFocusScreen;

    private static int count = 0;
    private static Timer timer;
    private static TimerTask timerTask;

    private final ValueAnimator[] valueAnimator = new ValueAnimator[1];

    public SpaceAnimationVisuals(View view) {
        travellingRocket = view.findViewById(R.id.indicator);
        scrim = view.findViewById(R.id.scrim_focus_screen);
        focusBg = view.findViewById(R.id.focus_bg);
        rocketFocusScreen = view.findViewById(R.id.rocket_focus_screen);
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
        focusBg.setOnPreparedListener(mp -> mp.setLooping(true));
        focusBg.setOnCompletionListener(mediaPlayer -> focusBg.startAnimation(AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_out)));
        focusBg.start();
    }

    public void pauseRocketAnim(View view) {

        travellingRocket.clearAnimation();
        focusBg.pause();
        scrim.startAnimation(AnimationUtils.loadAnimation(view.getContext(), android.R.anim.fade_in));
        scrim.setVisibility(View.VISIBLE);
        if (timer != null)
            stop();
    }

    public void animateRocket(double[] data, Activity activity) {

        double value = Double.parseDouble(Double.toString(data[count]).substring(0, 3));
        valueAnimator[0] = ValueAnimator.ofFloat(0f, (float) -value);
        valueAnimator[0].setInterpolator(new LinearInterpolator());
        valueAnimator[0].setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator[0].setRepeatCount(3);
        valueAnimator[0].setDuration(1000);
        valueAnimator[0].addUpdateListener(valueAnimator1 -> {
            float value1 = (float) valueAnimator1.getAnimatedValue();
            rocketFocusScreen.setTranslationY(value1);
        });

        timerTask = new TimerTask() {

            @Override
            public void run() {
                activity.runOnUiThread(() -> {
                    if (count <= data.length) {

                        if (valueAnimator[0].isPaused()) {
                            valueAnimator[0].resume();
                        } else {
                            valueAnimator[0].start();
                        }
                        count++;
                    } else {
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
            valueAnimator[0].pause();
            timer.cancel();
            timer = null;
        }
    }
}
