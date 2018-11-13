package com.yjyc.zhoubian.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.AcceptEvaluationExposeModel;
import com.yjyc.zhoubian.model.AcceptEvaluationExposes;
import com.yjyc.zhoubian.ui.activity.MyPublishActivity;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 我揭露的别人
 * Created by Administrator on 2018/10/10/010.
 */

@SuppressLint("ValidFragment")
public class MyExposeFragment extends BaseFragment {

    private MyPublishActivity activity;
    @BindView(R.id.recyclerview)
    public RecyclerView recyclerview;

    Unbinder unbinder;

    RequestOptions options;
    RequestOptions options2;

    public int page = 1;

    public AcceptEvaluationExposes body;
    private List<AcceptEvaluationExposes.AcceptEvaluationExpose> mList = new ArrayList<>();

    public MyExposeFragment(MyPublishActivity myPublishActivity) {
        activity = myPublishActivity;
    }

    public MyExposeFragment(){

    }

    @Override
    View initView() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_publish, container, false);

        unbinder = ButterKnife.bind(this, view);


        initViews();
        initDate();
        return view;
    }

    @Override
    protected void Loading() {
        if (!isVisible){
            return;
        }

        if(activity != null && !ProgressDialog.isShowing()){
            ProgressDialog.showDialog(activity);
        }

        giveEvaluationExpose();
    }

    public void giveEvaluationExpose() {
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.GIVEEVALUATIONEXPOSE)
                .addParams("uid", activity.uid)
                .addParams("listRows", "10")
                .addParams("cate_id", "2")
                .addParams("page", page + "")
                .execute(new AbsJsonCallBack<AcceptEvaluationExposeModel, AcceptEvaluationExposes>() {


                    @Override
                    public void onSuccess(AcceptEvaluationExposes body) {
                        if(body.list == null ){
                            ToastUtils.showShort("网络异常,请稍后重试" );
                            return;
                        }
                        MyExposeFragment.this.body = body;
                        ArrayList<AcceptEvaluationExposes.AcceptEvaluationExpose> datas = (ArrayList<AcceptEvaluationExposes.AcceptEvaluationExpose>) body.list;
                        if(page == 1){
                            myAdapter.refresh(datas);
                        }else {
                            myAdapter.add(datas);
                        }
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        ToastUtils.showShort(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                    }

                    @Override
                    public void onFinish() {
                        ProgressDialog.dismiss();
                        if(activity.refreshLayout != null){
                            activity.refreshLayout.finishRefresh();
                            activity.refreshLayout.finishLoadmore();
                        }
                    }
                });
    }

    private void initViews() {
        myAdapter = new MyAdapter();
        options = new RequestOptions()
                .centerCrop().placeholder( R.drawable.head_url).error(R.drawable.head_url);

        options2 = new RequestOptions()
                .centerCrop().placeholder( R.drawable.img_bg).error(R.drawable.img_bg);
    }

    MyAdapter myAdapter;
    private void initDate(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//纵向线性布局

        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(myAdapter);

    }

    public  class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemClickListener{
        private OnItemClickListener mOnItemClickListener;
        public static final int TYPE_ONE = 0;
        public static final int TYPE_TWO = 1;
        public static final int TYPE_THREE = 2;//三种不同的布局

        public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
            this. mOnItemClickListener=onItemClickListener;
        }

        @Override
        public void onClick(int position) {

        }

        @Override
        public void onLongClick(int position) {

        }

        public class MyViewHolderOne extends RecyclerView.ViewHolder {
            View myView;
            ImageView iv1;
            ImageView iv2;
            ImageView iv3;
            TextView tv_body;
            TextView tv_be_nickname;
            RoundedImageView iv_be_head_url;
            TextView tv_nickname;
            RoundedImageView iv_head_url;
            public MyViewHolderOne(View itemView) {
                super(itemView);
                myView = itemView;
                iv1 = itemView.findViewById(R.id.iv1);
                iv2 = itemView.findViewById(R.id.iv2);
                iv3 = itemView.findViewById(R.id.iv3);
                tv_body = itemView.findViewById(R.id.tv_body);
                tv_be_nickname = itemView.findViewById(R.id.tv_be_nickname);
                iv_be_head_url = itemView.findViewById(R.id.iv_be_head_url);
                tv_nickname = itemView.findViewById(R.id.tv_nickname);
                iv_head_url = itemView.findViewById(R.id.iv_head_url);
            }
        }

        public class MyViewHolderTwo extends RecyclerView.ViewHolder {

            public MyViewHolderTwo(View itemView) {
                super(itemView);
            }
        }


        @Override
        public int getItemViewType(int position) {
            AcceptEvaluationExposes.AcceptEvaluationExpose acc = mList.get(position);
            if(acc.pic != null && acc.pic.size() > 0){
                return TYPE_TWO;
            }else {
                return TYPE_ONE;
            }
        }
//        public MyAdapter(List<ItemBean> list){
//            this.mList = list;
//        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_ONE:
                    return new MyViewHolderOne(LayoutInflater.from(parent.getContext()).inflate(R.layout
                            .fragment_my_expose_item_img0, parent, false));
                case TYPE_TWO:
                    return new MyViewHolderTwo(LayoutInflater.from(parent.getContext()).inflate(R.layout.
                            fragment_my_expose_item_img1, parent, false));
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
            AcceptEvaluationExposes.AcceptEvaluationExpose acc = mList.get(position);

            if(!StringUtils.isEmpty(acc.pic.get(0))){
                Glide.with(getActivity())
                        .load(acc.pic.get(0))
                        .apply(options2)
                        .into(holderOne.iv1);
            }

            if(acc.pic.size() > 1 && !StringUtils.isEmpty(acc.pic.get(1))){
                Glide.with(getActivity())
                        .load(acc.pic.get(1))
                        .apply(options2)
                        .into(holderOne.iv2);
            }

            if(acc.pic.size() > 2 &&!StringUtils.isEmpty(acc.pic.get(2))){
                Glide.with(getActivity())
                        .load(acc.pic.get(2))
                        .apply(options2)
                        .into(holderOne.iv3);
            }

            if(!StringUtils.isEmpty(acc.body)){
                holderOne.tv_body.setText(acc.body);
            }

            if(acc.be_user_info != null){
                holderOne.tv_be_nickname.setText(StringUtils.isEmpty(acc.be_user_info.nickname) ? "" : acc.be_user_info.nickname);
                Glide.with(getActivity())
                        .load(acc.be_user_info.head_url)
                        .apply(options)
                        .into(holderOne.iv_be_head_url);
            }

            if(acc.user_info != null){
                holderOne.tv_nickname.setText(StringUtils.isEmpty(acc.user_info.nickname) ? "" : acc.user_info.nickname);
                Glide.with(getActivity())
                        .load(acc.user_info.head_url)
                        .apply(options)
                        .into(holderOne.iv_head_url);
            }
        }

        private void bindTypeTwo(MyViewHolderTwo holderTwo, int position) {
        }


        @Override
        public int getItemCount() {
            return mList.size();
        }


        //下面两个方法提供给页面刷新和加载时调用
        public void add(List<AcceptEvaluationExposes.AcceptEvaluationExpose> addMessageList) {
            //增加数据
            int position = mList.size();
            mList.addAll(position, addMessageList);
            notifyItemInserted(position);
        }

        public void refresh(List<AcceptEvaluationExposes.AcceptEvaluationExpose> newList) {
            //刷新数据
            mList.removeAll(mList);
            mList.addAll(newList);
            notifyDataSetChanged();
        }
    }

    public interface OnItemClickListener{
        void onClick( int position);
        void onLongClick( int position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}