package com.jepack.rcy.wrap;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import static com.jepack.rcy.wrap.LoadingMoreFooter.STATE_NOMORE;

/**
 * 默认的底部加载更多控制
 * Created by zhanghaihai on 2018/4/9.
 */
public abstract class DefaultListFooterPresenter implements ListFooterPresenter {

    private LoadingMoreFooter footerView;
    private FooterViewHolder footerViewHolder;
    @Override
    public boolean isLoadMoreEnable() {
        return true;
    }

    @Override
    public RecyclerView.ViewHolder getFooterHolder(ViewGroup parent, int viewType) {
        if(footerView == null) {
            footerView = new LoadingMoreFooter(parent.getContext());
        }

        if(footerViewHolder == null){
            footerViewHolder = new FooterViewHolder(footerView);
        }
        return footerViewHolder;
    }

    @Override
    public LoadingMoreFooter getFooterView() {
        return footerView;
    }

    @Override
    public void onLoadingMore(View footerView) {

    }

    @Override
    public int getLimitNumberToCallLoadMore() {
        return 1;
    }

    @Override
    public boolean isNoMore() {
        return  footerView == null || footerView.getState() == STATE_NOMORE;
    }

    @Override
    public boolean isLoadingData() {
        return false;
    }

    @Override
    public void setIsLoadingData(boolean b) {

    }

}
