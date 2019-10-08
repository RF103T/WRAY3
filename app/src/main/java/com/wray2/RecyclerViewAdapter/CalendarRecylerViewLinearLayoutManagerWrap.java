package com.wray2.RecyclerViewAdapter;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CalendarRecylerViewLinearLayoutManagerWrap extends LinearLayoutManager {
    public CalendarRecylerViewLinearLayoutManagerWrap(Context context) {
        super(context);
    }

    public CalendarRecylerViewLinearLayoutManagerWrap(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public CalendarRecylerViewLinearLayoutManagerWrap(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

}
