package com.jepack.rcy.wrap;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * 默认的Header显示控制
 * Created by zhanghaihai on 2018/4/8.
 */
public abstract class DefaultListHeaderPresenter implements ListHeaderPresenter {
    private ArrowRefreshHeader refreshHeader;
    private RefreshHeaderViewHolder refreshHeaderViewHolder;
    private boolean isRefreshEnable = true;
    @Override
    public boolean isRefreshEnable() {
        return isRefreshEnable;
    }

    @Override
    public int getHeaderCount() {
        return 0;
    }

    @Override
    public int getHeaderType(int position) {
        return 0;
    }

    @Override
    public void setRefreshHeaderEnable(boolean enable) {
        this.isRefreshEnable = enable;
    }

    @Override
    public void onOnPullingStarted() {

    }

    @Override
    public void onOnPulling(int degree) {

    }

    @Override
    public void onPullCanceled() {
        refreshHeader.setState(ArrowRefreshHeader.STATE_DONE);
    }

    @Override
    public abstract void onRefreshing();

    @Override
    public void onRefreshed() {
        refreshHeader.setState(ArrowRefreshHeader.STATE_DONE);
    }

    @Override
    public RecyclerView.ViewHolder getHeaderHolder(int viewType) {
        return null;
    }

    @Override
    public RecyclerView.ViewHolder getRefreshHeaderHolder(ViewGroup parent, int viewType) {
        if(refreshHeader == null) {
            refreshHeader = new ArrowRefreshHeader(parent.getContext());
        }

        if(refreshHeaderViewHolder == null){
            refreshHeaderViewHolder = new RefreshHeaderViewHolder(refreshHeader);
        }
        return refreshHeaderViewHolder;
    }

    @Override
    public void refresh() {
//        if (pullRefreshEnabled && mLoadingListener != null) {
        if(refreshHeader != null)
            refreshHeader.setState(ArrowRefreshHeader.STATE_REFRESHING);
        onRefreshing();
//        }
    }

    public ArrowRefreshHeader getRefreshHeader() {
        return refreshHeader;
    }
}
