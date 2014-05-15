CustomView
===========

自己在Android开发中，用到的一些自定义View。

*  ExpandCalendar: 日历控件，支持手势滑动展开和收缩，基于[android-times-square](https://github.com/square/android-times-square)项目修改而来。


ExpandCalendar
------------
因为项目需要，故将[android-times-square](https://github.com/square/android-times-square)大神的开源日历控件增加了手势展开和收缩的功能。
![正常效果](./zhankai.png "图片")
![收缩后效果](./shousuo.png "图片")


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

    其它常规用法见[android-times-square](https://github.com/square/android-times-square)，说明的已经很清楚了。









