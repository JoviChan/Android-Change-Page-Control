package com.example.chenzhiwei3.mythirdapp;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.example.chenzhiwei3.mythirdapp.ViewBorderFactory.ViewerBorderFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chenzhiwei3 on 2016/8/15.
 * ==============================================================================================
 * 上下滑切换View的控件, 对应layout文件至少要有两个View，可自行在Layout中添加多个View
 * ==============================================================================================
 * 默认支持ScrollView和ListView的添加，如果想添加其他View，需要在ViewBorderFactory类的getListener
 * 方法中添加一个分支，并增加实现判断该View的上下边缘的ViewBorder类
 */

public class ViewSwitcher extends RelativeLayout{
    //基本常量
    //<editor-fold>
    public static final int AUTO_UP = 0;    //自动上滑
    public static final int AUTO_DOWN = 1;  //自动下滑
    public static final int SWITCHING = 2;  //正在手动翻页
    public static final int DONE = 3;       //滑动完成

    public float speed; //自动滑动速度
    public int switchDistance;    //认为是翻页的翻页距离

    //判断是否到了页面的上下边缘，从而可以翻页
    private boolean isTop;
    private boolean isBottom;

    private boolean isMeasure;  //判断view是否初始化过，避免重复初始化
    private int currentState;    //当前状态(自动上滑，下滑，完成)

    //当需要在两个View之间开启自动滑动时，开启该Timer实现弹性滑动效果，直到回滚到某一个View再关闭
    private AutoScrollTimer mTimer;
    //当前View的宽度
    private int mViewWidth;
    //当前View的索引
    private int mCurrentIndex;
    //翻页时手指滑动距离 + mPrevHeight，是onLayout的依据
    private float mMoveLen;
    //最新记录的y坐标
    private float mLastY;
    //防止多点触控导致的界面跳变，默认为0，多点触控时为非0，舍弃导致界面跳变move事件并更新mLastY
    private int mEventFlag;

    //判断是否到View上下边界的工厂类
    private ViewerBorderFactory mBorderFactory;
    //上下翻页的回调函数
    private PgChangeCallBack mPgChangeCallBack;
    //</editor-fold>

