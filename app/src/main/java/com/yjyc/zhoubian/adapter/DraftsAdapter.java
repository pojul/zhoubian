package com.yjyc.zhoubian.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.EmptyEntity;
import com.yjyc.zhoubian.model.EmptyEntityModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.PostDraft;
import com.yjyc.zhoubian.ui.activity.BaseActivity;
import com.yjyc.zhoubian.ui.activity.EditPostDraftActivity;
import com.yjyc.zhoubian.utils.ArrayUtil;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DraftsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_ONE = 0;
    public static final int TYPE_TWO = 1;
    public static final int TYPE_THREE = 2;//三种不同的布局

    private Context mContext;
    private List<PostDraft.DraftData> datas;

    public DraftsAdapter(Context mContext, List<PostDraft.DraftData> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ONE:
                return new MyViewHolderOne(LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .activity_drafts_item_img0, parent, false));
            case TYPE_TWO:
                return new MyViewHolderTwo(LayoutInflater.from(parent.getContext()).inflate(R.layout.
                        activity_drafts_item_img1, parent, false));
            case TYPE_THREE:
                return new MyViewHolderThree(LayoutInflater.from(parent.getContext()).inflate(R.layout.
                        activity_drafts_item_img3, parent, false));
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
        return (datas == null ? 0 : datas.size());
    }

    @Override
    public int getItemViewType(int position) {
        PostDraft.DraftData draft = datas.get(position);
        if(draft.pic != null && draft.pic.size() > 1){
            return TYPE_THREE;
        }else if(draft.pic != null && draft.pic.size() == 1){
            return TYPE_TWO;
        }else {
            return TYPE_ONE;
        }
    }

    private void bindTypeOne(MyViewHolderOne holderOne, int position) {
        PostDraft.DraftData draft = datas.get(position);
        holderOne.title.setText(draft.title);
        holderOne.delete.setOnClickListener(v->{
            deleteDraft(draft, position);
        });
        holderOne.edit.setOnClickListener(v->{
            editDraft(draft);
        });
    }

    private void bindTypeTwo(MyViewHolderTwo holderTwo, int position) {
        PostDraft.DraftData draft = datas.get(position);
        holderTwo.title.setText(draft.title);
        if(draft.pic != null && draft.pic.size() >= 1){
            Glide.with(mContext).load(new File(draft.pic.get(0))).into(holderTwo.iv1);
        }
        holderTwo.delete.setOnClickListener(v->{
            deleteDraft(draft, position);
        });
        holderTwo.edit.setOnClickListener(v->{
            editDraft(draft);
        });
    }

    private void bindTypeThree(MyViewHolderThree holder, int position) {
        PostDraft.DraftData draft = datas.get(position);
        holder.title.setText(draft.title);
        if(draft.pic != null && draft.pic.size() >= 1){
            Glide.with(mContext).load(new File(draft.pic.get(0))).into(holder.iv1);
        }
        if(draft.pic != null && draft.pic.size() >= 2){
            Glide.with(mContext).load(new File(draft.pic.get(1))).into(holder.iv2);
        }
        if(draft.pic != null && draft.pic.size() >= 3){
            holder.iv3.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(new File(draft.pic.get(2))).into(holder.iv3);
        }else{
            holder.iv3.setVisibility(View.INVISIBLE);
        }
        holder.delete.setOnClickListener(v->{
            deleteDraft(draft, position);
        });
        holder.edit.setOnClickListener(v->{
            editDraft(draft);
        });
    }

    private void editDraft(PostDraft.DraftData draft) {
        Bundle bundle = new Bundle();
        bundle.putInt("draftId", draft.id);
        ((BaseActivity)mContext).startActivityAni(EditPostDraftActivity.class, bundle);
    }

    private void deleteDraft(PostDraft.DraftData draft, int position) {
        Login login = Hawk.get("LoginModel");
        if(login == null){
            return;
        }
        LoadingDialog.showLoading(mContext);
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.DELETEPOSTDRAFT)
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .addParams("id", draft.id + "")
                .execute(new AbsJsonCallBack<EmptyEntityModel, EmptyEntity>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(EmptyEntity body) {
                        LoadingDialog.closeLoading();
                        synchronized (datas){
                            datas.remove(position);
                            notifyItemRemoved(position);
                            notifyItemChanged(0, datas.size());
                        }
                    }
                });
    }

    public void clearData(){
        synchronized (datas){
            datas.clear();
            notifyDataSetChanged();
        }
    }

    public void addData(List<PostDraft.DraftData> dataList){
        if(dataList == null){
            return;
        }
        for (int i = 0; i < dataList.size(); i++) {
            PostDraft.DraftData draftData = dataList.get(i);
            draftData.pic = ArrayUtil.filterLocalPic(draftData.pic);
        }
        synchronized (datas){
            int position = datas.size();
            datas.addAll(dataList);
            notifyItemRangeChanged(position, dataList.size());
        }
    }

    public class MyViewHolderOne extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.delete)
        TextView delete;
        @BindView(R.id.edit)
        TextView edit;

        public MyViewHolderOne(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class MyViewHolderTwo extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.delete)
        TextView delete;
        @BindView(R.id.edit)
        TextView edit;
        @BindView(R.id.iv1)
        ImageView iv1;

        public MyViewHolderTwo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class MyViewHolderThree extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.delete)
        TextView delete;
        @BindView(R.id.edit)
        TextView edit;
        @BindView(R.id.iv1)
        ImageView iv1;
        @BindView(R.id.iv2)
        ImageView iv2;
        @BindView(R.id.iv3)
        ImageView iv3;

        public MyViewHolderThree(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
