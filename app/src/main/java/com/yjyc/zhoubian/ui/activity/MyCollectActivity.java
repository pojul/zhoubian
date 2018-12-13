package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
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
import android.widget.Toast;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.Fans;
import com.yjyc.zhoubian.model.FansModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.PostCollection;
import com.yjyc.zhoubian.model.PostCollectionLists;
import com.yjyc.zhoubian.model.PostCollectionListsModel;
import com.yjyc.zhoubian.model.PostCollectionModel;
import com.yjyc.zhoubian.model.UserPostList;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;
import com.yuqian.mncommonlibrary.refresh.header.MaterialHeader;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 我的收藏
 * Created by Administrator on 2018/10/11/011.
 */

public class MyCollectActivity extends BaseActivity {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    private Context mContext;
    private int page = 1;
    private PostCollectionLists body;
    private  ArrayList<PostCollectionLists.Data> datas = new ArrayList<>();
    RequestOptions options;
    private int loadDataFlag = 0; //0: refresh 1: load more

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_collect);
        mContext = this;
        ButterKnife.bind(this);

        if(Hawk.get("LoginModel") == null){
            com.yuqian.mncommonlibrary.utils.ToastUtils.show("请先登陆");
            startActivity(new Intent(mContext, LoginActivity.class));
            return;
        }
        initView();
    }

    private void initView() {
        options = new RequestOptions()
                .centerCrop().placeholder( R.drawable.img_bg).error(R.drawable.img_bg);
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("我的收藏", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        refreshLayout.setEnableLoadmore(false);
        //设置 Header 为 MaterialHeader
        refreshLayout.setRefreshHeader(new MaterialHeader(this));
        //设置 Footer 为 经典样式
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            loadDataFlag = 0;
            postCollectionLists();
        });
        refreshLayout.setOnLoadmoreListener(refreshlayout -> {
            loadDataFlag = 1;
            postCollectionLists();
        });
        myAdapter = new MyAdapter();

        if(!ProgressDialog.isShowing()){
            ProgressDialog.showDialog(mContext);
        }
        postCollectionLists();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//纵向线性布局
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                    if(lastVisiblePosition >= layoutManager.getItemCount() - 1){
                        loadDataFlag = 1;
                        postCollectionLists();
                    }
                }
            }
        });
    }

    private void postCollectionLists() {
        Login loginModel = Hawk.get("LoginModel");
        int tempPage = 0;
        if(loadDataFlag == 1){
            tempPage = page + 1;
        }else if(loadDataFlag == 0){
            page = 1;
            myAdapter.clearDatas();
        }

        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.POSTCOLLECTIONLISTS)
                .addParams("uid", loginModel.uid + "")
                .addParams("token", loginModel.token)
                .addParams("listRows", "10")
                .addParams("page", tempPage + "")
                .execute(new AbsJsonCallBack<PostCollectionListsModel, PostCollectionLists>() {
                    @Override
                    public void onSuccess(PostCollectionLists body) {
                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefresh();
                        if(body.list == null ){
                            showToast("网络异常,请稍后重试" );
                            return;
                        }
                        MyCollectActivity.this.body = body;
                        ArrayList<PostCollectionLists.Data> datas = (ArrayList<PostCollectionLists.Data>) body.list;
                        myAdapter.add(datas);
                        if(loadDataFlag == 0){
                        }else if(loadDataFlag == 1){
                            page = page + 1;
                        }
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefresh();
                        showToast(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
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

    public  class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemClickListener{
        private OnItemClickListener mOnItemClickListener;
        public static final int TYPE_NONE = -1;
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
            TextView tv_title;
            TextView tv_user_name;
            TextView tv_view;
            TextView tv_interval_time;
            TextView tv_distance;
            ImageView iv_red_package;
            TextView cancel_collect;
            LinearLayout root_ll;

            public MyViewHolderOne(View itemView) {
                super(itemView);
                myView = itemView;
                tv_title = itemView.findViewById(R.id.tv_title);
                tv_user_name = itemView.findViewById(R.id.tv_user_name);
                tv_view = itemView.findViewById(R.id.tv_view);
                tv_interval_time = itemView.findViewById(R.id.tv_interval_time);
                tv_distance = itemView.findViewById(R.id.tv_distance);
                iv_red_package = itemView.findViewById(R.id.iv_red_package);
                cancel_collect = itemView.findViewById(R.id.cancel_collect);
                root_ll = itemView.findViewById(R.id.root_ll);
            }
        }

        public class MyViewHolderTwo extends RecyclerView.ViewHolder {

            View myView;
            TextView tv_title;
            TextView tv_user_name;
            TextView tv_view;
            TextView tv_interval_time;
            TextView tv_distance;
            ImageView iv_red_package;
            ImageView iv1;
            TextView cancel_collect;
            LinearLayout root_ll;

            public MyViewHolderTwo(View itemView) {
                super(itemView);
                myView = itemView;
                tv_title = itemView.findViewById(R.id.tv_title);
                tv_user_name = itemView.findViewById(R.id.tv_user_name);
                tv_view = itemView.findViewById(R.id.tv_view);
                tv_interval_time = itemView.findViewById(R.id.tv_interval_time);
                tv_distance = itemView.findViewById(R.id.tv_distance);
                iv_red_package = itemView.findViewById(R.id.iv_red_package);
                iv1 = itemView.findViewById(R.id.iv1);
                cancel_collect = itemView.findViewById(R.id.cancel_collect);
                root_ll = itemView.findViewById(R.id.root_ll);
            }
        }

        public class MyViewHolderThree extends RecyclerView.ViewHolder {

            View myView;
            TextView tv_title;
            TextView tv_user_name;
            TextView tv_view;
            TextView tv_interval_time;
            TextView tv_distance;
            ImageView iv_red_package;
            ImageView iv1;
            ImageView iv2;
            ImageView iv3;
            TextView cancel_collect;
            LinearLayout root_ll;

            public MyViewHolderThree(View itemView) {
                super(itemView);
                myView = itemView;
                tv_title = itemView.findViewById(R.id.tv_title);
                tv_user_name = itemView.findViewById(R.id.tv_user_name);
                tv_view = itemView.findViewById(R.id.tv_view);
                tv_interval_time = itemView.findViewById(R.id.tv_interval_time);
                tv_distance = itemView.findViewById(R.id.tv_distance);
                iv_red_package = itemView.findViewById(R.id.iv_red_package);
                iv1 = itemView.findViewById(R.id.iv1);
                iv2 = itemView.findViewById(R.id.iv2);
                iv3 = itemView.findViewById(R.id.iv3);
                cancel_collect = itemView.findViewById(R.id.cancel_collect);
                root_ll = itemView.findViewById(R.id.root_ll);
            }
        }

        public class MyViewHolderNone extends RecyclerView.ViewHolder {
            TextView cancel_collect;
            public MyViewHolderNone(View itemView) {
                super(itemView);
                cancel_collect = itemView.findViewById(R.id.cancel_collect);
            }
        }

        @Override
        public int getItemViewType(int position) {
            PostCollectionLists.Data up = datas.get(position);
            PostCollectionLists.PostInfo info = up.post_info;
            if(info == null){
                return TYPE_NONE;
            }
            if(info.pic != null && info.pic.size() > 1){
                return TYPE_THREE;
            }else if(info.pic != null && info.pic.size() == 1){
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
                case TYPE_NONE:
                    return new MyViewHolderNone(LayoutInflater.from(parent.getContext()).inflate(R.layout
                            .activity_my_collect_item_imgnone, parent, false));
                case TYPE_ONE:
                    return new MyViewHolderOne(LayoutInflater.from(parent.getContext()).inflate(R.layout
                            .activity_my_collect_item_img0, parent, false));
                case TYPE_TWO:
                    return new MyViewHolderTwo(LayoutInflater.from(parent.getContext()).inflate(R.layout.
                            activity_my_collect_item_img1, parent, false));
                case TYPE_THREE:
                    return new MyViewHolderThree(LayoutInflater.from(parent.getContext()).inflate(R.layout.
                            activity_my_collect_item_img3, parent, false));
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
            } else if (holder instanceof MyViewHolderNone) {
                bindTypeNone((MyViewHolderNone) holder, position);
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

        private void bindTypeNone(MyViewHolderNone holder, int position) {
            PostCollectionLists.Data data = datas.get(position);
            holder.cancel_collect.setOnClickListener(v->{
                cancelPostCollection(position, data);
            });
        }

        private void bindTypeOne(MyViewHolderOne holderOne, int position) {
            PostCollectionLists.Data data = datas.get(position);
            if(data == null){
                return;
            }

            PostCollectionLists.PostInfo postInfo = data.post_info;
            if(postInfo == null){
                return;
            }
            String title= postInfo.title;
            if(postInfo.custom_post_cate != null && !postInfo.custom_post_cate.isEmpty()){
                title = "【" + postInfo.custom_post_cate + "】" + title;
            }else if(postInfo.post_cate_title != null && !postInfo.post_cate_title.isEmpty()){
                title = "【" + postInfo.post_cate_title + "】" + title;
            }
            holderOne.tv_title.setText(title);

            if(!StringUtils.isEmpty(postInfo.user_name)){
                holderOne.tv_user_name.setText(postInfo.user_name);
            }

            holderOne.tv_view.setText(postInfo.view + "阅读");

            if(!StringUtils.isEmpty(postInfo.interval_time)){
                holderOne.tv_interval_time.setText(postInfo.interval_time);
            }

            if(!StringUtils.isEmpty(postInfo.distance)){
                holderOne.tv_distance.setText("距离" + postInfo.distance);
            }

            if(postInfo.red_package_rule == 1){
                holderOne.iv_red_package.setVisibility(View.VISIBLE);
            }else {
                holderOne.iv_red_package.setVisibility(View.INVISIBLE);
            }
            holderOne.cancel_collect.setOnClickListener(v->{
                cancelPostCollection(position, data);
            });
            holderOne.root_ll.setOnClickListener(v->{
                Bundle bundle = new Bundle();
                bundle.putInt("PostId", data.post_id);
                startActivityAni(PostDetailsActivity.class, bundle);
            });
        }

        private void bindTypeTwo(MyViewHolderTwo holderTwo, int position) {
            PostCollectionLists.Data data = datas.get(position);
            if(data == null){
                return;
            }

            PostCollectionLists.PostInfo postInfo = data.post_info;
            if(postInfo == null){
                return;
            }

            String title= postInfo.title;
            if(postInfo.custom_post_cate != null && !postInfo.custom_post_cate.isEmpty()){
                title = "【" + postInfo.custom_post_cate + "】" + title;
            }else if(postInfo.post_cate_title != null && !postInfo.post_cate_title.isEmpty()){
                title = "【" + postInfo.post_cate_title + "】" + title;
            }
            holderTwo.tv_title.setText(title);

            if(!StringUtils.isEmpty(postInfo.user_name)){
                holderTwo.tv_user_name.setText(postInfo.user_name);
            }

            holderTwo.tv_view.setText(postInfo.view + "阅读");

            if(!StringUtils.isEmpty(postInfo.interval_time)){
                holderTwo.tv_interval_time.setText(postInfo.interval_time);
            }

            if(!StringUtils.isEmpty(postInfo.distance)){
                holderTwo.tv_distance.setText("距离" + postInfo.distance);
            }

            if(postInfo.red_package_rule == 1){
                holderTwo.iv_red_package.setVisibility(View.VISIBLE);
            }else {
                holderTwo.iv_red_package.setVisibility(View.INVISIBLE);
            }

            if(!StringUtils.isEmpty(postInfo.pic.get(0))){
                Glide.with(mContext)
                        .load(postInfo.pic.get(0))
                        .apply(options)
                        .into(holderTwo.iv1);
            }
            holderTwo.cancel_collect.setOnClickListener(v->{
                cancelPostCollection(position, data);
            });
            holderTwo.root_ll.setOnClickListener(v->{
                Bundle bundle = new Bundle();
                bundle.putInt("PostId", data.post_id);
                startActivityAni(PostDetailsActivity.class, bundle);
            });
        }

        private void bindTypeThree(MyViewHolderThree holder, int position) {  //在其中镶嵌一个RecyclerView
            PostCollectionLists.Data data = datas.get(position);
            if(data == null){
                return;
            }

            PostCollectionLists.PostInfo postInfo = data.post_info;
            if(postInfo == null){
                return;
            }

            String title= postInfo.title;
            if(postInfo.custom_post_cate != null && !postInfo.custom_post_cate.isEmpty()){
                title = "【" + postInfo.custom_post_cate + "】" + title;
            }else if(postInfo.post_cate_title != null && !postInfo.post_cate_title.isEmpty()){
                title = "【" + postInfo.post_cate_title + "】" + title;
            }
            holder.tv_title.setText(title);

            if(!StringUtils.isEmpty(postInfo.user_name)){
                holder.tv_user_name.setText(postInfo.user_name);
            }

            holder.tv_view.setText(postInfo.view + "阅读");

            if(!StringUtils.isEmpty(postInfo.interval_time)){
                holder.tv_interval_time.setText(postInfo.interval_time);
            }

            if(!StringUtils.isEmpty(postInfo.distance)){
                holder.tv_distance.setText("距离" + postInfo.distance);
            }

            if(postInfo.red_package_rule == 1){
                holder.iv_red_package.setVisibility(View.VISIBLE);
            }else {
                holder.iv_red_package.setVisibility(View.INVISIBLE);
            }

            if(!StringUtils.isEmpty(postInfo.pic.get(0))){
                Glide.with(mContext)
                        .load(postInfo.pic.get(0))
                        .apply(options)
                        .into(holder.iv1);
            }

            if(!StringUtils.isEmpty(postInfo.pic.get(1))){
                Glide.with(mContext)
                        .load(postInfo.pic.get(1))
                        .apply(options)
                        .into(holder.iv2);
            }

            if(postInfo.pic.size() > 2 && !StringUtils.isEmpty(postInfo.pic.get(2))){
                holder.iv3.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(postInfo.pic.get(2))
                        .apply(options)
                        .into(holder.iv3);
            }
            if(postInfo.pic.size() <= 2){
                holder.iv3.setVisibility(View.INVISIBLE);
            }
            holder.cancel_collect.setOnClickListener(v->{
                cancelPostCollection(position, data);
            });
            holder.root_ll.setOnClickListener(v->{
                Bundle bundle = new Bundle();
                bundle.putInt("PostId", data.post_id);
                startActivityAni(PostDetailsActivity.class, bundle);
            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }


        //下面两个方法提供给页面刷新和加载时调用
        public void add(List<PostCollectionLists.Data> addMessageList) {
            //增加数据
            int position = datas.size();
            datas.addAll(position, addMessageList);
            notifyItemInserted(position);
        }

        public void cancelPostCollection(int position, PostCollectionLists.Data data){
            if(Hawk.get("LoginModel") == null){
                showShortToats("请先登陆");
                startActivity(new Intent(mContext, LoginActivity.class));
                return;
            }
            /*if(((Login)Hawk.get("LoginModel")).uid == data.post_id){
                return;
            }*/
            LoadingDialog.showLoading(mContext);
            Login login = Hawk.get("LoginModel");
            new OkhttpUtils().with()
                    .post()
                    .url(HttpUrl.CANCELPOSTCOLLECTION)
                    .addParams("uid", ("" + login.uid))
                    .addParams("token", login.token)
                    .addParams("post_id", ("" + data.post_id))
                    .execute(new AbsJsonCallBack<PostCollectionModel, PostCollection>() {
                        @Override
                        public void onFailure(String errorCode, String errorMsg) {
                            showShortToats(errorMsg);
                            LoadingDialog.closeLoading();
                        }
                        @Override
                        public void onSuccess(PostCollection body) {
                            LoadingDialog.closeLoading();
                            synchronized (datas){
                                datas.remove(position);
                                notifyDataSetChanged();
                            }
                        }
                    });
        }

        private void showShortToats(String msg){
            com.yuqian.mncommonlibrary.utils.ToastUtils.show(msg);
        }

        public void clearDatas(){
            synchronized (datas){
                datas.clear();
                notifyDataSetChanged();
            }
        }

        public void refresh(List<PostCollectionLists.Data> newList) {
            //刷新数据
            datas.clear();
            datas.addAll(newList);
            notifyDataSetChanged();
        }
    }
    MyAdapter myAdapter;
}