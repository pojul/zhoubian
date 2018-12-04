package com.yjyc.zhoubian.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.MainActivitys;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.GetPic;
import com.yjyc.zhoubian.model.GetPicModel;
import com.yjyc.zhoubian.model.UserInfo;
import com.yjyc.zhoubian.model.UserInfoModel;
import com.yjyc.zhoubian.utils.CustomTimeDown;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity implements CustomTimeDown.OnTimeDownListener{

    @BindView(R.id.bg)
    public ImageView bg;

    private CustomTimeDown mCustomTimeDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        mCustomTimeDown = new CustomTimeDown(3000, 1000);
        mCustomTimeDown.setOnTimeDownListener(this);
        mCustomTimeDown.start();

        getPic();
    }

    private void getPic(){
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.GETPIC)
                .execute(new AbsJsonCallBack<GetPicModel, GetPic>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        mCustomTimeDown.onFinish();
                    }

                    @Override
                    public void onSuccess(GetPic body) {
                        if(body == null || body.app_pic == null){
                            mCustomTimeDown.onFinish();
                            return;
                        }
                        Glide.with(SplashActivity.this).load(body.app_pic).into(bg);
                    }
                });
    }

    @Override
    public void onTick(long l) {
    }

    @Override
    public void OnFinish() {
        startActivity(new Intent(SplashActivity.this, MainActivitys.class));
        finish();
    }
}
