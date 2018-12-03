package com.yjyc.zhoubian.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.AcceptEvaluationExposes;
import com.yjyc.zhoubian.model.EmptyEntity;
import com.yjyc.zhoubian.model.EmptyEntityModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.LoginModel;
import com.yjyc.zhoubian.model.ReplyEvaluationExpose;
import com.yjyc.zhoubian.model.ReplyEvaluationExposeModel;
import com.yjyc.zhoubian.model.ReplyPost;
import com.yjyc.zhoubian.model.ReplyPostModel;
import com.yjyc.zhoubian.ui.activity.LoginActivity;
import com.yjyc.zhoubian.ui.activity.MyPublishActivity;
import com.yjyc.zhoubian.utils.DateUtil;
import com.yjyc.zhoubian.utils.DialogUtil;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EvaluationExposeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int cate_id;
    private Context mContext;
    private List<AcceptEvaluationExposes.AcceptEvaluationExpose> datas;
    private int mode; //1: 收到的 1: 给出的

    public static final int TYPE_ONE = 0;
    public static final int TYPE_TWO = 1;
    public static final int TYPE_THREE = 2;//三种不同的布局

    public EvaluationExposeAdapter(int cate_id, Context mContext, List<AcceptEvaluationExposes.AcceptEvaluationExpose> datas, int mode) {
        this.cate_id = cate_id;
        this.mContext = mContext;
        this.datas = datas;
        this.mode = mode;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ONE:
                return new MyViewHolderOne(LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .item_evaluation_expose0, parent, false));
            case TYPE_TWO:
                return new MyViewHolderTwo(LayoutInflater.from(parent.getContext()).inflate(R.layout.
                        item_evaluation_expose1, parent, false));
            case TYPE_THREE:
                return new MyViewHolderThree(LayoutInflater.from(parent.getContext()).inflate(R.layout.
                        item_evaluation_expose2, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolderOne) {
            bindTypeOne((MyViewHolderOne) holder, position);
        } else if (holder instanceof MyViewHolderTwo) {
            bindTypeTwo((MyViewHolderTwo) holder, position);
        } else if (holder instanceof MyViewHolderThree) {
            bindTypeThree((MyViewHolderThree) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return (datas==null?0:datas.size());
    }

    @Override
    public int getItemViewType(int position) {
        AcceptEvaluationExposes.AcceptEvaluationExpose evaluationExpose = datas.get(position);
        if(evaluationExpose == null || evaluationExpose.pic == null || evaluationExpose.pic.size() <= 0){
            return TYPE_ONE;
        }else if(evaluationExpose.pic.size() == 1){
            return TYPE_TWO;
        }else{
            return TYPE_THREE;
        }
    }

    private void bindTypeOne(MyViewHolderOne holder, int position) {
        AcceptEvaluationExposes.AcceptEvaluationExpose evaluationExpose = datas.get(position);
        Login login = Hawk.get("LoginModel");
        if(evaluationExpose.user_info != null && evaluationExpose.user_info.head_url != null && !evaluationExpose.user_info.head_url.isEmpty()){
            Glide.with(mContext).load(evaluationExpose.user_info.head_url).into(holder.iv_head_url);
        }else{
            Glide.with(mContext).load(R.drawable.test_me).into(holder.iv_head_url);
        }
        if(evaluationExpose.user_info != null && evaluationExpose.user_info.nickname != null && !evaluationExpose.user_info.nickname.isEmpty()){
            holder.tv_nickname.setText(evaluationExpose.user_info.nickname);
        }else{
            holder.tv_nickname.setText("佚名");
        }
        if(evaluationExpose.be_user_info != null && evaluationExpose.be_user_info.head_url != null && !evaluationExpose.be_user_info.head_url.isEmpty()){
            Glide.with(mContext).load(evaluationExpose.be_user_info.head_url).into(holder.iv_be_head_url);
        }else{
            Glide.with(mContext).load(R.drawable.test_me).into(holder.iv_be_head_url);
        }
        if(evaluationExpose.be_user_info != null && evaluationExpose.be_user_info.nickname != null && !evaluationExpose.be_user_info.nickname.isEmpty()){
            holder.tv_be_nickname.setText(evaluationExpose.be_user_info.nickname);
        }else{
            holder.tv_be_nickname.setText("佚名");
        }
        holder.tv_create_time.setText(evaluationExpose.update_time);
        holder.tv_body.setText(evaluationExpose.body);
        if(cate_id == 1){
            holder.note.setText("进行了评价");
        }else{
            holder.note.setText("进行了揭露");
        }
        if(evaluationExpose.user_reply == null || evaluationExpose.user_reply.body == null || evaluationExpose.user_reply.body.isEmpty()){
            holder.reply_ll.setVisibility(View.GONE);
            if(login != null && (login.uid + "").equals((evaluationExpose.be_exposed_user_id + ""))){
                holder.reply_bt.setVisibility(View.VISIBLE);
            }else{
                holder.reply_bt.setVisibility(View.GONE);
            }
        }else{
            holder.reply_ll.setVisibility(View.VISIBLE);
            holder.reply_bt.setVisibility(View.GONE);
            if(login != null && (login.uid + "").equals((evaluationExpose.be_exposed_user_id + ""))){
                holder.operate_ll.setVisibility(View.VISIBLE);
            }else{
                holder.operate_ll.setVisibility(View.GONE);
            }
            if(evaluationExpose.be_user_info.head_url != null && !evaluationExpose.be_user_info.head_url.isEmpty()){
                Glide.with(mContext).load(evaluationExpose.be_user_info.head_url).into(holder.reply_photo);
            }else{
                Glide.with(mContext).load(R.drawable.test_me).into(holder.reply_photo);
            }
            if(evaluationExpose.be_user_info.nickname != null && !evaluationExpose.be_user_info.nickname.isEmpty()){
                holder.reply.setText(evaluationExpose.be_user_info.nickname + "回复");
            }else{
                holder.reply.setText("佚名回复");
            }
            holder.reply_time.setText(evaluationExpose.user_reply.create_time.substring(0,
                    (evaluationExpose.user_reply.create_time.length() - 3) ));
            holder.reply_body.setText(evaluationExpose.user_reply.body);
        }
        holder.reply_bt.setOnClickListener(v->{
            showReplyDialog(evaluationExpose, position);
        });
        holder.edit.setOnClickListener(v->{
            editReply(evaluationExpose, position);
        });
        holder.delete.setOnClickListener(v->{
            deleteReply(evaluationExpose, position);
        });
        holder.iv_head_url.setOnClickListener(v->{
            Intent intent = new Intent(mContext, MyPublishActivity.class);
            intent.putExtra("uid", evaluationExpose.user_id + "");
            mContext.startActivity(intent);
        });
        holder.iv_be_head_url.setOnClickListener(v->{
            Intent intent = new Intent(mContext, MyPublishActivity.class);
            intent.putExtra("uid", evaluationExpose.be_exposed_user_id + "");
            mContext.startActivity(intent);
        });
    }

    private void bindTypeTwo(MyViewHolderTwo holder, int position) {
        AcceptEvaluationExposes.AcceptEvaluationExpose evaluationExpose = datas.get(position);
        Login login = Hawk.get("LoginModel");
        if(evaluationExpose.user_info.head_url != null && !evaluationExpose.user_info.head_url.isEmpty()){
            Glide.with(mContext).load(evaluationExpose.user_info.head_url).into(holder.iv_head_url);
        }else{
            Glide.with(mContext).load(R.drawable.test_me).into(holder.iv_head_url);
        }
        if(evaluationExpose.user_info.nickname != null && !evaluationExpose.user_info.nickname.isEmpty()){
            holder.tv_nickname.setText(evaluationExpose.user_info.nickname);
        }else{
            holder.tv_nickname.setText("佚名");
        }
        if(evaluationExpose.be_user_info.head_url != null && !evaluationExpose.be_user_info.head_url.isEmpty()){
            Glide.with(mContext).load(evaluationExpose.be_user_info.head_url).into(holder.iv_be_head_url);
        }else{
            Glide.with(mContext).load(R.drawable.test_me).into(holder.iv_be_head_url);
        }
        if(evaluationExpose.be_user_info.nickname != null && !evaluationExpose.be_user_info.nickname.isEmpty()){
            holder.tv_be_nickname.setText(evaluationExpose.be_user_info.nickname);
        }else{
            holder.tv_be_nickname.setText("佚名");
        }
        holder.tv_create_time.setText(evaluationExpose.update_time);
        holder.tv_body.setText(evaluationExpose.body);
        if(cate_id == 1){
            holder.note.setText("进行了评价");
        }else{
            holder.note.setText("进行了揭露");
        }
        if(evaluationExpose.user_reply == null || evaluationExpose.user_reply.body == null || evaluationExpose.user_reply.body.isEmpty()){
            holder.reply_ll.setVisibility(View.GONE);
            if(login != null && (login.uid + "").equals((evaluationExpose.be_exposed_user_id + ""))){
                holder.reply_bt.setVisibility(View.VISIBLE);
            }else{
                holder.reply_bt.setVisibility(View.GONE);
            }
        }else{
            holder.reply_ll.setVisibility(View.VISIBLE);
            holder.reply_bt.setVisibility(View.GONE);
            if(login != null && (login.uid + "").equals((evaluationExpose.be_exposed_user_id + ""))){
                holder.operate_ll.setVisibility(View.VISIBLE);
            }else{
                holder.operate_ll.setVisibility(View.GONE);
            }
            if(evaluationExpose.be_user_info.head_url != null && !evaluationExpose.be_user_info.head_url.isEmpty()){
                Glide.with(mContext).load(evaluationExpose.be_user_info.head_url).into(holder.reply_photo);
            }else{
                Glide.with(mContext).load(R.drawable.test_me).into(holder.reply_photo);
            }
            if(evaluationExpose.be_user_info.nickname != null && !evaluationExpose.be_user_info.nickname.isEmpty()){
                holder.reply.setText(evaluationExpose.be_user_info.nickname + "回复");
            }else{
                holder.reply.setText("佚名回复");
            }
            holder.reply_time.setText(evaluationExpose.user_reply.create_time.substring(0,
                    (evaluationExpose.user_reply.create_time.length() - 3) ));
            holder.reply_body.setText(evaluationExpose.user_reply.body);
        }
        Glide.with(mContext).load(evaluationExpose.pic.get(0)).into(holder.iv1);
        holder.reply_bt.setOnClickListener(v->{
            showReplyDialog(evaluationExpose, position);
        });
        holder.edit.setOnClickListener(v->{
            editReply(evaluationExpose, position);
        });
        holder.delete.setOnClickListener(v->{
            deleteReply(evaluationExpose, position);
        });
        holder.iv_head_url.setOnClickListener(v->{
            Intent intent = new Intent(mContext, MyPublishActivity.class);
            intent.putExtra("uid", evaluationExpose.user_id + "");
            mContext.startActivity(intent);
        });
        holder.iv_be_head_url.setOnClickListener(v->{
            Intent intent = new Intent(mContext, MyPublishActivity.class);
            intent.putExtra("uid", evaluationExpose.be_exposed_user_id + "");
            mContext.startActivity(intent);
        });
    }

    private void bindTypeThree(MyViewHolderThree holder, int position) {
        AcceptEvaluationExposes.AcceptEvaluationExpose evaluationExpose = datas.get(position);
        Login login = Hawk.get("LoginModel");
        if(evaluationExpose.user_info.head_url != null && !evaluationExpose.user_info.head_url.isEmpty()){
            Glide.with(mContext).load(evaluationExpose.user_info.head_url).into(holder.iv_head_url);
        }else{
            Glide.with(mContext).load(R.drawable.test_me).into(holder.iv_head_url);
        }
        if(evaluationExpose.user_info.nickname != null && !evaluationExpose.user_info.nickname.isEmpty()){
            holder.tv_nickname.setText(evaluationExpose.user_info.nickname);
        }else{
            holder.tv_nickname.setText("佚名");
        }
        if(evaluationExpose.be_user_info.head_url != null && !evaluationExpose.be_user_info.head_url.isEmpty()){
            Glide.with(mContext).load(evaluationExpose.be_user_info.head_url).into(holder.iv_be_head_url);
        }else{
            Glide.with(mContext).load(R.drawable.test_me).into(holder.iv_be_head_url);
        }
        if(evaluationExpose.be_user_info.nickname != null && !evaluationExpose.be_user_info.nickname.isEmpty()){
            holder.tv_be_nickname.setText(evaluationExpose.be_user_info.nickname);
        }else{
            holder.tv_be_nickname.setText("佚名");
        }
        holder.tv_create_time.setText(evaluationExpose.update_time);
        holder.tv_body.setText(evaluationExpose.body);
        if(cate_id == 1){
            holder.note.setText("进行了评价");
        }else{
            holder.note.setText("进行了揭露");
        }
        if(evaluationExpose.user_reply == null || evaluationExpose.user_reply.body == null || evaluationExpose.user_reply.body.isEmpty()){
            holder.reply_ll.setVisibility(View.GONE);
            if(login != null && (login.uid + "").equals((evaluationExpose.be_exposed_user_id + ""))){
                holder.reply_bt.setVisibility(View.VISIBLE);
            }else{
                holder.reply_bt.setVisibility(View.GONE);
            }
        }else{
            holder.reply_ll.setVisibility(View.VISIBLE);
            holder.reply_bt.setVisibility(View.GONE);
            if(login != null && (login.uid + "").equals((evaluationExpose.be_exposed_user_id + ""))){
                holder.operate_ll.setVisibility(View.VISIBLE);
            }else{
                holder.operate_ll.setVisibility(View.GONE);
            }
            if(evaluationExpose.be_user_info.head_url != null && !evaluationExpose.be_user_info.head_url.isEmpty()){
                Glide.with(mContext).load(evaluationExpose.be_user_info.head_url).into(holder.reply_photo);
            }else{
                Glide.with(mContext).load(R.drawable.test_me).into(holder.reply_photo);
            }
            if(evaluationExpose.be_user_info.nickname != null && !evaluationExpose.be_user_info.nickname.isEmpty()){
                holder.reply.setText(evaluationExpose.be_user_info.nickname + "回复");
            }else{
                holder.reply.setText("佚名回复");
            }
            holder.reply_time.setText(evaluationExpose.user_reply.create_time.substring(0,
                    (evaluationExpose.user_reply.create_time.length() - 3) ));
            holder.reply_body.setText(evaluationExpose.user_reply.body);
        }
        Glide.with(mContext).load(evaluationExpose.pic.get(0)).into(holder.iv1);
        Glide.with(mContext).load(evaluationExpose.pic.get(1)).into(holder.iv2);
        if(evaluationExpose.pic.size() >= 3){
            holder.iv3.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(evaluationExpose.pic.get(2)).into(holder.iv3);
        }else{
            holder.iv3.setVisibility(View.INVISIBLE);
        }
        holder.reply_bt.setOnClickListener(v->{
            showReplyDialog(evaluationExpose, position);
        });
        holder.edit.setOnClickListener(v->{
            editReply(evaluationExpose, position);
        });
        holder.delete.setOnClickListener(v->{
            deleteReply(evaluationExpose, position);
        });
        holder.iv_head_url.setOnClickListener(v->{
            Intent intent = new Intent(mContext, MyPublishActivity.class);
            intent.putExtra("uid", evaluationExpose.user_id + "");
            mContext.startActivity(intent);
        });
        holder.iv_be_head_url.setOnClickListener(v->{
            Intent intent = new Intent(mContext, MyPublishActivity.class);
            intent.putExtra("uid", evaluationExpose.be_exposed_user_id + "");
            mContext.startActivity(intent);
        });
    }

    private void deleteReply(AcceptEvaluationExposes.AcceptEvaluationExpose evaluationExpose, int position) {
        Login login = Hawk.get("LoginModel");
        if(login == null){
            Toast.makeText(mContext, "请先登陆", Toast.LENGTH_SHORT).show();
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            return;
        }
        LoadingDialog.showLoading(mContext);
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.DELETEVALUATIONEXPOSE)
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .addParams("id", ("" + evaluationExpose.user_reply.id))
                .execute(new AbsJsonCallBack<EmptyEntityModel, EmptyEntity>(){
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(EmptyEntity body) {
                        LoadingDialog.closeLoading();
                        evaluationExpose.user_reply = null;
                        notifyItemChanged(position);
                    }
                });
    }

    private void editReply(AcceptEvaluationExposes.AcceptEvaluationExpose evaluationExpose, int position) {
        Login login = Hawk.get("LoginModel");
        if(login == null){
            Toast.makeText(mContext, "请先登陆", Toast.LENGTH_SHORT).show();
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            return;
        }
        DialogUtil.getInstance().showCommentDialog(mContext, ((MyPublishActivity)mContext).root_ll, 2, evaluationExpose.user_reply.body);
        DialogUtil.getInstance().setDialogClick(str -> {
            LoadingDialog.showLoading(mContext);
            OkhttpUtils.with()
                    .post()
                    .url(HttpUrl.UPDATEVALUATIONEXPOSE)
                    .addParams("uid", ("" + login.uid))
                    .addParams("token", login.token)
                    .addParams("id", ("" + evaluationExpose.user_reply.id))
                    .addParams("body", str)
                    .execute(new AbsJsonCallBack<EmptyEntityModel, EmptyEntity>(){
                        @Override
                        public void onFailure(String errorCode, String errorMsg) {
                            LoadingDialog.closeLoading();
                            Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess(EmptyEntity body) {
                            LoadingDialog.closeLoading();
                            evaluationExpose.user_reply.body = str;
                            notifyItemChanged(position);
                        }
                    });
        });
    }

    private void showReplyDialog(AcceptEvaluationExposes.AcceptEvaluationExpose evaluationExpose, int position) {
        Login login = Hawk.get("LoginModel");
        if(login == null){
            Toast.makeText(mContext, "请先登陆", Toast.LENGTH_SHORT).show();
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            return;
        }
        DialogUtil.getInstance().showCommentDialog(mContext, ((MyPublishActivity)mContext).root_ll, 2, null);
        DialogUtil.getInstance().setDialogClick(str -> {
            LoadingDialog.showLoading(mContext);
            OkhttpUtils.with()
                    .post()
                    .url(HttpUrl.REPLYEVALUATIONEXPOSE)
                    .addParams("uid", ("" + login.uid))
                    .addParams("token", login.token)
                    .addParams("evaluation_expose_id", ("" + evaluationExpose.id))
                    .addParams("body", str)
                    .execute(new AbsJsonCallBack<ReplyEvaluationExposeModel, ReplyEvaluationExpose>(){
                        @Override
                        public void onFailure(String errorCode, String errorMsg) {
                            LoadingDialog.closeLoading();
                            Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                        }
                        @Override
                        public void onSuccess(ReplyEvaluationExpose body) {
                            LoadingDialog.closeLoading();
                            AcceptEvaluationExposes.UserReply userReply = new AcceptEvaluationExposes().new UserReply();
                            userReply.body = str;
                            userReply.create_time = DateUtil.getDate();
                            evaluationExpose.user_reply = userReply;
                            notifyItemChanged(position);
                        }
                    });
        });
    }

    public void clearDatas() {
        synchronized (datas){
            datas.clear();
            notifyDataSetChanged();
        }
    }

    public void addDatas(ArrayList<AcceptEvaluationExposes.AcceptEvaluationExpose> dataList) {
        synchronized (datas){
            int position = datas.size();
            datas.addAll(dataList);
            notifyItemRangeInserted(position, dataList.size());
        }
    }

    public class MyViewHolderOne extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_head_url)
        RoundedImageView iv_head_url;
        @BindView(R.id.iv_be_head_url)
        RoundedImageView iv_be_head_url;
        @BindView(R.id.tv_nickname)
        TextView tv_nickname;
        @BindView(R.id.tv_be_nickname)
        TextView tv_be_nickname;
        @BindView(R.id.note)
        TextView note;
        @BindView(R.id.tv_create_time)
        TextView tv_create_time;
        @BindView(R.id.tv_body)
        TextView tv_body;
        @BindView(R.id.reply_photo)
        RoundedImageView reply_photo;
        @BindView(R.id.reply)
        TextView reply;
        @BindView(R.id.reply_time)
        TextView reply_time;
        @BindView(R.id.reply_body)
        TextView reply_body;
        @BindView(R.id.edit)
        TextView edit;
        @BindView(R.id.delete)
        TextView delete;
        @BindView(R.id.reply_ll)
        LinearLayout reply_ll;
        @BindView(R.id.reply_bt)
        TextView reply_bt;
        @BindView(R.id.operate_ll)
        LinearLayout operate_ll;

        public MyViewHolderOne(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class MyViewHolderTwo extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_head_url)
        RoundedImageView iv_head_url;
        @BindView(R.id.iv_be_head_url)
        RoundedImageView iv_be_head_url;
        @BindView(R.id.tv_nickname)
        TextView tv_nickname;
        @BindView(R.id.tv_be_nickname)
        TextView tv_be_nickname;
        @BindView(R.id.note)
        TextView note;
        @BindView(R.id.tv_create_time)
        TextView tv_create_time;
        @BindView(R.id.tv_body)
        TextView tv_body;
        @BindView(R.id.reply_photo)
        RoundedImageView reply_photo;
        @BindView(R.id.reply)
        TextView reply;
        @BindView(R.id.reply_time)
        TextView reply_time;
        @BindView(R.id.reply_body)
        TextView reply_body;
        @BindView(R.id.edit)
        TextView edit;
        @BindView(R.id.delete)
        TextView delete;
        @BindView(R.id.iv1)
        ImageView iv1;
        @BindView(R.id.reply_ll)
        LinearLayout reply_ll;
        @BindView(R.id.reply_bt)
        TextView reply_bt;
        @BindView(R.id.operate_ll)
        LinearLayout operate_ll;

        public MyViewHolderTwo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class MyViewHolderThree extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_head_url)
        RoundedImageView iv_head_url;
        @BindView(R.id.iv_be_head_url)
        RoundedImageView iv_be_head_url;
        @BindView(R.id.tv_nickname)
        TextView tv_nickname;
        @BindView(R.id.tv_be_nickname)
        TextView tv_be_nickname;
        @BindView(R.id.note)
        TextView note;
        @BindView(R.id.tv_create_time)
        TextView tv_create_time;
        @BindView(R.id.tv_body)
        TextView tv_body;
        @BindView(R.id.reply_photo)
        RoundedImageView reply_photo;
        @BindView(R.id.reply)
        TextView reply;
        @BindView(R.id.reply_time)
        TextView reply_time;
        @BindView(R.id.reply_body)
        TextView reply_body;
        @BindView(R.id.edit)
        TextView edit;
        @BindView(R.id.delete)
        TextView delete;
        @BindView(R.id.iv1)
        ImageView iv1;
        @BindView(R.id.iv2)
        ImageView iv2;
        @BindView(R.id.iv3)
        ImageView iv3;
        @BindView(R.id.reply_ll)
        LinearLayout reply_ll;
        @BindView(R.id.reply_bt)
        TextView reply_bt;
        @BindView(R.id.operate_ll)
        LinearLayout operate_ll;

        public MyViewHolderThree(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
