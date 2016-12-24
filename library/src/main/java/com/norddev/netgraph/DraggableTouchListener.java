package com.norddev.netgraph;

import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class DraggableTouchListener implements View.OnTouchListener {
    private int initialX;
    private int initialY;
    private float initialTouchX;
    private float initialTouchY;

    private final WindowManager.LayoutParams mLayoutParams;
    private final WindowManager mWindowManager;
    private final View mTargetView;

    public DraggableTouchListener(WindowManager windowManager, WindowManager.LayoutParams layoutParams, View targetView) {
        mLayoutParams = layoutParams;
        mWindowManager = windowManager;
        mTargetView = targetView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                initialX = mLayoutParams.x;
                initialY = mLayoutParams.y;
                initialTouchX = event.getRawX();
                initialTouchY = event.getRawY();
                return true;
            case MotionEvent.ACTION_UP:
                return true;
            case MotionEvent.ACTION_MOVE:
                mLayoutParams.x = initialX + (int) (event.getRawX() - initialTouchX);
                mLayoutParams.y = initialY + (int) (event.getRawY() - initialTouchY);
                mWindowManager.updateViewLayout(mTargetView, mLayoutParams);
                return true;
        }
        return false;
    }
}