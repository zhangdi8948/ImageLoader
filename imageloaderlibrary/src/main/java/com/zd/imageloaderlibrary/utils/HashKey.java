package com.zd.imageloaderlibrary.utils;

import java.security.MessageDigest;

/**
 * Created by zd on 2017/12/14.
 */

public class HashKey {

    /**
     * 这个方法是将url值转换成md5值，因为url中可能含有特殊字符会影响在Android中的使用
     * @param url 图片请求网址
     * @return url的md5值
     */
    public static String hashKeyFromUrl(String url) {

        String cacheKey = null;

        try {

            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(url.getBytes());
            cacheKey = bytesToHexString(digest.digest());

        } catch (Exception e) {
            cacheKey = String.valueOf(url.hashCode());
            e.printStackTrace();
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] digest) {

        StringBuilder builder = new StringBuilder();
        for (int i=0; i<digest.length; i++) {

            String hex = Integer.toHexString(0xFF & digest[i]);
            if (hex.length() == 1) {
                builder.append('0');
            }
            builder.append(hex);
        }

        return builder.toString();
    }
}
