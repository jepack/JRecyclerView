package com.jepack.rcy.wrap;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import static com.jepack.rcy.wrap.BaseRefreshHeader.STATE_NORMAL;

/**
 * TODO Add class comments here.
 * Created by zhanghaihai on 2018/4/10.
 */
public class WrapOnScrollListener extends RecyclerView.OnScrollListener {
    private ListFooterPresenter footerPresenter;
    private ListHeaderPresenter headerPresenter;

    public WrapOnScrollListener(ListFooterPresenter footerPresenter, ListHeaderPresenter headerPresenter) {
        this.footerPresenter = footerPresenter;
        this.headerPresenter = headerPresenter;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int state) {
        super.onScrollStateChanged(recyclerView, state);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if(footerPresenter != null && !footerPresenter.isLoadingData() && footerPresenter.isLoadMoreEnable()) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int lastVisibleItemPosition;
            if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
                lastVisibleItemPosition = findMax(into);
            } else {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            }
            int adjAdapterItemCount = layoutManager.getItemCount() ;

            int status = STATE_NORMAL;

            if(headerPresenter.getRefreshHeader() != null)
                status = headerPresenter.getRefreshHeader().getState();

            if (layoutManager.getChildCount() > 0
                    && lastVisibleItemPosition >= adjAdapterItemCount - footerPresenter.getLimitNumberToCallLoadMore()
                    && adjAdapterItemCount >= layoutManager.getChildCount()
                    && !footerPresenter.isNoMore()
                    && !footerPresenter.isLoadingData()
                    && status < ArrowRefreshHeader.STATE_REFRESHING) {
                if (footerPresenter.getFooterView() != null && footerPresenter.getFooterView() instanceof LoadingMoreFooter) {
                    footerPresenter.getFooterView().setState(LoadingMoreFooter.STATE_LOADING);
                } else {
                    footerPresenter.onLoadingMore(footerPresenter.getFooterView());
                }
                footerPresenter.onLoadMore();
            }
        }
    }

    private int getHeaders_includingRefreshCount(){
        return headerPresenter.getHeaderCount() + 1;
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
