package com.wray2.RecyclerViewAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.wray2.Class.Alert;
import com.wray2.FragmentsActivity;
import com.wray2.Manager.CalendarManager;
import com.wray2.R;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class CalendarRecyclerViewItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback {
    private CalendarRecyclerViewAdapter mBookShelfAdapter;
    private FragmentsActivity fragmentsActivity;
    private CoordinatorLayout coordinatorLayout;


    //保存被删除item信息，用于撤销操作
    //这里使用队列数据结构，当连续滑动删除几个item时可能会保存多个item数据，并需要记录数据
    ArrayList<Alert> mUserBookShelfResponses = new ArrayList<Alert>();
    BlockingQueue queue = new ArrayBlockingQueue(1);
    private Object CalendarRecyclerViewAddHolder;

    public CalendarRecyclerViewItemTouchHelperCallback(int dragDirs, int swipeDirs, CalendarRecyclerViewAdapter adapter, @NonNull CoordinatorLayout coordinatorLayout){
        super(dragDirs,swipeDirs);
        this.mBookShelfAdapter = adapter;
        this.coordinatorLayout = coordinatorLayout;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder)
    {
        if(viewHolder.getAdapterPosition() < recyclerView.getAdapter().getItemCount() - 1)
            return makeMovementFlags(ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT | ItemTouchHelper.UP | ItemTouchHelper.DOWN,ItemTouchHelper.START | ItemTouchHelper.END);
        else
            return makeMovementFlags(0,0);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        //得到拖动viewholder的position
        int fromPosition = viewHolder.getAdapterPosition();
        //得到目标viewholder的position
        int toPosition = target.getAdapterPosition();
        int maxItemIndex = mBookShelfAdapter.getItemCount() - 1;
        if (fromPosition < maxItemIndex && toPosition < maxItemIndex){
            CalendarManager.calendarManager.moveAlert(fromPosition,toPosition);
            mBookShelfAdapter.notifyItemMoved(fromPosition,toPosition);
        }
        //todo:交换数据仍有问题
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        //记录将要删除item的位置
        int position = viewHolder.getAdapterPosition();
        final Alert bookShelfResponse = CalendarManager.calendarManager.getAlertList().get(position);
        //将位置放到数据中，再保存到队列中方便操作
        bookShelfResponse.setPosition(position);
        if (!queue.isEmpty()){
            queue.clear();
        }
        queue.add(bookShelfResponse);
        //滑动删除，将该item数据从集合中移除
        //被移除的数据可能还需要被撤销，已经被保存到队列中了
        CalendarManager.calendarManager.removeAlert(position);
        mBookShelfAdapter.setCalendars(CalendarManager.calendarManager.getRealAlertList());
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

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        if (!queue.isEmpty()){
            //如果队列中有数据，说明刚才有删掉一些item
             Snackbar snackbar = Snackbar.make(coordinatorLayout,"已为您删除该日程",Snackbar.LENGTH_LONG);
             snackbar.getView().setBackgroundColor(Color.parseColor("#A5D6A7"));
             snackbar.setAction("撤销删除", v -> {
                //SnackBar的撤销按钮被点击，队列中取出刚被删掉的数据，然后再添加到数据集合
                final Alert bookShelfResponse = (Alert)queue.remove();
                CalendarManager.calendarManager.reAddAlert(bookShelfResponse.getPosition(),bookShelfResponse);
                mBookShelfAdapter.setCalendars(CalendarManager.calendarManager.getRealAlertList());
                mBookShelfAdapter.notifyItemInserted(bookShelfResponse.getPosition());
                if (bookShelfResponse.getPosition()==0){
                   recyclerView.smoothScrollToPosition(0);
                }
             });
             snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                 //不撤销将做正在的删除操作，监听SnackBar消失事件，
                 //SnackBar消失（非排挤式消失）出队、访问服务器删除数据。
                 @Override
                 public void onDismissed(Snackbar transientBottomBar, int event) {
                     super.onDismissed(transientBottomBar, event);
                     //排除一种情况就是连续删除多个item SnackBar挤掉前一个SnackBar导致的
                     if (event != DISMISS_EVENT_CONSECUTIVE){
                         queue.clear();
                     }
                 }
             });
             snackbar.show();

        }
        //todo:上下移动也会提示删除，快速删除snackbar多次弹出影响美观
    }


    public boolean isLongPressDragEnabled(){
        return true;
    }
}
