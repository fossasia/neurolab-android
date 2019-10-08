package io.neurolab.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.neurolab.R;

public class ShareDataAdapter extends RecyclerView.Adapter<ShareDataAdapter.ViewHolder>{

    private static final String TAG = "ShareDataAdapter";
    private List<String> mFilePath = new ArrayList<>();
    private List<File> mFileList = new ArrayList<>();
    private static List<File> selectedFiles = new ArrayList<>();
    private Context mcontext;


    public ShareDataAdapter(List<String> FilePath,List<File> FileNames, Context context){
        mFilePath = FilePath;
        mFileList = FileNames;
        mcontext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.share_data_logged,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.fileName.setText(mFilePath.get(i));
        viewHolder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            private int flag = 1;
            @Override
            public void onClick(View v) {
                Log.d("Clicked On ",mFilePath.get(i));
                File newFile = new File(mFilePath.get(i));
                Toast.makeText(mcontext,"Clicked On "+ mFilePath.get(i),Toast.LENGTH_SHORT).show();
                if(flag == 1) {
                    viewHolder.fileName.setText(mFilePath.get(i) + "Selected");
                    flag = 0;
                    selectedFiles.add(newFile);
                }
                else{
                    viewHolder.fileName.setText(mFilePath.get(i));
                    flag = 1;
                    selectedFiles.remove(newFile);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
      //  Log.i("Size of files array is ",toString(mFileNames.size()));
        return mFilePath.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CheckBox checkBox;
        private TextView fileName;
        private ConstraintLayout constraintLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.check_box);
            fileName = itemView.findViewById(R.id.file_name);
            constraintLayout = itemView.findViewById(R.id.share_constraint_layout);
        }
    }

}