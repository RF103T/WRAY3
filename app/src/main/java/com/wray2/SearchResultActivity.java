package com.wray2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;


public class SearchResultActivity extends AppCompatActivity {
    private ImageView pic_garbage_sort;
    private ImageView pic_garbage;
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

        Intent intent =getIntent();
        String garbage_name =intent.getStringExtra("str");
        txt_garbage.setText(garbage_name);


    }
}
