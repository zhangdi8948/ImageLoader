package com.zd.imageloaderlibrary.interfaces;

import com.zd.imageloaderlibrary.beans.ImageBean;

/**
 * Created by pactera on 2017/12/15.
 */

public interface ImageDownLoad {

    /**
     * 从网络下载图片的方法
     * @param cache 使用的缓存对象，因为下载完成后还要把图片压入缓存，方便下次直接从缓存读取
     * @param url 下载路径
     * @param key 图片对应的唯一key值
     * @param requireWidth 图片期望宽度
     * @param requireHeight 图片期望高度
     * @return 封装的一个包含图片、url、key等信息的实体类
     */
    ImageBean getFromHttp(ImageCache cache, String url, String key, int requireWidth, int requireHeight);
}
