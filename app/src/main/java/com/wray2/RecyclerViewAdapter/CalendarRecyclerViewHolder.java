package com.wray2.RecyclerViewAdapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wray2.R;

public class CalendarRecyclerViewHolder extends RecyclerView.ViewHolder{

    TextView calendarTime;
    TextView calendarAddress;

    public CalendarRecyclerViewHolder(@NonNull View itemView){
        super(itemView);
        calendarTime = itemView.findViewById(R.id.calendar_item_time);
        calendarAddress = itemView.findViewById(R.id.calendar_item_address);
    }
}
