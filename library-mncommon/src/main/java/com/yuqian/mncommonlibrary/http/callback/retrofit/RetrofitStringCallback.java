package com.yuqian.mncommonlibrary.http.callback.retrofit;

import com.yuqian.mncommonlibrary.http.callback.BaseCallback;
import com.yuqian.mncommonlibrary.http.callback.AbsStringCallback;
import com.yuqian.mncommonlibrary.http.constants.HttpErrorConstants;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * <pre>
 *     author : maning
 *     e-mail : xxx@xx
 *     time   : 2018/07/05
 *     desc   : Retrofit String类型的回调
 *     version: 1.0
 * </pre>
 */
public class RetrofitStringCallback implements Callback<String> {

    private BaseCallback callbackListener;

    public RetrofitStringCallback(BaseCallback callbackListener) {
        this.callbackListener = callbackListener;
        if (this.callbackListener == null) {
            throw new NullPointerException("网络请求回调监听器为空");
        }
        this.callbackListener.onStart();
    }

    @Override
    public void onResponse(Call<String> call, Response<String> response) {
        if (response.code() == 200) {
            String body = response.body();
            if (callbackListener instanceof AbsStringCallback) {
                ((AbsStringCallback) callbackListener).onSuccess(body);
            }
        } else {
            callbackListener.onFailure(response.code() + "", response.errorBody().toString());
        }
        callbackListener.onFinish();
    }

    @Override
    public void onFailure(Call<String> call, Throwable exception) {
        if (call.isCanceled()) {
            callbackListener.onFinish();
            return;
        }
        exception.printStackTrace();
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
}
