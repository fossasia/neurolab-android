package io.neurolab.gui;

import android.gesture.GestureStroke;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.Image;
import android.os.Build;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class ThresholdRenderer {
    private float min;
    private float max;
    private float range;
    private float cur;
    private float thresh;

    private int panelWidth;
    private int panelHeight;
    private Image renderImage;
    private ImageView renderGraphics;
    private GestureStroke stroke;
    private int pos;

    public synchronized void setRange(float min, float max) {
        this.min = min;
        this.max = max;
        this.range = Math.max(min,max) - Math.min(min,max);
    }

    public synchronized void setCur(float cur) {
        this.cur = cur;
    }
    public ThresholdRenderer(FrameLayout windowSize, float min, float max, float cur) {
        this.panelWidth = windowSize.getWidth()-16;
        this.panelHeight = windowSize.getHeight();
        setRange(min, max);
        this.cur = cur;
        thresh = 0.5f;
        Rect renderRect = new Rect(panelWidth, panelHeight, panelWidth, panelHeight);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            renderImage.setCropRect(renderRect);
        }
        pos = 0;
    }
    public void paint(ImageView image){
        image.setBackgroundColor(Color.WHITE);
        Rect outrect = new Rect(0, 0, panelWidth, panelHeight);
        image.getDrawingRect(outrect);
        image.setBackgroundColor(Color.BLACK);
        Rect newrect = new Rect(1, 1, panelWidth-1, panelHeight-1);
        image.getDrawingRect(newrect);
        pos = (int)((float)panelWidth*((cur-min)/range));
        Canvas newCanvas = new Canvas(); // A bitmap (background image) should be provided in future.
        image.draw(newCanvas);
    }
    public synchronized void setMin(float min) {
        setRange(min, max);
    }
    public synchronized void setMax(float max) {
        setRange(min, max);
    }
}
