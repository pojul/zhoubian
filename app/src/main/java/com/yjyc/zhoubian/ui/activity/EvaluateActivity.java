package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.BarUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yanzhenjie.permission.AndPermission;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/10/13/013.
 */

public class EvaluateActivity extends BaseActivity {
    @BindView(R.id.rl_add1)
    RelativeLayout rl_add1;

    @BindView(R.id.rl1)
    RelativeLayout rl1;

    @BindView(R.id.iv1)
    RoundedImageView iv1;

    @BindView(R.id.rl_add2)
    RelativeLayout rl_add2;

    @BindView(R.id.rl2)
    RelativeLayout rl2;

    @BindView(R.id.iv2)
    RoundedImageView iv2;

    @BindView(R.id.rl_add3)
    RelativeLayout rl_add3;

    @BindView(R.id.rl3)
    RelativeLayout rl3;

    @BindView(R.id.iv3)
    RoundedImageView iv3;

    private Context mContext;
    private List<LocalMedia> selectList = new ArrayList<>();
    private int tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate);
        mContext = this;
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("评价", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @OnClick(R.id.rl_add1)
    public void rl_add1(){
        tag = 1;
        selectPhoto();
    }

    @OnClick(R.id.iv_delete1)
    public void iv_delete1(){
        rl1.setVisibility(View.GONE);
        rl_add1.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.rl_add2)
    public void rl_add2(){
        tag = 2;
        selectPhoto();
    }

    @OnClick(R.id.iv_delete2)
    public void iv_delete2(){
        rl2.setVisibility(View.GONE);
        rl_add2.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.rl_add3)
    public void rl_add3(){
        tag = 3;
        selectPhoto();
    }

    @OnClick(R.id.iv_delete3)
    public void iv_delete3(){
        rl3.setVisibility(View.GONE);
        rl_add3.setVisibility(View.VISIBLE);
    }

    private void selectPhoto() {
        PermissionUtils.checkCameraPermission(this, new PermissionUtils.PermissionCallBack() {
            @Override
            public void onGranted() {
                PictureSelector.create(EvaluateActivity.this)
                        .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .maxSelectNum(1)// 最大图片选择数量 int
                        .minSelectNum(1)// 最小选择数量 int
                        .imageSpanCount(4)// 每行显示个数 int
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(true)// 是否可预览图片 true or false
                        .isCamera(true)// 是否显示拍照按钮 true or false
                        .selectionMedia(selectList)
                        .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                        .sizeMultiplier(0.8f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        .compress(true)// 是否压缩 true or false
                        .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                        .minimumCompressSize(300)// 小于300kb的图片不压缩
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            }

            @Override
            public void onDenied() {
                new MaterialDialog.Builder(mContext)
                        .title("提示")
                        .content("当前权限被拒绝导致功能不能正常使用，请到设置界面修改相机和存储权限允许访问")
                        .positiveText("去设置")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                AndPermission.permissionSetting(EvaluateActivity.this)
                                        .execute();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    LocalMedia localMedia = selectList.get(0);
                    String picUrl;
                    RequestOptions options = new RequestOptions()
                            .centerCrop();
                    if (localMedia.isCompressed()) {
                        picUrl = localMedia.getCompressPath();
                    } else {
                        picUrl = localMedia.getPath();
                    }
                    switch (tag){
                        case 1:
                            rl1.setVisibility(View.VISIBLE);
                            rl_add1.setVisibility(View.GONE);
                            Glide.with(context)
                                    .load(picUrl)
                                    .apply(options)
                                    .into(iv1);
                            break;
                        case 2:
                            rl2.setVisibility(View.VISIBLE);
                            rl_add2.setVisibility(View.GONE);
                            Glide.with(context)
                                    .load(picUrl)
                                    .apply(options)
                                    .into(iv2);
                            break;
                        case 3:
                            rl3.setVisibility(View.VISIBLE);
                            rl_add3.setVisibility(View.GONE);
                            Glide.with(context)
                                    .load(picUrl)
                                    .apply(options)
                                    .into(iv3);
                            break;
                    }
                    break;
            }
        }
    }
}