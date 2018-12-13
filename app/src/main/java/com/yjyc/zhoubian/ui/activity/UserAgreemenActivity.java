package com.yjyc.zhoubian.ui.activity;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.blankj.utilcode.util.BarUtils;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserAgreemenActivity extends BaseActivity {

    @BindView(R.id.web_view)
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreemen);
        ButterKnife.bind(this);
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("用户协议", v -> onBackPressed());

        WebSettings webSettings = webView.getSettings();
        //设置适应Html5的一些方法
        webSettings.setDomStorageEnabled(true);
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();//忽略ssl证书错误,继续加载网页
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        String url = HttpUrl.BASE_URL +  "index/privacy/privacy";
        webView.loadUrl(url);
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.activity_scale_in_anim, R.anim.activity_move_out_anim);
    }

}
