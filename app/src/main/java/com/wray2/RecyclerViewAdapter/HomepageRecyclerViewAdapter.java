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

public class HomepageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{

    private LayoutInflater inflater;
    private Context context;
    private LinkedList<Alert> alerts;

    public HomepageRecyclerViewAdapter(Context context, LinkedList<Alert> alerts)
    {
        setDataList(alerts);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setDataList(LinkedList<Alert> alerts)
    {
        this.alerts = new LinkedList<Alert>(alerts);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
            View view = inflater.inflate(R.layout.homepage_recylerview_item, parent, false);
            return new HomepageRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        if(position != alerts.size() - 1)
        {
            HomepageRecyclerViewHolder homepageRecyclerViewHolder = (HomepageRecyclerViewHolder)holder;
            homepageRecyclerViewHolder.time.setText(alerts.get(position).getTime());
            homepageRecyclerViewHolder.sort.setText(alerts.get(position).getSort());
            homepageRecyclerViewHolder.date.setText(alerts.get(position).getDate());
        }
    }

    @Override
    public int getItemCount()
    {
        return alerts.size() - 1;
    }
}
