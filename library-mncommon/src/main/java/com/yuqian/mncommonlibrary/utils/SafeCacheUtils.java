package com.yuqian.mncommonlibrary.utils;

import com.orhanobut.hawk.Hawk;

/**
 * <pre>
 *     author : maning
 *     e-mail : xxx@xx
 *     time   : 2018/07/04
 *     desc   : 安全的保存缓存文件-AES算法来加密数据
 *     version: 1.0
 * </pre>
 */
public class SafeCacheUtils {

    /**
     * 保存数据
     */
    public static <T> boolean put(String key, T value) {
        return Hawk.put(key, value);
    }

    /**
     * 获取数据
     */
    public static <T> T get(String key) {
        return Hawk.get(key);
    }


    /**
     * 获取数据：带默认值
     */
    public static <T> T get(String key, T defaultValue) {
        return Hawk.get(key, defaultValue);
    }

    /**
     * 保存的条数
     */
    public static long count() {
        return Hawk.count();
    }


    /**
     * 删除所有
     */
    public static boolean deleteAll() {
        return Hawk.deleteAll();
    }


    /**
     * 删除单个
     */
    public static boolean delete(String key) {
        return Hawk.delete(key);
    }


    /**
     * 是否包含
     */
    public static boolean contains(String key) {
        return Hawk.contains(key);
    }

}
