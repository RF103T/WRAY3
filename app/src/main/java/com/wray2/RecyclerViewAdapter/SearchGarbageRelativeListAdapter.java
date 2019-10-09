package com.wray2.RecyclerViewAdapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wray2.Class.Rubbish;
import com.wray2.R;

import java.util.LinkedList;

public class SearchGarbageRelativeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    private LinkedList<Rubbish> arrayList;

    public SearchGarbageRelativeListAdapter(Context context, LinkedList<Rubbish> arrayList) {
        this.arrayList=arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fragment_search_relativeitem,parent,false);
        return new SearchGarbageRelativeListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SearchGarbageRelativeListHolder searchGarbageRelativeListHolder = (SearchGarbageRelativeListHolder)holder;
        searchGarbageRelativeListHolder.rubbishId.setText(arrayList.get(position).getRubbishName());
        searchGarbageRelativeListHolder.rubbishsortId.setText(arrayList.get(position).getRubbishSortName());
        switch (arrayList.get(position).getRubbishSortNum())
        {
            case 0:
            {
                //蓝
                searchGarbageRelativeListHolder.rubbishsortId.setTextColor(Color.rgb(84, 96, 170));
                break;
            }
            case 1:
            {
                //灰
                searchGarbageRelativeListHolder.rubbishsortId.setTextColor(Color.rgb(136, 136, 136));
                break;
            }
            case 2:
            {
                //棕
                searchGarbageRelativeListHolder.rubbishsortId.setTextColor(Color.rgb(101, 63, 50));
                break;
            }
            case 3:
            {
                //红
                searchGarbageRelativeListHolder.rubbishsortId.setTextColor(Color.rgb(221, 6, 22));
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
