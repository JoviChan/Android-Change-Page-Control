package com.example.chenzhiwei3.mythirdapp.ViewBorderFactory;

import android.view.View;
import android.widget.ListView;
import android.widget.ScrollView;

import com.example.chenzhiwei3.mythirdapp.ViewSwitcher;

/**
 * 检测任何View的上下边界的简单工厂类
 * Created by chenzhiwei3 on 2016/8/19.
 */
public class ViewerBorderFactory {
    private ViewSwitcher mViewSwitcher;

    public ViewerBorderFactory(ViewSwitcher viewSwitcher) {
        mViewSwitcher = viewSwitcher;
    }

    /**
     * 判断View的类型，当前支持ListView和ScrollView，如果需要扩展，可在此加分支，并写一个相应
     * 的继承ViewBorder的判断View的上下边界的类
     * @param view  需要添加边界Listener的View
     * @return  监听是否到达View边界的Listener
     */
    public View.OnTouchListener getListener(View view) {
        //ListView判断分支
        if(view.getClass() == ListView.class) {
            return new ListViewBorder(mViewSwitcher).getListener();
        }
        //ScrollView判断分支
        else if(view.getClass() == ScrollView.class) {
            return new ScrollViewBorder(mViewSwitcher).getListener();
        }
        else {
            throw new IllegalArgumentException("View类型不支持，需扩展判断View边界的类");
        }
    }
}
