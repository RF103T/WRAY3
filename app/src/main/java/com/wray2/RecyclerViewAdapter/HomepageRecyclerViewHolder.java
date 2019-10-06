package com.wray2.RecyclerViewAdapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wray2.R;

public class HomepageRecyclerViewHolder extends RecyclerView.ViewHolder {

    TextView time;
    TextView sort;
    TextView date;

    public HomepageRecyclerViewHolder(@NonNull View itemView){
        super(itemView);
        date = itemView.findViewById(R.id.txt_item_date);
        time = itemView.findViewById(R.id.txt_item_time);
        sort = itemView.findViewById(R.id.txt_item_sort);
    }

}
