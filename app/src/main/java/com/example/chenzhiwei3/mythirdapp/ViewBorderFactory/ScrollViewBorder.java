package com.example.chenzhiwei3.mythirdapp.ViewBorderFactory;

import android.view.View;
import android.widget.ScrollView;

import com.example.chenzhiwei3.mythirdapp.ViewSwitcher;

/**
 * 判断ScrollView的上下边界
 * Created by chenzhiwei3 on 2016/8/19.
 */
public class ScrollViewBorder extends ViewBorder {

    public ScrollViewBorder(ViewSwitcher viewSwitcher) {
        super(viewSwitcher);
    }

    @Override
    public boolean getBottom(View mView) {
        ScrollView sv = (ScrollView) mView;
        return sv.getScrollY() >= (sv.getChildAt(0).getMeasuredHeight() - sv.getMeasuredHeight());
    }

    @Override
    public boolean getTop(View mView) {
        ScrollView sv = (ScrollView) mView;
        return sv.getScrollY() <= 0;
    }
}
