package io.neurolab.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.neurolab.R;
import io.neurolab.adapters.ShareDataAdapter;

import static io.neurolab.utilities.FilePathUtil.CSV_DIRECTORY;
import static io.neurolab.utilities.FilePathUtil.readWriteData;
import static io.neurolab.utilities.FilePathUtil.setupPath;

public class ShareDataActivity extends AppCompatActivity {

    private static Button shareBtn;
    private static Button cancelBtn;

    public static List<String> fileList = new ArrayList<>();
    public static List<File> filesList = new ArrayList<>();
    public static String PACKAGE_NAME;
    public static Context context;
    public static RecyclerView shareDataRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_data);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        shareBtn = findViewById(R.id.share_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        shareDataRecyclerView = findViewById(R.id.share_recycler_view);
        PACKAGE_NAME = getApplicationContext().getPackageName();
        context = getApplicationContext();
        File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                CSV_DIRECTORY);

       // String AppDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + CSV_DIRECTORY;
        showLoggedDataList(appDir);
    }

    private void showLoggedDataList(File appDir) {
        File[] files = appDir.listFiles();
        if (appDir.listFiles() != null && files.length > 0) {
            //noLoggedView.setVisibility(View.GONE);
            fileList.clear();
            for (File file : files) {
                fileList.add(file.getPath());
                        filesList.add(file);
            }

            ShareDataAdapter adapter = new ShareDataAdapter(fileList,filesList,context);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                    this, LinearLayoutManager.VERTICAL, false);
            shareDataRecyclerView.setLayoutManager(linearLayoutManager);
            shareDataRecyclerView.setAdapter(adapter);
        } else {
            setupPath();
            // noLoggedView.setVisibility(View.GONE);

            readWriteData("sample1", appDir);
            readWriteData("sample2", appDir);
            readWriteData("sample3", appDir);
            readWriteData("sample4", appDir);

            ShareDataAdapter adapter = new ShareDataAdapter(fileList, filesList, context);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                    this, LinearLayoutManager.VERTICAL, false);
            shareDataRecyclerView.setLayoutManager(linearLayoutManager);
            shareDataRecyclerView.setAdapter(adapter);

        }
    }

    public void onCancelClick(View v)
    {
        onSupportNavigateUp();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public void onShareClicked(View v)
    {
      //  Log.d("Share Button"," Clicked!");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT,"Here are files you shared.");
        intent.setType("*/*");

        ArrayList<Uri> uris = new ArrayList<Uri>();
        for(String path : fileList){
            File file = new File(path);
            Uri uri = Uri.fromFile(file);
            uris.add(uri);
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        startActivity(intent);
    }
}
