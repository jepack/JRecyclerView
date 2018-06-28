package com.jepack.rcy;

import android.os.Handler;
import android.support.annotation.IntRange;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.ethanhua.skeleton.SkeletonScreen;
import com.jepack.rcy.util.LogUtil;
import com.jepack.rcy.wrap.WrapAdapter;
import com.jepack.rcy.wrap.WrapOnScrollListener;
import com.jepack.rcy.wrap.WrapOnTouchListener;
import com.jepack.skeleton.SkeletonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.jepack.rcy.wrap.LoadingMoreFooter.STATE_COMPLETE;
import static com.jepack.rcy.wrap.LoadingMoreFooter.STATE_NOMORE;

/**
 * 列表数据控制
 * Created by zhanghaihai on 2018/3/29.
 */
public class ListAdapter extends RecyclerView.Adapter<ItemViewHolder>{
    private List<ListItem> items = new ArrayList<>();
    private Lock dataChangeLock = new ReentrantLock();
    private ListDataProvider<ListItem, Object> provider;
    private Disposable initDispose;
    private ItemPresenter<ListItem> itemPresenter;
    private int limit = 20;
    private boolean cacheEnable = false;
    private WrapAdapter wrapAdapter;
    private SkeletonScreen skeletonScreen;

    public ListAdapter(ListDataProvider<ListItem, ?> provider, ItemPresenter<? extends ListItem> itemPresenter) {
        this.provider = (ListDataProvider<ListItem, Object>) provider;
        this.itemPresenter = (ItemPresenter<ListItem>) itemPresenter;
    }

