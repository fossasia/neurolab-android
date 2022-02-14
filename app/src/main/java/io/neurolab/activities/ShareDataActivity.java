package io.neurolab.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import io.neurolab.R;

import static io.neurolab.utilities.FilePathUtil.CSV_DIRECTORY;

public class ShareDataActivity extends AppCompatActivity {

    private static String[] fileList;
    private static String[] newFileName;
    public static Context context;
    private static Intent resultIntent;
    private static ListView fileListView;
    private static int found = 0;
    private static Set<File> selectedFiles = new HashSet<>();
    private static Uri fileUri;
    private static ArrayList<Uri> selectedUri = new ArrayList<Uri>();
    private static Set<Uri> set;
    private static SparseBooleanArray sparseBooleanArray;
    private static TextView numberOfFiles;
    private static Button shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_data);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        fileListView = findViewById(R.id.fileListView);
        shareButton = findViewById(R.id.share_btn);
        numberOfFiles = findViewById(R.id.fileNumberView);
        context = getApplicationContext();

        resultIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        resultIntent.setType("text/plain");

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator +
                CSV_DIRECTORY);
        File[] files = appDir.listFiles();
        fileList = new String[files.length];
        newFileName = new String[files.length];

        if (appDir.listFiles() != null && files.length > 0) {
            int j = 0;
            for (int i = 0; i < files.length; i++) {
                fileList[j] = files[i].getAbsolutePath();
                newFileName[i] = files[i].getName();
                j++;
            }
            numberOfFiles.setVisibility(View.INVISIBLE);
            Toast.makeText(this, R.string.share_screen_toast, Toast.LENGTH_SHORT).show();
        }

        if (appDir.listFiles().length == 0) {
            numberOfFiles.setVisibility(View.VISIBLE);
            numberOfFiles.setText(R.string.no_datasets_message);
            shareButton.setVisibility(View.INVISIBLE);
        }

        final List<String> file_list = new ArrayList<String>(Arrays.asList(newFileName));
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_multiple_choice, file_list);
        fileListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        fileListView.setItemsCanFocus(false);
        fileListView.setAdapter(arrayAdapter);

        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowId) {

           sparseBooleanArray = fileListView.getCheckedItemPositions();
           File requestFile = new File(fileList[position]);

           try {
               fileUri = FileProvider.getUriForFile(ShareDataActivity.this, "io.neurolab.fileprovider", requestFile);
           } catch (IllegalArgumentException e) {
               e.printStackTrace();
           }

           for (int i = 0; i < fileListView.getCount(); i++) {
               if (sparseBooleanArray.get(i)) {
                   selectedFiles.add(files[i]);
               } else if (!sparseBooleanArray.get(i)) {
                   selectedFiles.remove(files[i]);
               }
           }
          updateSelectedFiles(selectedFiles);
          setPermission();
         }
       }
     );
   }

    private void setPermission() {
          List<ResolveInfo> resInfoList = getApplicationContext().getPackageManager().queryIntentActivities(resultIntent, PackageManager.MATCH_DEFAULT_ONLY);
          for (ResolveInfo resolveInfo : resInfoList) {
              String packageName = resolveInfo.activityInfo.packageName;
              getApplicationContext().grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
          }
      }

      private void updateSelectedFiles(Set<File> selectedFiles) {
          for (int i = 0; i < fileListView.getCount(); i++) {
              if (sparseBooleanArray.get(i)) {
                  found++;
              }
          }
          if (found == 0) {
              selectedFiles.clear();
          }
      }

    public void createUriList(Set<File> selectedFiles) {
        for (File file : selectedFiles) {
            Uri uri = Uri.fromFile(file);
            selectedUri.add(uri);
        }
        set = new HashSet<>(selectedUri);
        selectedUri.clear();
        selectedUri.addAll(set);
    }

    public void onShareClicked(View v) {
        if (selectedFiles.size() == 0) {
            Toast.makeText(this, R.string.share_toast, Toast.LENGTH_SHORT).show();
        } else {
            createUriList(selectedFiles);
            resultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            if (fileUri != null) {
                resultIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, selectedUri);
                resultIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(resultIntent, "Share"));
            }
            selectedFiles.clear();
            selectedUri.clear();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}