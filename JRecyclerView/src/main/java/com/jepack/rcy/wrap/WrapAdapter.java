package com.jepack.rcy.wrap;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.jepack.rcy.ItemViewHolder;
import com.jepack.rcy.ListAdapter;
import com.jepack.rcy.ListItem;
import com.jepack.rcy.util.LogUtil;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static android.view.View.NO_ID;
import static com.jepack.rcy.wrap.LoadingMoreFooter.STATE_COMPLETE;
import static com.jepack.rcy.wrap.LoadingMoreFooter.STATE_NOMORE;

/**
 * TODO Add class comments here.
 * Created by zhanghaihai on 2018/4/8.
 */
public class WrapAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final @NonNull
    ListAdapter listAdapter;
    private @NonNull
    ListHeaderPresenter listHeaderPresenter;
    private @NonNull
    ListFooterPresenter listFooterPresenter;
    private final int VIEW_TYPE = 100000;
    private final int VIEW_TYPE_REFRESH_HEADER = VIEW_TYPE + 1;
    private final int VIEW_TYPE_FOOTER = VIEW_TYPE + 2;
    private final int VIEW_TYPE_OTHER_HEADER = VIEW_TYPE + 1000;
    private Disposable dispose;
    public Disposable loadMoreDisposable;
    public WrapAdapter(@NonNull final ListAdapter listAdapter) {
        this.listAdapter = listAdapter;
        listHeaderPresenter = new DefaultListHeaderPresenter() {
            @Override
            public void onRefreshing() {
                if(dispose != null){
                    dispose.dispose();
                }
                dispose = Observable.create(new ObservableOnSubscribe<List<ListItem>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<ListItem>> e) throws Exception {
                        List<ListItem> data = listAdapter.initDataSync();
                        if(data != null) {
                            e.onNext(data);
                            if (data.size() > 0) {
                                e.onComplete();
                            }
                        }
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ListItem>>() {
                    @Override
                    public void accept(List<ListItem> items) throws Exception {
                        notifyDataSetChanged();
                        listAdapter.applyInitLoadState();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.e(throwable);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        listHeaderPresenter.getRefreshHeader().refreshComplete();
                    }
                });

            }
        };
        listFooterPresenter = new DefaultListFooterPresenter(){

            @Override
            public void onLoadMore() {
                if(loadMoreDisposable != null){
                    loadMoreDisposable.dispose();
                }
                final int size = listAdapter.getItemCount() - 1;
                loadMoreDisposable = Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> e) throws Exception {

                        int count= listAdapter.loadMoreSync();
                        if(count >= 0) e.onNext(count);
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer count) throws Exception {
                        LogUtil.d("footer load more changed", size, count);
                        notifyItemRangeChanged(size > 0 ? 0: size -1, count);
                        if(count == 0) {
                            listFooterPresenter.getFooterView().setState(STATE_NOMORE);
                        }else{
                            listFooterPresenter.getFooterView().setState(STATE_COMPLETE);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.e(throwable);
                    }
                });

            }
        };
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case VIEW_TYPE_REFRESH_HEADER:
                return listHeaderPresenter.getRefreshHeaderHolder(parent, viewType);
            case VIEW_TYPE_FOOTER:
                return listFooterPresenter.getFooterHolder(parent, viewType);
            case VIEW_TYPE_OTHER_HEADER:
                listHeaderPresenter.getHeaderHolder(viewType);
                default: return listAdapter.onCreateViewHolder(parent, viewType);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(!isRefreshHeaderPos(position) && !isHeaderPos(position) && !isFooterPos(position)){
            int adjPosition = getAdjPos(position);
            if(holder instanceof ItemViewHolder) {
                listAdapter.onBindViewHolder((ItemViewHolder) holder, adjPosition);
            }
        }

    }


    @Override
    public int getItemViewType(int position) {
        int type = 0;
        int adjPos = getAdjPos(position);
        if(adjPos > 0 && adjPos < listAdapter.getItemCount()){
            int viewType = listAdapter.getItemViewType(adjPos);

            if (viewType > VIEW_TYPE) {
                throw new IllegalStateException("ViewType must be less than" + VIEW_TYPE);
            }
            type = viewType;
        }else {
            if (isRefreshHeaderPos(position)) {
                type = VIEW_TYPE_REFRESH_HEADER;
            } else if (isHeaderPos(position)) {
                type = listHeaderPresenter.getHeaderType(position - 1);
            } else if (isFooterPos(position)) {
                type = VIEW_TYPE_FOOTER;
            }
        }
        return type;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        if(isRefreshHeaderPos(position) || isHeaderPos(position) || isFooterPos(position)) return;

        int adjPosition = getAdjPos(position);
        if (holder instanceof ItemViewHolder) {
            if (payloads.isEmpty()) {
                listAdapter.onBindViewHolder((ItemViewHolder) holder, adjPosition);
            }else{
                listAdapter.onBindViewHolder((ItemViewHolder) holder, adjPosition, payloads);
            }
        }
    }


    @Override
    public long getItemId(int position) {
        if(!isRefreshHeaderPos(position) && !isHeaderPos(position)) {
            return listAdapter.getItemId(position - listHeaderPresenter.getHeaderCount());
        }else{
            return NO_ID;
        }
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if(listAdapter.getItemCount() == 0 && !listHeaderPresenter.isRefreshEnable() ){
            count = 0;
        }else {
            int extra = (listHeaderPresenter.isRefreshEnable() ? 1 : 0) +
                    ((listAdapter.getItemCount() > 0 && listFooterPresenter.isLoadMoreEnable()) ? 1 : 0);

            count = listAdapter.getItemCount() + listHeaderPresenter.getHeaderCount() + extra;
        }
        return count;
    }

    public void setListHeaderPresenter(@NonNull ListHeaderPresenter listHeaderPresenter) {
        this.listHeaderPresenter = listHeaderPresenter;
    }

    public boolean isRefreshHeaderPos( int position){
        return listHeaderPresenter.isRefreshEnable() && (position == 0);

    }

    public boolean isHeaderPos( int position){
        return listHeaderPresenter.isRefreshEnable()?
                (position > 0 && position < getHeadCount()): (position >=0 && position < getHeadCount());
    }

    public boolean isFooterPos( int position){
        return listAdapter.getItemCount() != 0 && position == (getHeadCount() + listAdapter.getItemCount());
    }

    public void setListFooterPresenter(@NonNull ListFooterPresenter listFooterPresenter) {
        this.listFooterPresenter = listFooterPresenter;
    }

    public int getAdjPos(int position){
        return position - getHeadCount();
    }

    public int getWrapPos(int itemPosition){
        return itemPosition + getHeadCount();
    }

    @NonNull
    public ListHeaderPresenter getListHeaderPresenter() {
        return listHeaderPresenter;
    }

    @NonNull
    public ListFooterPresenter getListFooterPresenter() {
        return listFooterPresenter;
    }

    public int getHeadCount(){
        return listHeaderPresenter.isRefreshEnable()? listHeaderPresenter.getHeaderCount() + 1: listHeaderPresenter.getHeaderCount();
    }

    @NonNull
    public ListAdapter getListAdapter() {
        return listAdapter;
    }
}
