package com.zd.imageloaderlibrary.utils;

import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.zd.imageloaderlibrary.interfaces.SizeReadyCallBack;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * this class is in charge of get the ImageView's expect width and height, and
 * calculate the options.inSampleSize.
 * 这类个类是负责获取ImageView的期望宽高和计算图片的缩放比例用的。
 * 根据设计模式中的单一职责原则，这个类只负责计算图片大小的工作
 * Created by zd on 2017/12/8.
 */

public class ImageSizeUtil {

    private SizeReadyCallBack cbs;
    private LayoutSizeListener layoutSizeListener;
    private static final String TAG = "ImageSizeUtil";
    private SoftReference<ImageView> iv;
    /**
     * if you don't want to change bitmap's width and height ,you can use this value
     * as your expect bitmap's width and height.
     * 如果你想使用图片的原始宽高，不进行图片的缩放，可以使用这个值作为期望宽高
     */
    public static final int OriginalSize = Integer.MAX_VALUE;

    /**
     * if the imageview has been recycled in the measure processing, will return this value.
     * 在测量过程中如果imageview被系统回收将会返回这个错误值
     */
    public static final int ErrorSize = Integer.MIN_VALUE;

    /**
     * 在用户没有明确指定图片宽高的时候，根据imageview控件宽高获取图片的期望宽高
     * @param imageView 要加载图片的控件
     * @param cb 获取到图片期望宽高的回调方法
     */
    public void getExpectImageSize(SoftReference<ImageView> imageView, SizeReadyCallBack cb) {

        iv = imageView;

        //获取图片的期望宽高
        int width = getSize(false);

        int height = getSize(true);

        //如果获取的值均大于0，即控件有指定准确的大小值，或者是wrapcontent（表示期望图片原始大小）
        //则回调获取到的图片期望宽高;
        //否则，即如果有matchparent时，
        //为控件的ViewTreeObserver添加监听，直到获取到控件的宽高值为止
        if (width > 0 && height > 0) {

            cb.onSizeReady(width, height);

        } else {

            cbs = cb;

            if (iv.get() != null) {
                ViewTreeObserver observer = iv.get().getViewTreeObserver();
                layoutSizeListener = new LayoutSizeListener(this);
                observer.addOnPreDrawListener(layoutSizeListener);
            } else {
                cbs.onSizeReady(ErrorSize,ErrorSize);
            }
        }
    }

    /**
     * 获取图片加载控件的宽度或者高度值
     * @param isHeight true获取图片高度，false获取图片宽度
     * @return 根据条件得到的高度或者宽度值
     */
    private int getSize(boolean isHeight) {

        ImageView view = iv.get();
        int value = 0;

        if (view != null) {

            DisplayMetrics metrics = view.getContext().getResources().getDisplayMetrics();
            ViewGroup.LayoutParams lp = view.getLayoutParams();

            //获取控件的测量宽度
            if (isHeight) {
                value = view.getHeight();
            } else {
                value = view.getWidth();
            }

            //如果没有测量宽度值，则获取imageview在布局文件中的宽度设置值
            if (value <= 0) {

                if (isHeight) {
                    value = lp.height;
                } else {
                    value = lp.width;
                }

            }

            //如果在布局中没有指定具体值，而是wrapcontent
            if (value <= 0 && value == ViewGroup.LayoutParams.WRAP_CONTENT) {

                if (isHeight) {
                    value = metrics.heightPixels;
                } else {
                    value = metrics.widthPixels;
                }

            }
        } else {

            cbs.onSizeReady(ErrorSize, ErrorSize);
        }

        return value;
    }

    /***
     * 通过图片原始高度与期望宽高，计算图片缩放比例
     * @param options 储存了图片的原始宽高信息
     * @param requireWidth 期望的图片宽度
     * @param requirHeight 期望的图片高度
     * @return 缩放比例
     */
    public static int calculateSampleSize(BitmapFactory.Options options,
                                          int requireWidth, int requirHeight) {

        int sampleSize = 1;
        int width = options.outWidth;
        int height = options.outHeight;

        while (width > requireWidth && height > requirHeight) {

            //根据inSampleSize的解释，他必须是一个2的倍数才有效，否则系统会自动将这个值
            //转换成接近的2的倍数
            sampleSize *= 2;
            width = width / sampleSize;
            height = height / sampleSize;
        }

        return sampleSize;
    }

    /**
     * 控件的ViewTreeObserver中每次测量控件的宽高获取到值时会回调这个方法，
     * 直到获取到的控件宽高都大于0，调用获取到图片期望宽高的回调方法，
     * 移除、结束监听
     */
    private void checkCurrenDiments() {

        int width = getSize(false);
        int height = getSize(true);

        if (width > 0 && height > 0) {

            cbs.onSizeReady(width, height);
            ImageView view = iv.get();

            if (view != null) {
                ViewTreeObserver observer = view.getViewTreeObserver();
                if (observer.isAlive()) {
                    observer.removeOnPreDrawListener(layoutSizeListener);
                }
            }
            layoutSizeListener = null;
        }
    }

    private static class LayoutSizeListener implements ViewTreeObserver.OnPreDrawListener {

        private WeakReference<ImageSizeUtil> imageSizeUtil;

        public LayoutSizeListener(ImageSizeUtil imagesize) {
            imageSizeUtil = new WeakReference<ImageSizeUtil>(imagesize);
        }

        @Override
        public boolean onPreDraw() {

            Log.v(TAG, "OnGlobalLayoutListener called listener=" + this);
            ImageSizeUtil util = imageSizeUtil.get();
            if (util != null) {

                util.checkCurrenDiments();
            }
            return true;
        }
    }
}
