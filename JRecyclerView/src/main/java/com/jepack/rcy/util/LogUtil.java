package com.jepack.rcy.util;

import android.util.Log;

import com.jepack.rcy.BuildConfig;

/**
 * 日志打印，支持多参数自动加空格，提供日志保存功能
 * Created by haihai.zhang on 2017/2/22.
 */

public class LogUtil {
    private static final String _TAG = "AUDIO_LOG";

    public static void i(Object ... msg){
        StringBuilder sb = new StringBuilder();
        for(Object m:msg){
            m = m + "";
            sb.append(m).append(" ");
        }
        String endMsg = getStackInfo(new Throwable()) + sb.toString();
        Log.i(_TAG, endMsg);
    }

    public static void debug(String tag, Object ... msg){
        if(!BuildConfig.DEBUG) return;
        if(msg == null) return;
        StringBuilder sb = new StringBuilder();
        for(Object m:msg){
            m = m + "";
            sb.append(m).append(" ");
        }
        String endMsg = getStackInfo(new Throwable()) + sb.toString();
        Log.d(tag, endMsg);
    }

    public static void d(Object ... msg){
        debug(_TAG, msg);
    }

    private static String getStackInfo(Throwable throwable){
        if(throwable != null ){
            try{
                StackTraceElement[] s = throwable.getStackTrace();
                if(s != null && s.length > 2){
                    return "[" + s[2].getFileName() + ":" + s[2].getLineNumber() + "] ";
                }else{
                    if(s != null && s.length > 1){
                        return "[" + s[1].getFileName() + ":" + s[1].getLineNumber() + "] ";
                    }else{
                        return "";
                    }
                }
            }catch(Exception e){
                return "";
            }
        }else{
            return "";
        }

    }

    public static void e(Object ...msg) {
        StringBuilder sb = new StringBuilder();
        for(Object m:msg){
            m = m + "";
            sb.append(m).append(" ");
        }
        Log.e(_TAG, getStackInfo(new Throwable()) + sb.toString());

    }
}
