package com.yuqian.mncommonlibrary.http;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.readystatesoftware.chuck.ChuckInterceptor;
import com.yuqian.mncommonlibrary.MBaseManager;
import com.yuqian.mncommonlibrary.http.callback.AbsProgressListener;
import com.yuqian.mncommonlibrary.http.callback.BaseCallback;
import com.yuqian.mncommonlibrary.http.callback.okhttp.OkhttpCallback;
import com.yuqian.mncommonlibrary.http.model.OkhttpRequestModel;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import me.jessyan.progressmanager.ProgressManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author : maning
 * @desc : Okhttp 请求工具类
 */
public class OkhttpUtils {

    /**
     * 默认Callback
     */
    private Callback defaultCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {

        }
    };

    /**
     * MediaType
     */
    private static MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");
    public static MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
    public static MediaType MEDIA_TYPE_JPEG = MediaType.parse("image/jpeg;charset=utf-8");
    public static MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png;charset=utf-8");
    public static MediaType MEDIA_TYPE_IMAGE = MediaType.parse("image/*;charset=utf-8");

    /**
     * okHttpClient
     */
    private static OkHttpClient okHttpClient;
    /**
     * 请求的集合
     */
    private static HashMap<String, Call> mCallHashMap = new HashMap<>();
    /**
     * OkhttpUtils对象
     */
    private static OkhttpUtils okhttpUtils;
    /**
     * GET请求标记
     */
    public static final int GET = 0;
    /**
     * POST请求标记
     */
    private static final int POST_STRING = 1;
    private static final int POST_FORM = 2;
    /**
     * 文件上传
     */
    private static final int UPLOAD = 3;
    /**
     * 下载
     */
    private static final int DOWNLOAD = 4;
    /**
     * 请求相关参数
     */
    private OkhttpRequestModel okhttpRequestModel;

    private OkhttpUtils() {
        okhttpRequestModel = new OkhttpRequestModel();
    }

    /**
     * 回去当前实例
     *
     * @return
     */
    public static OkhttpUtils with() {
        okhttpUtils = new OkhttpUtils();
        return okhttpUtils;
    }

    /**
     * 设置请求Url
     *
     * @param url
     * @return
     */
    public OkhttpUtils url(String url) {
        okhttpRequestModel.setHttpUrl(url);
        return okhttpUtils;
    }

    /**
     * GET方法
     *
     * @return
     */
    public OkhttpUtils get() {
        okhttpRequestModel.setRequestMothed(GET);
        return okhttpUtils;
    }

    /**
     * POST方法-Json方式
     *
     * @return
     */
    public OkhttpUtils post() {
        okhttpRequestModel.setRequestMothed(POST_STRING);
        return okhttpUtils;
    }

    /**
     * POST方法-表单方式
     *
     * @return
     */
    public OkhttpUtils postForm() {
        okhttpRequestModel.setRequestMothed(POST_FORM);
        return okhttpUtils;
    }

    /**
     * 上传
     *
     * @return
     */
    public OkhttpUtils upload() {
        okhttpRequestModel.setRequestMothed(UPLOAD);
        return okhttpUtils;
    }

    /**
     * 下载
     *
     * @return
     */
    public OkhttpUtils download() {
        okhttpRequestModel.setRequestMothed(DOWNLOAD);
        return okhttpUtils;
    }

    /**
     * 下载文件保存的路径
     *
     * @param filePath
     * @return
     */
    public OkhttpUtils downloadPath(String filePath) {
        okhttpRequestModel.setDownloadPath(filePath);
        return okhttpUtils;
    }

    /**
     * 设置上传文件的路径
     *
     * @param uploadFilePaths
     * @return
     */
    public OkhttpUtils filePaths(List<String> uploadFilePaths) {
        okhttpRequestModel.setUploadFilePaths(uploadFilePaths);
        return okhttpUtils;
    }

    /**
     * 设置上传文件的路径
     *
     * @param uploadFilePath
     * @return
     */
    public OkhttpUtils addFilePath(String uploadFilePath) {
        okhttpRequestModel.getUploadFilePaths().add(uploadFilePath);
        return okhttpUtils;
    }


    /**
     * 给请求打上TAG,默认当前Url
     *
     * @param tag
     * @return
     */
    public OkhttpUtils tag(Object tag) {
        okhttpRequestModel.setTag(tag);
        return okhttpUtils;
    }

    /**
     * 设置参数
     *
     * @param paramsMap
     * @return
     */
    public OkhttpUtils params(Map<String, String> paramsMap) {
        okhttpRequestModel.setParamsMap(paramsMap);
        return okhttpUtils;
    }

    /**
     * 设置单个参数
     *
     * @param paramsKey
     * @param paramsValue
     * @return
     */
    public OkhttpUtils addParams(String paramsKey, String paramsValue) {
        okhttpRequestModel.getParamsMap().put(paramsKey, paramsValue);
        return okhttpUtils;
    }

    /**
     * 设置请求头
     *
     * @param headersMap
     * @return
     */
    public OkhttpUtils headers(Map<String, String> headersMap) {
        okhttpRequestModel.setHeadersMap(headersMap);
        return okhttpUtils;
    }

    /**
     * 设置单个请求头
     *
     * @param headerKey
     * @param headerValue
     * @return
     */
    public OkhttpUtils addHeader(String headerKey, String headerValue) {
        okhttpRequestModel.getHeadersMap().put(headerKey, headerValue);
        return okhttpUtils;
    }

    /**
     * 设置MediaType
     *
     * @param mediaType
     * @return
     */
    public OkhttpUtils setMediaType(MediaType mediaType) {
        okhttpRequestModel.setMediaType(mediaType);
        return okhttpUtils;
    }

    /**
     * 开始执行：默认回调
     *
     * @param callback 回调监听
     */
    public void execute(Callback callback) {
        okhttpRequestModel.setCallback(callback);
        //开始请求
        sendRequest();
    }

    /**
     * 开始执行：
     *
     * @param callbackListener 自定义回调监听
     */
    public void execute(BaseCallback callbackListener) {
        okhttpRequestModel.setCallbackListener(callbackListener);
        //开始请求
        sendRequest();
    }

    /**
     * 上传下载进度回调
     *
     * @param progressListener
     */
    public void execute(AbsProgressListener progressListener) {
        okhttpRequestModel.setProgressListener(progressListener);
        //开始请求
        sendRequest();
    }


    private void sendRequest() {
        if (okhttpRequestModel == null) {
            throw new NullPointerException("OkhttpRequestModel初始化失败");
        }
        //获取参数
        //请求地址
        String httpUrl = okhttpRequestModel.getHttpUrl();
        //请求Tag
        Object tag = okhttpRequestModel.getTag();
        //请求头
        Map<String, String> headersMap = okhttpRequestModel.getHeadersMap();
        //请求参数
        Map<String, String> paramsMap = okhttpRequestModel.getParamsMap();
        //回调监听
        BaseCallback callbackListener = okhttpRequestModel.getCallbackListener();
        //Content-Type
        MediaType mediaType = okhttpRequestModel.getMediaType();
        //上传文件的路径
        List<String> uploadFilePaths = okhttpRequestModel.getUploadFilePaths();
        //下载保存的路径
        final String downloadPath = okhttpRequestModel.getDownloadPath();
        //请求方法
        int requestMothed = okhttpRequestModel.getRequestMothed();
        //上传下载的监听
        final AbsProgressListener progressListener = okhttpRequestModel.getProgressListener();


        //获取OkHttpClient
        final OkHttpClient client = getOkhttpClient();
        //初始化请求
        final Request.Builder requestBuild = new Request.Builder();
        //添加请求地址
        requestBuild.url(httpUrl);
        //添加请求头
        if (headersMap != null && headersMap.size() > 0) {
            for (String key : headersMap.keySet()) {
                requestBuild.addHeader(key, headersMap.get(key));
            }
        }
        //添加Tag
        if (tag != null) {
            requestBuild.tag(okhttpRequestModel.getTag());
        } else {
            requestBuild.tag(httpUrl);
        }
        //GET---POST
        if (requestMothed == GET) {
            if (paramsMap != null && paramsMap.size() > 0) {
                //重新拼接参数
                httpUrl = appendParams(httpUrl, paramsMap);
                requestBuild.url(httpUrl);
            }
            requestBuild.get();
        } else if (requestMothed == POST_FORM) {
            //POST-表单形式
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            for (String key : paramsMap.keySet()) {
                formBodyBuilder.add(key, paramsMap.get(key));
            }
            requestBuild.post(formBodyBuilder.build());
        } else if (requestMothed == POST_STRING) {
            //POST-Json字符串形式
            String requestContent = new Gson().toJson(paramsMap);
            if (TextUtils.isEmpty(requestContent)) {
                requestContent = "";
            }
            //设置请求体
            if (mediaType == null) {
                mediaType = MEDIA_TYPE_JSON;
            }
            RequestBody requestBody = RequestBody.create(mediaType, requestContent);
            requestBuild.post(requestBody);
        } else if (requestMothed == UPLOAD) {
            //上传文件
            MultipartBody.Builder builder = new MultipartBody.Builder();
            //设置文件
            if (uploadFilePaths != null) {
                for (int i = 0; i < uploadFilePaths.size(); i++) {
                    String path = uploadFilePaths.get(i);
                    if (!TextUtils.isEmpty(path)) {
                        File file = new File(path);
                        String fileType = getMimeType(file.getName());
                        builder.addFormDataPart("upload" + i, file.getName(), RequestBody.create(MediaType.parse(fileType), file));
                    }
                }
            }
            //追加参数
            if (paramsMap != null && paramsMap.size() > 0) {
                for (String key : paramsMap.keySet()) {
                    String value = paramsMap.get(key);
                    builder.addFormDataPart(key, value);
                }
            }
            requestBuild.post(builder.build());
            //添加上传监听
            if (progressListener != null) {
                ProgressManager.getInstance().addRequestListener(httpUrl, progressListener);
            }
            //下载开始
            if (progressListener != null) {
                progressListener.onStart();
            }
        } else if (requestMothed == DOWNLOAD) {
            //添加下载监听
            if (progressListener != null) {
                ProgressManager.getInstance().addResponseListener(httpUrl, progressListener);
            }
            //下载开始
            if (progressListener != null) {
                progressListener.onStart();
            }
            //开启线程下载
            final String finalHttpUrl = httpUrl;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        //同步方法，放在子线程
                        Response response = client.newCall(requestBuild.build()).execute();
                        InputStream is = response.body().byteStream();
                        //保存路径
                        File file = new File(downloadPath);
                        FileOutputStream fos = new FileOutputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(is);
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = bis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                        }
                        fos.flush();
                        fos.close();
                        bis.close();
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (progressListener != null) {
                            //当外部发生错误时,使用此方法可以通知所有监听器的 onError 方法
                            ProgressManager.getInstance().notifyOnErorr(finalHttpUrl, e);
                        }
                    }
                }
            }).start();
            return;
        }
        //设置回调
        Callback callback;
        if (callbackListener != null) {
            callback = new OkhttpCallback(callbackListener);
        } else {
            callback = okhttpRequestModel.getCallback();
        }
        //执行请求
        Call call = client.newCall(requestBuild.build());
        if (callback != null) {
            call.enqueue(callback);
        } else {
            call.enqueue(defaultCallback);
        }

        //设置取消任务标签
        mCallHashMap.put(httpUrl, call);
    }

    /**
     * 获取文件MimeType
     *
     * @param filename 文件名
     * @return
     */
    private static String getMimeType(String filename) {
        FileNameMap filenameMap = URLConnection.getFileNameMap();
        String contentType = filenameMap.getContentTypeFor(filename);
        if (contentType == null) {
            //* exe,所有的可执行程序
            contentType = "application/octet-stream";
        }
        return contentType;
    }


    /**
     * 拼接Get请求Url
     *
     * @param url
     * @param params
     * @return
     */
    protected String appendParams(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Set<String> keys = params.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            builder.appendQueryParameter(key, params.get(key));
        }
        return builder.build().toString();
    }

    //-----------------------分割线-----------------------------

    /**
     * 动态设置okhttpBuilder
     *
     * @param okhttpBuilder
     */
    public static void setOkhttpBuilder(OkHttpClient.Builder okhttpBuilder) {
        OkhttpUtils.okHttpClient = ProgressManager.getInstance().with(okhttpBuilder).build();
    }

    /**
     * 获取OkHttpClient
     *
     * @return
     */
    public static OkHttpClient getOkhttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = getOkhttpDefaultBuilder();
            okHttpClient = ProgressManager.getInstance().with(builder).build();
        }
        return okHttpClient;
    }

    /**
     * 获取默认OkHttpClient.Builder
     *
     * @return
     */
    @NonNull
    public static OkHttpClient.Builder getOkhttpDefaultBuilder() {
        //默认信任所有的证书
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                X509Certificate[] x509Certificates = new X509Certificate[0];
                return x509Certificates;
            }
        };
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30000, TimeUnit.MILLISECONDS);
        builder.readTimeout(30000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(30000, TimeUnit.MILLISECONDS);
        builder.sslSocketFactory(sslSocketFactory, trustManager);
        builder.hostnameVerifier(DO_NOT_VERIFY);
        builder.addInterceptor(new ChuckInterceptor(MBaseManager.getApplication()));
        return builder;
    }


    //-----------------------分割线-----------------------------

    /**
     * 取消一个请求
     *
     * @param tag
     */
    public static void cancle(Object tag) {
        try {
            if (mCallHashMap != null && mCallHashMap.size() > 0) {
                //获取KEY的集合
                Set<Map.Entry<String, Call>> keyEntries = mCallHashMap.entrySet();
                //如果包含KEY
                if (keyEntries.contains(tag)) {
                    //获取对应的Call
                    Call call = mCallHashMap.get(tag);
                    if (call != null) {
                        //如果没有被取消 执行取消的方法
                        if (!call.isCanceled()) {
                            call.cancel();
                        }
                        //移除对应的KEY
                        mCallHashMap.remove(tag);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * 取消所有请求
     */
    public static void cancleAll() {
        try {
            if (mCallHashMap != null && mCallHashMap.size() > 0) {
                //获取KEY的集合
                Set<Map.Entry<String, Call>> keyEntries = mCallHashMap.entrySet();
                for (Map.Entry<String, Call> entry : keyEntries) {
                    //key
                    String key = entry.getKey();
                    //获取对应的Call
                    Call call = entry.getValue();
                    if (call != null) {
                        //如果没有被取消 执行取消的方法
                        if (!call.isCanceled()) {
                            call.cancel();
                        }
                        //移除对应的KEY
                        mCallHashMap.remove(key);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

}
