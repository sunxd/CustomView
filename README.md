CustomView
===========

自己在Android开发中，用到的一些自定义View。

*  ExpandCalendar: 日历控件，支持手势滑动展开和收缩，基于[android-times-square](https://github.com/square/android-times-square)项目修改而来。
*  SlideLayout: 类似抽屉的控件。


ExpandCalendar
------------
因为项目需要，故将[android-times-square](https://github.com/square/android-times-square)大神的开源日历控件增加了手势展开和收缩的功能。

![正常效果](https://raw.githubusercontent.com/sunxd/CustomView/master/ExpandCalendar/zhankai.jpg "正常效果")
![收缩后效果](https://raw.githubusercontent.com/sunxd/CustomView/master/ExpandCalendar/shousuo.jpg "收缩后效果")

### 使用方法

    //开启缩回 展开功能
    calendarView.setExpandable(true);
    //设置初始展现方式为收缩效果
    calendarView.post(new Runnable() {

        @Override
        public void run() {
            calendarView.manualExpand(false);
        }
    });

    其它常规用法见[android-times-square](https://github.com/square/android-times-square), 说明的已经很清楚了。



SlideLayout
------------
类似抽屉控件，位置随便放，给他一个distance,它就可以展开到distance的距离，比抽屉更灵活，不影响事件响应。


![正常效果](https://raw.githubusercontent.com/sunxd/CustomView/master/SlideLayout/slide.jpg "正常效果")
![收缩后效果](https://raw.githubusercontent.com/sunxd/CustomView/master/SlideLayout/slide_open.jpg "展开后效果")


### 使用方法
        //设置屏开的距离
        homeContainer.post(new Runnable() {

            @Override
            public void run() {
                //如：我想让它展开后，在homeAdImv的下方，则距离为：homeContainer.getTop() - homeAdImv.getBottom()
                int distance = (homeContainer.getTop() - homeAdImv.getBottom());
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



