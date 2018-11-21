package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.BarUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.hawk.Hawk;
import com.yanzhenjie.permission.AndPermission;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.EvaluationExpose;
import com.yjyc.zhoubian.model.EvaluationExposeModel;
import com.yjyc.zhoubian.model.ExperienceSave;
import com.yjyc.zhoubian.model.ExperienceSaveModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.LoginModel;
import com.yjyc.zhoubian.ui.view.pickpicview.PickPicView;
import com.yjyc.zhoubian.utils.PermissionUtils;
import com.yjyc.zhoubian.utils.UploadFileUtil;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;
import com.yuqian.mncommonlibrary.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/10/13/013.
 */

public class EvaluateActivity extends BaseActivity {

    @BindView(R.id.note)
    TextView note;
    @BindView(R.id.body)
    EditText body;
    @BindView(R.id.pick_pic_view)
    PickPicView pickPicView;
    @BindView(R.id.submit)
    TextView submit;

    private String evalauteEtHint = "说明：\n1.请客观阐述事实，对您的言论负责，若有辱骂侮辱，或证实滥刷评论，或证实与事实严重不符等，一律封号处理。" +
            "\n2.为防评论泛滥，刷评论，每3天才能评论一次，且对每个人只能评论一次。" +
            "\n3.若一个月不登录，则之前给出过的评论会自动消失。";
    private String exposeEtHint = "说明：\n1.请客观阐述事实，对您的言论负责，若有辱骂侮辱，或证实滥刷评论，或证实与事实严重不符等，一律封号处理。" +
            "\n2.为防止某些人到处抹黑别人，每30天只能揭露一次，且对每个人只能揭露一次。" +
            "\n3.若一个月不登录，则之前给出过的评论会自动消失。";
    private Context mContext;
    private int tag;
    private int cate_id; //类型，1评价，2揭露
    private int mode = 1; //1:提交; 2: 修改
    private Login login;
    private String beExposedUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate);
        mContext = this;
        ButterKnife.bind(this);

        login = Hawk.get("LoginModel");
        cate_id = getIntent().getIntExtra("cate_id", -1);
        beExposedUid = getIntent().getStringExtra("beExposedUid");
        if((cate_id != 1 && cate_id != 2) || login == null || beExposedUid == null){
            showToast("数据错误");
            finish();
            return;
        }

        initView();
    }

    private void initView() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        if(cate_id == 1){
            initTitleBar("评价", v -> onBackPressed());
        }else{
            initTitleBar("揭露", v -> onBackPressed());
            String noteStr = note.getText().toString().replace("评价", "揭露");
            note.setText(noteStr);
            body.setHint(exposeEtHint);
        }
    }

    @OnClick(R.id.submit)
    public void onclick(View view){
        switch (view.getId()){
            case R.id.submit:
                submitEvaluationExpose();
                break;
        }
    }

    public void submitEvaluationExpose(){
        if(body.getText().toString().isEmpty()){
            showToast("内容不能为空");
            return;
        }
        uploadPic();
    }

    private void uploadPic() {
        List<String> pics = pickPicView.getPics();
        LoadingDialog.showLoading(this);
        new UploadFileUtil().uploadFiles(pics, this, new UploadFileUtil.UploadFileCallBack() {
            @Override
            public void finish(List<String> strs) {
                LogUtil.e(new Gson().toJson(strs));
                submitData(strs);
            }

            @Override
            public void error(String msg) {
                LoadingDialog.closeLoading();
                showToast(msg);
            }
        });
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

    private void submitData(List<String> pics) {
        String picStrs = getUploadPics(pics);
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.EVALUATIONEXPOSE)
                .addParams("uid", login.uid + "")
                .addParams("token", (login.token + ""))
                .addParams("cate_id", (cate_id + ""))
                .addParams("be_exposed_user_id", ("" + beExposedUid))
                .addParams("body", body.getText().toString())
                .addParams("pic", picStrs)
                .execute(new AbsJsonCallBack<EvaluationExposeModel, EvaluationExpose>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showToast(errorMsg);
                    }
                    @Override
                    public void onSuccess(EvaluationExpose body) {
                        LoadingDialog.closeLoading();
                        showToast("提交成功");
                        finish();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pickPicView.onActivityResult(requestCode, resultCode, data);
    }

}