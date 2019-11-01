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

public class SearchGarbageRelativeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private LayoutInflater inflater;
    private Context context;
    private LinkedList<Rubbish> arrayList;

    public SearchGarbageRelativeListAdapter(Context context, LinkedList<Rubbish> arrayList)
    {
        this.arrayList = arrayList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.fragment_search_relativeitem, parent, false);
        return new SearchGarbageRelativeListHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        SearchGarbageRelativeListHolder searchGarbageRelativeListHolder = (SearchGarbageRelativeListHolder)holder;
        searchGarbageRelativeListHolder.rubbishName.setText(arrayList.get(position).getRubbishName());
        searchGarbageRelativeListHolder.rubbishSortName.setText(arrayList.get(position).getRubbishSortName());
        switch (arrayList.get(position).getRubbishSortNum())
        {
            case 0:
            {
                //蓝
                searchGarbageRelativeListHolder.rubbishSortName.setTextColor(Color.rgb(104, 106, 200));
                break;
            }
            case 1:
            {
                //灰
                searchGarbageRelativeListHolder.rubbishSortName.setTextColor(Color.rgb(156, 156, 156));
                break;
            }
            case 2:
            {
                //棕
                searchGarbageRelativeListHolder.rubbishSortName.setTextColor(Color.rgb(121, 63, 50));
                break;
            }
            case 3:
            {
                //红
                searchGarbageRelativeListHolder.rubbishSortName.setTextColor(Color.rgb(241, 6, 22));
                break;
            }
        }
    }

    @Override
    public int getItemCount()
    {
        return arrayList.size();
    }

    public void setList(LinkedList<Rubbish> rubbishes)
    {
        this.arrayList = rubbishes;
    }
}
