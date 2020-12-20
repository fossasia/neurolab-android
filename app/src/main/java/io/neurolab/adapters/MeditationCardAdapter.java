package io.neurolab.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.neurolab.R;
import io.neurolab.activities.MeditationListActivity;
import io.neurolab.model.MeditationCardData;

public class MeditationCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    List<MeditationCardData> mdata;
    Context mcxt;

    public MeditationCardAdapter(Context cxt, List<MeditationCardData> data){
        this.mcxt=cxt;
        this.mdata=data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            // Here Inflating your recyclerview item layout
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_meditation_cardlist_item, parent, false);
            return new viewHolder(itemView);
        }
        else if (viewType == TYPE_HEADER) {
            // Here Inflating your header view
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_meditation_header_image, parent, false);
            return new HeaderViewHolder(itemView);
        }
        else return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof HeaderViewHolder){

            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;

            // You have to set your header items values with the help of model class and you can modify as per your needs

            headerViewHolder.mimage.setImageResource(R.drawable.meditation_bg);

        }
        else if (holder instanceof viewHolder){

            final viewHolder itemViewHolder = (viewHolder) holder;

            itemViewHolder.mic.setImageResource(mdata.get(position-1).getIcon());
            itemViewHolder.mtext1.setText(mdata.get(position-1).getHead());
            itemViewHolder.mtext2.setText(mdata.get(position-1).getDesc());
            itemViewHolder.mcardView.setOnClickListener(v -> {
                Intent intent = new Intent(mcxt , MeditationListActivity.class);
                mcxt.startActivity(intent);
            });

        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return mdata.size()+1;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }


    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        ImageView mimage;

        public HeaderViewHolder(View view) {
            super(view);
            mimage=(ImageView) view.findViewById(R.id.image1);

        }
    }

    public static class viewHolder extends RecyclerView.ViewHolder{

        ImageView mic;
        TextView mtext1;
        TextView mtext2;
        CardView mcardView;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            mic=(ImageView) itemView.findViewById(R.id.icon2_0);
            mtext1=(TextView) itemView.findViewById(R.id.text1);
            mtext2=(TextView) itemView.findViewById(R.id.text2);
            mcardView=(CardView) itemView.findViewById(R.id.cardview);
        }
    }

}


