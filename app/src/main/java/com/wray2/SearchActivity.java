package com.wray2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wray2.CustomComponent.FlowLayout;
import com.wray2.Util.mValsUtils;

import java.util.LinkedList;

public class SearchActivity extends AppCompatActivity {
    private LinkedList<String> mVals = new LinkedList<>();
    private LayoutInflater mInflater;
    private FlowLayout mFlowLayout;
    private EditText searchGarbage;
    private ImageView search;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_garbage);
        mInflater = LayoutInflater.from(this);
        mFlowLayout = (FlowLayout) findViewById(R.id.flowlayout_history);
        searchGarbage = (EditText) findViewById(R.id.editxt_search_garbage);
        search =(ImageView)findViewById(R.id.img_card_search);
        initData();
        search.setOnClickListener(v ->  {
                if (!searchGarbage.getText().toString().isEmpty() ){ //不可以getText != null,返回类型是Editable
                    String str = searchGarbage.getText().toString();
                    Intent intent1 = new Intent(SearchActivity.this,SearchResultActivity.class);
                    str = searchGarbage.getText().toString();
                    intent1.putExtra("str",str);
                    startActivity(intent1);
                    new mValsUtils().upDatemValsList(SearchActivity.this,str);
                    initData();
                }else searchGarbage.setHint("查询名称不可以为空哦~");
                //todo：有BUG，跳转速度太慢导致看上去是先更新了布局再跳转
                //todo: 将Vmals[]动态存储起来
        });

    }

    public void initData(){
        mFlowLayout.removeAllViews();
        searchGarbage.setText("");
        mVals = new mValsUtils().readmValsList(SearchActivity.this);
        for (int i = 0; i<mVals.size();i++){
            TextView tv = (TextView) mInflater.inflate(R.layout.searched_lable_tv,mFlowLayout,false);
            tv.setText(mVals.get(i));
            //点击事件
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = tv.getText().toString();
                    Intent intent = new Intent(SearchActivity.this,SearchResultActivity.class);
                    intent.putExtra("str",str);
                    startActivity(intent);
                }
            });
            mFlowLayout.addView(tv);//添加到父View
        }
    }

    public boolean isRename(String str,String[] mVals){
        for (int i = 0; i < mVals.length;i++){
            if (str.equals(mVals[i])){
                return true;
            }
        }
        return false;
    }
}
