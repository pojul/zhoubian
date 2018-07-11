package com.yuqian.mncommonlibrary.http.callback;

import com.google.gson.Gson;
import com.yuqian.mncommonlibrary.http.constants.HttpErrorConstants;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


public abstract class AbsJsonCallBack<T> extends AbsStringCallback {

    public abstract void onSuccess(T body);

    @Override
    public void onSuccess(String responseStr) {
        try {
            Gson gson = new Gson();
            T body = (T) gson.fromJson(responseStr, getClasses());
            onSuccess(body);
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
