package com.jepack.rcy.wrap;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * TODO Add class comments here.
 * Created by zhanghaihai on 2018/4/8.
 */
public interface ListHeaderPresenter {

    public boolean isRefreshEnable();
    /**
     * 获取Header站位数量，通常为0, 1
     * @return
     */
    public int getHeaderCount();

    /**
     * 获取Header对应的Views
     * @return 所有的Header: 在刷新之后所有header都不会显示
     */
    public int getHeaderType(int position);

    /**
     *
     */
    public void setRefreshHeaderEnable(boolean enable);

    /**
     * 开始下拉时调用
     */
    public void onOnPullingStarted();

    /**
     * 正在下拉
     * @param degree 下拉达到的程度
     */
    public void onOnPulling(int degree);

    /**
     * 下拉在未达到可启动刷新位置就释放
     */
    public void onPullCanceled();

    /**
     * 下拉完成，正在刷新数据
     */
    public void onRefreshing();

    /**
     * 数据刷新完成时调用
     */
    public void onRefreshed();

    public RecyclerView.ViewHolder getHeaderHolder(int viewType);

    public RecyclerView.ViewHolder getRefreshHeaderHolder(ViewGroup parent, int viewType);

    public ArrowRefreshHeader getRefreshHeader();

    public void refresh() ;
}

