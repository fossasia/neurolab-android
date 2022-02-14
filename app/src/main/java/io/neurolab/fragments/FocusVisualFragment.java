package io.neurolab.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import io.neurolab.R;
import io.neurolab.activities.DataLoggerActivity;
import io.neurolab.activities.ProgramModeActivity;
import io.neurolab.communication.DataReceiver;
import io.neurolab.communication.USBCommunicationHandler;
import io.neurolab.gui.GraphicBgRenderer;
import io.neurolab.main.NeuroLab;
import io.neurolab.main.output.visual.SpaceAnimationVisuals;
import io.neurolab.utilities.FilePathUtil;
import io.neurolab.utilities.FrequencyProcessor;
import io.neurolab.utilities.LocationTracker;
import io.neurolab.utilities.PermissionUtils;

import static android.app.Activity.RESULT_OK;
import static io.neurolab.utilities.FilePathUtil.LOG_FILE_KEY;

public class FocusVisualFragment extends Fragment {

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    private boolean permission = false;
    private boolean isPlaying = false;
    private boolean isRecording = false;
    private static boolean isVisible = true;
    private static String[] extractedData;
    private static double[] freq;
    private AlertDialog progressDialog;
    private String filePath;
    private AlertDialog instructionsDialog;
    private static boolean showInstructions = true;
    public static LocationTracker locationTracker;
    private USBCommunicationHandler usbCommunicationHandler;
    private DataReceiver dataReceiver;
    private final String ACTION_USB_PERMISSION = "io.neurolab.USB_PERMISSION";
    private static Menu menu;
    private static final int ACTIVITY_CHOOSE_FILE1 = 1;
    private static final String[] READ_WRITE_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static final String FOCUS_FLAG = "Focus";

