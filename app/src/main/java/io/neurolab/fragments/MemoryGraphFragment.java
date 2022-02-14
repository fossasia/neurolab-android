package io.neurolab.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AlertDialog;
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
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import io.neurolab.R;
import io.neurolab.activities.DataLoggerActivity;
import io.neurolab.activities.MemoryGraphParent;
import io.neurolab.activities.ProgramModeActivity;
import io.neurolab.communication.DataReceiver;
import io.neurolab.communication.USBCommunicationHandler;
import io.neurolab.main.NeuroLab;
import io.neurolab.utilities.FilePathUtil;
import io.neurolab.utilities.LocationTracker;
import io.neurolab.utilities.PermissionUtils;

import static android.app.Activity.RESULT_OK;
import static io.neurolab.utilities.FilePathUtil.LOG_FILE_KEY;

public class MemoryGraphFragment extends Fragment implements OnChartValueSelectedListener {

    private LineChart memGraph;
    private View view;
    private Thread thread;
    private AlertDialog progressDialog;
    private TextView eegLabelView;
    private String[] parsedData;
    private String importedFilePath;
    private static boolean isPlaying;
    private boolean isRecording = false;
    private static boolean showInstructions = true;
    private AlertDialog instructionsDialog;
    private static Menu globalMenu;
    public static LocationTracker locationTracker;
    private USBCommunicationHandler usbCommunicationHandler;
    private DataReceiver dataReceiver;
    private final String ACTION_USB_PERMISSION = "io.neurolab.USB_PERMISSION";
    private String filePath;
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
        view = inflater.inflate(R.layout.fragment_memory_graph, container, false);
        ((MemoryGraphParent) getActivity()).setActionBarTitle(getResources().getString(R.string.mem_graph));
        memGraph = view.findViewById(R.id.mem_graph);
        LayoutInflater layoutInflater = getLayoutInflater();
        View progressView = layoutInflater.inflate(R.layout.progress_dialog_layout, null);
        AlertDialog.Builder progress = new AlertDialog.Builder(getContext());
        progress.setView(progressView);
        progress.setCancelable(false);
        progressDialog = progress.create();
        eegLabelView = view.findViewById(R.id.yAxis_label);

        permission = PermissionUtils.checkRuntimePermissions(this, READ_WRITE_PERMISSIONS);
        initializeMemGraph(memGraph);

        buildInstructionDialog();
        if (showInstructions) {
            instructionsDialog.show();
            showInstructions = false;
        }

