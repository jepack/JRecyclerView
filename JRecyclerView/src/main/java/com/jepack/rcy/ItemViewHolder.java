package com.jepack.rcy;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 *
 * Created by zhanghaihai on 2018/3/22.
 */

public class ItemViewHolder extends RecyclerView.ViewHolder{
    private final SparseArray<View> views = new SparseArray<>();
    private ViewDataBinding binding;
    private ListItem data;
    public ItemViewHolder(View itemView, ViewDataBinding binding) {
        super(itemView);
        this.binding = binding;
    }

    public ViewDataBinding getBinding() {
        return binding;
    }

    public SparseArray<View> getViews() {
        return views;
    }

    public void setData(ListItem data) {
        this.data = data;
    }

    public ListItem getData() {
        return data;
    }

}
