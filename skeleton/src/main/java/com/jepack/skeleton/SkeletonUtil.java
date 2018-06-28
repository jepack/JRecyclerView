package com.jepack.skeleton;

import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ethanhua.skeleton.R;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * TODO Add class comments here.
 * Created by zhanghaihai on 2018/6/5.
 */
public class SkeletonUtil {

    public final static int IMG_TXT_LIST = 0;
    public final static int IMG_LIST = 0;
    @IntDef({IMG_TXT_LIST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SKELETON_TYPE{};

    public static SkeletonScreen showSkeleton(View view, int type, RecyclerView.Adapter adapter){
        switch (type){
            case IMG_TXT_LIST:
                return showImgListSkeleton(view, adapter);
        }
        return null;
    }

    public static SkeletonScreen showImgListSkeleton(View view, RecyclerView.Adapter adapter){
        if(view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
            return Skeleton.bind(recyclerView)
                    .adapter(adapter)
                    .shimmer(true)
                    .angle(20)
                    .frozen(false)
                    .duration(1200)
                    .count(10)
                    .load(R.layout.item_skeleton_news)
                    .show(); //default count is 10
        }else{
            return null;
        }
    }

    public static SkeletonScreen showListSkeleton(View view, RecyclerView.Adapter adapter, @LayoutRes int itemSkeleton, RecyclerView.LayoutManager layoutManager){
        if(view instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(layoutManager);
            return Skeleton.bind(recyclerView)
                    .adapter(adapter)
                    .shimmer(true)
                    .angle(20)
                    .frozen(false)
                    .duration(1200)
                    .count(10)
                    .load(itemSkeleton)
                    .show(); //default count is 10
        }else{
            return null;
        }
    }

}
