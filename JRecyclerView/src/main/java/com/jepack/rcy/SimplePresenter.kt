package com.jepack.rcy

import android.support.v7.widget.RecyclerView

/**
 * TODO Add class comments here.
 * Created by zhanghaihai on 2018/6/28.
 */
abstract class SimplePresenter(rcy:RecyclerView):ItemPresenter<DefaultListItem>(rcy){
    override fun getLayoutId(viewType: Int): Int {
        return 0
    }

    abstract override fun onAction(action: Action<DefaultListItem>?)
}