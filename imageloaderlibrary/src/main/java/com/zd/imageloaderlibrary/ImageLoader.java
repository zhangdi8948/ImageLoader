package com.zd.imageloaderlibrary;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import com.zd.imageloaderlibrary.beans.ImageBean;
import com.zd.imageloaderlibrary.cache.DiskLruCache;
import com.zd.imageloaderlibrary.cache.MemoryCache;
import com.zd.imageloaderlibrary.download.HttpDownLoad;
import com.zd.imageloaderlibrary.interfaces.ImageCache;
import com.zd.imageloaderlibrary.interfaces.ImageDownLoad;
import com.zd.imageloaderlibrary.interfaces.SizeReadyCallBack;
import com.zd.imageloaderlibrary.utils.HashKey;
import com.zd.imageloaderlibrary.utils.ImageSizeUtil;
import com.zd.imageloaderlibrary.utils.Log;

import java.lang.ref.SoftReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 为了满足单一职责原则，这个类只负责图片的加载功能
 * Created by zd on 2017/12/4.
 */

public class ImageLoader {

    private static ImageLoader imageLoader;
    //图片缓存
    private ImageCache cache;
    //图片下载
    private ImageDownLoad downLoad;
    /**执行本地操作和网络操作的线程池*/
    private ExecutorService executorService;
    //主线程更新imageview显示图片的handler
    private Handler handler;
    //图片解析成功
    private static final int SUCCESS = 0;
    //图片解析失败
    private static final int ERROR = 1;
    private static final String TAG = "ImageLoader";

    private ImageLoader() {

        cache = MemoryCache.getInstance();
        downLoad = new HttpDownLoad();

        //创建线程池
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().
                availableProcessors());

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == SUCCESS) {

                    ImageBean bean = (ImageBean) msg.obj;
                    SoftReference<ImageView> iv = bean.getIv();
                    String url = bean.getUrl();

                    if (!checkImageViewIsNull(iv)) {
                        String key = (String) iv.get().getTag();
                        if (key.equals(url)) {
                            Bitmap bm = bean.getBm();
                            setBitmapToImageView(bm, iv);
                        }
                    }

                } else {

                    String error = (String) msg.obj;
                    Log.i(TAG, error);
                }
            }
        };
    }

    /**
     * 指定图片缓存类型
     * @param cache 图片缓存类型，已经实现的图片缓存类型有{@link MemoryCache}内存缓存，
     *              {@link DiskLruCache}本地缓存，
     *              {@link com.zd.imageloaderlibrary.cache.DoubleCache}内存与本地双缓存,
     *              你也可以根据自己的需要自定义实现缓存,如果不指定缓存类型，默认的是只内存缓存
     */
    public ImageLoader setImageCache(ImageCache cache) {

        this.cache = cache;
        return imageLoader;
    }

    /**
     * 指定使用哪种网络请求方式下载图片
     * @param downLoad 下载图片的网络请求方式，imageloader默认的是{@link HttpDownLoad}，
     *                 如果您不喜欢这个种网络请求方式也可以自定义。
     *
     */
    public ImageLoader setImageDownLoad(ImageDownLoad downLoad) {

        this.downLoad = downLoad;
        return imageLoader;
    }

    /**获取ImageLoader对象，采取单例模式，避免多次初始化缓存机制和线程池*/
    public static ImageLoader getNewIntance() {

        if (imageLoader == null) {

            synchronized (ImageLoader.class) {

                if (imageLoader == null) {
                    imageLoader = new ImageLoader();
                }
            }
        }

        return imageLoader;
    }

    /**
     * 将图片加载到imageview上，并且你期望图片的宽高由控件宽高决定时使用这个方法
     * @param url 图片加载路径
     * @param iv imageview控件
     */
    public void BindView(final String url, ImageView iv) {

        final SoftReference<ImageView> imageView = new SoftReference<ImageView>(iv);

        ImageSizeUtil util = new ImageSizeUtil();
        util.getExpectImageSize(imageView, new SizeReadyCallBack() {
            @Override
            public void onSizeReady(int width, int height) {

                if (!checkImageViewIsNull(imageView) &&
                        width != ImageSizeUtil.ErrorSize &&
                        height != ImageSizeUtil.ErrorSize) {
                    BindView(url, imageView.get(), width, height);
                }
            }
        });
    }

    /**
     * 将图片加载到imageview上
     * @param url 图片加载路径
     * @param iv imageview控件
     * @param requireWidth 期望图片宽度,如果你希望使用图片的原始宽高，
     *                     不进行缩放处理，这里可以使用{@link ImageSizeUtil#OriginalSize}
     * @param requireHeight 期望图片高度,如果你希望使用图片的原始宽高，
     *                     不进行缩放处理，这里可以使用{@link ImageSizeUtil#OriginalSize}
     */
    public void BindView(String url, ImageView iv, int requireWidth, int requireHeight) {

        String key = HashKey.hashKeyFromUrl(url);
        iv.setTag(key);

        SoftReference<ImageView> imageView = new SoftReference<ImageView>(iv);
        executorService.execute(buildTask(url, key, imageView, requireWidth, requireHeight));
    }

    /**
     * 检查imageview的软引用是否被回收
     * @param iv imageview的软引用
     * @return true已经被回收，false未被回收可以正常使用
     */
    private boolean checkImageViewIsNull(SoftReference<ImageView> iv) {

        if (iv.get() == null) {

            Log.i(TAG, "imageview图片加载控件已经被系统资源回收");
            return true;
        } else {
            return false;
        }
    }

    private Runnable buildTask(final String url, final String key,
                               final SoftReference<ImageView> iv,
                               final int requireWidth, final int requireHeight) {

        return new Runnable() {
            @Override
            public void run() {

                Bitmap bitmap = null;

                bitmap = cache.get(key, requireWidth, requireHeight);
                if (bitmap != null) {

                    sendSuccessMessageToHandler(key, iv, bitmap);

                } else {

                    ImageBean bean = downLoad.
                            getFromHttp(cache, url, key, requireWidth, requireHeight);
                    bitmap = bean.getBm();
                    if (bitmap != null) {

                        sendSuccessMessageToHandler(key, iv, bitmap);
                    } else {

                        sendErrorMessageToHandler(bean.getMsg());
                    }
                }
            }
        };
    }

    private void sendErrorMessageToHandler(String error) {

        Message msg = Message.obtain();
        msg.obj = error;
        msg.what = ERROR;
        handler.sendMessage(msg);
    }

    private void sendSuccessMessageToHandler(String url, SoftReference<ImageView> iv,
                                             Bitmap bitmap) {

        ImageBean bean = new ImageBean();
        bean.setBm(bitmap);
        bean.setIv(iv);
        bean.setUrl(url);
        Message msg = Message.obtain();
        msg.obj = bean;
        msg.what = SUCCESS;
        handler.sendMessage(msg);
    }

    /**将图片设置到imageview控件上*/
    private void setBitmapToImageView(Bitmap bitmap, SoftReference<ImageView> imageView) {

        if (!checkImageViewIsNull(imageView)) {
            imageView.get().setImageBitmap(bitmap);
        }
    }
}
