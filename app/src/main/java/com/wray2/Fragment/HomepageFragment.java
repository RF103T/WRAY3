package com.wray2.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wray2.CameraActivity;
import com.wray2.Class.Alert;
import com.wray2.FragmentsActivity;
import com.wray2.Manager.CalendarManager;
import com.wray2.R;
import com.wray2.RecyclerViewAdapter.HelpListItemTouchListener;
import com.wray2.RecyclerViewAdapter.HomepageRecyclerViewAdapter;
import com.wray2.Util.ScreenUtils;

public class HomepageFragment extends Fragment
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FragmentsActivity activity;

    private CardView cameraCard;
    private CardView searchCard;
    private RecyclerView alterList;
    private TextView alertListEmptyText;
    private HomepageRecyclerViewAdapter adapter;

    public HomepageFragment()
    {
        // Required empty public constructor
    }

    public static HomepageFragment newInstance(String param1, String param2)
    {
        HomepageFragment fragment = new HomepageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_wray_homepage, container, false);

        //初始化ScreenUtils的一些参数
        ScreenUtils.context = activity.getApplicationContext();

        //获得屏幕分辨率
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getRealSize(point);
        ScreenUtils.screenWidth = point.x;
        ScreenUtils.screenHeight = point.y;

        //初始化日程管理器
        CalendarManager.initCalendarManager(activity);

        //布局创建
        cameraCard = (CardView)view.findViewById(R.id.cardview2_homepage);
        searchCard = (CardView)view.findViewById(R.id.cardview3_homepage);
        alterList = (RecyclerView)view.findViewById(R.id.RecylerView_homepage);
        alertListEmptyText = (TextView)view.findViewById(R.id.txt_homepage_isEmpty);

        adapter = new HomepageRecyclerViewAdapter(activity, CalendarManager.calendarManager.getRealAlertList());
        alterList.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        alterList.setLayoutManager(linearLayoutManager);
        HelpListItemTouchListener helpListItemTouchListener = new HelpListItemTouchListener(activity, new HelpListItemTouchListener.OnRecyclerItemClickListener.Builder()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                activity.setTabBarPosition(3);
            }
        });
        alterList.addOnItemTouchListener(helpListItemTouchListener);

        alertListEmptyText.setOnClickListener(v ->
        {
            if (CalendarManager.calendarManager.getAlertList().size() < 1)
                activity.setTabBarPosition(3);
        });

        //跳转部分
        cameraCard.setOnClickListener(v -> activity.setTabBarPosition(2));

        searchCard.setOnClickListener(v -> activity.setTabBarPosition(1));

        return view;
    }

    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        activity = (FragmentsActivity)context;
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener)context;
        }
        else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onResume()
    {
        if (CalendarManager.calendarManager.getAlertList().size() < 1)
            alertListEmptyText.animate().alpha(0).alpha(1).setDuration(100).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationStart(Animator animation)
                {
                    super.onAnimationEnd(animation);
                    alertListEmptyText.setVisibility(View.VISIBLE);
                }
            });
        else
        {
            alertListEmptyText.setVisibility(View.GONE);
            alertListEmptyText.setAlpha(0);
        }
        if (alterList.getAdapter() != null){
            adapter.setDataList(CalendarManager.calendarManager.getRealAlertList());
            alterList.getAdapter().notifyDataSetChanged();
        }
        else{
            alterList.removeAllViews();
        }
        super.onResume();
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener
    {
        void onFragmentInteraction(Uri uri);
    }
}
