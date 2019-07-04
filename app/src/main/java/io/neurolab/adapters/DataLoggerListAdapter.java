package io.neurolab.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import io.neurolab.R;
import io.neurolab.activities.MemoryGraphParent;

import static io.neurolab.utilities.FilePathUtil.LOG_FILE_KEY;

public class DataLoggerListAdapter extends RecyclerView.Adapter<DataLoggerListAdapter.ViewHolder> {

    private Context context;
    private List<File> files;

    public DataLoggerListAdapter(Context context, List<File> files) {
        this.context = context;
        this.files = files;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.logged_data_item, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.fileNameView.setText(files.get(i).getName());
        viewHolder.loggedCardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MemoryGraphParent.class);
            intent.putExtra(LOG_FILE_KEY, files.get(i).getAbsolutePath());
            context.startActivity(intent);
            ((Activity) context).finish();
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fileNameView;
        private CardView loggedCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameView = itemView.findViewById(R.id.file_name_view);
            loggedCardView = itemView.findViewById(R.id.parent_logged_card);
        }
    }
}
