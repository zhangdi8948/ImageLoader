package com.zd.imageloaderlibrary.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import com.zd.imageloaderlibrary.utils.ImageSizeUtil;
import com.zd.imageloaderlibrary.interfaces.ImageCache;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 本地缓存实现类
 * Created by zd on 2017/12/12.
 */

public class DiskCache implements ImageCache<InputStream> {

    private static final String TAG = "DiskCache";
    private static DiskCache cache;
    /**本地缓存*/
    private DiskLruCache diskLruCache;
    /**DiskLruCache的缓存总大小是50M*/
    private long DISK_LRU_CACHE_SIZE;
    /**DiskLruCache的缓存地址*/
    private String DISK_LRU_PATH;
    private DiskCache(){}

    private DiskCache(String diskLruPath) throws Exception {

        //sd卡可用的状态下使用本地缓存
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            DISK_LRU_PATH = diskLruPath;
            //创建本地缓存目录
            File diskFile = new File(DISK_LRU_PATH);
            if (!diskFile.exists()) {
                diskFile.mkdirs();
            }

            //获取sd卡的可用空间大小，取可用空间的8分之一作为本地缓存的最大可用空间大小
            StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
            if (Build.VERSION.SDK_INT >= 18) {
                DISK_LRU_CACHE_SIZE = statFs.getBlockSizeLong() *
                        statFs.getAvailableBlocksLong() / 8;
            } else {
                DISK_LRU_CACHE_SIZE = statFs.getBlockSize() *
                        statFs.getAvailableBlocks() / 8;
            }

            diskLruCache = DiskLruCache.open(diskFile, 1, 1, DISK_LRU_CACHE_SIZE);
        } else {

            com.zd.imageloaderlibrary.utils.Log.e(TAG, "无法识别sd卡");
            throw new Exception("无法使用本地缓存，原因：无法识别sd卡");
        }
    }

    /**
     * 通过单例
     * @param diskLruPath 本地缓存路径，建议使用Environment.getExternalStorageDirectory()
    .getPath() + "/Android/data/应用包名/cache"，这样应用被删除时缓存一并删除，不用再做其他处理
     * @throws Exception 如果无法识别sd卡或者disklrucache创建失败时会出现异常，表示无法使用
     * 本地缓存
     */
    public static DiskCache getInstance(String diskLruPath) throws Exception {

        if (TextUtils.isEmpty(diskLruPath)) {
            throw new Exception("本地缓存路径不能为空");
        }

        if (cache == null) {

            synchronized (DiskCache.class) {

                if (cache == null) {
                    cache = new DiskCache(diskLruPath);
                }
            }
        }

        return cache;
    }

    @Override
    public void put(String key, InputStream is, int requireWidth, int requireHeight) {

        try {

            File file = new File(DISK_LRU_PATH+"/"+key);
            if (!file.exists()) {
                file.createNewFile();
            }

            DiskLruCache.Editor editor = diskLruCache.edit(key);
            if (editor != null) {

                BufferedOutputStream bos = new BufferedOutputStream(editor.newOutputStream(0));
                BufferedInputStream bis = new BufferedInputStream(is);
                byte[] buffered = new byte[1024];
                int count = 0;
                while ((count = bis.read(buffered, 0, buffered.length)) != -1) {

                    bos.write(buffered, 0, count);
                    bos.flush();
                }

                if (bis != null) {
                    bis.close();
                    bis = null;
                }

                if (bos != null) {
                    bos.close();
                    bos = null;
                }
                editor.commit();
                diskLruCache.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Bitmap get(String key, int requireWidth, int requireHeight) {

        Bitmap bitmap = null;
        try {

            DiskLruCache.Snapshot snap = diskLruCache.get(key);
            if (snap != null) {

                //由于FileInputStream是一个顺序执行的流，通过BitmapFactory.decodeStream
                //二次操作时由于指针位置已经移动到了文件的最后，会造成获得的图片为null的
                //问题。我在从网络获取流直接生成图片的时候做了is.mark()和is.reset处理，
                //以保证能够正常获取到图片，这里读取文件获取的流我们为了解决这个问题使用
                //文件描述符来解决。
                FileInputStream is = (FileInputStream) snap.getInputStream(0);
                FileDescriptor fd = is.getFD();
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(fd, null, opt);
                opt.inSampleSize = ImageSizeUtil.
                        calculateSampleSize(opt, requireWidth, requireHeight);
                opt.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFileDescriptor(fd, null, opt);

                if (is != null) {
                    is.close();
                    is = null;
                }

                if (snap != null) {
                    snap.close();
                    snap = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    @Override
    public boolean isUseDiskCache() {
        return true;
    }
}
