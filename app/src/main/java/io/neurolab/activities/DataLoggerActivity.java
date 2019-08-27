package io.neurolab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.neurolab.R;
import io.neurolab.adapters.DataLoggerListAdapter;

import static io.neurolab.utilities.FilePathUtil.CSV_DIRECTORY;

public class DataLoggerActivity extends AppCompatActivity {

    private RecyclerView dataloggerRecyclerView;
    private TextView noLoggedView;

    private List<String> fileList = new ArrayList<>();
    private List<File> filesList = new ArrayList<>();

    private String flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_logger);
        setTitle(R.string.logged_data);

        if (getIntent().getExtras() != null)
            flag = getIntent().getExtras().getString(ProgramModeActivity.PROGRAM_FLAG_KEY);

        dataloggerRecyclerView = findViewById(R.id.recycler_view);
        noLoggedView = findViewById(R.id.data_logger_blank_view);

        File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                CSV_DIRECTORY);

        showLoggedDataList(appDir);
    }

    private void showLoggedDataList(File appDir) {
        File[] files = appDir.listFiles();
        if (appDir.listFiles() != null && files.length > 0) {
            noLoggedView.setVisibility(View.GONE);
            fileList.clear();
            for (File file : files) {
                fileList.add(file.getPath());
                filesList.add(file);
            }
            DataLoggerListAdapter adapter = new DataLoggerListAdapter(this, filesList, flag);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                    this, LinearLayoutManager.VERTICAL, false);
            dataloggerRecyclerView.setLayoutManager(linearLayoutManager);
            dataloggerRecyclerView.setAdapter(adapter);
        } else {
            dataloggerRecyclerView.setVisibility(View.GONE);
            noLoggedView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (flag != null) {
            Intent intent = new Intent(this, ProgramModeActivity.class);
            intent.putExtra(ProgramModeActivity.INTENT_KEY_PROGRAM_MODE, flag);
            startActivity(intent);
        }
        finish();
    }
}