    //处理手指离开屏幕后的自动滚动
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //<editor-fold>
            //自动上滑，向下翻页
            if (currentState == AUTO_UP) {
                mMoveLen -= speed;
                if (mMoveLen <= 0) {
                    mMoveLen = 0;
                    currentState = DONE;
                    //向下回调
                    if(mPgChangeCallBack != null) {
                        mPgChangeCallBack.onPageDown();
                    }
                }
            }
            //自动下滑，向上翻页
            else if (currentState == AUTO_DOWN) {
                mMoveLen += speed;
                if (mMoveLen >= 0) {
                    mMoveLen = 0;
                    currentState = DONE;
                    //向上翻页回调
                    if(mPgChangeCallBack != null) {
                        mPgChangeCallBack.onPageUp();
                    }
                }
            } else {
                mTimer.cancel();    //滑动完或不需要滑动，则关闭Timer
            }
            //计算组件显示位置
            requestLayout();
            //</editor-fold>
        }

    };

    //基本构造函数
    //<editor-fold>
    public ViewSwitcher(Context context) {
        super(context);
        init();
    }

    public ViewSwitcher(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ViewSwitcher(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    //</editor-fold>

    public void init() {
        mTimer = new AutoScrollTimer(mHandler);
        mMoveLen = 0;
        currentState = DONE;
        isTop = false;
        isBottom = false;
        isMeasure = false;
        mCurrentIndex = 0;
        mEventFlag = 0;
        speed = 7f;
        switchDistance = 150;
        mBorderFactory = new ViewerBorderFactory(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(!isMeasure) {
            //每一个View设置触摸事件
            for(int i = 0; i < getChildCount(); i++) {
                getChildAt(i).setOnTouchListener(mBorderFactory.getListener(getChildAt(i)));
            }
            isMeasure= true;
        }
        mViewWidth = getMeasuredWidth();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //最多只layout当前View及前后View，从而减少layout的量，提升效率
        //当前不是第一个View时，前一个View
        if(mCurrentIndex > 0) {
            getChildAt(mCurrentIndex - 1).layout(0, -getChildAt(mCurrentIndex - 1).getMeasuredHeight()
                    + (int) mMoveLen, mViewWidth, (int) mMoveLen);
        }
        //当前View
        getChildAt(mCurrentIndex).layout(0, (int) mMoveLen, mViewWidth,
                (int) mMoveLen + getChildAt(mCurrentIndex).getMeasuredHeight());
        //当前不是最后一个View时，下一个View
        if(mCurrentIndex < getChildCount() - 1) {
            int top = getChildAt(mCurrentIndex).getMeasuredHeight() + (int)mMoveLen;
            int down = top + getChildAt(mCurrentIndex + 1).getMeasuredHeight();
            getChildAt(mCurrentIndex + 1).layout(0,top, mViewWidth, down);
        }
    }

    /**
     * 用来实现View自动上下滑的Timer
     */
    class AutoScrollTimer {
        private Handler handler;
        private Timer timer;
        private MyTask mTask;

        public AutoScrollTimer(Handler handler) {
            this.handler = handler;
            timer = new Timer();
        }

        /**
         * 设置Timer的周期
         * @param period 周期
         */
        public void schedule(long period) {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
            mTask = new MyTask(handler);
            timer.schedule(mTask, 0, period);
        }

        public void cancel() {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
        }

        class MyTask extends TimerTask {
            private Handler handler;

            public MyTask(Handler handler) {
                this.handler = handler;
            }

            @Override
            public void run() {
                handler.obtainMessage().sendToTarget();
            }

        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(currentState == DONE) {
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mLastY = ev.getY();
                    mEventFlag = 0; //重置事件flag，表明开始触控
                    break;
                case MotionEvent.ACTION_MOVE:
                    //滑到当前View底部，且当前不是最后一个View
                    if(isBottom && mCurrentIndex < getChildCount() - 1) {
                        //底部上拉，进入翻页状态
                        if(ev.getY() - mLastY < 0) {
                            currentState = SWITCHING;
                        }
                        else {
                            isBottom = false;   //即将不在底部
                        }
                    }
                    //滑到当前View顶部，且当前不是第一个View
                    else if (isTop && mCurrentIndex > 0) {
                        //顶部下拉，进入翻页状态
                        if (ev.getY() - mLastY > 0) {
                            currentState = SWITCHING;
                        }
                        // 顶部上拉,如果已进入翻页状态，则子View不能再单独滑动
                        else {
                            isTop = false;  //即将不在顶部
                        }
                    }
                    mLastY = ev.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    mLastY = ev.getY();
                    break;
            }
            super.dispatchTouchEvent(ev);
        }
        //如果自动滑动进行中，不进行相关判断
        else if(currentState == SWITCHING){
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    mLastY = ev.getY();
                    break;
                //多个手指按下或者抬起，改变事件flag，从而使得舍弃接下来的一个move事件,并更新LastY，防止跳变
                case MotionEvent.ACTION_POINTER_DOWN:
                case MotionEvent.ACTION_POINTER_UP:
                    mEventFlag = -1;
                    break;
                case MotionEvent.ACTION_MOVE:
                    if(mEventFlag == 0) {
                        //滑到当前View底部，且当前不是最后一个View
                        if(isBottom && mCurrentIndex < getChildCount() - 1) {
                            mMoveLen += (ev.getY() - mLastY);
                            if(mMoveLen > 0) {
                                mMoveLen = 0;
                            }
                            //如果手动下拉一段距离不放手，再继续下拉无效
                            if (mMoveLen < -getMeasuredHeight()) {
                                mMoveLen = -getMeasuredHeight();
                            }
                        }
                        //滑到当前View顶部，且当前不是第一个View
                        else if (isTop && mCurrentIndex > 0) {
                            mMoveLen += (ev.getY() - mLastY);
                            if(mMoveLen < 0) {
                                mMoveLen = 0;
                            }
                            //如果手动上拉一段距离不放手，再继续上拉无效，只能松开自动上拉
                            if (mMoveLen > getMeasuredHeight()) {
                                mMoveLen = getMeasuredHeight();
                            }
                        }
                    }
                    else {
                        mEventFlag = 0; //事件舍弃完毕，mLastY将得到更新，mMoveLen不会跳变
                    }
                    mLastY = ev.getY(); //更新最新y坐标值
                    requestLayout();    //更新视图
                    break;
                case MotionEvent.ACTION_UP:
                    mLastY = ev.getY();
                    //如果滑动距离到了一定数值（由switchFactor调节），则翻页，否则回滚
                    if(isBottom) {
                        if (mMoveLen < -switchDistance) {
                            currentState = AUTO_UP;
                            if(mCurrentIndex < getChildCount() - 1) {
                                mMoveLen += getMeasuredHeight();
                                mCurrentIndex++;
                            }
                        }
                        else{
                            currentState = AUTO_DOWN;
                        }
                    }
                    else if(isTop) {
                        if (mMoveLen > switchDistance) {
                            currentState = AUTO_DOWN;
                            if(mCurrentIndex > 0) {
                                mMoveLen -= getMeasuredHeight();
                                mCurrentIndex--;
                            }
                        }
                        else{
                            currentState = AUTO_UP;
                        }
                    }
                    mTimer.schedule(2); //每隔2毫秒更新一次自动滚动
                    break;
            }
        }
        return true;
    }

    public interface PgChangeCallBack {

        /**
         * 向上翻页的回调函数
         */
        void onPageUp();

        /**
         * 向下翻页的回调函数
         */
        void onPageDown();
    }

    //getter and setter
    //<editor-fold>
    public int getmCurrentIndex() {
        return mCurrentIndex;
    }

    public void setmCurrentIndex(int mCurrentIndex) {
        this.mCurrentIndex = mCurrentIndex;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public boolean isBottom() {
        return isBottom;
    }

    public void setBottom(boolean bottom) {
        isBottom = bottom;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public void setmPgChangeCallBack(PgChangeCallBack mPgChangeCallBack) {
        this.mPgChangeCallBack = mPgChangeCallBack;
    }
    //</editor-fold>
}
