package com.wray2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;


public class SearchResultActivity extends AppCompatActivity {
    private ImageView pic_garbage_sort;
    private ImageView pic_garbage;
    private ImageView pic_returnFragment;
    private TextView txt_garbage;
    private TextView txt_garbage_sortname;
    private TextView txt_garbage_sortname_detail;
    private TextView txt_garbagee_name_throw;
    private TextView getTxt_garbagee_name_throw_detail;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        pic_garbage = (ImageView)findViewById(R.id.img_search_result_pic);
        pic_garbage_sort = (ImageView)findViewById(R.id.img_garbage_sortname);
        txt_garbage = (TextView)findViewById(R.id.txt_garbage_name);
        txt_garbage_sortname = (TextView)findViewById(R.id.txt_garbage_sortname);
        txt_garbage_sortname_detail = (TextView)findViewById(R.id.txt_gatbage_sortname_introduction);
        txt_garbagee_name_throw = (TextView)findViewById(R.id.txt_garbage_sortname_throw_remand);
        getTxt_garbagee_name_throw_detail = (TextView)findViewById(R.id.txt_garbage_sortname_throw_remand_detail);
        pic_returnFragment = (ImageView)findViewById(R.id.search_result_return);
        Intent intent =getIntent();
        String garbage_name =intent.getStringExtra("str");
        txt_garbage.setText(garbage_name);

        pic_returnFragment.setOnClickListener(v->{
            finish();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        showSystemBar();
    }

    public void showSystemBar()
    {
        View decorView = this.getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        this.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }
}
