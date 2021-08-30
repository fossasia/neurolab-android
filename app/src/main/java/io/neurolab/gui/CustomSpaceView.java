package io.neurolab.gui;

import android.animation.TimeAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

import io.neurolab.R;

public class CustomSpaceView extends View {

    private static final int BASE_SPEED_DP_PER_S = 200;
    private static final int COUNT = 132;
    private static final int SEED = 1337;

    private static final float SCALE_MIN_PART = 0.45f;
    private static final float SCALE_RANDOM_PART = 0.55f;
    private static final float ALPHA_SCALE_PART = 0.5f;
    private static final float ALPHA_RANDOM_PART = 0.5f;

    private final Star[] stars = new Star[COUNT];
    private final Random random = new Random(SEED);

    private TimeAnimator timeAnimator;
    private Drawable drawable;
    private Drawable drawable2;
    private Drawable drawable3;

    private float baseSpeed;
    private float baseSize, baseSizeTwo, baseSizeThree;

    private static class Star {
        private float x;
        private float y;
        private float scale;
        private float alpha;
        private float speed;
    }

    /** @see View#View(Context) */
    public CustomSpaceView(Context context) {
        super(context);
        init();
    }

    /** @see View#View(Context, AttributeSet) */
    public CustomSpaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /** @see View#View(Context, AttributeSet, int) */
    public CustomSpaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_starsone);
        drawable2 = ContextCompat.getDrawable(getContext(), R.drawable.ic_starstwo);
        drawable3 = ContextCompat.getDrawable(getContext(), R.drawable.ic_starsthree);
        baseSize = Math.max(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()) / 2f;
        baseSpeed = BASE_SPEED_DP_PER_S * getResources().getDisplayMetrics().density;
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        for (int i = 0; i < stars.length; i++) {
            final Star star = new Star();
            initializeStar(star, width, height);
            stars[i] = star;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int viewHeight = getHeight();
        for (final Star star : stars) {
            final float starSizeOne = star.scale * baseSize;
            final float starSizeTwo = star.scale * baseSizeTwo;
            final float starSizeThree = star.scale * baseSizeThree;

            if (star.y + starSizeOne < 0 || star.y - starSizeOne > viewHeight) {
                continue;
            }

            final int save = canvas.save();

            canvas.translate(star.x, star.y);

            final int sizeOne = Math.round(starSizeOne);
            final int sizeTwo = Math.round(starSizeTwo);
            final int sizeThree = Math.round(starSizeThree);
            drawable.setBounds(-sizeOne, -sizeOne, sizeOne, sizeOne);
            drawable.setAlpha(Math.round(255 * star.alpha));
            drawable.draw(canvas);
            drawable2.setBounds(-sizeTwo, -sizeTwo, sizeTwo, sizeTwo);
            drawable2.setAlpha(Math.round(255 * star.alpha));
            drawable2.draw(canvas);
            drawable3.setBounds(-sizeThree, -sizeThree, sizeThree, sizeTwo);
            drawable3.setAlpha(Math.round(255 * star.alpha));
            drawable3.draw(canvas);

            canvas.restoreToCount(save);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        timeAnimator = new TimeAnimator();
        timeAnimator.setTimeListener((animation, totalTime, deltaTime) -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (!isLaidOut()) {
                    return;
                }
            }
            updateState(deltaTime);
            invalidate();
        });
        timeAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        timeAnimator.cancel();
        timeAnimator.setTimeListener(null);
        timeAnimator.removeAllListeners();
        timeAnimator = null;
    }

    private void updateState(float deltaMs) {
        final float deltaSeconds = deltaMs / 1000f;
        final int viewWidth = getWidth();
        final int viewHeight = getHeight();

        for (final Star star : stars) {
            star.y -= star.speed * deltaSeconds;

            final float size = star.scale * baseSize;
            if (star.y + size < 0) {
                initializeStar(star, viewWidth, viewHeight);
            }
        }
    }

    private void initializeStar(Star star, int viewWidth, int viewHeight) {
        star.scale = SCALE_MIN_PART + SCALE_RANDOM_PART * random.nextFloat();
        star.x = viewWidth * random.nextFloat();
        star.y = viewHeight;
        star.y += star.scale * baseSize;
        star.y += viewHeight * random.nextFloat() / 4f;

        star.alpha = ALPHA_SCALE_PART * star.scale + ALPHA_RANDOM_PART * random.nextFloat();
        star.speed = baseSpeed * star.alpha * star.scale * 2 ;
    }
}

