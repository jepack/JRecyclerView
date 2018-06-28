package com.jepack.rcy;

import android.view.View;

/**
 * Item 点击事件
 * Created by zhanghaihai on 2018/4/2.
 */
public interface OnItemClickListener<T extends ListItem> {
    public void onItemClick(View view, int position, T item);
}
