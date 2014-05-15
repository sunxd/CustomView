package com.sunxd.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;

import com.nineoldandroids.view.ViewHelper;
import com.squareup.timessquare.CalendarView;
import com.sunxd.widget.SlideLayout;

public class SlideLaoutActivity extends ActionBarActivity {


    /**
     * 自定义抽屉
     */
    private SlideLayout homeContainer;

    /**
     * 占位和遮照
     */
    private View space, cover;

    /**
     * 广告条
     */
    private ImageView homeAdImv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_layout);

        homeContainer = (SlideLayout) findViewById(R.id.home_container);
        space = findViewById(R.id.space);
        cover = findViewById(R.id.cover);
        homeAdImv = (ImageView) findViewById(R.id.imv_home_ad);
        ViewHelper.setAlpha(cover, 0F);

        //设置屏开的距离
        homeContainer.post(new Runnable() {

            @Override
            public void run() {
                int distance = (homeContainer.getTop() - homeAdImv.getBottom());
                //设置展开的距离
                homeContainer.setDistance(distance);
            }
        });
        homeContainer.setExpandListener(new SlideLayout.ExpandListener() {

            @Override
            public void onExpandChanged(boolean expand) {
                //是否展开
            }

            @Override
            public void onExpandPercent(float percent) {
                //设置明暗渐变
                ViewHelper.setAlpha(cover, percent);
            }
        });
    }

}
