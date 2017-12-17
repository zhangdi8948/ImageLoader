package com.zd.imageloaderlibrary.beans;

import android.graphics.Bitmap;
import android.widget.ImageView;

import java.lang.ref.SoftReference;

/**
 * 图片信息存放类，iv是加载图片的imageview控件，bm是待加载的图片，
 * url是请求地址通过md5获取的缓存key值,msg是加载相关信息
 * Created by zd on 2017/12/4.
 */

public class ImageBean {

    private SoftReference<ImageView> iv;
    private Bitmap bm;
    private String url;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public SoftReference<ImageView> getIv() {
        return iv;
    }

    public void setIv(SoftReference<ImageView> iv) {
        this.iv = iv;
    }

    public Bitmap getBm() {
        return bm;
    }

    public void setBm(Bitmap bm) {
        this.bm = bm;
    }
}
