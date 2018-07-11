package com.yuqian.mncommonlibrary.http;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * 获取Retrofit对象
 */
public class RetrofitClientUtils {

    public static String API_SERVER_URL = "http://www.xxx.com/";

    public static Retrofit mRetrofit;
    public static Retrofit mRetrofitString;

    /**
     * 基本的retrofit
     */
    public static Retrofit retrofit() {
        if (mRetrofit == null) {
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(API_SERVER_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(OkhttpUtils.getOkhttpClient())
                    .build();
        }
        return mRetrofit;
    }

    /**
     * String类型的retrofit
     */
    public static Retrofit retrofitStringConverter() {
        if (mRetrofitString == null) {
            mRetrofitString = new Retrofit.Builder()
                    .baseUrl(API_SERVER_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(OkhttpUtils.getOkhttpClient())
                    .build();
        }
        return mRetrofitString;
    }

}
