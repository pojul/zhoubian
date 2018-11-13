package com.yuqian.mncommonlibrary.http.callback.okhttp;

import com.yuqian.mncommonlibrary.http.callback.okhttp.HttpHead;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/10/15/015.
 */

public class HttpResponse<T, J> implements Serializable {

    private static final long serialVersionUID = -686453405647539973L;

    public HttpHead getHeader() {
        return header;
    }

    public void setHeader(HttpHead header) {
        this.header = header;
    }

    /**
     * head : {"errCode":"","msg":"","retCode":"success"}
     */
    private HttpHead header;

    private J body;


    public J getBody() {
        return body;
    }

    public void setBody(J body) {
        this.body = body;
    }
}