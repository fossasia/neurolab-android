package io.neurolab.main.output.visual;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

public class SpaceAnimationVisuals {

    private static boolean receivedMovement;
    private static int receivedLastPos;
    private static int receivedNewPos;

    public static void moveRocket(View view, int lastPos, int newPos, boolean moving) {
        receivedMovement = moving;
        receivedLastPos = lastPos;
        receivedNewPos = newPos;

        if (!receivedMovement) {

            Animation launch = new TranslateAnimation(0, 0, receivedLastPos, receivedNewPos);
            launch.setDuration(1000);
            launch.setFillAfter(true);
            launch.setRepeatCount(10);
            launch.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    receivedMovement = true;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    receivedMovement = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                   // required to repeat the Animation if user wants to see again
                }
            });
            int c = lastPos;
            receivedLastPos = newPos;
            receivedNewPos = c;

            view.startAnimation(launch);
        }
    }
}
