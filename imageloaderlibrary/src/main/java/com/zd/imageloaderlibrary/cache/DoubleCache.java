package com.zd.imageloaderlibrary.cache;

import android.graphics.Bitmap;

import com.zd.imageloaderlibrary.interfaces.ImageCache;

import java.io.InputStream;

/**
 * 同时实现内存缓存和本地缓存的实现类
 * Created by zd on 2017/12/12.
 */

public class DoubleCache implements ImageCache<InputStream> {

    private static DoubleCache cache;
    private MemoryCache memoryCache;
    private DiskCache diskCache;

    private DoubleCache(){}

    private DoubleCache(String diskLruPath) throws Exception {

        memoryCache = MemoryCache.getInstance();
        diskCache = DiskCache.getInstance(diskLruPath);
    }

    public static DoubleCache getInstance(String diskLruPath) throws Exception {

        if (cache == null) {

            synchronized (DoubleCache.class) {

                if (cache == null) {
                    cache = new DoubleCache(diskLruPath);
                }
            }
        }

        return cache;
    }

    /**
     * 双缓存的图片存储请使用这个四个参数的方法，不要使用两个参数的
     * @param key
     * @param object
     * @param requireWidth
     * @param requireHeight
     */
    @Override
    public void put(String key, InputStream object
            , int requireWidth, int requireHeight) {

        diskCache.put(key, object, requireWidth, requireHeight);
        Bitmap bitmap = diskCache.get(key, requireWidth, requireHeight);
        if (bitmap != null) {
            memoryCache.put(key, bitmap, requireWidth, requireHeight);
        }
    }

    @Override
    public Bitmap get(String key, int requireWidth, int requireHeight) {

        Bitmap bitmap = null;

        bitmap = memoryCache.get(key, requireWidth, requireHeight);
        if (bitmap == null) {
            bitmap = diskCache.get(key, requireWidth, requireHeight);
        }
        return bitmap;
    }

    @Override
    public boolean isUseDiskCache() {
        return true;
    }
}
