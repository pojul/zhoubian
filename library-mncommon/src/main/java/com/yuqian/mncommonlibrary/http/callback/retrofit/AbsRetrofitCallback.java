package com.yuqian.mncommonlibrary.http.callback.retrofit;


import com.yuqian.mncommonlibrary.http.constants.HttpErrorConstants;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * author : maning
 * desc   : RetrofitCallback 直接返回Model
 */
public abstract class AbsRetrofitCallback<M> implements Callback<M> {

    /**
     * 成功
     *
     * @param model 实体类
     */
    public abstract void onSuccess(M model);

    /**
     * 失败
     *
     * @param errorCode 失败错误码
     * @param errorMsg  失败消息
     */
    public abstract void onFailure(String errorCode, String errorMsg);

    /**
     * 完成
     */
    public void onFinish() {
    }

    /**
     * 开始
     */
    public void onStart() {
    }

    public AbsRetrofitCallback() {
        onStart();
    }

    @Override
    public void onResponse(Call<M> call, Response<M> response) {
        if (response.isSuccessful()) {
            onSuccess(response.body());
        } else {
            onFailure(response.code() + "", response.errorBody().toString());
        }
        onFinish();
    }

    @Override
    public void onFailure(Call<M> call, Throwable exception) {
        if (call.isCanceled()) {
            onFinish();
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
        onFailure(errorCode, errorMsg);
        onFinish();
    }
}
