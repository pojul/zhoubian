package com.yjyc.zhoubian.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.MainActivitys;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.DeleteReply;
import com.yjyc.zhoubian.model.DeleteReplyModel;
import com.yjyc.zhoubian.model.Like;
import com.yjyc.zhoubian.model.LikeModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.PostCollectionLists;
import com.yjyc.zhoubian.model.PostDetail;
import com.yjyc.zhoubian.model.PostDetailModel;
import com.yjyc.zhoubian.model.ReplyPost;
import com.yjyc.zhoubian.model.ReplyPostList;
import com.yjyc.zhoubian.model.ReplyPostModel;
import com.yjyc.zhoubian.model.UserInfo;
import com.yjyc.zhoubian.ui.activity.LoginActivity;
import com.yjyc.zhoubian.ui.fragment.PostDetailsFragment;
import com.yjyc.zhoubian.utils.DialogUtil;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostReplyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements PostDetailsFragment.OnItemClickListener {

    private PostDetailsFragment.OnItemClickListener mOnItemClickListener;
    public static final int TYPE_ONE = 0;
    public static final int TYPE_TWO = 1;
    public static final int TYPE_THREE = 2;//三种不同的布局
    private Context mContext;
    private List<ReplyPostList.ReplyPost> replys;
    private boolean hasMore;
    private View roolView;
    private int postId = -1;
    private int postOwnId = -1;
    public boolean showRedPackageMsg = true;

    public PostReplyAdapter(Context mContext, List<ReplyPostList.ReplyPost> replys) {
        this.mContext = mContext;
        this.replys = replys;
    }

    public void setOnItemClickListener(PostDetailsFragment.OnItemClickListener onItemClickListener ){
        this. mOnItemClickListener=onItemClickListener;
    }

    @Override
    public void onClick(int position) {

    }

    @Override
    public void onLongClick(int position) {

    }

    public class MyViewHolderOne extends RecyclerView.ViewHolder {

        @BindView(R.id.head_url)
        RoundedImageView headUrl;
        @BindView(R.id.nickname)
        TextView nickname;
        @BindView(R.id.body)
        TextView body;
        @BindView(R.id.interval_time)
        TextView intervalTime;
        @BindView(R.id.reply)
        TextView reply;
        @BindView(R.id.delete)
        TextView delete;
        @BindView(R.id.grab_red_package_msg)
        TextView grabRedPackageMsg;
        @BindView(R.id.thumb_up)
        ImageView thumb_up;

        public MyViewHolderOne(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class MyViewHolderTwo extends RecyclerView.ViewHolder {

        @BindView(R.id.head_url)
        RoundedImageView headUrl;
        @BindView(R.id.nickname)
        TextView nickname;
        @BindView(R.id.body)
        TextView body;
        @BindView(R.id.reply)
        TextView reply;
        @BindView(R.id.delete)
        TextView delete;
        @BindView(R.id.thumb_up)
        ImageView thumb_up;

        public MyViewHolderTwo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class MyFooterHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.more)
        TextView more;

        public MyFooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    public static final int TYPE_FOOTER_VIEW = 3;
    @Override
    public int getItemViewType(int position) {
       if(position >= replys.size()){
           return TYPE_FOOTER_VIEW;
       }else{
           ReplyPostList.ReplyPost replyPost = replys.get(position);
           if(replyPost._level == 1){
               return TYPE_ONE;
           }else{
               return TYPE_TWO;
           }
       }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_FOOTER_VIEW:
                final View footerView = LayoutInflater.from(mContext).inflate(R.layout
                        .post_details_foot_view, parent, false);
                return new MyFooterHolder(footerView);
            case TYPE_ONE:
                return new MyViewHolderOne(LayoutInflater.from(mContext).inflate(R.layout
                        .fragment_post_details_item_img0, parent, false));
            case TYPE_TWO:
                return new MyViewHolderTwo(LayoutInflater.from(mContext).inflate(R.layout
                        .fragment_post_details_item_img1, parent, false));
        }
        return null;

    }

    //将数据绑定到控件上
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolderOne) {
            bindTypeOne((MyViewHolderOne) holder, position);
        } else if (holder instanceof MyViewHolderTwo) {
            bindTypeTwo((MyViewHolderTwo) holder, position);
        } else if (holder instanceof MyFooterHolder) {
            bindTypeThree((MyFooterHolder) holder, position);
        }

        if( mOnItemClickListener!= null){
            holder.itemView.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
            holder. itemView.setOnLongClickListener( new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onLongClick(position);
                    return false;
                }
            });
        }
    }

    private void bindTypeOne(MyViewHolderOne holderOne, int position) {
        ReplyPostList.ReplyPost reply = replys.get(position);
        if(reply.head_url != null && !reply.head_url.isEmpty()){
            Glide.with(mContext).load(reply.head_url).into(holderOne.headUrl);
        }else{
            Glide.with(mContext).load(R.drawable.test_me).into(holderOne.headUrl);
        }
        if(showRedPackageMsg && reply.grab_red_package_msg != null && !reply.grab_red_package_msg.isEmpty()){
            holderOne.grabRedPackageMsg.setText(reply.grab_red_package_msg);
            holderOne.grabRedPackageMsg.setVisibility(View.VISIBLE);
        }else{
            holderOne.grabRedPackageMsg.setVisibility(View.GONE);
        }
        if(reply.nickname != null && !reply.nickname.isEmpty()){
            holderOne.nickname.setText(reply.nickname);
        }else{
            holderOne.nickname.setText("佚名");
        }
        if(reply.body != null && !reply.body.isEmpty()){
            holderOne.body.setText(reply.body);
        }else{
            holderOne.body.setText("");
        }
        if(reply.interval_time != null && !reply.interval_time.isEmpty()){
            holderOne.intervalTime.setText(reply.interval_time);
        }else{
            holderOne.intervalTime.setText("");
        }
        Login login = Hawk.get("LoginModel");
        if(login != null && login.uid == reply.uid){
            holderOne.delete.setVisibility(View.VISIBLE);
        }else{
            holderOne.delete.setVisibility(View.GONE);
        }
        if(login != null){
            holderOne.reply.setVisibility(View.VISIBLE);
        }else{
            holderOne.reply.setVisibility(View.GONE);
        }
        holderOne.reply.setOnClickListener(v->{
            showReplyDialog(reply, position);
        });
        holderOne.delete.setOnClickListener(v->{
            deleteReply(reply, position);
        });
        if(reply.is_like){
            holderOne.thumb_up.setSelected(true);
        }else{
            holderOne.thumb_up.setSelected(false);
        }
        holderOne.thumb_up.setOnClickListener(v->{
            if(reply.is_like){
                return;
            }
            thumbupReply(reply, position);
        });
    }

    private void bindTypeTwo(MyViewHolderTwo holderTwo, int position) {
        ReplyPostList.ReplyPost reply = replys.get(position);
        if(reply.head_url != null && !reply.head_url.isEmpty()){
            Glide.with(mContext).load(reply.head_url).into(holderTwo.headUrl);
        }else{
            Glide.with(mContext).load(R.drawable.test_me).into(holderTwo.headUrl);
        }
        if(reply.nickname != null && !reply.nickname.isEmpty()){
            holderTwo.nickname.setText(reply.nickname);
        }else{
            holderTwo.nickname.setText("佚名");
        }
        StringBuffer replyHeader = new StringBuffer();
        if(postOwnId == reply.uid){
            replyHeader.append("主人回复@");
        }else{
            if(reply.nickname != null && !reply.nickname.isEmpty()){
                replyHeader.append(reply.nickname + "回复@");
            }else{
                replyHeader.append("佚名回复@");
            }
        }
        if(reply.uid == reply.reply_user_id){
            replyHeader.append("自己");
        }else{
            if(reply.reply_user_nickname != null && !reply.reply_user_nickname.isEmpty()){
                replyHeader.append(reply.reply_user_nickname);
            }else{
                replyHeader.append("佚名");
            }
        }
        replyHeader.append("：");
        if(reply.body != null && !reply.body.isEmpty()){
            holderTwo.body.setText((replyHeader.toString() +  reply.body));
        }else{
            holderTwo.body.setText("");
        }
        Login login = Hawk.get("LoginModel");
        if(login != null && login.uid == reply.uid){
            holderTwo.delete.setVisibility(View.VISIBLE);
        }else{
            holderTwo.delete.setVisibility(View.GONE);
        }
        if(login != null){
            holderTwo.reply.setVisibility(View.VISIBLE);
        }else{
            holderTwo.reply.setVisibility(View.GONE);
        }
        holderTwo.reply.setOnClickListener(v->{
            showReplyDialog(reply, position);
        });
        holderTwo.delete.setOnClickListener(v->{
            deleteReply(reply, position);
        });
        if(reply.is_like){
            holderTwo.thumb_up.setSelected(true);
        }else{
            holderTwo.thumb_up.setSelected(false);
        }
        holderTwo.thumb_up.setOnClickListener(v->{
            if(reply.is_like){
                return;
            }
            thumbupReply(reply, position);
        });
    }

    private void thumbupReply(ReplyPostList.ReplyPost reply, int position) {
        Login login = Hawk.get("LoginModel");
        if(login == null){
            Toast.makeText(mContext, "请先登录", Toast.LENGTH_SHORT).show();
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            return;
        }
        LoadingDialog.showLoading(mContext);
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.POSTLIKE)
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .addParams("id", ("" + reply.id))
                .execute(new AbsJsonCallBack<LikeModel, Like>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Like body) {
                        LoadingDialog.closeLoading();
                        reply.is_like = true;
                        notifyItemChanged(position);
                    }
                });

    }

    private void deleteReply(ReplyPostList.ReplyPost reply, int position) {
        Login login = Hawk.get("LoginModel");
        if(login == null){
            return;
        }
        LoadingDialog.showLoading(mContext);
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.DELETEREPLY)
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .addParams("id", ("" + reply.id))
                .execute(new AbsJsonCallBack<DeleteReplyModel, DeleteReply>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(DeleteReply body) {
                        LoadingDialog.closeLoading();
                        deleteData(reply, position);
                    }
                });
    }

    private void showReplyDialog(ReplyPostList.ReplyPost reply, int position) {
        if(roolView == null || postId == -1){
            return;
        }
        DialogUtil.getInstance().showCommentDialog(mContext, roolView, 2, null);
        DialogUtil.getInstance().setDialogClick(str -> {
            if(Hawk.get("LoginModel") == null){
                Toast.makeText(mContext, "请先登陆", Toast.LENGTH_SHORT).show();
                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                return;
            }
            int replyTableId = 0;
            if(reply._level == 1){
                replyTableId = reply.id;
            }else{
                replyTableId = reply.oneLevelId;
            }
            Login login = Hawk.get("LoginModel");
            UserInfo userInfo = Hawk.get("userInfo");
            LoadingDialog.showLoading(mContext);
            OkhttpUtils.with()
                    .post()
                    .url(HttpUrl.REPLYPOST)
                    .addParams("uid", ("" + login.uid))
                    .addParams("token", login.token)
                    .addParams("reply_uid", ("" + reply.uid))
                    .addParams("article_id", ("" + postId))
                    .addParams("body", str)
                    .addParams("reply_table_id", ("" + replyTableId))
                    .execute(new AbsJsonCallBack<ReplyPostModel, ReplyPost>(){
                        @Override
                        public void onFailure(String errorCode, String errorMsg) {
                            Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                            LoadingDialog.closeLoading();
                        }
                        @Override
                        public void onSuccess(ReplyPost body) {
                            LoadingDialog.closeLoading();
                            ReplyPostList.ReplyPost replyPost = new ReplyPostList().new ReplyPost();
                            replyPost._level = 2;
                            replyPost.id = body.return_body.id;
                            replyPost.body = body.return_body.body;
                            replyPost.nickname = userInfo.nickname;
                            replyPost.head_url = userInfo.head_url_img;
                            replyPost.interval_time = "刚刚";
                            replyPost.article_id = postId;
                            replyPost.uid = login.uid;
                            if(reply._level == 1){
                                replyPost.oneLevelId = reply.id;
                            }else{
                                replyPost.oneLevelId = reply.oneLevelId;
                            }
                            replyPost.reply_user_id = reply.uid;
                            replyPost.reply_user_nickname = reply.nickname;
                            replyPost.reply_user_head_url = reply.head_url;
                            addTwoLevelData(replyPost, position);
                        }
                    });
        });
    }

    public void setPostId(int postId, int postOwnId){
        this.postId = postId;
        this.postOwnId = postOwnId;
    }

    public void setRootVew(View rootView){
        this.roolView = rootView;
    }

    private void bindTypeThree(MyFooterHolder holder, int position) {  //在其中镶嵌一个RecyclerView
    }

    @Override
    public int getItemCount() {
        if(hasMore){
            return (replys.size() + 1);
        }else{
            return replys.size();
        }
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
        synchronized (replys){
            notifyDataSetChanged();
        }
    }

    public void addRawData(List<ReplyPostList.ReplyPost> rawReplys){
        List<ReplyPostList.ReplyPost> tempReplys = new ArrayList<>();
        for (int i = 0; i < rawReplys.size(); i++) {
            ReplyPostList.ReplyPost oneLevelReply = rawReplys.get(i);
            oneLevelReply._level = 1;
            tempReplys.add(oneLevelReply);
            if(oneLevelReply._data != null){
                addTwoLevelReply(tempReplys, oneLevelReply._data, oneLevelReply.id);
            }
        }
        synchronized (replys){
            replys.addAll(tempReplys);
            if(hasMore){
                notifyItemRangeInserted((getItemCount() - 1), tempReplys.size());
            }else{
                notifyItemRangeInserted(getItemCount(), tempReplys.size());
            }
        }
    }

    private void addTwoLevelReply(List<ReplyPostList.ReplyPost> tempReplys, List<ReplyPostList.ReplyPost> datas, int oneLevelId) {
        for (int i = 0; i < datas.size(); i++) {
            ReplyPostList.ReplyPost twoLevelReply = datas.get(i);
            twoLevelReply._level = 2;
            twoLevelReply.oneLevelId = oneLevelId;
            tempReplys.add(twoLevelReply);
        }
    }

    public void addOneLevelData(ReplyPostList.ReplyPost reply){
        synchronized (replys){
            replys.add(0, reply);
            notifyItemInserted(0);
            notifyItemRangeChanged(0, replys.size());
        }
    }

    public void addTwoLevelData(ReplyPostList.ReplyPost replyPost, int position){
        synchronized (replys){
            replys.add((position + 1), replyPost);
            notifyItemInserted((position + 1));
            notifyItemRangeChanged(0, replys.size());
        }
    }

    public void deleteData(ReplyPostList.ReplyPost replyPost, int position){
        synchronized (replys) {
            if (replyPost._level == 1) {
                int oneLevelId = replyPost.id;
                int startPosition = position;
                List<ReplyPostList.ReplyPost> removeReplys = new ArrayList<>();
                removeReplys.add(replys.get(position));
                for (int i = startPosition; i < replys.size(); i++) {
                    if(i == startPosition){
                        continue;
                    }
                    ReplyPostList.ReplyPost tempPost = replys.get(i);
                    if(tempPost._level == 1 || tempPost.oneLevelId != oneLevelId){
                        break;
                    }
                    if(tempPost.oneLevelId == oneLevelId){
                        removeReplys.add(tempPost);
                    }
                }
                replys.removeAll(removeReplys);
                notifyDataSetChanged();
            } else {
                replys.remove(position);
                notifyDataSetChanged();
            }
        }
    }

    public void setShowRedPackageMsg(boolean showRedPackageMsg) {
        this.showRedPackageMsg = showRedPackageMsg;
        notifyDataSetChanged();
    }
}