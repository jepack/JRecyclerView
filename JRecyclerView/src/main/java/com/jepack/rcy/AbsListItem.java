package com.jepack.rcy;

/**
 * ListItem 的基本功能抽象
 * Created by zhanghaihai on 2018/3/29.
 */
public class AbsListItem<T> implements ListItem {
    T data;
    int viewType;
    public AbsListItem(T data, int viewType) {
        this.data = data;
        this.viewType = viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public int getViewType() {
        return viewType;
    }

    @Override
    public Object getItemData() {
        return getData();
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
