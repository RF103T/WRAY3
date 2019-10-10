package com.wray2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.wray2.Class.Rubbish;
import com.wray2.Thread.JsonDataObjects.ErrorData;
import com.wray2.Thread.SearchResultRunnable;

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
    private Rubbish rubbishInfo;
    private Bitmap resultpic;

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
        rubbishInfo =(Rubbish)getIntent().getParcelableExtra("rubbishInfo");
        @SuppressLint("HandlerLeak")
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (msg.what ==1){
                    Bundle bundle = msg.getData();
                    resultpic = bundle.getParcelable("feedback_pic");
                    updateUI();
                }
                else if (msg.what == -1){
                    Bundle bundle = msg.getData();
                    ErrorData errorData = bundle.getParcelable("error_data");
                    //Intent intent1 = new Intent(this,)
                }
            }
        };
        SearchResultRunnable searchResultRunnable = new SearchResultRunnable(rubbishInfo.getRubbishId(),handler);
        Thread thread = new Thread(searchResultRunnable);
        thread.start();


        pic_returnFragment.setOnClickListener(v->{
            finish();
        });



    }

    private void updateUI() {
        txt_garbage.setText(rubbishInfo.getRubbishName());
        pic_garbage.setImageBitmap(resultpic);
        switch (rubbishInfo.getRubbishSortNum()){
            case 0:
                txt_garbage_sortname.setText(this.getResources().getString(R.string.recycleName));
                txt_garbage_sortname_detail.setText(this.getResources().getText(R.string.recyclables_info));
                txt_garbagee_name_throw.setText(this.getResources().getText(R.string.recyclables_guide));
                getTxt_garbagee_name_throw_detail.setText(this.getResources().getText(R.string.recyclables_throw_demand));
                break;
            case 1:
                //drawablePicId = R.drawable.pic_dry_waste;
                txt_garbage_sortname.setText(this.getResources().getString(R.string.dryRubbishname));
                txt_garbage_sortname_detail.setText(this.getResources().getText(R.string.dryWaste_info));
                txt_garbagee_name_throw.setText(this.getResources().getText(R.string.dryWaste_guide));
                getTxt_garbagee_name_throw_detail.setText(this.getResources().getText(R.string.dryWaste_throw_demand));
                break;
            case 2:
                //drawablePicId = R.drawable.pic_wet_waste;
                txt_garbage_sortname.setText(this.getResources().getString(R.string.wetRubbishname));
                txt_garbage_sortname_detail.setText(this.getResources().getText(R.string.wetWaste_info));
                txt_garbagee_name_throw.setText(this.getResources().getText(R.string.wetWaste_guide));
                getTxt_garbagee_name_throw_detail.setText(this.getResources().getText(R.string.wetWaste_throw_demand));
                break;
            case 3:
                //drawablePicId = R.drawable.pic_harmful_waste;
                txt_garbage_sortname.setText(this.getResources().getString(R.string.harmfulRubbishname));
                txt_garbage_sortname_detail.setText(this.getResources().getText(R.string.harmful_waste_info));
                txt_garbagee_name_throw.setText(this.getResources().getText(R.string.harmful_waste_guide));
                getTxt_garbagee_name_throw_detail.setText(this.getResources().getText(R.string.harmful_waste_throw_demand));
                break;
        }
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
