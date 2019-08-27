package io.neurolab.gui;

import android.content.Context;
import android.widget.Scroller;
import android.widget.TextView;

public class CustomOutputView {
    private TextView textArea;
    private Context context;

    public CustomOutputView(Context context, TextView textArea) {
        this.textArea = textArea;
        this.context = context;
    }

    public void write(char b) {
        // redirects data to the text area
        textArea.append(String.valueOf(b));
        // scrolls the text area to the end of data
        textArea.setScroller(new Scroller(context));
    }
}
