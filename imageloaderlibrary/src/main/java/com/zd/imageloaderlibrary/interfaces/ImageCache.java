package com.zd.imageloaderlibrary.interfaces;

import android.graphics.Bitmap;

/**
 *定义缓存接口，让所有图片缓存类型都实现这个接口，实现开闭原则
 * Created by zd on 2017/12/12.
 */

public interface ImageCache<T> {

    /**
     * 将对象压入缓存的方法
     * @param key 对象在缓存中对应的唯一key值
     * @param object 对象本身，内存缓存时用的是bitmap类型，本地缓存时用的是流对象，所以这个地方用的是一个泛型对象
     *               方便以后自定义缓存类型时使用
     * @param requireWidth 期望宽度
     * @param requireHeight 期望高度
     */
    void put(String key, T object, int requireWidth, int requireHeight);

    /**
     * 从缓存中取出图片的方法
     * @param key 图片在缓存中对应的唯一key值
     * @param requireWidth 图片的期望宽度
     * @param requireHeight 图片的期望高度
     * @return
     */
    Bitmap get(String key, int requireWidth, int requireHeight);

    /**
     * 图片是否使用了本地缓存，网络下载的时候判断是将流直接转换成图片还是缓存到本地
     * @return 使用了本地缓存返回true，否则返回false
     */
    boolean isUseDiskCache();
}
