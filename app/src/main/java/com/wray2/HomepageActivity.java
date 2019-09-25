package com.wray2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wray2.Class.Alert;
import com.wray2.RecyclerViewAdapter.HomepageRecyclerViewAdapter;
import com.wray2.Util.AlertUtils;
import com.wray2.RecyclerViewAdapter.HelpListItemTouchListener;

import java.util.LinkedList;

public class HomepageActivity extends AppCompatActivity {


    private CardView cameraCard;
    private CardView searchCard;
    private RecyclerView alterList;

    //RecylerView的数据源
    private LinkedList<Alert> alertDatas = new LinkedList<>();
    
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrya_homepage);

        cameraCard=(CardView)findViewById(R.id.cardview2_homepage);
        searchCard=(CardView)findViewById(R.id.cardview3_homepage);
        alterList=(RecyclerView)findViewById(R.id.RecylerView_homepage); 

        //接收链表
        alertDatas = AlertUtils.readAlertList(HomepageActivity.this);

        HomepageRecyclerViewAdapter alertListAdapter = new HomepageRecyclerViewAdapter(HomepageActivity.this, alertDatas);
        alterList.setAdapter(alertListAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        alterList.setLayoutManager(linearLayoutManager);
        HelpListItemTouchListener helpListItemTouchListener = new HelpListItemTouchListener(this,new HelpListItemTouchListener.OnRecyclerItemClickListener.Builder()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                Intent intent =new Intent(HomepageActivity.this,CalendarActivity.class);
                startActivity(intent);
            }
        });
        alterList.addOnItemTouchListener(helpListItemTouchListener);

        
        //跳转部分
        cameraCard.setOnClickListener(v -> {
            Intent intent1 = new Intent(HomepageActivity.this,CameraActivity.class);
            startActivity(intent1);
        });

        searchCard.setOnClickListener(v -> {
            Intent intent2 = new Intent(HomepageActivity.this,SearchActivity.class);
            startActivity(intent2);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        alertDatas.clear();
        alertDatas.addAll(AlertUtils.readAlertList(HomepageActivity.this));
        //如果 alertDatas = new .... 看似覆盖了其实只是指向了新的地址，而Adapter.notifyDataSetChanged()始终观察着原来的地址没有发生改变.
        if (alterList.getAdapter()!=null){
            alterList.getAdapter().notifyDataSetChanged();
        }
    }

}
