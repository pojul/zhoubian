package com.yuqian.mncommonlibrary.http.callback;

import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.yuqian.mncommonlibrary.http.callback.okhttp.HttpHead;
import com.yuqian.mncommonlibrary.http.callback.okhttp.HttpResponse;
import com.yuqian.mncommonlibrary.http.constants.HttpErrorConstants;
import com.yuqian.mncommonlibrary.utils.LogUtil;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public abstract class AbsJsonCallBack<T, J> extends AbsStringCallback {

    public abstract void onSuccess(J body);

    @Override
    public void onSuccess(String responseStr) {
        try {
            //LogUtil.e(responseStr);
            Log.e("AbsJsonCallBack", "onSuccess: " + responseStr);
            Gson gson = new Gson();
            HttpResponse<T, J> response = (HttpResponse<T, J>) gson.fromJson(responseStr, getClasses());
            HttpHead head = response.getHeader();
            J body = response.getBody();
            //判断是不是成功了
            //head为空
            if (head == null) {
                onFailure(com.yuqian.mncommonlibrary.http.callback.okhttp.HttpErrorConstants.ERR_HTTPRESPONSE_HEAD_ERROR_CODE, com.yuqian.mncommonlibrary.http.callback.okhttp.HttpErrorConstants.ERR_HTTPRESPONSE_HEAD_ERROR);
                return;
            }
            if(body == null){
                onFailure(com.yuqian.mncommonlibrary.http.callback.okhttp.HttpErrorConstants.ERR_HTTPRESPONSE_HEAD_ERROR_CODE, com.yuqian.mncommonlibrary.http.callback.okhttp.HttpErrorConstants.ERR_HTTPRESPONSE_ERROR);
                return;
            }
            String errCode = head.getCode();
            String errMsg = head.getMsg();
            String retCode = head.getStatus();
            if ("success".equals(retCode)) {
                //成功
                onSuccess(body);
            } else {
                //失败
                onFailure(errCode, errMsg);
                //登陆过期
            }
            onFinish();
        } catch (Exception e) {
            e.printStackTrace();
            //失败
            onFailure(HttpErrorConstants.ERR_HTTPRESPONSE_JSONPARSE_ERROR_CODE, e.getMessage());
        }
    }

    private Class getClasses() {
        Type[] params = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments();
        Class<T> cls = (Class<T>) params[0];
        return cls;
    }

}
