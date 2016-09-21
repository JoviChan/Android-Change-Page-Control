package com.example.chenzhiwei3.mythirdapp.ViewBorderFactory;

import android.view.MotionEvent;
import android.view.View;

import com.example.chenzhiwei3.mythirdapp.ViewSwitcher;

/**
 * 判断是否View的上下边界的通用抽象类
 * Created by chenzhiwei3 on 2016/8/19.
 */
public abstract class ViewBorder {
    private ViewSwitcher mViewSwitcher;

    public ViewBorder(ViewSwitcher viewSwitcher) {
        mViewSwitcher = viewSwitcher;
    }

    /**
     * 判断是否View的顶部
     * @return  返回是否顶部
     */
    public abstract boolean getTop(View mView);

    /**
     * 判断是否View的底部
     * @return 返回是否底部
     */
    public abstract boolean getBottom(View mView);

    /**
     * 根据View判断上下边界的规则生成的View边界Listener
     * @return 生成的判断边界Listener
     */
    public View.OnTouchListener getListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    mViewSwitcher.setTop(getTop(v));
                    mViewSwitcher.setBottom(getBottom(v));
                }
                return false;
            }
        };
    }
}
