package com.wray2.RecyclerViewAdapter;

import android.content.Context;
import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.wray2.Class.Alert;
import com.wray2.FragmentsActivity;
import com.wray2.Manager.CalendarManager;


import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class CalendarRecyclerViewItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback {
    private RecyclerView.Adapter mBookShelfAdapter;
    private FragmentsActivity fragmentsActivity;

    //保存被删除item信息，用于撤销操作
    //这里使用队列数据结构，当连续滑动删除几个item时可能会保存多个item数据，并需要记录数据
    BlockingQueue queue = new ArrayBlockingQueue(3);

    public CalendarRecyclerViewItemTouchHelperCallback(int dragDirs, int swipeDirs,RecyclerView.Adapter adapter){
        super(dragDirs,swipeDirs);
        this.mBookShelfAdapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        //得到拖动viewholder的position
        int fromPosition = viewHolder.getAdapterPosition();
        //得到目标viewholder的position
        int toPosition = target.getAdapterPosition();
        CalendarManager.calendarManager.moveAlert(fromPosition,toPosition);
        mBookShelfAdapter.notifyItemMoved(fromPosition,toPosition);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        //记录将要删除item的位置
        int position = viewHolder.getAdapterPosition();
        final Alert bookShelfResponse = CalendarManager.calendarManager.getAlertList().get(position);
        //将位置放到数据中，再保存到队列中方便操作
        queue.add(bookShelfResponse);
        //滑动删除，将该item数据从集合中移除
        //被移除的数据可能还需要被撤销，已经被保存到队列中了
        CalendarManager.calendarManager.removeAlert(position);
        mBookShelfAdapter.notifyItemRemoved(position);
    }

    public void onChildDraw(Canvas c,RecyclerView recyclerView,RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            //滑动时改变Item的透明度，以实现滑动过程中实现渐变效果
            final float alpha = 1 - Math.abs(dX) / (float) viewHolder.itemView.getWidth();
            viewHolder.itemView.setAlpha(alpha);
            viewHolder.itemView.setTranslationX(dX);
        }else {
            super.onChildDraw(c,recyclerView,viewHolder,dX,dY,actionState,isCurrentlyActive);
        }
    }




    public boolean isLongPressDragEnabled(){
        return true;
    }
}
