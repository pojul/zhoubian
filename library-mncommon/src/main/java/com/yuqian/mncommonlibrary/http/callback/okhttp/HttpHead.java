package com.yuqian.mncommonlibrary.http.callback.okhttp;

/**
 * Created by maning on 2017/11/15.
 */

public class HttpHead {

    /**
     * errCode :
     * msg :
     * retCode : success
     */

    private String code;
    private String msg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

}
