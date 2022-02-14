package io.neurolab.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import io.neurolab.R;
import io.neurolab.adapters.MeditationListAdapter;

public class MeditationListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditation_list);
        setTitle("Meditations");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ArrayList<Field> resFields;
        ArrayList<Field> finalList;
        Field[] finalFields;
        int resId;
        resFields = new ArrayList<>(Arrays.asList(R.raw.class.getFields()));
        finalList = new ArrayList<>();
        for (int z = 0; z < resFields.size(); z++) {
            String name = resFields.get(z).getName();
            TypedValue value = new TypedValue();
            resId = getResources().getIdentifier(name, "raw", getPackageName());
            getResources().getValue(resId, value, true);

            if ((getExtension(value.string.toString()).indexOf(".csv") == -1)) {
                finalList.add(resFields.get(z));
            }
        }

        finalFields = finalList.toArray(new Field[finalList.size()]);
        RecyclerView meditationsRecyclerView = findViewById(R.id.meditation_recycler_view);
        MeditationListAdapter meditationListAdapter = new MeditationListAdapter(this, finalFields);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false);
        meditationsRecyclerView.setLayoutManager(linearLayoutManager);
        meditationsRecyclerView.setAdapter(meditationListAdapter);
    }

    public String getExtension(String path) {
        String[] token = path.split("/raw/", 2);
        String ext = token[1];
        return ext;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MeditationHome.class));
        finish();
    }
}
