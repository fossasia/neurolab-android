package io.neurolab.gui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

public class GraphicBgRenderer extends VideoView {

    private int mForceHeight = 0;
    private int mForceWidth = 0;

    public GraphicBgRenderer(Context context) {
        super(context);
    }

    public GraphicBgRenderer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GraphicBgRenderer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDimensions(int w, int h) {
        this.mForceHeight = h;
        this.mForceWidth = w;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mForceWidth, mForceHeight);
    }
}
