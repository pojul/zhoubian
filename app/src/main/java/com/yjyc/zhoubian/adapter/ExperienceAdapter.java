package com.yjyc.zhoubian.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.MainActivitys;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.ExperienceList;
import com.yjyc.zhoubian.ui.activity.BaseActivity;
import com.yjyc.zhoubian.ui.activity.ValuableBookDetailActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ExperienceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final int TYPE_ONE = 0;
    public static final int TYPE_TWO = 1;
    public static final int TYPE_THREE = 2;//三种不同的布局

    private List<ExperienceList.Experience> datas;
    private Context context;

    public ExperienceAdapter(List<ExperienceList.Experience> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        ExperienceList.Experience experience = datas.get(position);
        if(experience == null || experience.pic == null || experience.pic.size() <= 0){
            return TYPE_ONE;//第一种布局
        }else if(experience.pic.size() == 1){
            return TYPE_TWO;//第一种布局
        }else{
            return TYPE_THREE;//第一种布局
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ONE:
                return new MyViewHolderOne(LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .fragment_valuable_book_item_img0, parent, false));
            case TYPE_TWO:
                return new MyViewHolderTwo(LayoutInflater.from(parent.getContext()).inflate(R.layout.
                        fragment_valuable_book_item_img1, parent, false));
            case TYPE_THREE:
                return new MyViewHolderThree(LayoutInflater.from(parent.getContext()).inflate(R.layout.
                        fragment_valuable_book_item_img3, parent, false));
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
        } else if (holder instanceof MyViewHolderThree) {
            bindTypeThree((MyViewHolderThree) holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return (datas==null?0:datas.size());
    }


    private void bindTypeOne(MyViewHolderOne holderOne, int position) {
        ExperienceList.Experience experience = datas.get(position);

        String title= experience.title;
        if(experience.custom_cate != null && !experience.custom_cate.isEmpty()){
            title = "【" + experience.custom_cate + "】" + title;
        }else if(experience.cate_name != null && !experience.cate_name.isEmpty()){
            title = "【" + experience.cate_name + "】" + title;
        }
        holderOne.text.setText(title);

        if(experience.user_name == null){
            holderOne.nick_name.setText("佚名");
        }else{
            holderOne.nick_name.setText(experience.user_name);
        }
        holderOne.comments.setText(experience.view + "阅读");
        holderOne.time.setText(experience.time);
        holderOne.root_ll.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString("id", (experience.id + ""));
            ((MainActivitys)context).startActivityAni(ValuableBookDetailActivity.class, bundle);
        });
    }

    private void bindTypeTwo(MyViewHolderTwo holderTwo, int position) {
        ExperienceList.Experience experience = datas.get(position);
        String title= experience.title;
        if(experience.custom_cate != null && !experience.custom_cate.isEmpty()){
            title = "【" + experience.custom_cate + "】" + title;
        }else if(experience.cate_name != null && !experience.cate_name.isEmpty()){
            title = "【" + experience.cate_name + "】" + title;
        }
        holderTwo.text.setText(title);
        if(experience.user_name == null){
            holderTwo.nick_name.setText("佚名");
        }else{
            holderTwo.nick_name.setText(experience.user_name);
        }
        holderTwo.comments.setText(experience.view + "阅读");
        holderTwo.time.setText(experience.time);
        Glide.with(context).load(experience.pic.get(0)).into(holderTwo.iv1);
        holderTwo.root_ll.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString("id", (experience.id + ""));
            ((MainActivitys)context).startActivityAni(ValuableBookDetailActivity.class, bundle);
        });
    }

    private void bindTypeThree(MyViewHolderThree holder, int position) {  //在其中镶嵌一个RecyclerView
        ExperienceList.Experience experience = datas.get(position);
        String title= experience.title;
        if(experience.custom_cate != null && !experience.custom_cate.isEmpty()){
            title = "【" + experience.custom_cate + "】" + title;
        }else if(experience.cate_name != null && !experience.cate_name.isEmpty()){
            title = "【" + experience.cate_name + "】" + title;
        }
        holder.text.setText(title);
        if(experience.user_name == null){
            holder.nick_name.setText("佚名");
        }else{
            holder.nick_name.setText(experience.user_name);
        }
        holder.comments.setText(experience.view + "阅读");
        holder.time.setText(experience.time);
        if(!StringUtils.isEmpty(experience.pic.get(0))){
            Glide.with(context)
                    .load(experience.pic.get(0))
                    .into(holder.iv1);
        }

        if(!StringUtils.isEmpty(experience.pic.get(1))){
            Glide.with(context)
                    .load(experience.pic.get(1))
                    .into(holder.iv2);
        }

        if(experience.pic.size() > 2 && !StringUtils.isEmpty(experience.pic.get(2))){
            holder.iv3.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(experience.pic.get(2))
                    .into(holder.iv3);
        }else{
            holder.iv3.setVisibility(View.INVISIBLE);
        }
        holder.root_ll.setOnClickListener(v->{
            Bundle bundle = new Bundle();
            bundle.putString("id", (experience.id + ""));
            ((MainActivitys)context).startActivityAni(ValuableBookDetailActivity.class, bundle);
        });
    }

    public class MyViewHolderOne extends RecyclerView.ViewHolder {

        @BindView(R.id.root_ll)
        LinearLayout root_ll;
        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.nick_name)
        TextView nick_name;
        @BindView(R.id.comments)
        TextView comments;
        @BindView(R.id.time)
        TextView time;

        public MyViewHolderOne(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class MyViewHolderTwo extends RecyclerView.ViewHolder {

        @BindView(R.id.root_ll)
        LinearLayout root_ll;
        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.nick_name)
        TextView nick_name;
        @BindView(R.id.comments)
        TextView comments;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.iv1)
        ImageView iv1;

        public MyViewHolderTwo(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class MyViewHolderThree extends RecyclerView.ViewHolder {

        @BindView(R.id.root_ll)
        LinearLayout root_ll;
        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.nick_name)
        TextView nick_name;
        @BindView(R.id.comments)
        TextView comments;
        @BindView(R.id.time)
        TextView time;
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

    public void addDatas(List<ExperienceList.Experience> experiences){
        synchronized (datas){
            int position = datas.size();
            datas.addAll(experiences);
            notifyItemRangeChanged(position, experiences.size());
        }
    }

    public void clearDatas(){
        synchronized (datas){
            datas.clear();
            notifyDataSetChanged();
        }
    }

}
