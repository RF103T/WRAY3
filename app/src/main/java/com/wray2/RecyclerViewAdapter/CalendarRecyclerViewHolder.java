package com.wray2.RecyclerViewAdapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wray2.R;

public class CalendarRecyclerViewHolder extends RecyclerView.ViewHolder{

    TextView calendarDate;
    TextView calendarSorts;
    TextView calendarTime;


    public CalendarRecyclerViewHolder(@NonNull View itemView){
        super(itemView);
        calendarDate = itemView.findViewById(R.id.calendar_item_date);
        calendarSorts = itemView.findViewById(R.id.calendar_item_sorts);
        calendarTime = itemView.findViewById(R.id.calendar_item_time);
    }
}
