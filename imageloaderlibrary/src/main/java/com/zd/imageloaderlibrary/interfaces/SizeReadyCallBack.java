package com.zd.imageloaderlibrary.interfaces;

import com.zd.imageloaderlibrary.utils.ImageSizeUtil;

/**
 * Created by zd on 2017/12/8.
 */

public interface SizeReadyCallBack {

    /**
     * 在主线程的回调方法
     * @param width 目标图片的宽度，单位是pixels，
     *              如果这个值是{@link ImageSizeUtil#ErrorSize}
     *              表示在测量imageview控件宽高的过程中，imageview被系统回收，无需进行接下来的步骤了
     * @param height 目标图片的高度，单位是pixels，
     *              如果这个值是{@link ImageSizeUtil#ErrorSize}
     *              表示在测量imageview控件宽高的过程中，imageview被系统回收，无需进行接下来的步骤了
     */
    void onSizeReady(int width, int height);
}
