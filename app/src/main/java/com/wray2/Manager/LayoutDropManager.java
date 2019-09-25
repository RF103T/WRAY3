package com.wray2.Manager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.wray2.Util.ScreenUtils;

public class LayoutDropManager
{
    private ConstraintLayout layout;

    private ViewTreeObserver globalObserver;

    private int minHeight = ScreenUtils.dp2Px(55);//px
    private int maxHeight = 0;//px

    private int mLastY;

    @SuppressLint("ClickableViewAccessibility")
    public LayoutDropManager(final ConstraintLayout layout)
    {
        this.layout = layout;

        globalObserver = layout.getViewTreeObserver();
        globalObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                //绘图后获取高度
                maxHeight = LayoutDropManager.this.layout.getHeight();
            }
        });
        //监听触摸事件
        layout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                //gestureDetector.onTouchEvent(event);
                int y = (int)event.getY();
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                    {
                        mLastY = y;
                        break;
                    }
                    case MotionEvent.ACTION_MOVE:
                    {
                        int offsetY;
                        int layoutShowHeight = layout.getRootView().getHeight() - layout.getTop();
                        offsetY = y - mLastY;
                        //未到边界，到下边界往上拉，到上边界往下拉
                        if ((layoutShowHeight > minHeight && layoutShowHeight < maxHeight) ||
                                (layoutShowHeight <= minHeight && offsetY <= 0) ||
                                (layoutShowHeight >= maxHeight && offsetY >= 0))
                        {
                            //快到边界的时候限制偏移量
                            if (layoutShowHeight - offsetY >= maxHeight)
                                offsetY = layoutShowHeight - maxHeight;
                            if (layoutShowHeight - offsetY <= minHeight)
                                offsetY = layoutShowHeight - minHeight;
                            layout.offsetTopAndBottom(offsetY);
                        }
                        else
                            layout.offsetTopAndBottom(0);
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        int layoutShowHeight = layout.getRootView().getHeight() - layout.getTop();
                        if ((layoutShowHeight > minHeight && layoutShowHeight < maxHeight))
                        {
                            if (layoutShowHeight < maxHeight / 2)
                                layout.animate().translationY(layoutShowHeight - minHeight).setDuration(500).setListener(new AnimatorListenerAdapter()
                                {
                                    @Override
                                    public void onAnimationEnd(Animator animation)
                                    {
                                        //属性动画修改的是TranslationY，重绘Layout靠的是Top和Bottom，所以需要重置
                                        super.onAnimationEnd(animation);
                                        layout.setTop(layout.getRootView().getHeight() - minHeight);
                                        layout.setBottom(layout.getTop() + maxHeight);
                                        layout.setTranslationY(0);
                                    }
                                });//向最小界面收拢
                            else
                                layout.animate().translationY(layoutShowHeight - maxHeight).setDuration(500).setListener(new AnimatorListenerAdapter()
                                {
                                    @Override
                                    public void onAnimationEnd(Animator animation)
                                    {
                                        super.onAnimationEnd(animation);
                                        layout.setTop(layout.getRootView().getHeight() - maxHeight);
                                        layout.setBottom(layout.getRootView().getHeight());
                                        layout.setTranslationY(0);
                                    }
                                });//向最大界面收拢
                        }
                        break;
                    }
                }
                return true;
            }
        });
    }

    public void setMinHeight(int height)
    {
        minHeight = height;
    }

    public int getMinHeight()
    {
        return minHeight;
    }

    public int getMaxHeight()
    {
        return maxHeight;
    }
}

