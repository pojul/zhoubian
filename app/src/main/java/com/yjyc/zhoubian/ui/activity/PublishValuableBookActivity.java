package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.blankj.utilcode.util.BarUtils;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.PostCollection;
import com.yjyc.zhoubian.model.PostCollectionModel;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import net.masonliu.multipletextview.library.MultipleTextViewGroup;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/10/11/011.
 */

public class PublishValuableBookActivity extends BaseActivity {
    @BindView(R.id.main_rl)
    public MultipleTextViewGroup main_rl;

    private Context mContext;
    private static final int INIT = 3438;
    //private List<>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_valuable_book);
        mContext = this;
        ButterKnife.bind(this);

        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("发布宝典", v -> onBackPressed());
        List<String> dataList = new ArrayList<String>();
        dataList.add("APP使用教程");
        dataList.add("用好APP的成功经验");
        dataList.add("防骗防坑经验");
        dataList.add("如何去看杰伦演唱会");
        main_rl.setTextViews(dataList);

        mHandler.sendEmptyMessageDelayed(INIT, 100);
    }

    private void initView() {
        reqExperienceCate();
    }

    private void initData() {

    }

    private void reqExperienceCate() {
        LoadingDialog.showLoading(this);
        if(Hawk.get("LoginModel") == null){
            showToast("请先登陆");
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        LoadingDialog.showLoading(this);
        OkhttpUtils.with()
                .get()
                .url(HttpUrl.EXPERIENCECATE)
                .execute(new AbsJsonCallBack<PostCollectionModel, PostCollection>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                    }

                    @Override
                    public void onSuccess(PostCollection body) {
                        LoadingDialog.closeLoading();
                    }
                });
    }

    private PublishValuableBookActivity.MyHandler mHandler = new PublishValuableBookActivity.MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<PublishValuableBookActivity> activity;

        MyHandler(PublishValuableBookActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (activity.get() == null) {
                return;
            }
            switch (msg.what) {
                case INIT:
                    activity.get().initView();
                    break;
            }
        }
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.activity_scale_in_anim, R.anim.activity_move_out_anim);
    }

}