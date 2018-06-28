package com.jepack.rcy;

import android.support.annotation.Nullable;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 列表数据获取，转换
 * 转换：将原始数据，例如数据库查询结果封装为你自定义的ListItem。
 * Created by zhanghaihai on 2018/3/29.
 */
public abstract class ListDataProvider<T extends ListItem, K> {

    private List<ListItem> items;

    @Nullable
    public List<T> getData(Integer limit){
        List<K> tList = loadData(0, limit, null);
        return convertList(tList);
    }

    @Nullable
    public List<T> loadMore(int start, int limit, K endItem){
        List<K> tList = loadData(start, limit, endItem);
        return convertList(tList);
    }

    @Nullable
    public abstract List<K> loadData(int start, int limit, K endItem);

    public List<T> convertList(List<K> tList){
        List<T> items = new ArrayList<>();
        if(tList == null) return items;
        for(K item : tList){
            items.add(convert(item));
        }
        return items;
    }

    public abstract T convert(K data);

    public void setListCache(List<ListItem> items){
        this.items = items;
    }

    public List<ListItem> getListCache() {
        return items;
    }

    public void onError(int start, int limit, K item, int code, String msg){

    }

    public int getLayout(Object data){
        Map<Class<?>, Integer> classLayoutMap = getClassLayoutMap();

        for(Class<?> clazz: classLayoutMap.keySet()){
            if(clazz.isInstance(data)){
                return classLayoutMap.get(clazz);
            }
        }
        return 0;
    }

    public Map<Class<?>, Integer> getClassLayoutMap(){
        return null;
    }

}