    private SpaceAnimationVisuals rocketAnimation;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_focus_visual, container, false);

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    if (isVisible) {
                        view.findViewById(R.id.anim_seekbar).setVisibility(View.INVISIBLE);
                        view.findViewById(R.id.anim_timer).setVisibility(View.INVISIBLE);
                    } else {
                        view.findViewById(R.id.anim_seekbar).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.anim_timer).setVisibility(View.VISIBLE);
                    }
                    isVisible = !isVisible;
                }
                return true;
            }
        });

        LayoutInflater layoutInflater = getLayoutInflater();
        View progressView = layoutInflater.inflate(R.layout.progress_dialog_layout, null);
        AlertDialog.Builder progress = new AlertDialog.Builder(getContext());
        progress.setView(progressView);
        progress.setCancelable(false);
        progressDialog = progress.create();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        GraphicBgRenderer focusBg = view.findViewById(R.id.focus_bg);
        focusBg.setDimensions(size.x, size.y);
        focusBg.getHolder().setFixedSize(size.x, size.y);
        rocketAnimation = new SpaceAnimationVisuals(view);

        buildInstructionDialog();
        if (showInstructions) {
            instructionsDialog.show();
            showInstructions = false;
        }

        if (getArguments() != null) {
            progressDialog.show();
            filePath = getArguments().getString(LOG_FILE_KEY);
            new ParseDataAsync(filePath, rocketAnimation, view, getActivity()).execute();

        } else {
            new Handler().postDelayed(() -> rocketAnimation.pauseRocketAnim(view), 400);
        }
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        FocusVisualFragment.menu = menu;
        toggleMenuItem(menu, true);
        toggleRecordItem(menu, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.focus_utility_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.play_focus_anim) {
            toggleMenuItem(menu, !isPlaying);
            rocketAnimation.playRocketAnim(view);
            rocketAnimation.animateRocket(freq, getActivity());

        } else if (id == R.id.stop_focus_anim) {
            toggleMenuItem(menu, isPlaying);
            rocketAnimation.pauseRocketAnim(view);
        } else if (id == R.id.save_focus_data) {
            recordData();
        } else if (id == R.id.stop_record) {
            toggleRecordItem(menu, isRecording);
            dataReceiver.stopConnection();
            displayLogLocationOnSnackBar();
        } else if (id == R.id.focus_data_logger) {
            if (!permission)
                getRuntimePermissions();
            Intent intent = new Intent(getContext(), DataLoggerActivity.class);
            intent.putExtra(ProgramModeActivity.PROGRAM_FLAG_KEY, FOCUS_FLAG);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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
            toggleRecordItem(menu, !isRecording);
            Snackbar.make(view, R.string.recording_message, Snackbar.LENGTH_LONG).show();
        } else
            Snackbar.make(view, R.string.no_rec_msg, Snackbar.LENGTH_LONG).show();
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
            intent.putExtra(ProgramModeActivity.PROGRAM_FLAG_KEY, FOCUS_FLAG);
            startActivity(intent);
        }).setActionTextColor(Color.RED).show();
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
            case LocationTracker.GPS_PERMISSION:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(getContext(), getResources().getString(R.string.perm_not_granted), Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getContext(), getResources().getString(R.string.perm_not_granted), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case ACTIVITY_CHOOSE_FILE1:
                if (resultCode == RESULT_OK) {
                    String realPath = FilePathUtil.getRealPath(getContext(), data.getData());
                    filePath = realPath;
                    FilePathUtil.saveData(realPath);
                    Intent intent = new Intent(getContext(), DataLoggerActivity.class);
                    intent.putExtra(ProgramModeActivity.PROGRAM_FLAG_KEY, FOCUS_FLAG);
                    startActivity(intent);
                    ((Activity) getContext()).finish();
                }
                break;
            default:
                Toast.makeText(getContext(), getResources().getString(R.string.perm_not_granted), Toast.LENGTH_SHORT).show();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void getRuntimePermissions() {
        PermissionUtils.requestRuntimePermissions(this,
                READ_WRITE_PERMISSIONS,
                PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
    }

    /**
     * Toggle menu items.
     *
     * @param menu
     * @param isPlaying
     */
    private void toggleMenuItem(Menu menu, boolean isPlaying) {
        MenuItem play = menu.findItem(R.id.play_focus_anim);
        MenuItem stop = menu.findItem(R.id.stop_focus_anim);

        if (getArguments() != null) {
            play.setVisible(!isPlaying);
            stop.setVisible(isPlaying);
        } else {
            play.setVisible(false);
            stop.setVisible(false);
        }
    }

    private void toggleRecordItem(Menu menu, boolean isRecording) {
        MenuItem record = menu.findItem(R.id.save_focus_data);
        MenuItem stop = menu.findItem(R.id.stop_record);

        if (getArguments() != null) {
            record.setVisible(false);
            stop.setVisible(false);
        } else {
            record.setVisible(!isRecording);
            stop.setVisible(isRecording);
        }
    }

    private void buildInstructionDialog() {
        instructionsDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.focus)
                .setMessage(R.string.focus_rec_ins)
                // This actually server as a positive action
                .setNegativeButton(R.string.yes_focus_msg, (dialog, which) -> recordData())
                // This actually server as a negative action
                .setNeutralButton(R.string.cancel, (dialog, which) -> {
                })
                // This actually server as a neutral action
                .setPositiveButton(R.string.focus_test_msg, (dialog, which) -> {
                    Intent intent = new Intent(getContext(), DataLoggerActivity.class);
                    intent.putExtra(ProgramModeActivity.PROGRAM_FLAG_KEY, FOCUS_FLAG);
                    startActivity(intent);
                })
                .create();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        rocketAnimation.pauseRocketAnim(view);
        extractedData = null;
        freq = null;
        SpaceAnimationVisuals.count = 0;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private class ParseDataAsync extends AsyncTask<Void, Void, String[]> {

        private String filePath;

        private SpaceAnimationVisuals rocketAnimation;
        private View view;
        private Activity activity;

        public ParseDataAsync(String filePath, SpaceAnimationVisuals rocketAnimation, View view, Activity activity) {
            this.filePath = filePath;
            this.rocketAnimation = rocketAnimation;
            this.view = view;
            this.activity = activity;
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
                        rawData.add(rawDataSize, "0.00");
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
            extractedData = strings;
            FrequencyProcessor frequencyProcessor = new FrequencyProcessor(extractedData.length, 32, 16.0);
            freq = frequencyProcessor.processFFTData(convertToDouble(extractedData));
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (extractedData != null) {
                        progressDialog.dismiss();
                        rocketAnimation.animateRocket(freq, activity);
                        rocketAnimation.playRocketAnim(view);
                    } else {
                        rocketAnimation.pauseRocketAnim(view);
                    }
                }
            });
        }
    }

    private static double[] convertToDouble(String[] parsedData) {
        double[] parsedDoubleData = new double[parsedData.length];
        int startTrimIndex = 0;
        int endTrimIndex = 3;
        final int maxRawData = 5060;
        for (int i = 0; i < parsedData.length; i++) {
            if (parsedData[i].length() > 3) {
                parsedDoubleData[i] = Double.parseDouble(parsedData[i].substring(startTrimIndex, endTrimIndex));
                if (parsedDoubleData[i] > maxRawData) {
                    parsedDoubleData[i] = maxRawData;
                }
            }
        }
        return parsedDoubleData;
    }
}
