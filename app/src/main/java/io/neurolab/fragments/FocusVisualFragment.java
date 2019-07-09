package io.neurolab.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import io.neurolab.R;
import io.neurolab.activities.DataLoggerActivity;
import io.neurolab.activities.ProgramModeActivity;
import io.neurolab.main.output.visual.SpaceAnimationVisuals;
import io.neurolab.utilities.FilePathUtil;
import io.neurolab.utilities.PermissionUtils;

import static android.app.Activity.RESULT_OK;
import static io.neurolab.utilities.FilePathUtil.LOG_FILE_KEY;

public class FocusVisualFragment extends android.support.v4.app.Fragment {

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 1;
    private boolean permission = false;
    private boolean isPlaying = false;
    private boolean recordState;
    private String filePath;
    private static Menu menu;
    private static final int ACTIVITY_CHOOSE_FILE1 = 1;
    private static final String[] READ_WRITE_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    public static final String FOCUS_FLAG = "Focus";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_focus_visual, container, false);
        view.findViewById(R.id.animated_view).setVisibility(View.INVISIBLE);

        if (getArguments() != null) {
            view.findViewById(R.id.animated_view).setVisibility(View.VISIBLE);
            SpaceAnimationVisuals.spaceAnim(view);
            recordState = true;
            filePath = getArguments().getString(LOG_FILE_KEY);
        }
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        this.menu = menu;
        toggleMenuItem(menu, !isPlaying);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.focus_utility_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.import_data_focus) {
            if (!permission)
                getRuntimePermissions();
            selectCSVFile();
        } else if (id == R.id.play_focus_anim) {
            SpaceAnimationVisuals.playAnim();
            toggleMenuItem(menu, !isPlaying);
        } else if (id == R.id.stop_focus_anim) {
            SpaceAnimationVisuals.stopAnim();
            toggleMenuItem(menu, isPlaying);
        } else if (id == R.id.save_focus_data) {
            try {
                FilePathUtil.saveData(filePath);
                toggleRecordState(item, recordState);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.focus_data_logger) {
            Intent intent = new Intent(getContext(), DataLoggerActivity.class);
            intent.putExtra(ProgramModeActivity.PROGRAM_FLAG_KEY, FOCUS_FLAG);
            startActivity(intent);
        } else if (id == R.id.focus_program_info) {
            AlertDialog.Builder progress = new AlertDialog.Builder(getContext());
            progress.setCancelable(true);
            progress.setTitle(R.string.program_info_label);
            progress.setMessage(R.string.focus_program_info);
            AlertDialog infoDialog = progress.create();
            infoDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleRecordState(MenuItem item, boolean state) {
        if (state) {
            item.setIcon(R.drawable.ic_record_stop_white);
            recordState = !state;
        } else {
            item.setIcon(R.drawable.ic_record_white);
        }
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
        play.setVisible(!isPlaying);
        MenuItem stop = menu.findItem(R.id.stop_focus_anim);
        stop.setVisible(isPlaying);
    }

    private void selectCSVFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/*");
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.import_csv)), ACTIVITY_CHOOSE_FILE1);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
