package com.wray2.CustomComponent.FragmentTab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.wray2.Interface.FragmentPagerSwitchListener;
import com.wray2.R;
import com.wray2.Util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

public class MyTabBar implements View.OnClickListener
{
    private FragmentActivity activity;

    private FragmentPagerSwitchListener switchCallback = index ->
    {
    };

    //TabBar中包含的组件
    private List<ImageView> imageViewList = new ArrayList<ImageView>(10);
    private List<TextView> textViewList = new ArrayList<TextView>(10);

    //相邻TabBar组件之间的切换动画
    private List<AnimatorSet> pageScrollingTabBarAnimators = new ArrayList<AnimatorSet>();

    //管理Fragment滚动
    private ViewPager2 viewPager;

    private ViewPager2Adapter adapter;

    private int textViewMaxWidth = 0;//TabBar中文本的最大宽度
    private int imageDistance;//TabBar的Item中图像和文字的距离

    private int moveDistance;//每个Item移动的距离

    private int nowSelectedIndex = 0;//当前选择的页面
    private int lastSelectedIndex = 0;//上次选择的页面

    private int lastCallBackIndex = -1;//上次回调后的页面

    private int lastPosition = 0;//上次滚过的页面

    private boolean isAnimationPlaying = false;//指示点击切换动画是否在播放
    private boolean isTabBarInit = false;//指示TabBar是否已经初始化
    private boolean isPageSelectedScrolling = false;//指示Page页切换后是否还在滑动
    private boolean isPageScrolling = false;//指示Page页是否正在被滑动切换
    private boolean isFakeDrag = false;//指示Page页是否正在假滑

    public MyTabBar(FragmentActivity activity)
    {
        this.activity = activity;
        imageViewList.add((ImageView)activity.findViewById(R.id.tabBar_item1_image));
        imageViewList.add((ImageView)activity.findViewById(R.id.tabBar_item2_image));
        imageViewList.add((ImageView)activity.findViewById(R.id.tabBar_item3_image));
        imageViewList.add((ImageView)activity.findViewById(R.id.tabBar_item4_image));
        imageViewList.add((ImageView)activity.findViewById(R.id.tabBar_item5_image));
        textViewList.add((TextView)activity.findViewById(R.id.tabBar_item1_text));
        textViewList.add((TextView)activity.findViewById(R.id.tabBar_item2_text));
        textViewList.add((TextView)activity.findViewById(R.id.tabBar_item3_text));
        textViewList.add((TextView)activity.findViewById(R.id.tabBar_item4_text));
        textViewList.add((TextView)activity.findViewById(R.id.tabBar_item5_text));

        adapter = new ViewPager2Adapter(activity);
        viewPager = activity.findViewById(R.id.tabBar_viewPager);
        viewPager.setAdapter(adapter);

        //ViewPager2和TabBar的联动切换效果
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback()
        {
            //todo:ViewPager2似乎有bug，有些情况下调用了onPageScrollStateChanged以后还会调用一次onPageScrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (!isPageSelectedScrolling && isTabBarInit)
                {
                    isPageScrolling = true;
                    if (position >= 0 && position < 4)
                        pageScrollingTabBarAnimators.get(position).setCurrentPlayTime((long)(300 * positionOffset));
                    if (position != lastPosition)
                    {
                        AnimatorSet set;
                        if (position > lastPosition)
                        {
                            set = pageScrollingTabBarAnimators.get(lastPosition);
                            if (set.getCurrentPlayTime() != 300)
                                set.setCurrentPlayTime((long)300);
                        }
                        lastPosition = position;
                    }
                }
            }

            @Override
            public void onPageSelected(int position)
            {
                super.onPageSelected(position);
                if (isTabBarInit)
                {
                    lastSelectedIndex = nowSelectedIndex;
                    nowSelectedIndex = position;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
                super.onPageScrollStateChanged(state);
                if (isTabBarInit)
                {
                    if (isAnimationPlaying && state == 2)
                    {
                        isPageSelectedScrolling = true;
                        viewPager.setUserInputEnabled(false);
                    }
                    if (state == 0)
                    {
                        //动画结束回调
                        //判断是为了防止在首尾页无法滑动时重复回调
                        if (lastCallBackIndex != nowSelectedIndex)
                        {
                            switchCallback.onPagerSwitch(nowSelectedIndex);
                            lastCallBackIndex = nowSelectedIndex;
                        }
                        if (isPageSelectedScrolling)
                        {
                            isPageSelectedScrolling = false;
                            viewPager.setUserInputEnabled(true);
                        }
                        isPageScrolling = false;
                    }
                }
            }
        });

