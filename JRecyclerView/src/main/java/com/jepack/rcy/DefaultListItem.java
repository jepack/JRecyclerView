package com.jepack.rcy;

/**
 * ListItem 的基本功能抽象
 * Created by zhanghaihai on 2018/3/29.
 */
public class DefaultListItem extends AbsListItem {

    public DefaultListItem(Object data, int viewType) {
        super(data, viewType);
    }

    public <T> T getData(Class<T> clazz){
        return (T)data;
    }
}
