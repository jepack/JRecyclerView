package com.jepack.rcy.wrap;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * TODO Add class comments here.
 * Created by zhanghaihai on 2018/4/8.
 */
public interface ListFooterPresenter {

    public boolean isLoadMoreEnable();

    public RecyclerView.ViewHolder getFooterHolder(ViewGroup parent, int viewType);

    public LoadingMoreFooter getFooterView();

    public void onLoadMore();

    public void onLoadingMore(View footerView);

    public int getLimitNumberToCallLoadMore();

    public boolean isNoMore();

    public boolean isLoadingData();

    public void setIsLoadingData(boolean b);
}
