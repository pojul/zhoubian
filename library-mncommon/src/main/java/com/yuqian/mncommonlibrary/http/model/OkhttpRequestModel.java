package com.yuqian.mncommonlibrary.http.model;


import com.yuqian.mncommonlibrary.http.callback.AbsProgressListener;
import com.yuqian.mncommonlibrary.http.callback.BaseCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.MediaType;

/**
 * @author : maning
 * @desc : Okhttp请求参数相关
 */
public class OkhttpRequestModel {

    /**
     * 自定义回调
     */
    private BaseCallback callbackListener;
    /**
     * 原始的回调
     */
    private Callback callback;
    /**
     * 请求方式标记:0:get,1:post
     */
    private int requestMothed;
    /**
     * 请求地址
     */
    private String httpUrl;
    /**
     * 请求参数
     */
    private Map<String, String> paramsMap;
    /**
     * 请求头
     */
    private Map<String, String> headersMap;
    /**
     * Content-Type
     */
    private MediaType mediaType;
    /**
     * 请求Tag
     */
    private Object tag;
    /**
     * 文件上传的路径
     */
    private List<String> uploadFilePaths;
    /**
     * 下载文件保存的路径
     */
    private String downloadPath;
    /**
     * 上传下载的回调
     */
    private AbsProgressListener progressListener;

    public AbsProgressListener getProgressListener() {
        return progressListener;
    }

    public void setProgressListener(AbsProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    public List<String> getUploadFilePaths() {
        if(uploadFilePaths == null){
            uploadFilePaths = new ArrayList<>();
        }
        return uploadFilePaths;
    }

    public void setUploadFilePaths(List<String> uploadFilePaths) {
        this.uploadFilePaths = uploadFilePaths;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public BaseCallback getCallbackListener() {
        return callbackListener;
    }

    public void setCallbackListener(BaseCallback callbackListener) {
        this.callbackListener = callbackListener;
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public int getRequestMothed() {
        return requestMothed;
    }

    public void setRequestMothed(int requestMothed) {
        this.requestMothed = requestMothed;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public Map<String, String> getParamsMap() {
        if (paramsMap == null) {
            paramsMap = new HashMap<>();
        }
        return paramsMap;
    }

    public void setParamsMap(Map<String, String> paramsMap) {
        this.paramsMap = paramsMap;
    }

    public Map<String, String> getHeadersMap() {
        if (headersMap == null) {
            headersMap = new HashMap<>();
        }
        return headersMap;
    }

    public void setHeadersMap(Map<String, String> headersMap) {
        this.headersMap = headersMap;
    }
}
