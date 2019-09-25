package com.wray2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.wray2.Class.Alert;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SettingCalendarActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    private TextView dateText, timeText;
    private Button affirm;
    private EditText addressText;
    private static int key;
    private static int position;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_calendar);

        showSystemBar();

        dateText = (TextView)findViewById(R.id.txt_setDate);
        timeText = (TextView)findViewById(R.id.txt_setTime);
        addressText = (EditText)findViewById(R.id.edittxt_setAddress);
        affirm = (Button)findViewById(R.id.but_setting_affirm);
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinator_setCalendar);

        dateText.setOnClickListener(this);
        timeText.setOnClickListener(this);

        Intent intent = getIntent();
        key = intent.getIntExtra("key", 0);
        position = intent.getIntExtra("position", 0);
        if (key == 1)
        {
            String get_date = intent.getStringExtra("alter_date");
            String get_time = intent.getStringExtra("alter_time");
            String get_address = intent.getStringExtra("alter_address");
            dateText.setText(get_date);
            timeText.setText(get_time);
            addressText.setText(get_address);
        }
        else if (key == 0)
        {
            Calendar calendar = Calendar.getInstance();
            dateText.setText(String.format(Locale.CHINA, "%d年%d月%d日", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)));
            timeText.setText(String.format(Locale.CHINA, "%d时%02d分", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
        }

        affirm.setOnClickListener(v ->
        {
            if (dateText.getText().toString().isEmpty() || timeText.getText().toString().isEmpty() || addressText.getText().toString().isEmpty())
            {
                Snackbar.make(coordinatorLayout, "请填写完整的信息噢~", Snackbar.LENGTH_SHORT).show();
            }
            else
            {
                Intent intent1 = new Intent();
                Alert alert = new Alert(dateText.getText().toString(), timeText.getText().toString(), addressText.getText().toString());
                Bundle bundle = new Bundle();
                bundle.putParcelable("Alert", alert);
                bundle.putInt("position", position);
                intent1.putExtras(bundle);
                setResult(key == 1 ? 2 : 3, intent1);
                finish();
            }
        });
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.txt_setDate)
        {
            Calendar calendar = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
            DatePickerDialog dialog = new DatePickerDialog(this, R.style.dialog_date, this,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        }
        else if (id == R.id.txt_setTime)
        {
            Calendar calendar = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
            TimePickerDialog dialog = new TimePickerDialog(this, R.style.dialog_date, this,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE), true);
            dialog.show();
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
        dateText.setText(desc);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        String desc = String.format(Locale.CHINA, "%d时%02d分", hourOfDay, minute);
        timeText.setText(desc);
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