        for (ImageView view : imageViewList)
            view.setOnClickListener(this);

        //界面初始化
        for (int i = 1; i < 5; i++)
            textViewList.get(i).setAlpha(0);
    }

    public boolean isTabBarInit()
    {
        return isTabBarInit;
    }

    public boolean isViewPagerFakeDrag()
    {
        return isFakeDrag || viewPager.isFakeDragging();
    }

    public int getNowSelectedFragmentIndex()
    {
        return nowSelectedIndex;
    }

    public Fragment getNowSelectedFragment()
    {
        return adapter.getNowFragment();
    }

    public void setSwitchListener(FragmentPagerSwitchListener callback)
    {
        this.switchCallback = callback;
    }

    //直接设置Position，如果设置到预加载页面之外，TabBar动画会出错
    public void setCurrentPosition(int position)
    {
        viewPager.setCurrentItem(position);
    }

    //循环递增减设置Position，避免setCurrentPosition(position:int):void的问题
    public void fakeDragToPosition(int position)
    {
        isFakeDrag = true;
        int start = viewPager.getCurrentItem();
        if (start > position)
            for (int i = start - 1; i >= position; i--)
                viewPager.setCurrentItem(i);
        else
            for (int i = start + 1; i <= position; i++)
                viewPager.setCurrentItem(i);
        isFakeDrag = false;
    }

    public void initTabBar()
    {
        if (!isTabBarInit)
        {
            //算出元素之间的距离
            //imageDistance = imageViewList.get(2).getLeft() - imageViewList.get(1).getRight();
            imageDistance = textViewList.get(0).getLeft() - imageViewList.get(0).getRight() + (int)textViewList.get(0).getTranslationX();
            //使用元素距离改变其余文本框位置，并获取文本框宽度
            for (int i = 0; i < 5; i++)
            {
                if (i > 0)
                    textViewList.get(i).setTranslationX(imageDistance);
                if (textViewMaxWidth < textViewList.get(i).getWidth())
                    textViewMaxWidth = textViewList.get(i).getWidth();
            }
            //初始化常用动画
            initAnimatorList();
            isTabBarInit = true;
        }
    }

    private void initAnimatorList()
    {
        moveDistance = textViewMaxWidth + imageDistance;
        for (int i = 0; i < 4; i++)
        {
            AnimatorSet set = new AnimatorSet();
            ObjectAnimator textHide = ObjectAnimator.ofFloat(textViewList.get(i), "Alpha", 1, 0).setDuration(100);
            ObjectAnimator textShow = ObjectAnimator.ofFloat(textViewList.get(i + 1), "Alpha", 0, 1).setDuration(100);
            set.play(textHide);
            TextView textView = textViewList.get(i + 1);
            ObjectAnimator textItemMove = ObjectAnimator.ofFloat(textView, "TranslationX", imageDistance, -moveDistance + imageDistance);
            textItemMove.setDuration(300);
            ImageView imageView = imageViewList.get(i + 1);
            ObjectAnimator imageItemMove = ObjectAnimator.ofFloat(imageView, "TranslationX", 0, -moveDistance);
            imageItemMove.setDuration(300);
            set.play(textItemMove).with(imageItemMove);
            set.play(textShow).after(100);
            pageScrollingTabBarAnimators.add(set);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (!isAnimationPlaying)//&& !isPageScrolling
        {
            lastSelectedIndex = nowSelectedIndex;
            switch (v.getId())
            {
                case R.id.tabBar_item1_image:
                case R.id.tabBar_item1_text:
                {
                    nowSelectedIndex = 0;
                    break;
                }
                case R.id.tabBar_item2_image:
                case R.id.tabBar_item2_text:
                {
                    nowSelectedIndex = 1;
                    break;
                }
                case R.id.tabBar_item3_image:
                case R.id.tabBar_item3_text:
                {
                    nowSelectedIndex = 2;
                    break;
                }
                case R.id.tabBar_item4_image:
                case R.id.tabBar_item4_text:
                {
                    nowSelectedIndex = 3;
                    break;
                }
                case R.id.tabBar_item5_image:
                case R.id.tabBar_item5_text:
                {
                    nowSelectedIndex = 4;
                    break;
                }
            }
            if (!(lastSelectedIndex == nowSelectedIndex))
                clickAnimation(lastSelectedIndex, nowSelectedIndex);
        }
    }

    public void fakeClick(int index)
    {
        clickAnimation(viewPager.getCurrentItem(), index);
    }

    private void clickAnimation(int lastSelectedIndex, int nowSelectedIndex)
    {
        //动画实现
        AnimatorSet set = new AnimatorSet();
        //切换文本显示
        ObjectAnimator textShow = ObjectAnimator.ofFloat(textViewList.get(nowSelectedIndex), "Alpha", 0, 1).setDuration(100);
        ObjectAnimator textHide = ObjectAnimator.ofFloat(textViewList.get(lastSelectedIndex), "Alpha", 1, 0).setDuration(100);
        set.play(textHide);
        //不同的方向对元素的处理方式不同
        if (lastSelectedIndex < nowSelectedIndex)
        {
            for (int i = lastSelectedIndex + 1; i <= nowSelectedIndex; i++)
            {
                TextView textView = textViewList.get(i);
                float textViewTranslationX = textView.getTranslationX();
                ObjectAnimator textItemMove = ObjectAnimator.ofFloat(textView, "TranslationX", textViewTranslationX, textViewTranslationX - moveDistance);
                textItemMove.setDuration(300);

                ImageView imageView = imageViewList.get(i);
                float imageViewTranslationX = imageView.getTranslationX();
                ObjectAnimator imageItemMove = ObjectAnimator.ofFloat(imageView, "TranslationX", imageViewTranslationX, imageViewTranslationX - moveDistance);
                imageItemMove.setDuration(300);

                set.play(textItemMove).with(imageItemMove);
            }
        }
        else
        {
            for (int i = nowSelectedIndex + 1; i <= lastSelectedIndex; i++)
            {
                TextView textView = textViewList.get(i);
                float textViewTranslationX = textView.getTranslationX();
                ObjectAnimator textItemMove = ObjectAnimator.ofFloat(textView, "TranslationX", textViewTranslationX, textViewTranslationX + moveDistance);
                textItemMove.setDuration(300);

                ImageView imageView = imageViewList.get(i);
                float imageViewTranslationX = imageView.getTranslationX();
                ObjectAnimator imageItemMove = ObjectAnimator.ofFloat(imageView, "TranslationX", imageViewTranslationX, imageViewTranslationX + moveDistance);
                imageItemMove.setDuration(300);

                set.play(textItemMove).with(imageItemMove);
            }
        }
        set.play(textShow).after(100);

        set.addListener(new AnimatorListenerAdapter()
        {
            @Override
            public void onAnimationEnd(Animator animation)
            {
                isAnimationPlaying = false;
                super.onAnimationEnd(animation);
            }
        });

        isAnimationPlaying = true;
        set.start();
        //viewPager.setCurrentItem(nowSelectedIndex);
        fakeDragToPosition(nowSelectedIndex);
        lastPosition = nowSelectedIndex;
    }
}
