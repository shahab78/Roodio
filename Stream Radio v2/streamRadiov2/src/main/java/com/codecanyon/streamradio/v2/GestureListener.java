package com.codecanyon.streamradio.v2;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by User on 2014.08.03..
 */
class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private String currentGestureDetected;

    @Override
    public boolean onSingleTapUp(MotionEvent ev) {
        setCurrentGestureDetected("");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent ev) {
        setCurrentGestureDetected("");
    }

    @Override
    public void onLongPress(MotionEvent ev) {
        setCurrentGestureDetected("");
        RadioList.showDialog();
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        setCurrentGestureDetected("");
        return true;
    }

    @Override
    public boolean onDown(MotionEvent ev) {
        setCurrentGestureDetected("");
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        setCurrentGestureDetected("");
        return true;
    }

	public String getCurrentGestureDetected() {
		return currentGestureDetected;
	}

	public void setCurrentGestureDetected(String currentGestureDetected) {
		this.currentGestureDetected = currentGestureDetected;
	}
}
