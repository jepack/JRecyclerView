package com.jepack.rcy

/**
 * TODO Add class comments here.
 * Created by zhanghaihai on 2018/6/28.
 */
abstract class SimpleDataProvider: ListDataProvider<ListItem, Any>() {
    abstract override fun loadData(start: Int, limit: Int, endItem: Any?): MutableList<Any>?

    override fun convert(data: Any?): ListItem {
        return DefaultListItem(data, getLayout(data))
    }

    abstract override fun getClassLayoutMap(): MutableMap<Class<*>, Int>
}