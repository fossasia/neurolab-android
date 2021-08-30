package io.neurolab.adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Field;

import io.neurolab.R;
import io.neurolab.activities.MeditationActivity;

import static io.neurolab.activities.MeditationHome.MEDITATION_DIR_KEY;

public class MeditationListAdapter extends RecyclerView.Adapter<MeditationListAdapter.ViewHolder> {

    private Context context;
    private Field[] resIds;

    public MeditationListAdapter(Context context, Field[] resIds) {
        this.context = context;
        this.resIds = resIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.item_meditation_layout, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.meditationNameView.setText(getMeditationName(resIds[i].getName()));
        viewHolder.fabPlayMeditation.setOnClickListener(v -> {
            Intent intent = new Intent(context, MeditationActivity.class);
            try {
                intent.putExtra(MEDITATION_DIR_KEY, resIds[i].getInt(resIds[i]));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return resIds.length;
    }

    private String getMeditationName(String rawName) {
        String name = "" + rawName.charAt(0);
        name = name.toUpperCase();
        for (int i = 1; i < rawName.length(); i++) {
            if (rawName.charAt(i) == '_') {
                name += ' ';
                continue;
            }
            name += rawName.charAt(i);
        }
        return name;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView meditationNameView;
        private ImageView fabPlayMeditation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            meditationNameView = itemView.findViewById(R.id.meditation_name);
            fabPlayMeditation = itemView.findViewById(R.id.play_meditation);
        }
    }
}
