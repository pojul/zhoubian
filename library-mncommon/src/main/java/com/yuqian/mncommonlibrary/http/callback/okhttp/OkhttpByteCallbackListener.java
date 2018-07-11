package com.yuqian.mncommonlibrary.http.callback.okhttp;


import com.yuqian.mncommonlibrary.http.callback.BaseCallbackListener;

/**
 * <pre>
 *     author : maning
 *     e-mail : xxx@xx
 *     time   : 2018/07/03
 *     desc   : 返回byte[] 类型的回调监听
 *     version: 1.0
 * </pre>
 */
public abstract class OkhttpByteCallbackListener extends BaseCallbackListener {

    public abstract void onSuccess(byte[] responseByte);

}
