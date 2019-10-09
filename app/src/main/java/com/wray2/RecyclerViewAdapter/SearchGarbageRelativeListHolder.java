package com.wray2.RecyclerViewAdapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wray2.R;

public class SearchGarbageRelativeListHolder extends RecyclerView.ViewHolder {
    TextView rubbishName;
    TextView rubbishSortName;


    public SearchGarbageRelativeListHolder(@NonNull View itemView) {
        super(itemView);
        rubbishName = itemView.findViewById(R.id.relativeitem_txt);
        rubbishSortName = itemView.findViewById(R.id.relativeitem_sortname);
    }
}
