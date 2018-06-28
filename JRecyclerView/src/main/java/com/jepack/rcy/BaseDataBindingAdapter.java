package com.jepack.rcy;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jepack.rcy.util.GlideUtils;
import com.jepack.rcy.util.ViewUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Proxy;

/**
 *
 * Created by zhanghaihai on 2017/7/27.
 */
public class BaseDataBindingAdapter {

    @BindingAdapter("url")
    public static void setLoadUrl(WebView view, String url){
        view.loadUrl(url);
    }

    @BindingAdapter("android:src")
    public static void setSrc(ImageView view, Bitmap bitmap) {
        view.setImageBitmap(bitmap);
    }

    @BindingAdapter("localSrc")
    public static void setLocalSrc(ImageView view, Integer resId) {
        if(resId != null) {
            view.setImageResource(resId);
        }
    }

    @BindingAdapter(value = {"imageUrl"})
    public static void setImageUrl(ImageView view, String imageUrl){
        setImageUrl2(view, imageUrl, null);
    }



    @BindingAdapter(value = {"imageUrl", "placeHolder"})
    public static void setImageUrl2(ImageView view, String imageUrl, Drawable placeHolder){
        view.setImageDrawable(null);
        if(imageUrl == null) {
            return;
        }
        view.setImageDrawable(null);
        GlideUtils.load(view.getContext(), imageUrl, view, placeHolder);
    }
//
//    @BindingAdapter(value = {"circleImageUrl", "placeHolder"})
//    public static void setRoundImageUrl(final CircleImageView view, final String imageUrl, Drawable placeHolder){
//        view.setImageDrawable(placeHolder);
//        view.setTag(imageUrl);
//        if(imageUrl == null) {
//            return;
//        }
//        GlideUtils.load(view.getContext(), imageUrl, new SimpleTarget<Drawable>() {
//            @Override
//            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
//                if(view.getTag() != null && view.getTag().equals(imageUrl))
//                    view.setImageDrawable(resource);
//            }
//        }, placeHolder);
//    }
    @BindingAdapter("tag")
    public static void setTag(View view, Object tag) {
        view.setTag(tag);
    }

