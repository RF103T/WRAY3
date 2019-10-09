package com.wray2.RecyclerViewAdapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerViewItemTouchListener implements RecyclerView.OnItemTouchListener {

    private GestureDetector mGestureDetector;

    private View selectedView;
    private int selectedPosition;
    private boolean longPressed = false;

    public RecyclerViewItemTouchListener(Context context, final OnRecyclerItemClickListener listener){
        mGestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e){
                if (selectedView != null && listener != null){
                    listener.onItemClick(selectedView,selectedPosition);
                    return  true;
                }
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e){
                longPressed = true;
                if (selectedView!=null&&listener != null)
                    listener.onItemLongClick(selectedView,selectedPosition);
            }
        });
    }

    public boolean isLongPressed(){ return  longPressed;}

    @Override
    public  boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e){
        if (e.getAction()==MotionEvent.ACTION_UP)
            longPressed = false;
        View childView = recyclerView.findChildViewUnder(e.getX(),e.getY());
        if (childView == null)
            return false;
        selectedView = childView;
        selectedPosition = recyclerView.getChildAdapterPosition(childView);
        return mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView,MotionEvent event){

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }

    public interface  OnRecyclerItemClickListener{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
        class Builder implements OnRecyclerItemClickListener{

            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }
    }
}
