package io.neurolab.main.output.visual;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import io.neurolab.R;

public class SpaceAnimationVisuals {

    private static final long DURATION = 10000L;
    private static ValueAnimator animator;
    private static Animation animation;
    private static View parentSpaceView;
    private static ImageView travellingRocket;

    public static void spaceAnim(View view) {

        parentSpaceView = view;

        final View backgroundOne = view.findViewById(R.id.background_one);
        final View backgroundTwo = view.findViewById(R.id.background_two);

        animator = ValueAnimator.ofFloat(0.0f, 1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(DURATION);
        animator.addUpdateListener(animation -> {
            final float progress = (float) animation.getAnimatedValue();
            final float height = backgroundOne.getHeight();
            final float translationY = height * progress;
            backgroundOne.setTranslationY(translationY);
            backgroundTwo.setTranslationY(translationY - height);
        });
        animator.start();

        handleIndicator(view);
    }

    public static void stopAnim() {
        try {
            animator.pause();
            travellingRocket.clearAnimation();
            parentSpaceView.findViewById(R.id.animated_view).setVisibility(View.INVISIBLE);

        } catch (Exception e) {
            e.printStackTrace(); // animator not initialized
        }
    }

    public static void playAnim() {
        try {
            animator.resume();
            travellingRocket.startAnimation(animation);
            parentSpaceView.findViewById(R.id.animated_view).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace(); // animator not initialized
        }
    }

    private static void handleIndicator(View view) {
        travellingRocket = view.findViewById(R.id.indicator);
        animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
        animation.setDuration(300); //1 second duration for each animation cycle
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
        animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
        travellingRocket.startAnimation(animation);
    }

}
