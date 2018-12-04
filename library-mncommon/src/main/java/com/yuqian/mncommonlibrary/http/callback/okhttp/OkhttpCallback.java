package com.yuqian.mncommonlibrary.http.callback.okhttp;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;


import com.google.gson.Gson;
import com.yuqian.mncommonlibrary.http.callback.BaseCallback;
import com.yuqian.mncommonlibrary.http.callback.AbsStringCallback;
import com.yuqian.mncommonlibrary.http.constants.HttpErrorConstants;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * author : maning
 * desc   : OkhttpCallback 回调
 */
public class OkhttpCallback implements Callback {

    private Handler mUITransHandler;
    private BaseCallback mCallbackListener;

    private OkhttpCallback() {

    }

    public OkhttpCallback(BaseCallback callbackListener) {
        this.mUITransHandler = new Handler(Looper.getMainLooper());
        this.mCallbackListener = callbackListener;
        if (this.mCallbackListener == null) {
            throw new NullPointerException("网络请求回调监听器为空");
        }
        //开始执行
        mUITransHandler.post(new Runnable() {
            @Override
            public void run() {
                mCallbackListener.onStart();
            }
        });
    }

    @Override
    public void onFailure(final Call call, final IOException exception) {
        mUITransHandler.post(new Runnable() {
            @Override
            public void run() {
                //取消请求
                if (call.isCanceled()) {
                    mCallbackListener.onFinish();
                    return;
                }
                //处理错误信息
                String errorCode = HttpErrorConstants.ERR_NETEXCEPTION_ERROR_CODE;
                String errorMsg;
                if (exception instanceof UnknownHostException || exception instanceof ConnectException) {
                    //网络未连接
                    errorMsg = HttpErrorConstants.ERR_UNKNOWNHOSTEXCEPTION_ERROR;
                } else if (exception instanceof SocketTimeoutException) {
                    //连接超时
                    errorMsg = HttpErrorConstants.ERR_SOCKETTIMEOUTEXCEPTION_ERROR;
                } else {
                    //其他网络异常
                    Log.e("OkhttpCallback", "onFailure 71");
                    errorMsg = HttpErrorConstants.ERR_NETEXCEPTION_ERROR;
                }
                mCallbackListener.onFailure(errorCode, errorMsg);
                mCallbackListener.onFinish();
            }
        });
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        int responseCode = response.code();
        String responseStr = response.body().string();
        Log.e("OkhttpCallback", "onResponse: 98" + responseStr);
        new Handler(Looper.getMainLooper()).post(()->{
            if (200 == responseCode) {
                if (mCallbackListener instanceof AbsOkhttpByteCallback) {
                    ((AbsStringCallback) mCallbackListener).onFailure(10001 + "", "not support Callback");
                    mCallbackListener.onFinish();
                } else if (mCallbackListener instanceof AbsStringCallback) {
                    if (TextUtils.isEmpty(responseStr)) {
                        mCallbackListener.onFailure(HttpErrorConstants.ERR_HTTPRESPONSE_ERROR_CODE, HttpErrorConstants.ERR_HTTPRESPONSE_ERROR);
                        mCallbackListener.onFinish();
                        return;
                    }
                    ((AbsStringCallback) mCallbackListener).onSuccess(responseStr);
                    mCallbackListener.onFinish();
                }
            } else {
                mCallbackListener.onFailure(response.code() + "", response.message());
                mCallbackListener.onFinish();
            }
        });
        /*mUITransHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("OkhttpCallback", "code: " + response.code());
                    if (200 == response.code()) {
                        if (mCallbackListener instanceof AbsOkhttpByteCallback) {
                            //byte类型
                            final byte[] responseBytes = response.body().bytes();
                            if (responseBytes == null || responseBytes.length == 0) {
                                Log.e("OkhttpCallback", "onResponse: 92");
                                mCallbackListener.onFailure(HttpErrorConstants.ERR_HTTPRESPONSE_ERROR_CODE, HttpErrorConstants.ERR_HTTPRESPONSE_ERROR);
                                return;
                            }
                            ((AbsOkhttpByteCallback) mCallbackListener).onSuccess(responseBytes);
                        } else if (mCallbackListener instanceof AbsStringCallback) {
                            Log.e("OkhttpCallback", "onResponse: 98 response: " + response);
                            Log.e("OkhttpCallback", "onResponse: 98" + new Gson().toJson(response));
                            //字符串类型
                            String responseStr = response.body().string();
                            Log.e("OkhttpCallback", "onResponse: 98 body: " + responseStr);
                            if (TextUtils.isEmpty(responseStr)) {
                                mCallbackListener.onFailure(HttpErrorConstants.ERR_HTTPRESPONSE_ERROR_CODE, HttpErrorConstants.ERR_HTTPRESPONSE_ERROR);
                                return;
                            }
                            ((AbsStringCallback) mCallbackListener).onSuccess(responseStr);
                        }
                    } else {
                        //请求异常
                        mCallbackListener.onFailure(response.code() + "", response.message());
                    }
                } catch (Exception e) {
                    //发生异常
                    Log.e("OkhttpCallback", "onFailure 110 e: " + new Gson().toJson(e));
                    e.printStackTrace();
                    mCallbackListener.onFailure(HttpErrorConstants.ERR_NETEXCEPTION_ERROR_CODE, HttpErrorConstants.ERR_NETEXCEPTION_ERROR);
                } finally {
                    //完成
                    mCallbackListener.onFinish();
                }
            }
        });*/
    }

}
