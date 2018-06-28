package com.jepack.rcy.util;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.CompoundButton;
import android.widget.TextView;

/**
 * View 通用操作
 * Created by zhanghaihai on 2017/6/7.
 */

public class ViewUtils {

    public static void setCompoundDrawables(TextView view, Drawable drawableLeft, Drawable drawableTop, Drawable drawableRight, Drawable drawableBottom){
        setCompoundBounds(drawableLeft);
        setCompoundBounds(drawableTop);
        setCompoundBounds(drawableRight);
        setCompoundBounds(drawableBottom);
        if(view instanceof CompoundButton) {
            ((CompoundButton)view).setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        view.setCompoundDrawables(drawableLeft, drawableTop, drawableRight, drawableBottom);

    }

    public static void setCompoundBounds(Drawable drawable){
        if(drawable != null) {
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            drawable.setBounds(0, 0, w, h);
        }
    }

}
