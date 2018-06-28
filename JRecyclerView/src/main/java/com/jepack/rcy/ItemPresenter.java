package com.jepack.rcy;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jepack.rcy.wrap.WrapAdapter;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;

/**
 * 用于处理专辑显示
 * Created by zhanghaihai on 2018/3/22.
 */

public abstract class ItemPresenter<T extends ListItem> {
    private final Map<Class, Method> presenterMethods;
    private final Map<Class, Method> clickMethods;
    private final Map<Class, SparseArray<Method>> actionMethods;
    private WeakReference<RecyclerView> rcy;

    private boolean bind = false;

    public final PublishSubject<Action<T>> actionSubject = PublishSubject.create();
    private Consumer<Action<T>> consumer;
    private Disposable disposable;

    public abstract int getLayoutId(int viewType);

    public ItemPresenter(RecyclerView recyclerView) {
        rcy = new WeakReference<>(recyclerView);
        presenterMethods = new HashMap<>();
        clickMethods = new HashMap<>();
        actionMethods = new HashMap<>();
        consumer = new Consumer<Action<T>>() {
            @Override
            public void accept(Action<T> tAction) throws Exception {
                onAction(tAction);
            }
        };
        disposable = actionSubject.observeOn(AndroidSchedulers.mainThread()).subscribe(consumer);
    }

    public void bindMethodAnnotations(){
        if(bind) return;//已经bind，则无需等待解锁
        synchronized (this) {
            if(bind) return;
            Method[] methods = getClass().getDeclaredMethods();
            for (Method method : methods) {
                Annotation presenterA = method.getAnnotation(ModelPresenter.class);
                if (presenterA != null) {
                    presenterMethods.put(ModelPresenter.class.cast(presenterA).value(), method);
                }
                Annotation presenterClick = method.getAnnotation(ModelClick.class);
                if (presenterClick != null) {
                    clickMethods.put(ModelClick.class.cast(presenterClick).value(), method);
                }

                Annotation presenterAction = method.getAnnotation(ModelViewAction.class);
                if (presenterAction != null) {
                    Class clazz = ModelViewAction.class.cast(presenterAction).value();
                    int id = ModelViewAction.class.cast(presenterAction).id();
                    SparseArray<Method> actionViewMap;
                    if (actionMethods.containsKey(clazz)) {
                        actionViewMap = actionMethods.get(clazz);
                        actionViewMap.put(id, method);
                    } else {
                        actionViewMap = new SparseArray<>();
                        actionViewMap.put(id, method);
                        actionMethods.put(clazz, actionViewMap);
                    }
                }
            }

            bind = true;
        }
    }

    public ItemViewHolder createViewHolder(ViewGroup parent, int viewType){
        ViewDataBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), getLayoutId(viewType), parent, false);
        ItemViewHolder holder = instantItemViewHolder(binding);
        return initViews(holder);
    }

    public ItemViewHolder instantItemViewHolder(@NonNull ViewDataBinding binding){
        return new ItemViewHolder(binding.getRoot(), binding);
    }

    public void update(final ItemViewHolder holder, final T data){
        holder.setData(data);
        holder.getBinding().setVariable(BR.item, data);
        if(data instanceof AbsListItem) {
            holder.getBinding().setVariable(BR.itemData, ((AbsListItem) data).data);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionSubject.onNext(new Action<T>(data, Action.ACT_BASE + 1));
            }
        });
    }

    /**
     * 初始化View
     * @param holder
     * @return
     */
    @CallSuper
    public ItemViewHolder initViews(ItemViewHolder holder){
        Integer[] ids = getViewIds();

        if(ids != null) {
            for (Integer id : ids) {
                holder.getViews().put(id, findViewById(id, holder.itemView));
            }
        }
        return holder;
    }

    @Nullable
    public Integer[] getViewIds(){
        return null;
    }

    public <V extends View> V findViewById(int id, View view){
        View child = view.findViewById(id);
        return child == null? null: (V) child;
    }

    @Nullable
    public <V extends View> V getView(ItemViewHolder holder, int id){
        View view = holder.getViews().get(id);
        if(view == null){
            view = (V) findViewById(id, holder.itemView);
            if(view == null){
                return null;
            }else {
                holder.getViews().put(id, view);
                return (V) view;
            }
        }else{
            return (V) view;
        }
    }

    @Nullable
    public RecyclerView getRecyclerView(){
        if(rcy.get() != null){
            return rcy.get();
        }else{
            return null;
        }
    }

    public void onAction(Action<T> action){

    }

    public static class Action<T>{
        public T data;
        public int action;
        public int arg1;
        public String arg2;
        public Object arg3;
        public final static int ACT_BASE = 0;

        public Action(T data, int action) {
            this.data = data;
            this.action = action;
        }

        public Action(T data, int action, int arg1) {
            this.data = data;
            this.action = action;
            this.arg1 = arg1;
        }

        public Action(T data, int action, int arg1, String arg2) {
            this.data = data;
            this.action = action;
            this.arg1 = arg1;
            this.arg2 = arg2;
        }

        public Action(T data, int action, Object arg3) {
            this.data = data;
            this.action = action;
            this.arg3 = arg3;
        }

        public Action(T data, int action, int arg1, Object arg3) {
            this.data = data;
            this.action = action;
            this.arg1 = arg1;
            this.arg3 = arg3;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if(disposable != null){
            disposable.dispose();
        }
        super.finalize();
    }

    @Nullable
    public ListAdapter getAdapter(){
        if(getRecyclerView() != null && getRecyclerView().getAdapter() != null) {
            RecyclerView.Adapter adapter = getRecyclerView().getAdapter();
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
