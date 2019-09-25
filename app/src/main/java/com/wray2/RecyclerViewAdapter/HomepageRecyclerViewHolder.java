package com.wray2.RecyclerViewAdapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wray2.R;

public class HomepageRecyclerViewHolder extends RecyclerView.ViewHolder {

    TextView dateTime;
    TextView address;

    public HomepageRecyclerViewHolder(@NonNull View itemView){
        super(itemView);
        dateTime = itemView.findViewById(R.id.txt_item_time);
        address = itemView.findViewById(R.id.txt_item_address);
    }

}
