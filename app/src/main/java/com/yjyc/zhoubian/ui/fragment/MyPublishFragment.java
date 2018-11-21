package com.yjyc.zhoubian.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifOptions;
import com.bumptech.glide.request.RequestOptions;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.im.chat.ui.ChatActivity;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.UserInfo;
import com.yjyc.zhoubian.model.UserPostList;
import com.yjyc.zhoubian.model.UserPostListModel;
import com.yjyc.zhoubian.ui.activity.MyPublishActivity;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 我的发布
 * Created by Administrator on 2018/10/10/010.
 */

@SuppressLint("ValidFragment")
public class MyPublishFragment extends BaseFragment {

    private MyPublishActivity activity;
    @BindView(R.id.recyclerview)
    public RecyclerView recyclerview;

    Unbinder unbinder;
    public int page = 1;
    public UserPostList body;
    private List<UserPostList.UserPost> mList = new ArrayList<>();
    RequestOptions options;
    private UserInfo userInfo;
    private String uid;

    public MyPublishFragment(MyPublishActivity myPublishActivity, UserInfo userInfo, String uid) {
        this.userInfo = userInfo;
        this.uid = uid;
        activity = myPublishActivity;
    }

    public MyPublishFragment(){

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

        userPostList();
    }

    public void userPostList() {
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.USERPOSTLIST)
                .addParams("uid", uid)
                .addParams("listRows", "10")
                .addParams("page", page + "")
                .execute(new AbsJsonCallBack<UserPostListModel, UserPostList>() {

                    @Override
                    public void onSuccess(UserPostList body) {
                        if(body.list == null ){
                            ToastUtils.showShort("网络异常,请稍后重试" );
                            return;
                        }
                        MyPublishFragment.this.body = body;
                        ArrayList<UserPostList.UserPost> datas = (ArrayList<UserPostList.UserPost>) body.list;
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
        Login login = Hawk.get("LoginModel");
        if(login == null || !(login.uid + "").equals(uid)){
            myAdapter = new MyAdapter(2);
        }else{
            myAdapter = new MyAdapter(1);
        }
        options = new RequestOptions()
                .centerCrop();
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
        public int adapterType = 2; //1: self; 2:others

        public MyAdapter(int adapterType) {
            this.adapterType = adapterType;
        }

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
            TextView tv_title;
            //            TextView tv_price;
            TextView tv_user_name;
            TextView tv_view;
            TextView tv_time;
            ImageView iv_red_package;
            TextView tv_delete;
            View v1;
            LinearLayout operate_ll;
            public MyViewHolderOne(View itemView) {
                super(itemView);
                myView = itemView;
                tv_title = itemView.findViewById(R.id.tv_title);
//                tv_price = itemView.findViewById(R.id.tv_price);
                tv_user_name = itemView.findViewById(R.id.tv_user_name);
                tv_view = itemView.findViewById(R.id.tv_view);
                tv_time = itemView.findViewById(R.id.tv_time);
                iv_red_package = itemView.findViewById(R.id.iv_red_package);
                tv_delete = itemView.findViewById(R.id.tv_delete);
                v1 = itemView.findViewById(R.id.v1);
                operate_ll = itemView.findViewById(R.id.operate_ll);
            }
        }

        public class MyViewHolderTwo extends RecyclerView.ViewHolder {

            View myView;
            TextView tv_title;
            //            TextView tv_price;
            TextView tv_user_name;
            TextView tv_view;
            TextView tv_time;
            ImageView iv_red_package;
            ImageView iv1;
            TextView tv_delete;
            View v1;
            LinearLayout operate_ll;
            public MyViewHolderTwo(View itemView) {
                super(itemView);
                myView = itemView;
                tv_title = itemView.findViewById(R.id.tv_title);
//                tv_price = itemView.findViewById(R.id.tv_price);
                tv_user_name = itemView.findViewById(R.id.tv_user_name);
                tv_view = itemView.findViewById(R.id.tv_view);
                tv_time = itemView.findViewById(R.id.tv_time);
                iv_red_package = itemView.findViewById(R.id.iv_red_package);
                iv1 = itemView.findViewById(R.id.iv1);
                tv_delete = itemView.findViewById(R.id.tv_delete);
                v1 = itemView.findViewById(R.id.v1);
                operate_ll = itemView.findViewById(R.id.operate_ll);
            }
        }

        public class MyViewHolderThree extends RecyclerView.ViewHolder {
            View myView;
            TextView tv_title;
            //            TextView tv_price;
            TextView tv_user_name;
            TextView tv_view;
            TextView tv_time;
            ImageView iv_red_package;
            ImageView iv1;
            ImageView iv2;
            ImageView iv3;
            TextView tv_delete;
            View v1;
            LinearLayout operate_ll;
            public MyViewHolderThree(View itemView) {
                super(itemView);
                myView = itemView;
                tv_title = itemView.findViewById(R.id.tv_title);
//                tv_price = itemView.findViewById(R.id.tv_price);
                tv_user_name = itemView.findViewById(R.id.tv_user_name);
                tv_view = itemView.findViewById(R.id.tv_view);
                tv_time = itemView.findViewById(R.id.tv_time);
                iv_red_package = itemView.findViewById(R.id.iv_red_package);
                iv1 = itemView.findViewById(R.id.iv1);
                iv2 = itemView.findViewById(R.id.iv2);
                iv3 = itemView.findViewById(R.id.iv3);
                tv_delete = itemView.findViewById(R.id.tv_delete);
                v1 = itemView.findViewById(R.id.v1);
                operate_ll = itemView.findViewById(R.id.operate_ll);
            }
        }

        @Override
        public int getItemViewType(int position) {
            UserPostList.UserPost up = mList.get(position);
            if(up.pic != null && up.pic.size() > 1){
                return TYPE_THREE;
            }else if(up.pic != null && up.pic.size() == 1){
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
                            .fragment_my_publish_item_img0, parent, false));
                case TYPE_TWO:
                    return new MyViewHolderTwo(LayoutInflater.from(parent.getContext()).inflate(R.layout.
                            fragment_my_publish_item_img1, parent, false));
                case TYPE_THREE:
                    return new MyViewHolderThree(LayoutInflater.from(parent.getContext()).inflate(R.layout.
                            fragment_my_publish_item_img3, parent, false));
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

        private void bindTypeOne(MyViewHolderOne holderOne, final int position) {
            final UserPostList.UserPost up = mList.get(position);
            if(adapterType == 1){
                holderOne.operate_ll.setVisibility(View.VISIBLE);
            }else{
                holderOne.operate_ll.setVisibility(View.GONE);
            }
            if(!StringUtils.isEmpty(up.title)){
                holderOne.tv_title.setText(up.title);
            }

//            if(!StringUtils.isEmpty(up.price)){
//                holderOne.tv_price.setText(up.price);
//            }

            if(!StringUtils.isEmpty(up.user_name)){
                holderOne.tv_user_name.setText(up.user_name);
            }

            holderOne.tv_view.setText(up.view + "阅读");

            if(!StringUtils.isEmpty(up.time)){
                holderOne.tv_time.setText(up.time);
            }

            if(up.red_package_rule == 1){
                holderOne.iv_red_package.setVisibility(View.VISIBLE);
                holderOne.tv_delete.setVisibility(View.GONE);
                holderOne.v1.setVisibility(View.GONE);
            }else {
                holderOne.iv_red_package.setVisibility(View.INVISIBLE);
                holderOne.tv_delete.setVisibility(View.VISIBLE);
                holderOne.v1.setVisibility(View.VISIBLE);
            }

            holderOne.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deletePost(up.id, position);
                }
            });
        }

        private void bindTypeTwo(MyViewHolderTwo holderTwo, final int position) {
            final UserPostList.UserPost up = mList.get(position);
            if(adapterType == 1){
                holderTwo.operate_ll.setVisibility(View.VISIBLE);
            }else{
                holderTwo.operate_ll.setVisibility(View.GONE);
            }
            if(!StringUtils.isEmpty(up.title)){
                holderTwo.tv_title.setText(up.title);
            }

//            if(!StringUtils.isEmpty(up.price)){
//                holderTwo.tv_price.setText(up.price);
//            }

            if(!StringUtils.isEmpty(up.user_name)){
                holderTwo.tv_user_name.setText(up.user_name);
            }

            holderTwo.tv_view.setText(up.view + "阅读");

            if(!StringUtils.isEmpty(up.time)){
                holderTwo.tv_time.setText(up.time);
            }

            if(up.red_package_rule == 1){
                holderTwo.iv_red_package.setVisibility(View.VISIBLE);
                holderTwo.tv_delete.setVisibility(View.GONE);
                holderTwo.v1.setVisibility(View.GONE);
            }else {
                holderTwo.iv_red_package.setVisibility(View.INVISIBLE);
                holderTwo.tv_delete.setVisibility(View.VISIBLE);
                holderTwo.v1.setVisibility(View.VISIBLE);
            }

            if(!StringUtils.isEmpty(up.pic.get(0))){
                Glide.with(getActivity())
                        .load(up.pic.get(0))
                        .apply(options)
                        .into(holderTwo.iv1);
            }

            holderTwo.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deletePost(up.id, position);
                }
            });
        }

        private void bindTypeThree(MyViewHolderThree holder, final int position) {  //在其中镶嵌一个RecyclerView
            final UserPostList.UserPost up = mList.get(position);
            if(adapterType == 1){
                holder.operate_ll.setVisibility(View.VISIBLE);
            }else{
                holder.operate_ll.setVisibility(View.GONE);
            }
            if(!StringUtils.isEmpty(up.title)){
                holder.tv_title.setText(up.title);
            }

//            if(!StringUtils.isEmpty(up.price)){
//                holder.tv_price.setText(up.price);
//            }

            if(!StringUtils.isEmpty(up.user_name)){
                holder.tv_user_name.setText(up.user_name);
            }

            holder.tv_view.setText(up.view + "阅读");

            if(!StringUtils.isEmpty(up.time)){
                holder.tv_time.setText(up.time);
            }

            if(up.red_package_rule == 1){
                holder.iv_red_package.setVisibility(View.VISIBLE);
                holder.tv_delete.setVisibility(View.GONE);
                holder.v1.setVisibility(View.GONE);
            }else {
                holder.iv_red_package.setVisibility(View.INVISIBLE);
                holder.tv_delete.setVisibility(View.VISIBLE);
                holder.v1.setVisibility(View.VISIBLE);
            }

            if(!StringUtils.isEmpty(up.pic.get(0))){
                Glide.with(getActivity())
                        .load(up.pic.get(0))
                        .apply(options)
                        .into(holder.iv1);
            }

            if(!StringUtils.isEmpty(up.pic.get(1))){
                Glide.with(getActivity())
                        .load(up.pic.get(1))
                        .apply(options)
                        .into(holder.iv2);
            }

            if(up.pic.size() > 2 && !StringUtils.isEmpty(up.pic.get(2))){
                Glide.with(getActivity())
                        .load(up.pic.get(2))
                        .apply(options)
                        .into(holder.iv3);
            }

            holder.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deletePost(up.id, position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }


        //下面两个方法提供给页面刷新和加载时调用
        public void add(List<UserPostList.UserPost> addMessageList) {
            //增加数据
            int position = mList.size();
            mList.addAll(position, addMessageList);
            notifyItemInserted(position);
        }

        public void refresh(List<UserPostList.UserPost> newList) {
            //刷新数据
            mList.removeAll(mList);
            mList.addAll(newList);
            notifyDataSetChanged();
        }
    }

    private void deletePost(int id, final int position) {
        Login login = Hawk.get("LoginModel");
        if(login == null || !(login.uid + "").equals(uid)){
            return;
        }
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.DELETEPOST)
                .addParams("uid", login.uid + "")
                .addParams("token", login.token)
                .addParams("id", id + "")
                .execute(new AbsJsonCallBack<UserPostListModel, UserPostList>() {


                    @Override
                    public void onSuccess(UserPostList body) {
                        mList.remove(position);
                        myAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        ToastUtils.showShort(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                    }

                    @Override
                    public void onFinish() {
                        ProgressDialog.dismiss();
                    }
                });
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