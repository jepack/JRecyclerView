package com.jepack.rcy;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * TODO Add class comments here.
 * Created by zhanghaihai on 2018/5/24.
 */
public class XItemMenu extends LinearLayout {
    public final static int MENU_SHOWING = 0;
    public final static  int MENU_SHOWED = MENU_SHOWING + 1;
    public final static  int MENU_HIDED = MENU_SHOWING + 2;
    private @MENU_STATE int state;
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MENU_SHOWING, MENU_SHOWED, MENU_HIDED})
    public @interface MENU_STATE{}

    public XItemMenu(Context context) {
        super(context);
    }

    public XItemMenu(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public XItemMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public int getState() {
        return state;
    }

    public void setState(@MENU_STATE int state){
        this.state = state;
    }
}
