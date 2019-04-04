package io.neurolab.tools;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public final class Animations {

    private Animations() {

    }

    public static void rotateView(View view, float fromDegrees, float toDegrees) {
        RotateAnimation rotate = new RotateAnimation(fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setInterpolator(new LinearInterpolator());
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setDuration(30000);
        rotate.setInterpolator(new LinearInterpolator());
        view.startAnimation(rotate);
    }

}
