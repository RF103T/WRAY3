package com.wray2.RecyclerViewAdapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wray2.R;

public class SearchGarbageRelativeListHolder extends RecyclerView.ViewHolder {
    TextView rubbishId;
    TextView rubbishsortId;


    public SearchGarbageRelativeListHolder(@NonNull View itemView) {
        super(itemView);
        rubbishId = itemView.findViewById(R.id.relativeitem_txt);
        rubbishsortId = itemView.findViewById(R.id.relativeitem_sortname);
    }
}
