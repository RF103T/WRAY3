package com.wray2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.wray2.Class.Alert;

import java.util.Calendar;
import java.util.Locale;

import static com.wray2.R.drawable.ic_large;
import static com.wray2.R.drawable.ic_large_choose;
import static com.wray2.R.drawable.ic_small;
import static com.wray2.R.drawable.ic_small_choose;

public class SettingCalendarActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    private TextView endTimeText, startTimeText;
    private Button affirm;
    //private EditText addressText;
    private static int key;
    private static int position;
    private CoordinatorLayout coordinatorLayout;
    private Button sunday,monday,tuesday,wednesday,thursday,friday,saturday;
    private ImageButton recycle,dry,wet,harmful;
    private int[] dates = new int[]{0,0,0,0,0,0,0};
    private int[] rubbishSorts = new int[]{0,0,0,0};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_calendar);
        showSystemBar();

        endTimeText = (TextView)findViewById(R.id.txt_setting_of_end);
        startTimeText = (TextView)findViewById(R.id.txt_setting_of_start);
        //addressText = (EditText)findViewById(R.id.edittxt_setAddress);
        affirm = (Button)findViewById(R.id.but_setting_affirm);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator_setCalendar);
        sunday = (Button)findViewById(R.id.btn_setting_date_sunday);
        monday = (Button)findViewById(R.id.btn_setting_date_monday);
        tuesday = (Button)findViewById(R.id.btn_setting_date_tuesday);
        wednesday = (Button)findViewById(R.id.btn_setting_date_wednesday);
        thursday = (Button)findViewById(R.id.btn_setting_date_thursday);
        friday = (Button)findViewById(R.id.btn_setting_date_friday);
        saturday = (Button)findViewById(R.id.btn_setting_date_saturday);
        recycle = (ImageButton) findViewById(R.id.img_sort_recycle);
        dry = (ImageButton) findViewById(R.id.img_sort_dry);
        wet = (ImageButton) findViewById(R.id.img_sort_wet);
        harmful = (ImageButton) findViewById(R.id.img_sort_harmful);



        endTimeText.setOnClickListener(this);
        startTimeText.setOnClickListener(this);
        sunday.setOnClickListener(this);
        tuesday.setOnClickListener(this);
        wednesday.setOnClickListener(this);
        thursday.setOnClickListener(this);
        friday.setOnClickListener(this);
        saturday.setOnClickListener(this);
        monday.setOnClickListener(this);
        recycle.setOnClickListener(this);
        dry.setOnClickListener(this);
        wet.setOnClickListener(this);
        harmful.setOnClickListener(this);



        Intent intent = getIntent();
        key = intent.getIntExtra("key", 0);
        position = intent.getIntExtra("position", 0);
        if (key == 1)
        {
            dates = intent.getIntArrayExtra("alter_dates");
            rubbishSorts = intent.getIntArrayExtra("alter_sorts");
            String get_starttime = intent.getStringExtra("alter_starttime");
            String get_endtime = intent.getStringExtra("alter_endtime");
            endTimeText.setText(get_endtime);
            startTimeText.setText(get_starttime);
            initDates(dates);
            initSorts(rubbishSorts);
            //addressText.setText(get_address);
        }
        else if (key == 0)
        {
            Calendar calendar = Calendar.getInstance();
            endTimeText.setText(String.format(Locale.CHINA, "%d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
            startTimeText.setText(String.format(Locale.CHINA, "%d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        }

        affirm.setOnClickListener(v ->
        {
            if (endTimeText.getText().toString().isEmpty() || startTimeText.getText().toString().isEmpty() || datesIsEmpty() || sortIsEmpty())
            {
                Snackbar.make(coordinatorLayout, "请填写完整的信息噢~", Snackbar.LENGTH_SHORT).show();
            }
            else
            {
                Intent intent1 = new Intent();
                Alert alert = new Alert(startTimeText.getText().toString(), endTimeText.getText().toString(),dates,rubbishSorts);
                Bundle bundle = new Bundle();
                bundle.putParcelable("Alert", alert);
                bundle.putInt("position", position);
                intent1.putExtras(bundle);
                setResult(key == 1 ? 2 : 3, intent1);
                bundle.clear();
                finish();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.txt_setting_of_start)
        {
            Calendar calendar = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
            TimePickerDialog startDialog = new TimePickerDialog(this, R.style.dialog_date, (view, hourOfDay, minute) -> {
                String desc = String.format(Locale.CHINA, "%d:%02d", hourOfDay, minute);
                startTimeText.setText(desc);
            },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), true);
            startDialog.show();
        }
        else if (id == R.id.txt_setting_of_end)
        {
            Calendar calendar = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
            TimePickerDialog endDialog = new TimePickerDialog(this, R.style.dialog_date, this,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), true);
            endDialog.show();
        }
        else if (id == R.id.btn_setting_date_sunday){
            dateClicklisener(sunday,0);
        }
        else if (id == R.id.btn_setting_date_monday){
            dateClicklisener(monday,1);
        }
        else if (id == R.id.btn_setting_date_tuesday){
            dateClicklisener(tuesday,2);
        }
        else if (id == R.id.btn_setting_date_wednesday){
            dateClicklisener(wednesday,3);
        }
        else if (id == R.id.btn_setting_date_thursday){
            dateClicklisener(thursday,4);
        }
        else if (id == R.id.btn_setting_date_friday){
            dateClicklisener(friday,5);
        }
        else if (id == R.id.btn_setting_date_saturday){
            dateClicklisener(saturday,6);
        }
        else if (id == R.id.img_sort_recycle){
            sortClickLisener(recycle,0);
        }
        else if (id == R.id.img_sort_dry){
            sortClickLisener(dry,1);
        }
        else if (id == R.id.img_sort_wet){
            sortClickLisener(wet,2);
        }
        else if (id == R.id.img_sort_harmful){
            sortClickLisener(harmful,3);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        showSystemBar();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
    {
        String desc = String.format(Locale.CHINA, "%d年%d月%d日", year, month + 1, dayOfMonth);
        endTimeText.setText(desc);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
            String desc = String.format(Locale.CHINA, "%d:%02d", hourOfDay, minute);
            endTimeText.setText(desc);
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

    public void dateClicklisener(Button button, int i){
        if (dates[i] == 0){
            button.setBackground(this.getDrawable(ic_small_choose));
            button.setTextColor(Color.WHITE);
            dates[i] = 1;
        }else {
            button.setBackground(this.getDrawable(ic_small));
            button.setTextColor(this.getColor(R.color.colortxtgreen));
            dates[i] = 0;
        }
    }

    public void sortClickLisener(ImageButton imageButton, int i){
        if (rubbishSorts[i] == 0){
            imageButton.setBackground(this.getDrawable(ic_large_choose));
            rubbishSorts[i] = 1;
        }else {
            imageButton.setBackground(this.getDrawable(ic_large));
            rubbishSorts[i] = 0;
        }
    }

    public boolean datesIsEmpty(){
        for (int i = 0 ;i<7;i++){
            if (dates[i] != 0){
                return false;
            }
        }
        return true;
    }

    public boolean sortIsEmpty(){
        for (int i = 0; i < 4 ; i++){
            if (rubbishSorts[i] !=0){}{
                return false;
            }
        }
        return true;
    }

    public void initDates(int[] dates){
        Button[] datebuttons = new Button[]{sunday,monday,tuesday,wednesday,thursday,friday,saturday};
        for (int i =0; i<7; i++){
            if (dates[i] == 1){
                datebuttons[i].setBackground(this.getDrawable(ic_small_choose));
                datebuttons[i].setTextColor(Color.WHITE);
            }
        }
    }

    public void initSorts(int[] rubbishSorts){
        ImageButton[] rubbishImageViews = new ImageButton[]{recycle,dry,wet,harmful};
        for (int i = 0; i<4;i++){
            if (rubbishSorts[i] == 1){
                rubbishImageViews[i].setBackground(this.getDrawable(ic_large_choose));
            }
        }
    }
}


