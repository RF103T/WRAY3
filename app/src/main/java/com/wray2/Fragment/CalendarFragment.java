package com.wray2.Fragment;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wray2.Class.Alert;
import com.wray2.FragmentsActivity;
import com.wray2.Manager.CalendarManager;
import com.wray2.R;
import com.wray2.RecyclerViewAdapter.CalendarRecyclerViewAdapter;
import com.wray2.RecyclerViewAdapter.CalendarRecyclerViewItemTouchHelperCallback;
import com.wray2.RecyclerViewAdapter.CalendarRecylerViewLinearLayoutManagerWrap;
import com.wray2.RecyclerViewAdapter.HelpListItemTouchListener;
import com.wray2.SettingCalendarActivity;

public class CalendarFragment extends Fragment
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FragmentsActivity activity;

    private RecyclerView calendarList;
    private CalendarRecyclerViewAdapter calendarAdapter;
    private CoordinatorLayout coordinatorLayout;

    private CardView addCalendar;

    public CalendarFragment()
    {
        // Required empty public constructor
    }

    public static CalendarFragment newInstance(String param1, String param2)
    {
        CalendarFragment fragment = new CalendarFragment();
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
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        //布局创建
        calendarList = (RecyclerView)view.findViewById(R.id.calendar_RecycleView);
        addCalendar = (CardView)view.findViewById(R.id.calendar_addCardView);
        coordinatorLayout = (CoordinatorLayout)view.findViewById(R.id.calendar_coordinatorLayout);

        calendarAdapter = new CalendarRecyclerViewAdapter(activity, CalendarManager.calendarManager.getRealAlertList(), calendarList);
        calendarList.setAdapter(calendarAdapter);
        CalendarRecylerViewLinearLayoutManagerWrap linearLayoutManager = new CalendarRecylerViewLinearLayoutManagerWrap(activity, LinearLayoutManager.VERTICAL, false);
        calendarList.setLayoutManager(linearLayoutManager);

        HelpListItemTouchListener helpListItemTouchListener = new HelpListItemTouchListener(activity, new HelpListItemTouchListener.OnRecyclerItemClickListener.Builder()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Intent intent1 = new Intent(activity, SettingCalendarActivity.class);
                if (calendarAdapter.getItemViewType(position) == 1)
                {
                    startActivityForResult(intent1, 1);
                }
                else
                {
                    intent1.putExtra("alter_dates", CalendarManager.calendarManager.getRealAlertList().get(position).getDates());
                    intent1.putExtra("alter_starttime", CalendarManager.calendarManager.getRealAlertList().get(position).getStarttime());
                    intent1.putExtra("alter_endtime", CalendarManager.calendarManager.getRealAlertList().get(position).getEndtime());
                    intent1.putExtra("alter_sorts",CalendarManager.calendarManager.getRealAlertList().get(position).getSorts());
                    intent1.putExtra("position", position);
                    intent1.putExtra("key", 1);
                    Pair<View, String> pair = Pair.create(((View)view.findViewById(R.id.calendarItemCardView)), "constraintLayout");
                    startActivityForResult(intent1, 1, ActivityOptions.makeSceneTransitionAnimation(activity, pair).toBundle());
                }
            }
        });
        calendarList.addOnItemTouchListener(helpListItemTouchListener);
        CalendarRecyclerViewItemTouchHelperCallback calendarRecyclerViewItemTouchHelperCallback = new CalendarRecyclerViewItemTouchHelperCallback(ItemTouchHelper.UP |ItemTouchHelper.DOWN,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT,calendarAdapter,coordinatorLayout);
        ItemTouchHelper touchHelper = new ItemTouchHelper(calendarRecyclerViewItemTouchHelperCallback);
        touchHelper.attachToRecyclerView(calendarList);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
        {
            //修改日程
            if (resultCode == 2)
            {
                Bundle bundle = data.getExtras();
                Alert getAlert = bundle.getParcelable("Alert");
                int positon = bundle.getInt("position");
                CalendarManager.calendarManager.setAlert(positon, getAlert);
                calendarAdapter.setCalendars(CalendarManager.calendarManager.getRealAlertList());
            }
            //添加日程
            if (resultCode == 3)
            {
                Bundle bundle = data.getExtras();
                Alert getAlert = bundle.getParcelable("Alert");
                CalendarManager.calendarManager.addAlert(getAlert);
                calendarAdapter.setCalendars(CalendarManager.calendarManager.getRealAlertList());
            }
            if (calendarList.getAdapter() != null){
                calendarList.getAdapter().notifyDataSetChanged();
            }
        }
    }

    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
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
