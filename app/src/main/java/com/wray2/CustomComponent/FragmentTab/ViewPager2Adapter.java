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
    public ViewPager2Adapter(FragmentActivity fragmentActivity)
    {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position)
    {
        Fragment r;
        switch (position)
        {
            case 1:
                r = SearchFragment.newInstance("1", "2");
                break;
            case 2:
                r = CameraFragment.newInstance("1", "2");
                break;
            case 3:
                r = CalendarFragment.newInstance("1", "2");
                break;
            case 4:
                r = SettingFragment.newInstance("1", "2");
                break;
            default:
                r = HomepageFragment.newInstance("1", "2");
                break;
        }
        return r;
    }

    @Override
    public int getItemCount()
    {
        return 5;
    }
}
