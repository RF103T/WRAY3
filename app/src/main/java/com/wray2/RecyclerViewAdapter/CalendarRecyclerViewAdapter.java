package com.wray2.RecyclerViewAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wray2.Class.Alert;
import com.wray2.R;

import java.util.LinkedList;
import java.util.List;

public class CalendarRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private List<Alert> calendars;

    private RecyclerView rv;

    public CalendarRecyclerViewAdapter(Context context, LinkedList<Alert> alerts, RecyclerView rv) {
        this.calendars = alerts;
        this.context = context;
        this.rv = rv;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = inflater.inflate(R.layout.calendar_recylerview_additem, parent, false);
            return new CalendarRecyclerViewAddHolder(view);
        } else {
            View view = inflater.inflate(R.layout.calendar_recylerview_item, parent, false);
            return new CalendarRecyclerViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CalendarRecyclerViewHolder) {
            CalendarRecyclerViewHolder calendarRecyclerViewHolder = (CalendarRecyclerViewHolder) holder;
            calendarRecyclerViewHolder.calendarTime.setText(calendars.get(position).getTime());
            calendarRecyclerViewHolder.calendarAddress.setText(calendars.get(position).getAddress());
        }
    }

    @Override
    public int getItemCount() {
        return calendars.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (calendars.get(position).getAddress().equals("ADD_NEW") &&
                calendars.get(position).getDate().equals("NONE") &&
                calendars.get(position).getTime().equals("NONE")) {
            return 1;
        }
        return 0;
    }
}
