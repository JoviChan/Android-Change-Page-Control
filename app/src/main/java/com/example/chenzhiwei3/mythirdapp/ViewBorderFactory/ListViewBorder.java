package com.example.chenzhiwei3.mythirdapp.ViewBorderFactory;

import android.view.View;
import android.widget.ListView;

import com.example.chenzhiwei3.mythirdapp.ViewSwitcher;

/**
 * 判断ListView的上下边界
 * Created by chenzhiwei3 on 2016/8/19.
 */
public class ListViewBorder extends ViewBorder {

    public ListViewBorder(ViewSwitcher viewSwitcher) {
        super(viewSwitcher);
    }

    @Override
    public boolean getTop(View mView) {
        ListView lv = (ListView) mView;
        return lv.getFirstVisiblePosition() == 0;
    }

    @Override
    public boolean getBottom(View mView) {
        ListView lv = (ListView) mView;
        return lv.getLastVisiblePosition() == lv.getCount() - 1;
    }


}
