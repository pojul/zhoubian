package com.yjyc.zhoubian.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.adapter.CardAdapter;
import com.yjyc.zhoubian.adapter.PostReplyAdapter;
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.im.chat.ui.ChatActivity;
import com.yjyc.zhoubian.model.Follow;
import com.yjyc.zhoubian.model.FollowModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.LoginModel;
import com.yjyc.zhoubian.model.PostCollection;
import com.yjyc.zhoubian.model.PostCollectionListsModel;
import com.yjyc.zhoubian.model.PostCollectionModel;
import com.yjyc.zhoubian.model.PostDetail;
import com.yjyc.zhoubian.model.PostDetailModel;
import com.yjyc.zhoubian.model.ReplyPost;
import com.yjyc.zhoubian.model.ReplyPostList;
import com.yjyc.zhoubian.model.ReplyPostListModel;
import com.yjyc.zhoubian.model.ReplyPostModel;
import com.yjyc.zhoubian.model.UserGroups;
import com.yjyc.zhoubian.model.UserInfo;
import com.yjyc.zhoubian.ui.activity.LoginActivity;
import com.yjyc.zhoubian.ui.activity.MyPublishActivity;
import com.yjyc.zhoubian.ui.activity.PostDetailsActivity;
import com.yjyc.zhoubian.ui.activity.ReportActivity;
import com.yjyc.zhoubian.utils.DateUtil;
import com.yjyc.zhoubian.utils.DialogUtil;
import com.yjyc.zhoubian.utils.MyDistanceUtil;
import com.yjyc.zhoubian.utils.PermissionUtils;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/10/12/012.
 */

