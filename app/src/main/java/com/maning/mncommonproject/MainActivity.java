package com.maning.mncommonproject;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.AppUtils;
import com.maning.mncommonproject.app.BaseApplication;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsProgressListener;
import com.yuqian.mncommonlibrary.http.callback.AbsStringCallback;
import com.yuqian.mncommonlibrary.refresh.CommonRefreshLayout;
import com.yuqian.mncommonlibrary.utils.ToastUtils;

import java.util.TreeMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public String mDownloadUrl = new String("http://oh0vbg8a6.bkt.clouddn.com/app-debug.apk");
    public String mUploadUrl = new String("http://upload.qiniu.com/");
    public String mGetUrl = new String("http://gank.io/api/random/data/Android/20");
    public String mPostUrl = new String("http://xxxx:8132/loan_srv/app/check_version");

    private Context mContext;
    /**
     * 文件上传
     */
    private Button mBtnNetUpload;
    /**
     * 文件下载
     */
    private Button mBtnNetDownload;
    /**
     * get请求
     */
    private Button mBtnNetGet;
    /**
     * post请求
     */
    private Button mBtnNetPost;
    /**
     * post请求-表单提交
     */
    private Button mBtnNetPostForm;
    /**
     * 刷新
     */
    private CommonRefreshLayout mRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initView();

        initPermission();

    }

    private void initPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                10001);
    }

    private void initView() {
        mBtnNetUpload = (Button) findViewById(R.id.btn_net_upload);
        mBtnNetUpload.setOnClickListener(this);
        mBtnNetDownload = (Button) findViewById(R.id.btn_net_download);
        mBtnNetDownload.setOnClickListener(this);
        mBtnNetGet = (Button) findViewById(R.id.btn_net_get);
        mBtnNetGet.setOnClickListener(this);
        mBtnNetPost = (Button) findViewById(R.id.btn_net_post);
        mBtnNetPost.setOnClickListener(this);
        mBtnNetPostForm = (Button) findViewById(R.id.btn_net_post_form);
        mBtnNetPostForm.setOnClickListener(this);
        mRefreshLayout = (CommonRefreshLayout) findViewById(R.id.refreshLayout);

        //初始化刷新
        mRefreshLayout.setEnableRefresh(true);
        mRefreshLayout.setEnableLoadmore(true);
        mRefreshLayout.setEnableAutoLoadmore(true);
        mRefreshLayout.setDisableContentWhenRefresh(true);
        mRefreshLayout.setDisableContentWhenLoading(true);
        mRefreshLayout.setEnableHeaderTranslationContent(false);
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                BaseApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.finishRefresh();
                    }
                }, 2000);
            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                BaseApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshLayout.finishLoadmore();
                    }
                }, 2000);
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_net_upload:
                startUpload();
                break;
            case R.id.btn_net_download:
                startDownload();
                break;
            case R.id.btn_net_get:
                startGet();
                break;
            case R.id.btn_net_post:
                startPost();
                break;
            case R.id.btn_net_post_form:
                startPostForm();
                break;
        }

    }

    private void startPostForm() {
        TreeMap<String, String> map = new TreeMap<>();
        map.put("password", "123");
        map.put("username", "123");
        map.put("deviceId", "000000000000000");
        map.put("captcha", "666666");
        map.put("channelID", "MFJR");
        map.put("versionNum", AppUtils.getAppVersionName());
        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            sb.append(map.get(key));
        }
        String signature = Md5Util.GetMD5Code(sb.toString());
        map.put("signature", signature);
        OkhttpUtils.with()
                .postForm()
                .url("http://xxxx:1406/cardealersys/app/login")
                .addParams("username", "123")
                .addParams("password", "123")
                .addParams("captcha", "666666")
                .addParams("deviceId", "000000000000000")
                .addParams("signature", signature)
                .addParams("channelID", "MFJR")
                .addParams("versionNum", AppUtils.getAppVersionName())
                .execute(new AbsStringCallback() {
                    @Override
                    public void onSuccess(String responseStr) {
                        Logger.i("onSuccess:" + responseStr);
                        ToastUtils.show(responseStr);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        Logger.i("onFailure:" + errorMsg);
                        ToastUtils.show("onFailure:" + errorMsg);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        LoadingDialog.showLoading(mContext);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        LoadingDialog.closeLoading();
                    }
                });
    }

    private void startPost() {
        OkhttpUtils.with()
                .post()
                .url(mPostUrl)
                .addParams("channel", "android")
                .addParams("versionNumbern", "1.2.0")
                .execute(new AbsStringCallback() {
                    @Override
                    public void onSuccess(String responseStr) {
                        Logger.i("onSuccess:" + responseStr);
                        ToastUtils.show(responseStr);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        Logger.i("onFailure:" + errorMsg);
                        ToastUtils.show("onFailure:" + errorMsg);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        LoadingDialog.showLoading(mContext);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        LoadingDialog.closeLoading();
                    }
                });
    }

    private void startGet() {
        OkhttpUtils
                .with()
                .get()
                .url(mGetUrl)
                .execute(new AbsStringCallback() {
                    @Override
                    public void onSuccess(String responseStr) {
                        Logger.i("onSuccess:" + responseStr);
                        ToastUtils.show(responseStr);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        Logger.i("onFailure:" + errorMsg);
                        ToastUtils.show("onFailure:" + errorMsg);
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        LoadingDialog.showLoading(mContext);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        LoadingDialog.closeLoading();
                    }
                });
    }

    private void startDownload() {
        OkhttpUtils
                .with()
                .download()
                .url(mDownloadUrl)
                .downloadPath(Environment.getExternalStorageDirectory().getPath() + "/test.apk")
                .execute(new AbsProgressListener() {
                    @Override
                    public void onProgress(long currentBytes, long contentLength) {
                        Logger.i("下载onProgress----currentBytes:" + currentBytes + ",contentLength:" + contentLength);
                        mBtnNetDownload.setText(currentBytes * 100 / contentLength + "%");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ToastUtils.show("onFailure:" + e.toString());
                    }

                    @Override
                    public void onStart() {
                        Logger.i("onStart");
                    }

                    @Override
                    public void onFinish() {
                        mBtnNetDownload.setText("文件下载-完成");
                        ToastUtils.show("onFinish");
                    }
                });
    }

    private void startUpload() {
        OkhttpUtils
                .with()
                .upload()
                .addFilePath(Environment.getExternalStorageDirectory() + "/test.jpg")
                .url(mUploadUrl)
                .execute(new AbsProgressListener() {
                    @Override
                    public void onProgress(long currentBytes, long contentLength) {
                        Logger.i("上传onProgress----currentBytes:" + currentBytes + ",contentLength:" + contentLength);
                        mBtnNetUpload.setText(currentBytes * 100 / contentLength + "%");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        ToastUtils.show("onFailure:" + e.toString());
                    }

                    @Override
                    public void onStart() {
                        Logger.i("onStart");
                    }

                    @Override
                    public void onFinish() {
                        ToastUtils.show("onFinish");
                        mBtnNetUpload.setText("文件上传-完成");
                    }
                });
    }
}
