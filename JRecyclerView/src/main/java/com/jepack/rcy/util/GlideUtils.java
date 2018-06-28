package com.jepack.rcy.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.AnimRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;

/**
 * Glide通用类
 * Created by zhanghaihai on 2017/10/9.
 */

public class GlideUtils {

    public static void load(Context context, Object url, ImageView imageView, @Nullable @DrawableRes Integer placeHolder){
        if(isDestroyed(context)) return;
        RequestOptions options = applyWifiLoadingSetting(new RequestOptions());
        if(placeHolder != null){
            options = options.placeholder(placeHolder);
        }
        Glide.with(context).load(url).apply(options)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }
    public static void loadWithListener(Context context, Object url, ImageView imageView, @DrawableRes int placeHolder, RequestListener<Drawable> requestListener){
        if(isDestroyed(context)) return;
        RequestBuilder<Drawable> builder = Glide.with(context).load(url);
        if(requestListener != null) builder.listener(requestListener);
        builder.apply(applyWifiLoadingSetting(new RequestOptions()).placeholder(placeHolder))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    public static void load(Context context, Object url, ImageView imageView, Drawable placeHolder){
        if(isDestroyed(context)) return;
        Glide.with(context).load(url)
                .apply(applyWifiLoadingSetting(new RequestOptions()).placeholder(placeHolder))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    public static void load(Context context, Object url, final SimpleTarget<Drawable> target, Drawable placeHolder){
        if(isDestroyed(context)) return;
        Glide.with(context).load(url)
                .apply(applyWifiLoadingSetting(new RequestOptions()).placeholder(placeHolder))
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(target);
    }

    public static void load(Context context, Object url, ImageView imageView, @DrawableRes int placeHolder, BitmapTransformation transformation){
        if(isDestroyed(context)) return;
        RequestOptions  options = applyWifiLoadingSetting(new RequestOptions()).transform(transformation).placeholder(placeHolder);
        Glide.with(context).load(url)
                .apply(options)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    public static void load(Context context, Object url
            , ImageView imageView
            , Drawable placeHolder, Drawable error, @NonNull BitmapTransformation transformation){
        if(isDestroyed(context)) return;
        RequestOptions options = applyWifiLoadingSetting(new RequestOptions()).transform(transformation);

        Glide.with(context).load(url)
                .apply(options.placeholder(placeHolder).error(error)).transition(DrawableTransitionOptions.withCrossFade()).into(imageView);
    }

    public static void load(Context context, Object url
            , ImageView imageView
            , @DrawableRes int placeHolder
            , BitmapTransformation transformation
            , TransitionOptions<?, Drawable> transitionOptions){
        if(isDestroyed(context)) return;
        Glide.with(context).load(url)
                .apply(applyWifiLoadingSetting(new RequestOptions()).transform(transformation).placeholder(placeHolder).transform(transformation)).transition(transitionOptions).into(imageView);
    }

    public static TransitionOptions<?, Drawable> transition(@AnimRes int animation){
        return GenericTransitionOptions.with(animation);
    }


    private static boolean isDestroyed(Context context){
        if(context instanceof Activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (((Activity) context).isDestroyed() || ((Activity) context).isFinishing())
                    return true;
            } else if (((Activity) context).isFinishing()) {
                return true;
            }
        }
        return false;
    }

    public static RequestOptions applyWifiLoadingSetting(RequestOptions options){
        return options.skipMemoryCache(true).onlyRetrieveFromCache(false);
    }
}