@SuppressLint("ValidFragment")
public class PostDetailsFragment extends BaseFragment {
    private PostDetailsActivity activity;
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.recyclerview2)
    RecyclerView recyclerView2;
    Unbinder unbinder;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.user_photo)
    RoundedImageView userPhoto;
    @BindView(R.id.nick_name)
    TextView nickName;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.come_before)
    TextView comeBefore;
    @BindView(R.id.follow)
    TextView follow;
    @BindView(R.id.base_info)
    TextView baseInfo;
    @BindView(R.id.visit_num)
    TextView visitNum;
    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.post_note)
    TextView postNote;
    @BindView(R.id.post_image1)
    ImageView postImage1;
    @BindView(R.id.post_image2)
    ImageView postImage2;
    @BindView(R.id.post_image3)
    ImageView postImage3;
    @BindView(R.id.key_word)
    TextView keyWord;
    @BindView(R.id.price_ll)
    LinearLayout priceLl;
    @BindView(R.id.red_package_ll)
    LinearLayout redPackageLl;
    @BindView(R.id.iv_collect)
    ImageView ivCollect;
    @BindView(R.id.tv_collect)
    TextView tvCollect;
    @BindView(R.id.root_rl)
    RelativeLayout rootRl;
    @BindView(R.id.red_passwd_tv)
    TextView redPasswdTv;
    @BindView(R.id.red_package_password)
    TextView redPackagePassword;
    @BindView(R.id.rob_red_package_note)
    TextView robRedPackageNote;
    @BindView(R.id.rea_package_left)
    TextView reaPackageLeft;
    @BindView(R.id.comment_num)
    TextView commentNum;


    //MyAdapter myAdapter;
    PostReplyAdapter replyAdapter;
    CardAdapter myAdapter2;

    private int postId = -1;
    private static final String TAG = "PostDetailsFragment";
    private PostDetail postDetail;
    private static final int INIT = 328;
    private List<ReplyPostList.ReplyPost> replys = new ArrayList<>();
    private UserInfo userInfo;


    public PostDetailsFragment(PostDetailsActivity activity, int postId) {
        this.activity = activity;
        this.postId = postId;
    }

    public PostDetailsFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_details, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void initViews() {
        //myAdapter = new MyAdapter();
        replyAdapter = new PostReplyAdapter(getActivity(), replys);
        myAdapter2 = new CardAdapter();
        recyclerView.setNestedScrollingEnabled(false);
        if(postId != -1){
            getPostDetails();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mHandler.sendEmptyMessageDelayed(INIT, 10);
    }

    private void getPostDetails() {
        LoadingDialog.showLoading(getActivity());
        /**需去掉身份验证**/
        int uid = -1;
        String token = "";
        Login loginModel = Hawk.get("LoginModel");
        if(loginModel != null){
            uid = loginModel.uid;
            token = loginModel.token;
        }
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.POSTDETAIL)
                .addParams("id", ("" + postId))
                .addParams("uid", ("" + uid))
                .addParams("token", token)
                .execute(new AbsJsonCallBack<PostDetailModel, PostDetail>() {

                    @Override
                    public void onSuccess(PostDetail body) {
                        //LoadingDialog.closeLoading();
                        //Log.e(TAG, "onSuccess");
                        postDetail = body;
                        if(activity != null){
                            activity.setPostDeatil(postDetail);
                        }
                        initData();
                        rootRl.setVisibility(View.VISIBLE);
                        reqReplyLists();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showShortToats(errorMsg);
                        //if(errorMsg.equals("抱歉，帖子不存在或已被删除")){
                        getActivity().finish();
                       // }
                    }
                });

    }

    @Override
    View initView() {
        return null;
    }

    @Override
    protected void Loading() {

    }

    private void initData(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//纵向线性布局

        recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setAdapter(myAdapter);
        recyclerView.setAdapter(replyAdapter);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());//纵向线性布局

        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.setAdapter(myAdapter2);

        myAdapter2.setOnItemClickListener(new com.yjyc.zhoubian.adapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                startActivity(new Intent(getActivity(), PostDetailsActivity.class));
            }

            @Override
            public void onLongClick(int position) {

            }

            @Override
            public void onDeleteClick(ImageView iv_delete, boolean isDown, int[] position) {
                showPopWindow(iv_delete, isDown, position);
            }
        });

        title.setText(postDetail.title);
        nickName.setText(postDetail.user_name);
        phone.setText(postDetail.phone);
        if(postDetail.head_url != null && !postDetail.head_url.isEmpty()){
            Glide.with(this).load((HttpUrl.BASE_URL + postDetail.head_url)).into(userPhoto);
        }
        if(postDetail.come_before != null && !postDetail.come_before.isEmpty()){
            comeBefore.setText(postDetail.come_before + "来过");
        }else{
            comeBefore.setVisibility(View.GONE);
        }
        if(Hawk.get("LoginModel") != null && ((Login)Hawk.get("LoginModel")).uid != postDetail.user_id){
            follow.setVisibility(View.VISIBLE);
        }
        if(postDetail.is_follow_user){
            follow.setText("关注");
        }else{
            follow.setText("已关注");
        }
        if(postDetail.is_collect){
            ivCollect.setSelected(true);
            tvCollect.setSelected(true);
        }
        BDLocation location = BaseApplication.getIntstance().getLocation();
        if(location == null){
            distance.setText("获取位置失败");
        }else{
            double ddistance = DistanceUtil.getDistance(new LatLng(postDetail.lat, postDetail.lon), new LatLng(location.getLatitude(), location.getLongitude()));
            String distanceStr = MyDistanceUtil.getDisttanceStr(ddistance);
            distance.setText("距离" + distanceStr);
        }

        StringBuffer baseInfoStr = new StringBuffer();
        if(Hawk.contains("userGroups")){
            ArrayList<UserGroups.UserGroup> userGroups = Hawk.get("userGroups");
            for (int i = 0; i < userGroups.size(); i++) {
                UserGroups.UserGroup userGroup = userGroups.get(i);
                if(postDetail.user_group_id == userGroup.id){
                    baseInfoStr.append("【");
                    baseInfoStr.append(userGroup.title);
                    baseInfoStr.append("】");
                    break;
                }
            }
        }
        baseInfoStr.append(postDetail.user_name);
        baseInfoStr.append("发布于");
        baseInfoStr.append(DateUtil.getPostDetailDate(postDetail.create_time));
        baseInfo.setText(baseInfoStr.toString());
        visitNum.setText(("浏览数" + postDetail.view));
        if(postDetail.price > 0){
            String priceStr = postDetail.price + "";
            if(postDetail.price_unit != null){
                priceStr = priceStr + postDetail.price_unit;
            }
            price.setText(priceStr);
        }else{
            priceLl.setVisibility(View.GONE);
        }
        postNote.setText(postDetail.body);
        if(postDetail.pic != null){
            if(postDetail.pic.size() >= 1){
                postImage1.setVisibility(View.VISIBLE);
                Glide.with(this).load(postDetail.pic.get(0)).into(postImage1);
            }
            if(postDetail.pic.size() >= 2){
                postImage2.setVisibility(View.VISIBLE);
                Glide.with(this).load(postDetail.pic.get(1)).into(postImage2);
            }
            if(postDetail.pic.size() >= 3){
                postImage3.setVisibility(View.VISIBLE);
                Glide.with(this).load(postDetail.pic.get(2)).into(postImage3);
            }
        }

        if(postDetail.key_word != null && !postDetail.key_word.isEmpty()){
            keyWord.setText("关键词：" + postDetail.key_word);
        }else{
            keyWord.setVisibility(View.GONE);
        }
        if(postDetail.red_package_money > 0){
            redPackageLl.setVisibility(View.VISIBLE);
            if(postDetail.red_package_password != null && !postDetail.red_package_password.isEmpty()){
                redPasswdTv.setVisibility(View.VISIBLE);
                redPackagePassword.setVisibility(View.VISIBLE);
                redPackagePassword.setText(postDetail.red_package_password);
                robRedPackageNote.setVisibility(View.VISIBLE);
            }else{
                redPasswdTv.setVisibility(View.GONE);
                redPackagePassword.setVisibility(View.GONE);
                robRedPackageNote.setVisibility(View.GONE);
            }
            reaPackageLeft.setText( ("已抢" + postDetail.grad_red_package_number + "份，剩余"
                    + (postDetail.red_package_number - postDetail.grad_red_package_number) + "份" ) );
        }
        replyAdapter.setRootVew(rootRl);
        replyAdapter.setPostId(postDetail.id, postDetail.user_id);
    }

    @OnClick({R.id.follow, R.id.collect_rl, R.id.comment_rl, R.id.call, R.id.chat, R.id.user_photo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.follow:
                if(follow.getText().equals("关注")){
                    followUser();
                }else{
                    cancelFollow();
                }
                break;
            case R.id.collect_rl:
                if(ivCollect.isSelected()){
                    cancelPostCollection();
                }else{
                    collectPost();
                }
                break;
            case R.id.comment_rl:
                DialogUtil.getInstance().showCommentDialog(getActivity(), rootRl, 1);
                DialogUtil.getInstance().setDialogClick(str -> {
                    postComment(str);
                });
                break;
            case R.id.call:
                PermissionUtils.checkPhonePermission(getActivity(), new PermissionUtils.PermissionCallBack() {
                    @Override
                    public void onGranted() {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        Uri data = Uri.parse("tel:" + postDetail.phone);
                        intent.setData(data);
                        startActivity(intent);
                    }

                    @Override
                    public void onDenied() {
                        showShortToats("通话权限被拒绝");
                    }
                });
                break;
            case R.id.chat:
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra("frindId", ("" + postDetail.user_id));
                startActivity(intent);
                break;
            case R.id.user_photo:
                intent = new Intent(getActivity(), MyPublishActivity.class);
                intent.putExtra("uid", postDetail.user_id + "");
                startActivity(intent);
                break;
        }
    }

    private void postComment(String str) {
        if(Hawk.get("LoginModel") == null){
            showShortToats("请先登陆");
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }
        Login login = Hawk.get("LoginModel");
        UserInfo userInfo = Hawk.get("userInfo");
        LoadingDialog.showLoading(getActivity());
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.REPLYPOST)
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .addParams("reply_uid", ("" + postDetail.user_id))
                .addParams("article_id", ("" + postDetail.id))
                .addParams("body", str)
                .addParams("reply_table_id", ("" + 0))
                .execute(new AbsJsonCallBack<ReplyPostModel, ReplyPost>(){
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        showShortToats(errorMsg);
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
                        replyPost.article_id = postDetail.id;
                        replyPost.uid = login.uid;
                        replyAdapter.addOneLevelData(replyPost);
                    }
                });
    }

    private void reqReplyLists() {
        /**需去掉身份验证**/
        int uid = 0;
        String token = "";
        Login loginModel = Hawk.get("LoginModel");
        if(loginModel != null){
            uid = loginModel.uid;
            token = loginModel.token;
        }
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.REPLYPOSTLIST)
                .addParams("uid", ("" + uid))
                .addParams("token", token)
                .addParams("post_id", ("" + postDetail.id))
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
                        commentNum.setText("评论 · " + body.size());
                        if(body.size() > 3){
                            replyAdapter.setHasMore(true);
                        }else{
                            replyAdapter.setHasMore(false);
                        }
                        replyAdapter.addRawData(body);
                    }
                });
    }

    public void cancelFollow(){
        if(Hawk.get("LoginModel") == null){
            showShortToats("请先登陆");
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }
        if(((Login)Hawk.get("LoginModel")).uid == postDetail.user_id){
            return;
        }
        LoadingDialog.showLoading(getActivity());
        Login login = Hawk.get("LoginModel");
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.CANCELFOLLOW)
                .addParams("follow_user_id", ("" + postDetail.user_id))
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .execute(new AbsJsonCallBack<FollowModel, Follow>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        showShortToats("取消关注失败");
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
            showShortToats("请先登陆");
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }
        if(((Login)Hawk.get("LoginModel")).uid == postDetail.user_id){
            return;
        }
        LoadingDialog.showLoading(getActivity());
        Login login = Hawk.get("LoginModel");
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.FOLLOW)
                .addParams("follow_user_id", ("" + postDetail.user_id))
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .execute(new AbsJsonCallBack<FollowModel, Follow>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        showShortToats(errorMsg);
                        LoadingDialog.closeLoading();
                    }

                    @Override
                    public void onSuccess(Follow body) {
                        showShortToats("关注成功");
                        follow.setText("已关注");
                        LoadingDialog.closeLoading();
                    }
                });
    }

    public void collectPost(){
        if(Hawk.get("LoginModel") == null){
            showShortToats("请先登陆");
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }
        if(((Login)Hawk.get("LoginModel")).uid == postDetail.user_id){
            return;
        }
        LoadingDialog.showLoading(getActivity());
        Login login = Hawk.get("LoginModel");
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.POSTCOLLECTION)
                .addParams("post_id", ("" + postDetail.id))
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .execute(new AbsJsonCallBack<PostCollectionModel, PostCollection>() {

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        showShortToats(errorMsg);
                        LoadingDialog.closeLoading();
                    }

                    @Override
                    public void onSuccess(PostCollection body) {
                        ivCollect.setSelected(true);
                        tvCollect.setSelected(true);
                        LoadingDialog.closeLoading();
                    }
                });
    }

    public void cancelPostCollection(){
        if(Hawk.get("LoginModel") == null){
            showShortToats("请先登陆");
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }
        if(((Login)Hawk.get("LoginModel")).uid == postDetail.user_id){
            return;
        }
        LoadingDialog.showLoading(getActivity());
        Login login = Hawk.get("LoginModel");
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.CANCELPOSTCOLLECTION)
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .addParams("post_id", ("" + postDetail.id))
                .execute(new AbsJsonCallBack<PostCollectionModel, PostCollection>() {

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        showShortToats(errorMsg);
                        LoadingDialog.closeLoading();
                    }

                    @Override
                    public void onSuccess(PostCollection body) {
                        ivCollect.setSelected(false);
                        tvCollect.setSelected(false);
                        LoadingDialog.closeLoading();
                    }
                });
    }

    public interface OnItemClickListener{
        void onClick( int position);
        void onLongClick( int position);
    }

    //public  class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemClickListener

    private PopupWindow popWindow;
    private int offsetX;
    private int offsetY;
    private void showPopWindow(ImageView iv_delete, boolean isDown, int[] location) {
        View contentView;
        if(isDown){
            contentView = LayoutInflater.from(getActivity()).inflate(R.layout.deletepopupdownlayout, null);

        }else {
            contentView = LayoutInflater.from(getActivity()).inflate(R.layout.deletepopuptoplayout, null);
        }
        RelativeLayout rl_dismiss = contentView.findViewById(R.id.rl_dismiss);
        rl_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
            }
        });

        TextView tv_report = contentView.findViewById(R.id.tv_report);
        tv_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
                startActivity(new Intent(getActivity(), ReportActivity.class));
            }
        });

        popWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popWindow.setContentView(contentView);
        contentView.measure(makeDropDownMeasureSpec(popWindow.getWidth()),
                makeDropDownMeasureSpec(popWindow.getHeight()));
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                activity.rl_bg.setVisibility(View.GONE);
            }
        });

        //显示PopupWindow
        if(isDown){
            offsetX = Math.abs(popWindow.getContentView().getMeasuredWidth()-iv_delete.getWidth()) / 2;
            popWindow.showAsDropDown(iv_delete);
        }else {
            offsetX = Math.abs(popWindow.getContentView().getMeasuredWidth()-iv_delete.getWidth()) / 2;
            PopupWindowCompat.showAsDropDown(popWindow, iv_delete, offsetX, 0, Gravity.START);
        }
        activity.rl_bg.setVisibility(View.VISIBLE);
    }

    @SuppressWarnings("ResourceType")
    private static int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private PostDetailsFragment.MyHandler mHandler = new PostDetailsFragment.MyHandler(this);
    static class MyHandler extends Handler {
        //注意下面的“”类是MyHandler类所在的外部类，即所在的activity或者fragment
        WeakReference<PostDetailsFragment> fragment;

        MyHandler(PostDetailsFragment fragment) {
            this.fragment = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (fragment.get() == null) {
                return;
            }
            switch (msg.what) {
                case INIT:
                    fragment.get().initViews();
                    break;
            }
        }
    }
}
