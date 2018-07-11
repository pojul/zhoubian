package com.yuqian.mncommonlibrary.http.callback;


import com.yuqian.mncommonlibrary.http.constants.HttpErrorConstants;

import org.json.JSONObject;

public abstract class AbsJSONObjectCallback extends AbsStringCallback {

    public abstract void onSuccess(JSONObject responseObj);

    @Override
    public void onSuccess(String responseStr) {
        try {
            JSONObject resultObj = new JSONObject(responseStr);
            onSuccess(resultObj);
        } catch (Exception e) {
            onFailure(HttpErrorConstants.ERR_HTTPRESPONSE_DATA_ERROR_CODE, e.getMessage());
        }
    }
}
