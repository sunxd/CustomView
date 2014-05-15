package com.sunxd.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.squareup.timessquare.CalendarView;

public class ExpandCalendarActivity extends ActionBarActivity {


    private CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expand_calendar);
        calendarView = (CalendarView) findViewById(R.id.calendar);

        //开启缩回 展开功能
        calendarView.setExpandable(true);
        //设置初始展现方式为 收缩
        calendarView.post(new Runnable() {

            @Override
            public void run() {
                calendarView.manualExpand(false);
            }
        });
    }

}
