package com.wray2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wray2.Class.Alert;
import com.wray2.RecyclerViewAdapter.CalendarRecyclerViewAdapter;
import com.wray2.RecyclerViewAdapter.HelpListItemTouchListener;
import com.wray2.Util.AlertUtils;

import java.util.LinkedList;

public class CalendarActivity extends AppCompatActivity
{


    private RecyclerView calendarList;
    private CardView addCalendar;
    private static int key = 0;
    //RecylerView的数据源
    private LinkedList<Alert> Alertdatas = new LinkedList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarList = (RecyclerView)findViewById(R.id.calendar_RecycleView);
        addCalendar = (CardView)findViewById(R.id.calendar_addCardView);

        //读取列表
        Alertdatas = AlertUtils.readAlertList(CalendarActivity.this);

        //接收数据
        initList();
        HelpListItemTouchListener helpListItemTouchListener = new HelpListItemTouchListener(this, new HelpListItemTouchListener.OnRecyclerItemClickListener.Builder()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Intent intent1 = new Intent(CalendarActivity.this, SettingCalendarActivity.class);
                intent1.putExtra("alter_date", Alertdatas.get(position).getDate());
                intent1.putExtra("alter_time", Alertdatas.get(position).getStarttime());
                intent1.putExtra("alter_address", Alertdatas.get(position).getEndtime());
                intent1.putExtra("position", position);
                intent1.putExtra("key", 1);
                startActivityForResult(intent1, 1);
            }
        });
        calendarList.addOnItemTouchListener(helpListItemTouchListener);

        //todo:删除日程的选项

        //点击跳转事件
        addCalendar.setOnClickListener(v ->
        {
            Intent intent = new Intent(CalendarActivity.this, SettingCalendarActivity.class);
            intent.putExtra("key", 0);
            startActivityForResult(intent, 1);
        });
    }

    protected void onResume()
    {
        super.onResume();
        Alertdatas.clear();
        Alertdatas.addAll(AlertUtils.readAlertList(CalendarActivity.this));
        if (calendarList.getAdapter() != null)
        {
            calendarList.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
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
                AlertUtils.upDateAlertList(CalendarActivity.this, positon, getAlert);
                initList();
            }
            //添加日程
            if (resultCode == 3)
            {
                Bundle bundle = data.getExtras();
                Alert getAlert = bundle.getParcelable("Alert");
                AlertUtils.addAlterToAlterList(CalendarActivity.this, getAlert);
                initList();
            }
        }
    }

    void initList()
    {
        CalendarRecyclerViewAdapter calendarAdapter = new CalendarRecyclerViewAdapter(CalendarActivity.this, Alertdatas, calendarList);
        calendarList.setAdapter(calendarAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        calendarList.setLayoutManager(linearLayoutManager);
    }//todo:这个重新绘制的方法好像不是很好
}