        if (StatisticsFragment.parsedData != null) {
            instructionsDialog.dismiss();
            parsedData = StatisticsFragment.parsedData;
            plotGraph();
        }
        if (getArguments().getString(LOG_FILE_KEY) != null && StatisticsFragment.parsedData == null) {
            instructionsDialog.dismiss();
            filePath = getArguments().getString(LOG_FILE_KEY);
        }
        return view;
    }

    private void buildInstructionDialog() {
        instructionsDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.mem_graph)
                .setMessage(R.string.focus_rec_ins)
                // This actually server as a positive action
                .setNegativeButton(R.string.yes_focus_msg, (dialog, which) -> recordData())
                // This actually server as a negative action
                .setNeutralButton(R.string.cancel, (dialog, which) -> {
                })
                // This actually server as a neutral action
                .setPositiveButton(R.string.focus_test_msg, (dialog, which) -> {
                    Intent intent = new Intent(getContext(), DataLoggerActivity.class);
                    intent.putExtra(ProgramModeActivity.PROGRAM_FLAG_KEY, MemoryGraphParent.MEMORY_GRAPH_FLAG);
                    startActivity(intent);
                })
                .create();
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

        if (data != null && parsedData != null && parsedData.length > i) {
            Activity memActivity = getActivity();
            if (parsedData[i].length() > 0 && !(parsedData[i].isEmpty())) {
                currPlotValue = createPlotValues(parsedData[i]);
                if (currPlotValue < maxEEGValue) {
                    if (memActivity != null)
                        memActivity.runOnUiThread(() -> {
                            ILineDataSet set = data.getDataSetByIndex(0);
                            if (set == null) {
                                set = createSet();
                                data.addDataSet(set);
                            }
                            data.addEntry(new Entry(set.getEntryCount(), currPlotValue + effectiveDistance), 0);
                            data.notifyDataChanged();

                            // let the memGraph know it's data has changed
                            memGraph.notifyDataSetChanged();

                            // limit the number of visible entries
                            if (parsedData != null)
                                memGraph.setVisibleXRangeMaximum(parsedData.length);
                            // memGraph.setVisibleYRange(30, AxisDependency.LEFT);

                            // move to the latest entry
                            memGraph.moveViewToX(data.getEntryCount());
                        });
                } else
                    return;
            } else {
                memActivity.runOnUiThread(() -> memGraph.notifyDataSetChanged());
            }
        } else {
            getActivity().runOnUiThread(() -> memGraph.notifyDataSetChanged());
        }
    }

    private Float createPlotValues(String value) {
        return (float) Double.parseDouble(value);
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
                if (i < parsedData.length) {
                    int dataIndex = i;
                    addEntry(dataIndex);
                    i++;
                    try {
                        Thread.sleep(100);
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

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    String realPath = FilePathUtil.getRealPath(getContext(), data.getData());
                    importLoggedData(realPath);
                }
                break;
            default:
                Toast.makeText(getContext(), getResources().getString(R.string.perm_not_granted), Toast.LENGTH_SHORT).show();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void importLoggedData(String path) {
        if (!permission)
            getRuntimePermissions();
        importedFilePath = path;
        toggleMenuItem(globalMenu, isPlaying);
        progressDialog.show();
        new ParseDataAsync(path).execute();
    }

    /**
     * Toggle globalMenu items.
     *
     * @param menu
     * @param isPlaying
     */
    private void toggleMenuItem(Menu menu, boolean isPlaying) {
        MenuItem play = menu.findItem(R.id.play_graph);
        MenuItem stop = menu.findItem(R.id.stop_data);

        if (getArguments().getString(LOG_FILE_KEY) == null) {
            play.setVisible(false);
            stop.setVisible(false);
        } else {
            play.setVisible(!isPlaying);
            stop.setVisible(isPlaying);
        }
    }

    private void toggleRecordMenuItem(Menu menu, boolean isRecording) {
        MenuItem record = menu.findItem(R.id.save_graph_data);
        MenuItem stop = menu.findItem(R.id.stop_graph_data);

        if (getArguments().getString(LOG_FILE_KEY) != null) {
            record.setVisible(false);
            stop.setVisible(false);
        } else {
            record.setVisible(!isRecording);
            stop.setVisible(isRecording);
        }
    }

    private void recordData() {
        usbCommunicationHandler = USBCommunicationHandler.getInstance(getContext(), NeuroLab.getUsbManager());
        locationTracker = new LocationTracker(getContext(), (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE));

        dataReceiver = new DataReceiver(getContext(), usbCommunicationHandler);

        IntentFilter intentFilter = new IntentFilter();
        // adding the possible USB intent actions.
        intentFilter.addAction(ACTION_USB_PERMISSION);
        getContext().registerReceiver(dataReceiver, intentFilter);
        usbCommunicationHandler.searchForArduinoDevice(getContext());
        locationTracker.startCaptureLocation();
        if (usbCommunicationHandler.getSerialPort() != null) {
            toggleRecordMenuItem(globalMenu, !isRecording);
            Snackbar.make(view, R.string.recording_message, Snackbar.LENGTH_LONG).show();
        } else
            Snackbar.make(view, R.string.no_rec_msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MemoryGraphFragment.globalMenu = menu;
        toggleMenuItem(globalMenu, !isPlaying);
        toggleRecordMenuItem(globalMenu, false);
        if (filePath != null) {
            isPlaying = true;
            importLoggedData(filePath);
        }
        if (StatisticsFragment.parsedData != null) {
            toggleMenuItem(globalMenu, isPlaying);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.utility_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.info_program) {
            AlertDialog.Builder progress = new AlertDialog.Builder(getContext());
            progress.setCancelable(true);
            progress.setTitle(R.string.program_info_label);
            progress.setMessage(R.string.program_info);
            AlertDialog infoDialog = progress.create();
            infoDialog.show();

        } else if (id == R.id.stop_data) {
            parsedData = null;
            Toast.makeText(getContext(), "Stopped", Toast.LENGTH_SHORT).show();
            isPlaying = true;
            toggleMenuItem(globalMenu, !isPlaying);
        } else if (id == R.id.play_graph && (parsedData == null && StatisticsFragment.parsedData != null)) {
            parsedData = StatisticsFragment.parsedData;
            plotGraph();
            toggleMenuItem(globalMenu, isPlaying);
        } else if (id == R.id.data_logger_menu) {
            if (!permission)
                getRuntimePermissions();
            Intent intent = new Intent(getContext(), DataLoggerActivity.class);
            intent.putExtra(ProgramModeActivity.PROGRAM_FLAG_KEY, MemoryGraphParent.MEMORY_GRAPH_FLAG);
            startActivity(intent);
        } else if (id == R.id.save_graph_data) {
            recordData();
        } else if (id == R.id.stop_graph_data) {
            toggleRecordMenuItem(globalMenu, isRecording);
            dataReceiver.stopConnection();
            displayLogLocationOnSnackBar();
        }
        return super.onOptionsItemSelected(item);
    }

    public void displayLogLocationOnSnackBar() {
        final File logDirectory = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + FilePathUtil.CSV_DIRECTORY);
        String logLocation;
        try {
            logLocation = getString(R.string.log_saved_directory) + logDirectory.getCanonicalPath();
        } catch (IOException e) {
            logLocation = getString(R.string.log_saved_failed);
        }

        Snackbar.make(view, logLocation, Snackbar.LENGTH_LONG).setAction(R.string.open_label, v -> {
            Intent intent = new Intent(getContext(), DataLoggerActivity.class);
            intent.putExtra(ProgramModeActivity.PROGRAM_FLAG_KEY, MemoryGraphParent.MEMORY_GRAPH_FLAG);
            startActivity(intent);
        }).setActionTextColor(Color.RED).show();
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
            ArrayList<String> rawData = new ArrayList<>();
            String[] eegValues = null;
            int eegValueSize = 0;
            int rawDataSize = 0;
            try {
                CSVReader reader = new CSVReader(new FileReader(filePath));
                String[] nextLine;
                while ((nextLine = reader.readNext()) != null) {
                    if (rawDataSize == 0) {
                        rawData.add(rawDataSize, "4150.00"); // Default min value
                        rawDataSize++;
                        continue;
                    }
                    if (nextLine.length <= 2)
                        continue;
                    rawData.add(rawDataSize, nextLine[2]);
                    rawDataSize++;
                }
                eegValues = new String[rawDataSize];
                for (int i = 0; i < rawData.size(); i++) {
                    if (rawData.get(i) != null) {
                        eegValues[eegValueSize] = rawData.get(i);
                        eegValueSize++;
                    }
                }
            } catch (IOException | CsvValidationException e) {
                e.printStackTrace();
            }
            return eegValues;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);
            StatisticsFragment.parsedData = parsedData = strings;
            progressDialog.dismiss();
            plotGraph();
        }
    }
}
