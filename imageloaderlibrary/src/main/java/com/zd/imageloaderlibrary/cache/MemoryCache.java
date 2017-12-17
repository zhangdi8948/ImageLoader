package com.zd.imageloaderlibrary.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.zd.imageloaderlibrary.interfaces.ImageCache;

/**
 * 内存缓存实现类
 * Created by zd on 2017/12/12.
 */

public class MemoryCache implements ImageCache<Bitmap> {

    /**内存缓存*/
    private LruCache<String, Bitmap> lruCache;
    private static MemoryCache cache;

    private MemoryCache() {

        //获取可用内存大小，取8分之一作为内存缓存的最大值
        int maxSize = (int) (Runtime.getRuntime().maxMemory()/1024/8);
        lruCache = new LruCache<String, Bitmap>(maxSize) {

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes()*value.getHeight()/1024;
            }
        };
    }

    public static MemoryCache getInstance() {

        if (cache == null) {

            synchronized (MemoryCache.class) {

                if (cache == null) {
                    cache = new MemoryCache();
                }
            }
        }

        return cache;
    }

    @Override
    public void put(String key, Bitmap object, int requireWidth, int requireHeight) {

        lruCache.put(key, object);
    }

    @Override
    public Bitmap get(String key, int requireWidth, int requireHeight) {
        return lruCache.get(key);
    }

    @Override
    public boolean isUseDiskCache() {
        return false;
    }
}
