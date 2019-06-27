package io.neurolab.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import io.neurolab.R;
import io.neurolab.program_modes.MemoryGraphParent;
import io.neurolab.utilities.FilePathUtil;
import io.neurolab.utilities.PermissionUtils;

import static android.app.Activity.RESULT_OK;

public class MemoryGraphFragment extends Fragment implements OnChartValueSelectedListener {

    private static final int ACTIVITY_CHOOSE_FILE1 = 1;
    private LineChart memGraph;
    private Thread thread;
    private AlertDialog progressDialog;
    private TextView eegLabelView;
    private String[] parsedData;
    private boolean isPlaying = false;
    private Menu menu;
    private ArrayList<String[]> rawData;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    private float maxEEGValue = 9000f;
    private float effectiveDistance = 30f;
    private boolean permission = false;
    private static final String[] READ_WRITE_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    public static MemoryGraphFragment newInstance() {
        MemoryGraphFragment fragment = new MemoryGraphFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_memory_graph, container, false);
        ((MemoryGraphParent) getActivity()).setActionBarTitle(getResources().getString(R.string.mem_graph));
        memGraph = rootView.findViewById(R.id.mem_graph);
        LayoutInflater layoutInflater = getLayoutInflater();
        View progressView = layoutInflater.inflate(R.layout.progress_dialog_layout, null);
        AlertDialog.Builder progress = new AlertDialog.Builder(getContext());
        progress.setView(progressView);
        progress.setCancelable(false);
        progressDialog = progress.create();
        eegLabelView = rootView.findViewById(R.id.yAxis_label);

        permission = PermissionUtils.checkRuntimePermissions(this, READ_WRITE_PERMISSIONS);
        initializeMemGraph(memGraph);
        if (StatisticsFragment.parsedData != null) {
            parsedData = StatisticsFragment.parsedData;
            plotGraph();
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void initializeMemGraph(LineChart memGraph) {

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
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawTopYLabelEntry(true);
        leftAxis.setGridColor(Color.WHITE);

        YAxis rightAxis = memGraph.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void addEntry(int i) {

        LineData data = memGraph.getData();
        Float currPlotValue;

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }
            if (parsedData[i].length() > 0) {
                currPlotValue = createPlotValues(parsedData[i]);
                if (currPlotValue < maxEEGValue) {
                    data.addEntry(new Entry(set.getEntryCount(), currPlotValue + effectiveDistance), 0);
                } else
                    return;
            } else
                return;

            data.notifyDataChanged();

            // let the memGraph know it's data has changed
            memGraph.notifyDataSetChanged();

            // limit the number of visible entries
            memGraph.setVisibleXRangeMaximum(parsedData.length);
            // memGraph.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            memGraph.moveViewToX(data.getEntryCount());
        }
    }

    private Float createPlotValues(String value) {
        int startTrimIndex = 0;
        int endTrimIndex = 9;
        return (float) Double.parseDouble(value.substring(startTrimIndex, endTrimIndex));
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, getResources().getString(R.string.eeg_data_label));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.GREEN);
        set.setDrawCircles(false);
        set.setLineWidth(2f);
        set.setFillAlpha(65);
        set.setFillColor(Color.GREEN);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(false);
        return set;
    }

    private void plotGraph() {

        eegLabelView.setVisibility(View.INVISIBLE);

        if (thread != null)
            thread.interrupt();

        thread = new Thread(() -> {
            int i = 0;
            while (parsedData != null) {
                if (i < parsedData.length / 4) {
                    int dataIndex = i;
                    addEntry(dataIndex);
                    i++;
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length < 1)
            return;
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permission = true;
                } else
                    Toast.makeText(getContext(), getResources().getString(R.string.perm_not_granted), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getContext(), getResources().getString(R.string.perm_not_granted), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void getRuntimePermissions() {
        PermissionUtils.requestRuntimePermissions(this,
                READ_WRITE_PERMISSIONS,
                PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (thread != null) {
            thread.interrupt();
        }
    }

    private void selectCSVFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.import_csv)), ACTIVITY_CHOOSE_FILE1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case ACTIVITY_CHOOSE_FILE1:
                if (resultCode == RESULT_OK) {
                    String realPath = FilePathUtil.getRealPath(getContext(), data.getData());
                    importData(realPath);
                }
                break;
            default:
                Toast.makeText(getContext(), getResources().getString(R.string.perm_not_granted), Toast.LENGTH_SHORT).show();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void importData(String path) {
        if (permission && (StatisticsFragment.parsedData == null || parsedData == null)) {
            isPlaying = true;
            toggleMenuItem(menu, isPlaying);
            progressDialog.show();
            ParseDataAsync parseDataAsync = new ParseDataAsync(path);
            parseDataAsync.execute();
        } else {
            getRuntimePermissions();
        }
    }

    /**
     * Toggle menu items.
     *
     * @param menu
     * @param isPlaying
     */
    private void toggleMenuItem(Menu menu, boolean isPlaying) {
        MenuItem play = menu.findItem(R.id.play_graph);
        play.setVisible(!isPlaying);
        MenuItem stop = menu.findItem(R.id.stop_data);
        stop.setVisible(isPlaying);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.menu = menu;
        toggleMenuItem(menu, !isPlaying);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.utility_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.import_data) {
            getRuntimePermissions();
            selectCSVFile();
        } else if (id == R.id.info_program) {
            AlertDialog.Builder progress = new AlertDialog.Builder(getContext());
            progress.setCancelable(true);
            progress.setTitle(R.string.program_info_label);
            progress.setMessage(R.string.program_info);
            AlertDialog infoDialog = progress.create();
            infoDialog.show();

        } else if (id == R.id.stop_data) {
            parsedData = null;
            Toast.makeText(getContext(), "Stopped", Toast.LENGTH_SHORT).show();
            toggleMenuItem(menu, !isPlaying);
        } else if (id == R.id.play_graph && (parsedData == null && StatisticsFragment.parsedData != null)) {
            parsedData = StatisticsFragment.parsedData;
            plotGraph();
            toggleMenuItem(menu, isPlaying);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        // When particular value on the graph is selected.
    }

    @Override
    public void onNothingSelected() {
        // When nothing is selected.
    }

    private class ParseDataAsync extends AsyncTask<Void, Void, String[]> {

        private String filePath;

        public ParseDataAsync(String filePath) {
            this.filePath = filePath;
        }

        @Override
        protected String[] doInBackground(Void... voids) {
            rawData = new ArrayList<>();
            String[] eegValues = null;
            int eegValueSize = 0;
            int rawDataSize = 0;
            try {
                CSVReader reader = new CSVReader(new FileReader(filePath));
                while ((reader.readNext()) != null) {
                    rawData.add(rawDataSize, reader.readNext());
                    rawDataSize++;
                }
                eegValues = new String[(rawDataSize - 1) * rawData.get(0).length];
                for (int i = 0; i < rawData.size(); i++) {
                    if (rawData.get(i) != null) {
                        for (int j = 0; j < rawData.get(i).length; j++) {
                            eegValues[eegValueSize] = rawData.get(i)[j];
                            eegValueSize++;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return eegValues;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            StatisticsFragment.parsedData = parsedData = strings;
            progressDialog.hide();
            plotGraph();
        }
    }
}
