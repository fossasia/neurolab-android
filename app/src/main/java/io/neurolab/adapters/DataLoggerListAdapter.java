package io.neurolab.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import io.neurolab.R;
import io.neurolab.activities.MemoryGraphParent;
import io.neurolab.activities.ProgramModeActivity;
import io.neurolab.fragments.FocusVisualFragment;
import io.neurolab.fragments.RelaxVisualFragment;
import io.neurolab.utilities.FilePathUtil;

import static io.neurolab.utilities.FilePathUtil.LOG_FILE_KEY;

public class DataLoggerListAdapter extends RecyclerView.Adapter<DataLoggerListAdapter.ViewHolder> {

    private Context context;
    private List<File> files;
    private String flag;
    private String newFileName;

    public DataLoggerListAdapter(Context context, List<File> files, String flag) {
        this.context = context;
        this.files = files;
        this.flag = flag;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.logged_data_item, viewGroup, false);
        if (flag != null) {
            itemView.findViewById(R.id.focus_play).setVisibility(View.GONE);
            itemView.findViewById(R.id.mem_graph_play).setVisibility(View.GONE);
        } else {
            itemView.findViewById(R.id.gen_play_view).setVisibility(View.GONE);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.fileNameView.setText(files.get(i).getName());
        viewHolder.renameView.setOnClickListener(v -> {
            final EditText enterNameView = new EditText(context);
            FrameLayout container = new FrameLayout(context);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = context.getResources().getDimensionPixelSize(R.dimen.layout_margin_large);
            params.rightMargin = context.getResources().getDimensionPixelSize(R.dimen.layout_margin_large);
            enterNameView.setLayoutParams(params);
            container.addView(enterNameView);
            enterNameView.setText(files.get(i).getName());
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("Rename the file")
                    .setView(container)
                    .setPositiveButton(R.string.ok_button, (dialog, which) -> {
                        newFileName = enterNameView.getText().toString();
                        FilePathUtil.setFileName(files.get(i).getAbsolutePath(), newFileName);
                        Intent intent = ((Activity) context).getIntent();
                        ((Activity) context).finish();
                        context.startActivity(intent);
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> {
                    }).create();
            alertDialog.show();
            enterNameView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // No need to be implemented
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (enterNameView.getText().toString().length() > 0) {
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (enterNameView.getText().toString().length() <= 0) {
                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    }
                }
            });
        });
        viewHolder.deleteFileView.setOnClickListener(v -> {
            FrameLayout container = new FrameLayout(context);
            AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .setTitle("Delete file " + files.get(i).getName())
                    .setView(container).
                            setPositiveButton(R.string.ok_button, ((dialog, which) -> {
                                FilePathUtil.deleteFile(files.get(i));
                                files.remove(i);
                                notifyItemRemoved(i);
                                notifyItemRangeChanged(i, 1);
                            }))
                    .setNegativeButton(R.string.cancel, ((dialog, which) -> {
                    })).create();
            alertDialog.show();
        });
        viewHolder.focusPlayView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            Intent intent = new Intent(context, ProgramModeActivity.class);
            bundle.putString(ProgramModeActivity.INTENT_KEY_PROGRAM_MODE, FocusVisualFragment.FOCUS_FLAG);
            bundle.putString(FocusVisualFragment.FOCUS_FLAG, FocusVisualFragment.FOCUS_FLAG);
            bundle.putString(LOG_FILE_KEY, files.get(i).getAbsolutePath());
            intent.putExtras(bundle);
            context.startActivity(intent);
            ((Activity) context).finish();
        });
        viewHolder.memGraphPlayView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            Intent intent = new Intent(context, ProgramModeActivity.class);
            bundle.putString(ProgramModeActivity.INTENT_KEY_PROGRAM_MODE, MemoryGraphParent.MEMORY_GRAPH_FLAG);
            bundle.putString(LOG_FILE_KEY, files.get(i).getAbsolutePath());
            intent.putExtras(bundle);
            context.startActivity(intent);
            ((Activity) context).finish();
        });
        viewHolder.genPlayView.setOnClickListener(v -> {
            Intent intent;
            Bundle bundle = new Bundle();
            switch (flag) {
                case FocusVisualFragment.FOCUS_FLAG:
                    intent = new Intent(context, ProgramModeActivity.class);
                    bundle.putString(ProgramModeActivity.INTENT_KEY_PROGRAM_MODE, flag);
                    bundle.putString(FocusVisualFragment.FOCUS_FLAG, flag);
                    bundle.putString(LOG_FILE_KEY, files.get(i).getAbsolutePath());
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    break;
                case MemoryGraphParent.MEMORY_GRAPH_FLAG:
                    intent = new Intent(context, MemoryGraphParent.class);
                    intent.putExtra(LOG_FILE_KEY, files.get(i).getAbsolutePath());
                    context.startActivity(intent);
                    break;
                case RelaxVisualFragment
                        .RELAX_PROGRAM_FLAG:
                    intent = new Intent(context, ProgramModeActivity.class);
                    bundle.putString(ProgramModeActivity.INTENT_KEY_PROGRAM_MODE, flag);
                    bundle.putString(RelaxVisualFragment.RELAX_PROGRAM_FLAG, flag);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    break;
                default:
                    break;
            }
            ((Activity) context).finish();
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView fileNameView;
        private ImageView focusPlayView;
        private ImageView memGraphPlayView;
        private ImageView renameView;
        private ImageView genPlayView;
        private CardView loggedCardView;
        private ImageView deleteFileView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileNameView = itemView.findViewById(R.id.file_name_view);
            focusPlayView = itemView.findViewById(R.id.focus_play);
            memGraphPlayView = itemView.findViewById(R.id.mem_graph_play);
            renameView = itemView.findViewById(R.id.rename_file_btn);
            genPlayView = itemView.findViewById(R.id.gen_play_view);
            loggedCardView = itemView.findViewById(R.id.parent_logged_card);
            deleteFileView = itemView.findViewById(R.id.delete_file_btn);
        }
    }
}
