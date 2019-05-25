package io.neurolab.main.output.visual;

import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import io.neurolab.R;

public class SpaceAnimationVisuals {

    private static final long DURATION = 10000L;

    public static void spaceAnim(View view) {

        final View backgroundOne = view.findViewById(R.id.background_one);
        final View backgroundTwo = view.findViewById(R.id.background_two);

        final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
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

    private static void handleIndicator(View view) {
        final ImageView v = view.findViewById(R.id.indicator);
        Animation animation = new AlphaAnimation(1, 0); //to change visibility from visible to invisible
        animation.setDuration(300); //1 second duration for each animation cycle
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(Animation.INFINITE); //repeating indefinitely
        animation.setRepeatMode(Animation.REVERSE); //animation will start from end point once ended.
        v.startAnimation(animation);
    }

}
