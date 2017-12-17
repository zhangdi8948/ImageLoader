package com.zd.imageloaderlibrary.utils;

/**
 * Created by pactera on 2017/12/12.
 */

public class Log {

    public static boolean isOpenLog = true;

    public static void i(String tag, String msg) {

        if (isOpenLog) {
            android.util.Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg) {

        if (isOpenLog) {
            android.util.Log.e(tag, msg);
        }
    }
}
