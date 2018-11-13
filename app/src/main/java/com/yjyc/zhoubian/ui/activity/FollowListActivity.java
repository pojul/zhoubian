package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.FollowUsers;
import com.yjyc.zhoubian.model.FollowUsersModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yjyc.zhoubian.ui.view.SwipeItemLayout;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;
import com.yuqian.mncommonlibrary.refresh.header.MaterialHeader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/10/16/016.
 */

public class FollowListActivity extends BaseActivity {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    private Context mContext;
    private int page = 1;
    private FollowUsers body;
    private ArrayList<FollowUsers.Data> datas = new ArrayList<>();
    RequestOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vermicelli_list);
        mContext = this;
        ButterKnife.bind(this);
        initView();
        initDate();
        setPullRefresher();
    }

    private void initView() {
        options = new RequestOptions()
                .centerCrop();
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("关注列表", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        myAdapter = new MyAdapter();

        followUsers();
    }

    private void followUsers() {
        Login loginModel = Hawk.get("LoginModel");
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.FOLLOWUSERS)
                .addParams("uid", loginModel.uid + "")
                .addParams("token", loginModel.token)
                .addParams("listRows", "10")
                .addParams("page", page + "")
                .execute(new AbsJsonCallBack<FollowUsersModel, FollowUsers>() {


                    @Override
                    public void onSuccess(FollowUsers body) {
                        if(body.list == null ){
                            ToastUtils.showShort("网络异常,请稍后重试" );
                            return;
                        }
                        FollowListActivity.this.body = body;
                        ArrayList<FollowUsers.Data> datas = (ArrayList<FollowUsers.Data>) body.list;
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
                    }
                });
    }

    private void initDate(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//纵向线性布局

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);

        recyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(this));
    }

    public interface OnItemClickListener{
        void onClick( int position);
        void onLongClick( int position);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements OnItemClickListener{
        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
            this. onItemClickListener =onItemClickListener;
        }

        @Override
        public void onClick(int position) {

        }

        @Override
        public void onLongClick(int position) {

        }

        class ViewHolder extends RecyclerView.ViewHolder{
            View myView;
            RoundedImageView iv_head_url;
            TextView tv_nickname;
            TextView tv_sign;
            Button bt_delete;
            public ViewHolder(View itemView) {
                super(itemView);
                myView = itemView;
                iv_head_url = itemView.findViewById(R.id.iv_head_url);
                tv_nickname = itemView.findViewById(R.id.tv_nickname);
                tv_sign = itemView.findViewById(R.id.tv_sign);
                bt_delete = itemView.findViewById(R.id.bt_delete);
            }
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vermicelli_list_item,null);
            final MyAdapter.ViewHolder holder = new MyAdapter.ViewHolder(view);
            return holder;
        }

        //将数据绑定到控件上
        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, final int position) {
            if( onItemClickListener!= null){
                holder.itemView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onClick(position);
                    }
                });
                holder. itemView.setOnLongClickListener( new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onItemClickListener.onLongClick(position);
                        return false;
                    }
                });
            }
            final FollowUsers.Data data = datas.get(position);

            if(!StringUtils.isEmpty(data.head_url)){
                Glide.with(mContext)
                        .load(data.head_url)
                        .apply(options)
                        .into(holder.iv_head_url);
            }

            if(!StringUtils.isEmpty(data.nickname)){
                holder.tv_nickname.setText(data.nickname);
            }

            if(!StringUtils.isEmpty(data.sign)){
                holder.tv_sign.setText(data.sign);
            }

            holder.bt_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!ProgressDialog.isShowing()){
                        ProgressDialog.showDialog(mContext);
                    }
                    cancelFollow(data.follow_user_id, position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        //下面两个方法提供给页面刷新和加载时调用
        public void add(List<FollowUsers.Data> addMessageList) {
            //增加数据
            int position = datas.size();
            datas.addAll(position, addMessageList);
            notifyItemInserted(position);
        }

        public void refresh(List<FollowUsers.Data> newList) {
            //刷新数据
            datas.clear();
            datas.addAll(newList);
            notifyDataSetChanged();
        }
    }

    private void cancelFollow(int follow_user_id, final int position) {
        Login loginModel = Hawk.get("LoginModel");
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.CANCELFOLLOW)
                .addParams("uid", loginModel.uid + "")
                .addParams("token", loginModel.token)
                .addParams("follow_user_id", follow_user_id + "")
                .execute(new AbsJsonCallBack<FollowUsersModel, FollowUsers>() {


                    @Override
                    public void onSuccess(FollowUsers body) {
                        datas.remove(position);
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

    MyAdapter myAdapter;

    private void setPullRefresher(){
        //设置 Header 为 MaterialHeader
        refreshLayout.setRefreshHeader(new MaterialHeader(this));
        //设置 Footer 为 经典样式
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                followUsers();
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if(body != null){
                    if(body.hasNextPages){
                        page++;
                        followUsers();
                    }else {
                        refreshLayout.finishLoadmore();
                        ToastUtils.showShort("没有更多");
                    }
                }else {
                    refreshLayout.finishLoadmore();
                }
                //模拟网络请求到的数据

            }
        });
    }
}