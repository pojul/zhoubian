package com.yuqian.mncommonlibrary.http.callback.okhttp;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;


import com.yuqian.mncommonlibrary.http.callback.BaseCallbackListener;
import com.yuqian.mncommonlibrary.http.callback.AbsStringCallbackListener;
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
    private BaseCallbackListener callbackListener;

    private OkhttpCallback() {

    }

    public OkhttpCallback(BaseCallbackListener callbackListener) {
        this.mUITransHandler = new Handler(Looper.getMainLooper());
        this.callbackListener = callbackListener;
        if (this.callbackListener == null) {
            throw new NullPointerException("网络请求回调监听器为空");
        }
        //开始执行
        this.callbackListener.onStart();
    }

    @Override
    public void onFailure(final Call call, final IOException exception) {
        mUITransHandler.post(new Runnable() {
            @Override
            public void run() {
                //取消请求
                if (call.isCanceled()) {
                    callbackListener.onFinish();
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
                    errorMsg = HttpErrorConstants.ERR_NETEXCEPTION_ERROR;
                }
                callbackListener.onFailure(errorCode, errorMsg);
                callbackListener.onFinish();
            }
        });
    }

    @Override
    public void onResponse(Call call, final Response response) throws IOException {
        mUITransHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (200 == response.code()) {
                        if (callbackListener instanceof OkhttpByteCallbackListener) {
                            //byte类型
                            final byte[] responseBytes = response.body().bytes();
                            if (responseBytes == null || responseBytes.length == 0) {
                                callbackListener.onFailure(HttpErrorConstants.ERR_HTTPRESPONSE_ERROR_CODE, HttpErrorConstants.ERR_HTTPRESPONSE_ERROR);
                                return;
                            }
                            ((OkhttpByteCallbackListener) callbackListener).onSuccess(responseBytes);
                        } else if (callbackListener instanceof AbsStringCallbackListener) {
                            //字符串类型
                            final String responseStr = response.body().string();
                            if (TextUtils.isEmpty(responseStr)) {
                                callbackListener.onFailure(HttpErrorConstants.ERR_HTTPRESPONSE_ERROR_CODE, HttpErrorConstants.ERR_HTTPRESPONSE_ERROR);
                                return;
                            }
                            ((AbsStringCallbackListener) callbackListener).onSuccess(responseStr);
                        }
                    } else {
                        //请求异常
                        callbackListener.onFailure(response.code() + "", response.message());
                    }
                } catch (Exception e) {
                    //发生异常
                    callbackListener.onFailure(HttpErrorConstants.ERR_NETEXCEPTION_ERROR_CODE, HttpErrorConstants.ERR_NETEXCEPTION_ERROR);
                } finally {
                    //完成
                    callbackListener.onFinish();
                }
            }
        });
    }

}
