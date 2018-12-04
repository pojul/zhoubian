package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.ExperienceCate;
import com.yjyc.zhoubian.model.ExperienceCateModel;
import com.yjyc.zhoubian.model.ExperienceSave;
import com.yjyc.zhoubian.model.ExperienceSaveModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.ui.view.FlowTagView;
import com.yjyc.zhoubian.ui.view.pickpicview.PickPicView;
import com.yjyc.zhoubian.utils.UploadFileUtil;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;
import com.yuqian.mncommonlibrary.utils.LogUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/10/11/011.
 */

public class PublishValuableBookActivity extends BaseActivity {

    @BindView(R.id.cates)
    public FlowTagView cates;
    @BindView(R.id.pick_pic_view)
    public PickPicView pickPicView;
    @BindView(R.id.publish)
    public TextView publish;
    @BindView(R.id.custom_cate)
    public EditText customCate;
    @BindView(R.id.title)
    public EditText title;
    @BindView(R.id.body)
    public EditText body;

    private Context mContext;
    private static final int INIT = 3438;
    private List<ExperienceCate> experienceCates = new ArrayList<>();
    private List<String> cateStrs = new ArrayList<>();
    private Login login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_valuable_book);
        mContext = this;
        ButterKnife.bind(this);

        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("发布宝典", v -> onBackPressed());
        login = Hawk.get("LoginModel");

        mHandler.sendEmptyMessageDelayed(INIT, 100);
    }

    private void initView() {
        /*cates.datas(cateStrs)
                .listener((view, position) -> {
                    //showShortToas("选中了:" + position);
                }).commit();*/


        reqExperienceCate();
    }

    private void initData() {
        if(cateStrs.size() > 0){
            cates.setVisibility(View.VISIBLE);
            cates.datas(cateStrs)
                    .textColor(Color.parseColor("#343434"), Color.WHITE)
                    .backgroundColor(Color.parseColor("#bbbbbb"), Color.parseColor("#d53c3c"))
                    .listener((view, position) -> {
                        //showShortToas("选中了:" + position);
                    }).commit();
        }
    }

    private void reqExperienceCate() {
        LoadingDialog.showLoading(this);
        if(Hawk.get("LoginModel") == null){
            showToast("请先登陆");
            startActivity(new Intent(this, LoginActivity.class));
            LoadingDialog.closeLoading();
            return;
        }
        LoadingDialog.showLoading(this);
        new OkhttpUtils().with()
                .get()
                .url(HttpUrl.EXPERIENCECATE)
                .execute(new AbsJsonCallBack<ExperienceCateModel, List<ExperienceCate>>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showToast(errorMsg);
                    }

                    @Override
                    public void onSuccess(List<ExperienceCate> body) {
                        LoadingDialog.closeLoading();
                        experienceCates = body;
                        for (int i = 0; i < experienceCates.size(); i++) {
                            ExperienceCate experienceCate = experienceCates.get(i);
                            cateStrs.add(experienceCate.title);
                        }
                        initData();
                    }
                });
    }

    private void publish(){
        if(cates.getSelectTags().size() <= 0){
            showToast("请选择宝典类别");
            return;
        }
        if(title.getText().toString().isEmpty()){
            showToast("请填写宝典标题");
            return;
        }
        if(body.getText().toString().isEmpty()){
            showToast("请填写宝典内容");
            return;
        }
        LoadingDialog.showLoading(this);
        List<String> pics = pickPicView.getPics();
        new UploadFileUtil().uploadFiles(pics, this, new UploadFileUtil.UploadFileCallBack() {
            @Override
            public void finish(List<String> strs) {
                LogUtil.e(new Gson().toJson(strs));
                publishValueBook(strs);
            }

            @Override
            public void error(String msg) {
                LoadingDialog.closeLoading();
                showToast(msg);
            }
        });
    }

    public int getCateId(String cate){
        for (int i = 0; i < experienceCates.size(); i++) {
            ExperienceCate experienceCate = experienceCates.get(i);
            if(cate.equals(experienceCate.title)){
                return experienceCate.id;
            }
        }
        return  -1;
    }

    public String getUploadPics(List<String> pics){
        StringBuffer picStrs = new StringBuffer();
        for (int i = 0; i < pics.size(); i++) {
            if(i != 0){
                picStrs.append(",");
            }
            picStrs.append(pics.get(i));
        }
        return picStrs.toString();
    }

    private void publishValueBook(List<String> pics) {
        int cateId = getCateId(cates.getSelectTags().get(0));
        String customCateStr = "" + customCate.getText().toString();
        String picStrs = getUploadPics(pics);
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.EXPERIENCESAVE)
                .addParams("uid", login.uid + "")
                .addParams("token", (login.token + ""))
                .addParams("pid", ("" + cateId))
                .addParams("title", title.getText().toString())
                .addParams("body", body.getText().toString())
                .addParams("custom_cate", customCateStr)
                .addParams("pic", picStrs)
                .execute(new AbsJsonCallBack<ExperienceSaveModel, ExperienceSave>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showToast(errorMsg);
                    }

                    @Override
                    public void onSuccess(ExperienceSave body) {
                        LoadingDialog.closeLoading();
                        showToast("发布成功");
                        finish();
                        //clearData();
                    }
                });
    }

    private void clearData() {
        customCate.setText("");
        title.setText("");
        body.setText("");
        pickPicView.clearData();
    }

    @OnClick({R.id.publish})
    public void click(View v){
        switch (v.getId()){
            case R.id.publish:
                publish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pickPicView.onActivityResult(requestCode, resultCode, data);
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