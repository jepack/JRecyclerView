package com.jepack.rcy;

import android.databinding.BindingConversion;

/**
 * 用于DataBinding中自动将指定类型转换为需要的数据类型
 * Created by zhanghaihai on 2017/8/23.
 */
public class BaseDataBindingConversion {

    /**
     * Long转字符串
     * @param number
     * @return
     */
    @BindingConversion
    public static CharSequence convertLong2Str(Long number) {
        if(number == null){
            return "";
        }
        return String.valueOf(number);
    }

    @BindingConversion
    public static CharSequence convertLong2Str(Integer number) {
        if(number == null){
            return "";
        }
        return String.valueOf(number);
    }
}
