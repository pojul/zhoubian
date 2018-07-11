package com.yuqian.mncommonlibrary.http.callback;


/**
 *  @author : maning
 *  @desc   : 父类回调监听
 */
public abstract class BaseCallbackListener {
    /**
     * 请求开始
     */
    public void onStart() {
        //可以不用实现
    }

    /**
     * 请求完成
     */
    public void onFinish() {
        //可以不用实现
    }

    /**
     * 请求失败
     *
     * @param errorCode 失败错误码
     * @param errorMsg  失败错误信息
     */
    public abstract void onFailure(String errorCode, String errorMsg);


}
