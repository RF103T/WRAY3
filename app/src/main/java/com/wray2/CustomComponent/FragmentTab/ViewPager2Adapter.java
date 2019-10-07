package com.wray2.CustomComponent.FragmentTab;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.wray2.Fragment.CalendarFragment;
import com.wray2.Fragment.CameraFragment;
import com.wray2.Fragment.HomepageFragment;
import com.wray2.Fragment.SearchFragment;
import com.wray2.Fragment.SettingFragment;

public class ViewPager2Adapter extends FragmentStateAdapter
{
    private Fragment nowFragment;

    public ViewPager2Adapter(FragmentActivity fragmentActivity)
    {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        switch (position)
        {
            case 1:
                nowFragment = SearchFragment.newInstance("1", "2");
                break;
            case 2:
                nowFragment = CameraFragment.newInstance("1", "2");
                break;
            case 3:
                nowFragment = CalendarFragment.newInstance("1", "2");
                break;
            case 4:
                nowFragment = SettingFragment.newInstance("1", "2");
                break;
            default:
                nowFragment = HomepageFragment.newInstance("1", "2");
                break;
        }
        return nowFragment;
    }

    @Override
    public int getItemCount()
    {
        return 5;
    }

    public Fragment getNowFragment()
    {
        return nowFragment;
    }
}