    private void initData(){
        disposeAll();
        initDispose = Observable.create((new ObservableOnSubscribe<List<ListItem>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ListItem>> e) throws Exception {
                List<ListItem> data = initDataSync();
                if(data != null) {
                    e.onNext(data);
                }
            }
        })).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ListItem>>() {
            @Override
            public void accept(List<ListItem> o) throws Exception {
                hideSkeleton();
                if(wrapAdapter  != null) {
                    wrapAdapter.notifyDataSetChanged();
                    if(!applyInitLoadState()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(!applyInitLoadState()) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            applyInitLoadState();
                                        }
                                    }, 200);
                                }
                            }
                        }, 300);
                    }
                }else{
                    notifyDataSetChanged();
                }

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                LogUtil.e(throwable);
            }
        });
    }

    /**
     *  初始化完成，更新加载更多Footer状态
     */
    public boolean applyInitLoadState(){
        if (wrapAdapter.getListFooterPresenter().getFooterView() != null) {
            if(items.size() < limit) {
                wrapAdapter.getListFooterPresenter().getFooterView().setState(STATE_NOMORE);
            }else{
                wrapAdapter.getListFooterPresenter().getFooterView().setState(STATE_COMPLETE);
            }
            return true;
        }else{
            return false;
        }
    }

    public List<ListItem> initDataSync(){
        int lastSize = items.size();
        dataChangeLock.lock();
        try {
            List<ListItem> data = provider.getData(limit);
            int size = items.size();
            if (size > 0) {
                items.clear();
            }

            if (data != null) {
                addAll(data);
                return data;
            }else{
            }

        } catch (Exception ex) {
            LogUtil.e(ex);
        } finally {
            dataChangeLock.unlock();
        }
        return null;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return itemPresenter.createViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        itemPresenter.update(holder, items.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        if(position < items.size()) {
            ListItem item = items.get(position);
            return item.getViewType();
        }else{
            return 0;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     *
     * @return -1 取消加载更多 0 没有更多数据 > 0 有数据
     */

    @IntRange(from = -1)
    public int loadMoreSync(){
        int lastSize = items.size();
        dataChangeLock.lock();
        int size = items.size();
        //Size 为0，或者小于设定的limit，表示没有更多数据; size 与lastSize不相等说明数据在加载更多时发生变化不在处理数据
        if(size != lastSize) return -1;
        ListItem lastItem = items.get(lastSize - 1);
        List<ListItem> list =  provider.loadMore(lastSize -1, limit, lastItem.getItemData());
        if(list != null && size == items.size()){//只有在等待锁过程中没有数据变化才会加载更多
            addAll(list);
        }
        dataChangeLock.unlock();
        return (list == null || list.size() == 0) ? 0: list.size();
    }

    public ListAdapter enableHeaderFooter(RecyclerView recyclerView, boolean enableHeader, boolean enableFooter, Integer loadLimit, Integer loadMore){
        if(enableFooter || enableHeader) {
            wrapAdapter = new WrapAdapter(this);
            recyclerView.setAdapter(wrapAdapter);
            if(loadLimit != null &&  loadLimit > 0){
                setLimit(loadLimit);
            }
            if(!enableHeader) {
                wrapAdapter.getListHeaderPresenter().setRefreshHeaderEnable(false);
            }else {
                recyclerView.addOnItemTouchListener(new WrapOnTouchListener(wrapAdapter.getListHeaderPresenter()));
            }
            recyclerView.addOnScrollListener(new WrapOnScrollListener(wrapAdapter.getListFooterPresenter(), wrapAdapter.getListHeaderPresenter()));
        }else{
            setLimit(0);
            recyclerView.setAdapter(this);
        }
        return this;
    }

    public void showSkeleton(RecyclerView recyclerView, int skeletonType){
        skeletonScreen = SkeletonUtil.showSkeleton(recyclerView, skeletonType, recyclerView.getAdapter());
    }

    public void showListSkeleton(RecyclerView recyclerView, @LayoutRes int itemSkeleton, RecyclerView.LayoutManager layoutManager){
        skeletonScreen = SkeletonUtil.showListSkeleton(recyclerView, recyclerView.getAdapter(), itemSkeleton, layoutManager);
    }

    private void hideSkeleton(){
        if(skeletonScreen != null){
            skeletonScreen.hide();
        }
    }

    public void reload(){
        initData();
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setCacheEnable(boolean cacheEnable){
        this.cacheEnable = cacheEnable;
    }

    private void addAll(List<ListItem> items){
        this.items.addAll(items);
        if(cacheEnable)
            provider.setListCache(items);
    }

    /**
     * 若存在缓存使用缓存数据，缓存数据为空时重新获取数据
     */
    public void loadCache(final ListDataProvider<ListItem, Object> provider, ItemPresenter<ListItem> itemPresenter){
        this.provider = provider;
        this.itemPresenter = itemPresenter;
        if(provider.getListCache() != null){
            Disposable loadCacheDis = Observable.create(new ObservableOnSubscribe<Integer>() {
                @Override
                public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                    dataChangeLock.lock();
                    items.clear();
                    addAll(provider.getListCache());
                    e.onNext(items.size());
                    dataChangeLock.unlock();
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                @Override
                public void accept(Integer integer) throws Exception {
                    notifyDataSetChanged();
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    LogUtil.e(throwable);
                }
            });

        }else{
            initData();
        }
    }


    public ListDataProvider<ListItem, Object> getProvider() {
        return provider;
    }

    public void setProvider(ListDataProvider<ListItem, Object> provider) {
        this.provider = provider;
    }

    public void disposeAll(){
        if(initDispose != null && !initDispose.isDisposed()) {
            initDispose.dispose();
        }
    }


    public ListItem del(int index){
        dataChangeLock.lock();
        try {
            if (index < items.size()) {
                ListItem item = items.remove(index);
                notifyItemRemoved(index);
                return item;
            }
        }catch (Exception e){
            //Nothing need to do
        }finally {
            dataChangeLock.unlock();
        }
        return null;
    }

    public ListItem add(int index, ListItem item){
        dataChangeLock.lock();
        try {
            if (index < items.size()) {
                items.add(item);
                notifyItemInserted(index);
                return item;
            }
        }catch (Exception e){
            //Nothing need to do
        }finally {
            dataChangeLock.unlock();
        }
        return null;
    }

    public List<ListItem> delAll(int[] indexes){
        List<ListItem> delItems = new ArrayList<>();
        dataChangeLock.lock();
        try {
            for (int index: indexes) {
                if (index < items.size()) {
                    delItems.add(items.remove(index));
                }
            }

            notifyDataSetChanged();
        }catch (Exception e){
            //Nothing need to do
        }finally {
            dataChangeLock.unlock();
        }
        return delItems;
    }

    public <T extends ListItem> int indexOf(ComparableCallback<T> comparableCallback){
        for(ListItem item: items){
            int code = comparableCallback.compare((T)item);
            if(code == 0){
                return items.indexOf(item);
            }
        }
        return -1;
    }

    public interface ComparableCallback<T extends ListItem>{
        public int compare(T item);
    }

    @Nullable
    public ListAdapter getAdapter(){
        if(itemPresenter.getRecyclerView() != null && itemPresenter.getRecyclerView().getAdapter() != null) {
            RecyclerView.Adapter adapter = itemPresenter.getRecyclerView().getAdapter();
            if(adapter instanceof WrapAdapter){
                return ((WrapAdapter) adapter).getListAdapter();
            }else{
                return (ListAdapter) adapter;
            }
        }else{
            return null;
        }
    }

}