    @BindingAdapter("rvAdapter")
    public  static void setAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter){
        if(adapter == null) return;
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @BindingAdapter("rvLayoutManager")
    public  static void setLayoutManager(RecyclerView recyclerView, RecyclerView.LayoutManager layoutManager){
        if(layoutManager == null) return;
        recyclerView.setLayoutManager(layoutManager);
    }

    @BindingAdapter(value = {"tvTextLeftIcon", "tvIfShowLeftIcon"}, requireAll = false)
    public static void setLeftIcon(final TextView view, Object leftIcon, Boolean ifShow){
        if(leftIcon == null && (ifShow == null || !ifShow)) return;
        if(leftIcon instanceof Integer) {
            ViewUtils.setCompoundDrawables(view, ContextCompat.getDrawable(view.getContext(), (Integer) leftIcon), null, null, null);
        }else if(leftIcon instanceof Drawable){
            ViewUtils.setCompoundDrawables(view, (Drawable) leftIcon, null, null, null);
        }else if(leftIcon instanceof String){
            Glide.with(view.getContext()).asBitmap().load(leftIcon.toString()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    view.setCompoundDrawables(new BitmapDrawable(view.getResources(), resource), null, null, null);;
                }
            });

        }
    }

    @BindingAdapter(value = {"tvTextRightIcon", "tvIfShowRightIcon"}, requireAll = false)
    public static void setRightIcon(final TextView view, Object rightIcon, Boolean ifShow){
        if(rightIcon == null && (ifShow == null || !ifShow)) return;
        if(rightIcon instanceof Integer) {
            ViewUtils.setCompoundDrawables(view, null, null, ContextCompat.getDrawable(view.getContext(), (Integer) rightIcon), null);
        }else if(rightIcon instanceof Drawable){
            ViewUtils.setCompoundDrawables(view, null, null, (Drawable) rightIcon, null);
        }else if(rightIcon instanceof String){
            Glide.with(view.getContext()).asBitmap().load(rightIcon.toString()).into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    view.setCompoundDrawables(null, null, new BitmapDrawable(view.getResources(), resource), null);
                }
            });
        }
    }

    @BindingAdapter("backgroundColor")
    public static void setBackgroundColor(View view, @ColorInt Integer color){
        view.setBackgroundColor(color);
    }

    @BindingAdapter("backgroundColorRes")
    public static void setBackgroundColorRes(View view, Integer color){
        view.setBackgroundColor(ContextCompat.getColor(view.getContext(), color));
    }

    @BindingAdapter(value = {"backgroundShape", "backgroundShapeColor"})
    public static void setBackgroundColorShape(View view, Drawable drawable, String color){
        if( drawable == null || color == null) return;
        Drawable tmpDrawable;
        if(drawable instanceof GradientDrawable){
            GradientDrawable myGrad = (GradientDrawable)drawable;
            myGrad.setColor(Color.parseColor(color));
            tmpDrawable = myGrad;
        }else{
            tmpDrawable = drawable;
        }
        view.setBackground(tmpDrawable);

    }

    @BindingAdapter(value = {"leftShape", "leftShapeColor", "leftShapeWidth", "leftShapeHeight"})
    public static void setLeftColorShape(TextView view, Drawable drawable, String color, float width, float height){
        Drawable tmpDrawable;
        if(drawable instanceof GradientDrawable){
            GradientDrawable myGrad = (GradientDrawable)drawable;
            myGrad.setColor(Color.parseColor(color));
            tmpDrawable = myGrad;
        }else{
            tmpDrawable = drawable;
        }
        tmpDrawable.setBounds(0, 0, (int)width, (int) height);
        view.setCompoundDrawables(tmpDrawable, null, null, null);

    }
    @BindingAdapter("backgroundDrawableRes")
    public static void setBackgroundDrawableRes(View view, Integer drawable){
        view.setBackground(ContextCompat.getDrawable(view.getContext(), drawable));
    }

    @BindingAdapter("seekChangeListener")
    public static void setSeekBarChangeListener(SeekBar seekBar, SeekBar.OnSeekBarChangeListener listener){
        if(listener != null)
            seekBar.setOnSeekBarChangeListener(listener);
    }

    @BindingAdapter("selectedBackground")
    public static void setSelectedBackground(View view, Object background){
        if(background == null ) return;
            if(background instanceof Integer) {
//                ViewCompat.setBackground(view, ContextCompat.getDrawable(view.getContext(), (Integer) background));
                view.setBackgroundColor(ContextCompat.getColor(view.getContext(), (Integer) background));
            }else if(background instanceof Drawable){
                ViewCompat.setBackground(view, (Drawable) background);
            }else if(background instanceof String){
//            Glide.with(view.getContext()).load(background.toString()).asBitmap().diskCacheStrategy(DiskCacheStrategy.RESULT).into(new SimpleTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                    ViewCompat.setBackground(view, new BitmapDrawable(view.getResources(), resource));
//                }
//            });
                final WeakReference<View> holdView = new WeakReference<>(view);
                Glide.with(view.getContext()).asBitmap().load(background.toString()).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                       if(holdView.get() != null) {
                           ViewCompat.setBackground(holdView.get(), new BitmapDrawable(holdView.get().getResources(), resource));
                       }
//                    view.setCompoundDrawables(null, null, new BitmapDrawable(view.getResources(), resource), null);
                    }
                });
        }
    }

    @BindingAdapter(value = {"selected"})
    public static void setSourceSelected(View view, Boolean selected){
        if(selected != null)
            view.setSelected(selected);
    }

    @BindingAdapter(value = {"itemChecked"})
    public static void setSourceSelected(CheckBox view, Boolean checked){
        if(checked != null)
            view.setSelected(checked);
    }

    @BindingAdapter(value = {"listAdapter", "listLayoutManager"})
    public static void bindListData(RecyclerView rcy, ListAdapter listAdapter, RecyclerView.LayoutManager layoutManager){
        if(listAdapter != null && layoutManager != null){
            rcy.setLayoutManager(layoutManager);
            rcy.setAdapter(listAdapter);
        }
    }

    @BindingAdapter(value = "itemDecoration")
    public static void addItemDecoration(RecyclerView rcy, XItemDecoration itemDecoration){
        if(itemDecoration != null){
            rcy.addItemDecoration(itemDecoration);
        }
    }
}

