package io.neurolab.program_modes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

import io.neurolab.R;
import io.neurolab.main.MainActivity;

public class MemoryGraphActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    private LineChart memGraph;
    private Thread thread;
    private String rawData;
    private ArrayList eegDataValues;

    private ArrayList parseDataForGraph(String data) {
        ArrayList eegValues = new ArrayList();
        int currDataStart = 0;
        for (int i = 0; i <= data.length(); i++) {
            if (data.charAt(i) == ',') {
                eegValues.add(data.substring(currDataStart, i));
                currDataStart = i + 1;
            }
        }
        return eegValues;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_memory_graph);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setTitle(R.string.mem_graph);

        MainActivity mainActivity = new MainActivity();
        rawData = mainActivity.getDeviceData();
        if(rawData != null)
        parseDataForGraph(rawData);

        memGraph = findViewById(R.id.mem_graph);

        memGraph.setOnChartValueSelectedListener(this);

        // enable description text
        memGraph.getDescription().setEnabled(true);

        // enable touch gestures
        memGraph.setTouchEnabled(true);

        // enable scaling and dragging
        memGraph.setDragEnabled(true);
        memGraph.setScaleEnabled(true);
        memGraph.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        memGraph.setPinchZoom(true);

        // set an alternative background color
        memGraph.setBackgroundColor(getResources().getColor(R.color.memory_graph_background));

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        memGraph.setData(data);

        memGraph.getDescription().setText(getResources().getString(R.string.axis_desc_time));
        memGraph.getDescription().setTextColor(Color.WHITE);

        // get the legend (only possible after setting data)
        Legend l = memGraph.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.WHITE);

        XAxis xl = memGraph.getXAxis();
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = memGraph.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(Color.WHITE);

        YAxis rightAxis = memGraph.getAxisRight();
        rightAxis.setEnabled(false);

        feedMultiple();
    }

    private void addEntry() {

        LineData data = memGraph.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
            data.notifyDataChanged();

            // let the memGraph know it's data has changed
            memGraph.notifyDataSetChanged();

            // limit the number of visible entries
            memGraph.setVisibleXRangeMaximum(120);
            // memGraph.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            memGraph.moveViewToX(data.getEntryCount());
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "EEG Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.GREEN);
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(Color.GREEN);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    private void feedMultiple() {

        if (thread != null)
            thread.interrupt();

        final Runnable runnable = () -> addEntry();

        thread = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {

                // Don't generate garbage runnables inside the loop.
                runOnUiThread(runnable);

                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        // When particular value on the graph is selected.
    }

    @Override
    public void onNothingSelected() {
        // When nothing is selected.
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        thread.interrupt();
    }

    public ArrayList getEegDataValues() {
        return eegDataValues;
    }
}
