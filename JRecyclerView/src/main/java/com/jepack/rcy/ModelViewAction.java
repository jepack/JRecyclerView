package com.jepack.rcy;

import android.support.annotation.IdRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * TODO Add class comments here.
 * Created by zhanghaihai on 2018/5/20.
 */
@Retention(RetentionPolicy.RUNTIME) @Target(METHOD)
public @interface ModelViewAction {
    Class value() default ModelViewAction.class;
    @IdRes int id();
    String actionType() default "";
}
