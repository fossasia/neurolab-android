package io.neurolab.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.opencsv.CSVReader;

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
import io.neurolab.utilities.LocationTracker;
import io.neurolab.utilities.PermissionUtils;

import static android.app.Activity.RESULT_OK;
import static io.neurolab.fragments.StatisticsFragment.convertToDouble;
import static io.neurolab.utilities.FilePathUtil.LOG_FILE_KEY;

public class FocusVisualFragment extends android.support.v4.app.Fragment {

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    private boolean permission = false;
    private boolean isPlaying = false;
    private String[] parsedData;
    private String filePath;
    private AlertDialog recordDialog;
    private AlertDialog disconnectDialog;
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

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        GraphicBgRenderer focusBg = view.findViewById(R.id.focus_bg);
        focusBg.setDimensions(size.x, size.y);
        focusBg.getHolder().setFixedSize(size.x, size.y);
        rocketAnimation = new SpaceAnimationVisuals(view);

        // setting up the UsbManager instance with the desired USB service.
        usbCommunicationHandler = USBCommunicationHandler.getInstance(getContext(), NeuroLab.getUsbManager());
        locationTracker = new LocationTracker(getContext(), (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE));

        dataReceiver = new DataReceiver(getContext(), usbCommunicationHandler);

        IntentFilter intentFilter = new IntentFilter();
        // adding the possible USB intent actions.
        intentFilter.addAction(ACTION_USB_PERMISSION);
        getContext().registerReceiver(dataReceiver, intentFilter);

        buildRecDialog();
        buildDisconnectDialog();
        buildInstructionDialog();
        if (showInstructions) {
            instructionsDialog.show();
            showInstructions = false;
        }

        if (getArguments() != null) {
            rocketAnimation.playRocketAnim(view);
            filePath = getArguments().getString(LOG_FILE_KEY);
            new ParseDataAsync(filePath).execute();
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
        boolean isRecording = false;
        if (id == R.id.import_data_focus) {
            if (!permission)
                getRuntimePermissions();
            selectCSVFile();
        } else if (id == R.id.play_focus_anim) {
            toggleMenuItem(menu, !isPlaying);
            rocketAnimation.playRocketAnim(view);
            rocketAnimation.animateRocket(convertToDouble(parsedData), getActivity());

        } else if (id == R.id.stop_focus_anim) {
            toggleMenuItem(menu, isPlaying);
            rocketAnimation.pauseRocketAnim(view);
        } else if (id == R.id.save_focus_data) {
            recordData();
            toggleRecordItem(menu, !isRecording);
            // TODO: Implement Snackbar similar to PSLab
        } else if (id == R.id.stop_record) {
            toggleRecordItem(menu, isRecording);
            dataReceiver.stopConnection();
            // TODO: Implement Snackbar similar to PSLab
        } else if (id == R.id.focus_data_logger) {
            Intent intent = new Intent(getContext(), DataLoggerActivity.class);
            intent.putExtra(ProgramModeActivity.PROGRAM_FLAG_KEY, FOCUS_FLAG);
            startActivity(intent);
        } else if (id == R.id.focus_program_info) {
            AlertDialog.Builder progress = new AlertDialog.Builder(view.getContext());
            progress.setCancelable(true);
            progress.setTitle(R.string.program_info_label);
            progress.setMessage(R.string.focus_program_info);
            AlertDialog infoDialog = progress.create();
            infoDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void recordData() {
        usbCommunicationHandler.searchForArduinoDevice(getContext());
        locationTracker.startCaptureLocation();
        if (usbCommunicationHandler.getSerialPort() != null)
            recordDialog.show();
        else disconnectDialog.show();
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

        record.setVisible(!isRecording);
        stop.setVisible(isRecording);
    }

    private void selectCSVFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.import_csv)), ACTIVITY_CHOOSE_FILE1);
    }

    private void buildRecDialog() {
        LayoutInflater layoutInflater = ((Activity) getContext()).getLayoutInflater();
        View progressView = layoutInflater.inflate(R.layout.record_dialog_layout, null);
        recordDialog = new AlertDialog.Builder(getContext())
                .setView(progressView)
                .setCancelable(false)
                .setPositiveButton(R.string.stop, (dialog, which) -> dataReceiver.stopConnection()).create();
    }

    private void buildDisconnectDialog() {
        disconnectDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.no_device)
                .setMessage(R.string.no_rec_msg)
                .setPositiveButton(R.string.ok_button, (dialog, which) -> {
                }).create();
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
        rocketAnimation.playRocketAnim(view);
    }

    @Override
    public void onPause() {
        super.onPause();
        rocketAnimation.pauseRocketAnim(view);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private class ParseDataAsync extends AsyncTask<Void, Void, String[]> {

        private String filePath;

        public ParseDataAsync(String filePath) {
            this.filePath = filePath;
        }

        @Override
        protected String[] doInBackground(Void... voids) {
            ArrayList<String[]> rawData = new ArrayList<>();
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
            parsedData = strings;
            getActivity().runOnUiThread(() -> rocketAnimation.animateRocket(convertToDouble(parsedData), getActivity()));
        }
    }
}
