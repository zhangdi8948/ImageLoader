package com.zd.imageloaderlibrary.download;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.zd.imageloaderlibrary.beans.ImageBean;
import com.zd.imageloaderlibrary.interfaces.ImageCache;
import com.zd.imageloaderlibrary.interfaces.ImageDownLoad;
import com.zd.imageloaderlibrary.utils.ImageSizeUtil;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 使用httpurlconnection请求网络下载图片，并根据缓存类型进行缓存
 * Created by zd on 2017/12/15.
 */

public class HttpDownLoad implements ImageDownLoad {


    @Override
    public ImageBean getFromHttp(ImageCache cache, String url, String key, int requireWidth, int requireHeight) {

        Bitmap bitmap = null;
        ImageBean bean = new ImageBean();

        try {

            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            if (connection.getResponseCode() == 200) {

                BufferedInputStream is = new BufferedInputStream(connection.getInputStream());

                if (!cache.isUseDiskCache()) {
                    BitmapFactory.Options opt = new BitmapFactory.Options();
                    opt.inJustDecodeBounds = true;
                    is.mark(1024 * 1024);
                    BitmapFactory.decodeStream(is, null, opt);
                    opt.inSampleSize = ImageSizeUtil.
                            calculateSampleSize(opt, requireWidth, requireHeight);
                    opt.inJustDecodeBounds = false;
                    is.reset();
                    bitmap = BitmapFactory.decodeStream(is, null, opt);
                    cache.put(key, bitmap, requireWidth, requireHeight);
                } else {

                    cache.put(key, is, requireWidth, requireHeight);
                    bitmap = cache.get(key, requireWidth, requireHeight);
                }

                if (is != null) {
                    is.close();
                    is = null;
                }

                if (connection != null) {
                    connection.disconnect();
                    connection = null;
                }
            } else {

                BufferedInputStream bis = new BufferedInputStream(connection.getErrorStream());
                byte[] buffer = new byte[1024];
                StringBuilder builder = new StringBuilder();
                while (bis.read(buffer, 0, buffer.length) != -1) {

                    builder.append(new String(buffer));
                }
                bean.setMsg(builder.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        bean.setBm(bitmap);
        return bean;
    }
}
