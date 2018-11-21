package com.yjyc.zhoubian.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.adapter.ExperienceReplyAdapter;
import com.yjyc.zhoubian.adapter.PostReplyAdapter;
import com.yjyc.zhoubian.model.ExperienceDetail;
import com.yjyc.zhoubian.model.ExperienceDetailModel;
import com.yjyc.zhoubian.model.Follow;
import com.yjyc.zhoubian.model.FollowModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.ReplyPost;
import com.yjyc.zhoubian.model.ReplyPostList;
import com.yjyc.zhoubian.model.ReplyPostListModel;
import com.yjyc.zhoubian.model.ReplyPostModel;
import com.yjyc.zhoubian.model.UserInfo;
import com.yjyc.zhoubian.utils.DensityUtil;
import com.yjyc.zhoubian.utils.DialogUtil;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 宝典详情
 * Created by Administrator on 2018/10/10/010.
 */

public class ValuableBookDetailActivity extends BaseActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;
    @BindView(R.id.body_ll)
    LinearLayout bodyLl;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.photo)
    RoundedImageView photo;
    @BindView(R.id.nick_name)
    TextView nick_name;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.follow)
    TextView follow;
    @BindView(R.id.view)
    TextView view;
    @BindView(R.id.text)
    TextView text;
    @BindView(R.id.comments)
    TextView comments;
    @BindView(R.id.comment_rl)
    RelativeLayout comment_rl;
    @BindView(R.id.chat_rl)
    RelativeLayout chat_rl;
    @BindView(R.id.collect_rl)
    RelativeLayout collect_rl;
    @BindView(R.id.image_ll)
    LinearLayout image_ll;
    @BindView(R.id.root_rl)
    RelativeLayout rootRl;

    private Context mContext;
    private String id;
    private ExperienceDetail experienceDetail;
    private static final int INIT = 547;
    private Login login;
    private List<ReplyPostList.ReplyPost> replys = new ArrayList<>();
    private ExperienceReplyAdapter replyAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valuable_book_detail);
        id = getIntent().getStringExtra("id");
        if(id == null || id.isEmpty()){
            finish();
            showToast("数据错误");
            return;
        }
        mContext = this;
        ButterKnife.bind(this);
        login = Hawk.get("LoginModel");

        mHandler.sendEmptyMessageDelayed(INIT, 10);
    }

    private void initView() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("宝典详情", v -> onBackPressed());
        replyAdapter = new ExperienceReplyAdapter(this, replys);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//纵向线性布局
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(replyAdapter);

        reqData();
    }

    private void reqData() {
        LoadingDialog.showLoading(this);
        OkhttpUtils okHttpUtils = OkhttpUtils.with()
                .get()
                .url(HttpUrl.EXPERIENCEDETAIL + id);
        if(login != null){
            okHttpUtils
                    .addParams("uid", ("" + login.uid))
                    .addParams("token", ("" + login.token));
        }
        okHttpUtils
                .execute(new AbsJsonCallBack<ExperienceDetailModel, ExperienceDetail>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showToast(errorMsg);
                        finish();
                    }

                    @Override
                    public void onSuccess(ExperienceDetail body) {
                        LoadingDialog.closeLoading();
                        if(body == null){
                            showToast("数据错误");
                            finish();
                            return;
                        }
                        experienceDetail = body;
                        initData();
                        reqReplyLists();
                    }
                });
    }

    @OnClick({R.id.follow, R.id.comment_rl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.follow:
                if(follow.getText().equals("关注")){
                    followUser();
                }else{
                    cancelFollow();
                }
                break;
            case R.id.comment_rl:
                DialogUtil.getInstance().showCommentDialog(this, rootRl, 1, null);
                DialogUtil.getInstance().setDialogClick(str -> {
                    postComment(str);
                });
                break;

        }
    }

    @SuppressLint("SetTextI18n")
    private void initData(){
        bodyLl.setVisibility(View.VISIBLE);
        title.setText(experienceDetail.detail.title);
        if(experienceDetail.user.head_url != null && !experienceDetail.user.head_url.isEmpty()){
            Glide.with(this).load(experienceDetail.user.head_url).into(photo);
        }
        if(experienceDetail.user.nickname == null || experienceDetail.user.nickname.isEmpty()){
            nick_name.setText("");
        }else{
            nick_name.setText(experienceDetail.user.nickname);
        }
        phone.setText("（" + experienceDetail.user.phone.substring(0, 3)+ "****" +
                experienceDetail.user.phone.substring(7, experienceDetail.user.phone.length()) + "）");
        time.setText(experienceDetail.user.login_interval_time);
        text.setText(experienceDetail.detail.body);
        view.setText(("浏览数" + experienceDetail.detail.view));
        if(experienceDetail.detail.pic != null && experienceDetail.detail.pic.size() > 0){
            image_ll.setVisibility(View.VISIBLE);
            for (int i = 0; i < experienceDetail.detail.pic.size(); i++) {
                ImageView iv = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.topMargin = DensityUtil.dp2px(this, 5);
                image_ll.addView(iv, params);
                Glide.with(this).load(experienceDetail.detail.pic.get(i)).into(iv);
            }
        }
        if(experienceDetail != null && login.uid != experienceDetail.detail.uid){
            follow.setVisibility(View.VISIBLE);
        }
        if(experienceDetail.detail.is_follow_user){
            follow.setText("已关注");
        }else{
            follow.setText("关注");
        }
        replyAdapter.setRootVew(rootRl);
        replyAdapter.seExpericeId(experienceDetail.detail.id, experienceDetail.detail.uid);
    }

    public void cancelFollow(){
        if(Hawk.get("LoginModel") == null){
            showToast("请先登陆");
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        if(((Login)Hawk.get("LoginModel")).uid == experienceDetail.detail.uid){
            return;
        }
        LoadingDialog.showLoading(this);
        Login login = Hawk.get("LoginModel");
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.CANCELFOLLOW)
                .addParams("follow_user_id", ("" + experienceDetail.detail.uid))
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .execute(new AbsJsonCallBack<FollowModel, Follow>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        showToast("取消关注失败");
                        LoadingDialog.closeLoading();
                    }

                    @Override
                    public void onSuccess(Follow body) {
                        follow.setText("关注");
                        LoadingDialog.closeLoading();
                    }
                });
    }

    public void followUser(){
        if(Hawk.get("LoginModel") == null){
            showToast("请先登陆");
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        if(((Login)Hawk.get("LoginModel")).uid == experienceDetail.detail.uid){
            return;
        }
        LoadingDialog.showLoading(this);
        Login login = Hawk.get("LoginModel");
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.FOLLOW)
                .addParams("follow_user_id", ("" + experienceDetail.detail.uid))
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .execute(new AbsJsonCallBack<FollowModel, Follow>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        showToast(errorMsg);
                        LoadingDialog.closeLoading();
                    }

                    @Override
                    public void onSuccess(Follow body) {
                        showToast("关注成功");
                        follow.setText("已关注");
                        LoadingDialog.closeLoading();
                    }
                });
    }

    private void postComment(String str) {
        if(Hawk.get("LoginModel") == null){
            showToast("请先登陆");
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }
        Login login = Hawk.get("LoginModel");
        UserInfo userInfo = Hawk.get("userInfo");
        LoadingDialog.showLoading(this);
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.REPLYEXPERIENCE)
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .addParams("reply_uid", ("" + experienceDetail.detail.uid))
                .addParams("article_id", ("" + experienceDetail.detail.id))
                .addParams("body", str)
                .addParams("reply_table_id", ("" + 0))
                .execute(new AbsJsonCallBack<ReplyPostModel, ReplyPost>(){
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        showToast(errorMsg);
                        LoadingDialog.closeLoading();
                    }
                    @Override
                    public void onSuccess(ReplyPost body) {
                        LoadingDialog.closeLoading();
                        ReplyPostList.ReplyPost replyPost = new ReplyPostList().new ReplyPost();
                        replyPost._level = 1;
                        replyPost.id = body.return_body.id;
                        replyPost.body = body.return_body.body;
                        replyPost.nickname = userInfo.nickname;
                        replyPost.head_url = userInfo.head_url_img;
                        replyPost.interval_time = "刚刚";
                        replyPost.article_id = experienceDetail.detail.id;
                        replyPost.uid = login.uid;
                        replyAdapter.addOneLevelData(replyPost);
                    }
                });
    }

    private void reqReplyLists() {
        int uid = 0;
        String token = "";
        Login loginModel = Hawk.get("LoginModel");
        if(loginModel != null){
            uid = loginModel.uid;
            token = loginModel.token;
        }
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.REPLYEXPERIENCELIST)
                .addParams("uid", ("" + uid))
                .addParams("token", token)
                .addParams("experience_id", ("" + experienceDetail.detail.id))
                .execute(new AbsJsonCallBack<ReplyPostListModel, List<ReplyPostList.ReplyPost>>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                    }
                    @Override
                    public void onSuccess(List<ReplyPostList.ReplyPost> body) {
                        LoadingDialog.closeLoading();
                        if(body == null || body.size() <= 0){
                            return;
                        }
                        comments.setText("评论 · " + body.size());
                        replyAdapter.addRawData(body);
                    }
                });
    }


    private ValuableBookDetailActivity.MyHandler mHandler = new ValuableBookDetailActivity.MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<ValuableBookDetailActivity> activity;

        MyHandler(ValuableBookDetailActivity activity) {
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