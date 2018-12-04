package com.yuqian.mncommonlibrary.http.callback.okhttp;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.yuqian.mncommonlibrary.http.constants.HttpErrorConstants;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * <pre>
 *     author : maning
 *     e-mail : xxx@xx
 *     time   : 2018/01/11
 *     desc   : 自定义Callback：直接返回Model
 *     version: 1.0
 * </pre>
 */
public abstract class AbsOkhttpCallback<T extends Object> implements Callback {

    private static final String TAG = "OKhttpCallBack";

    private Handler handler;
    private Type type;
    private Class<T> clazz;

    public AbsOkhttpCallback(Type type) {
        handler = new Handler();
        this.type = type;
    }

    public AbsOkhttpCallback(Class<T> clazz) {
        handler = new Handler();
        this.clazz = clazz;
    }

    public AbsOkhttpCallback() {
        handler = new Handler();
    }

    @Override
    public void onFailure(final Call call, final IOException exception) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (call.isCanceled()) {
                    //取消请求
                    onFinish();
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
                    Log.e("AbsOkhttpCallback", "onFailure 72");
                    errorMsg = HttpErrorConstants.ERR_NETEXCEPTION_ERROR;
                }
                onFail(errorCode, errorMsg);
                onFinish();
            }
        });
    }

    @Override
    public void onResponse(Call call, final Response response) throws IOException {
        Log.e("AbsOkhttpCallback", "onResponse: 83 response: " + response);
        try {
            if (200 == response.code()) {
                String body = response.body().string();
                String url = response.request().url().toString();
                //解析
                T result;
                Gson gson = new Gson();
                boolean gsonException = false;
                try {
                    if (type != null) {
                        result = gson.fromJson(body, type);
                    } else if (clazz != null) {
                        result = gson.fromJson(body, clazz);
                    } else {
                        result = (T) gson.fromJson(body, getClasses());
                    }
                } catch (Exception e) {
                    gsonException = true;
                    result = null;
                }
                final T resultFinal = result;
                final boolean isGsonExceptionFinal = gsonException;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (resultFinal != null) {
                            onSuccess(resultFinal);
                        } else {
                            if (isGsonExceptionFinal) {
                                //解析异常
                                onFail(HttpErrorConstants.ERR_HTTPRESPONSE_JSONPARSE_ERROR_CODE, HttpErrorConstants.ERR_HTTPRESPONSE_JSONPARSE_ERROR);
                            } else {
                                onFail(HttpErrorConstants.ERR_HTTPRESPONSE_ERROR_CODE, HttpErrorConstants.ERR_HTTPRESPONSE_ERROR);
                            }
                        }
                    }
                });
            } else {
                String url = response.request().url().toString();
                Log.i(TAG, "onResponse-----url:" + url + "code:" + response.code());
                //接口异常
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //解析异常
                        Log.e("AbsOkhttpCallback", "onResponse 128");
                        onFail(response.code() + "", HttpErrorConstants.ERR_NETEXCEPTION_ERROR);
                    }
                });
            }
        } catch (Exception e) {
            //异常
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //解析异常
                    onFail(HttpErrorConstants.ERR_HTTPRESPONSE_DATA_ERROR_CODE, HttpErrorConstants.ERR_HTTPRESPONSE_DATA_ERROR);
                }
            });
        } finally {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //完成
                    onFinish();
                }
            });
        }
    }

    private Class getClasses() {
        Type t = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) t).getActualTypeArguments();
        Class<T> cls = (Class<T>) params[0];
        return cls;
    }

    /**
     * 成功
     */
    public abstract void onSuccess(T result);

    /**
     * 失败
     */
    public abstract void onFail(String errorCode, String errorMsg);

    /**
     * 完成
     */
    public abstract void onFinish();

}
