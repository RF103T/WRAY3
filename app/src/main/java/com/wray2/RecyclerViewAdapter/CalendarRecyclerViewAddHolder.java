package com.wray2.RecyclerViewAdapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wray2.R;

public class CalendarRecyclerViewAddHolder extends RecyclerView.ViewHolder {

    TextView add;
    ImageView pic1;
    ImageView line;
    ImageView addpic;

    public CalendarRecyclerViewAddHolder(@NonNull View itemView) {
        super(itemView);
        add = itemView.findViewById(R.id.txt_calendar_addCard);
        pic1 = itemView.findViewById(R.id.Img_calendar_loading);
        line = itemView.findViewById(R.id.Img_calendar_line);
        addpic = itemView.findViewById(R.id.Img_calendar_addpic);
    }
}
