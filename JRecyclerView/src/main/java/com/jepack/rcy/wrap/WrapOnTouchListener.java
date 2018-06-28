package com.jepack.rcy.wrap;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * TODO Add class comments here.
 * Created by zhanghaihai on 2018/4/10.
 */
public class WrapOnTouchListener implements RecyclerView.OnItemTouchListener {

    private float mLastY;
    private boolean pullRefreshEnabled = true;
    private ListHeaderPresenter headerPresenter;
    private static final float DRAG_RATE = 3;
    private AppBarStateChangeListener.State appbarState = AppBarStateChangeListener.State.EXPANDED;

    public WrapOnTouchListener(ListHeaderPresenter headerPresenter) {
        this.headerPresenter = headerPresenter;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        onTouchEvent(rv, e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent ev) {
        if (mLastY == -1) {
            mLastY = ev.getRawY();
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float deltaY = ev.getRawY() - mLastY;
                mLastY = ev.getRawY();
                if (isOnTop() && pullRefreshEnabled && appbarState == AppBarStateChangeListener.State.EXPANDED) {
                    if(headerPresenter.getRefreshHeader() == null)
                        break;
                    headerPresenter.getRefreshHeader().onMove(deltaY / DRAG_RATE);
                    if (headerPresenter.getRefreshHeader().getVisibleHeight() > 0 && headerPresenter.getRefreshHeader().getState() < ArrowRefreshHeader.STATE_REFRESHING) {
                        return ;
                    }
                }
                break;
            default:
                mLastY = -1; // reset
                if (isOnTop() && pullRefreshEnabled && appbarState == AppBarStateChangeListener.State.EXPANDED) {
                    if (headerPresenter.getRefreshHeader() != null && headerPresenter.getRefreshHeader().releaseAction()) {
                        headerPresenter.onRefreshing();
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    private boolean isOnTop() {
        return headerPresenter.getRefreshHeader() != null && headerPresenter.getRefreshHeader().getParent() != null;
    }
}
