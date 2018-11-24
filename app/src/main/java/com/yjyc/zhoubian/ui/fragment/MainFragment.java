package com.yjyc.zhoubian.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;


import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.MainActivitys;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.adapter.CardAdapter;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.PostCate;
import com.yjyc.zhoubian.model.PostCateItem;
import com.yjyc.zhoubian.model.PostCateModel;
import com.yjyc.zhoubian.model.SearchPostModel;
import com.yjyc.zhoubian.model.SearchPosts;
import com.yjyc.zhoubian.ui.activity.HobbySettingActivity;
import com.yjyc.zhoubian.ui.activity.LocationSettingActivity;
import com.yjyc.zhoubian.ui.activity.LoginActivity;
import com.yjyc.zhoubian.ui.activity.PostDetailsActivity;
import com.yjyc.zhoubian.ui.activity.ReportActivity;
import com.yjyc.zhoubian.ui.activity.SearchActivity;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 首页
 * Created by Administrator on 2018/10/9/009.
 */

@SuppressLint("ValidFragment")
public class MainFragment extends Fragment{

    @BindView(R.id.recyclerview)
    public RecyclerView recyclerview;
    @BindView(R.id.refreshLayout)
    public RefreshLayout refreshLayout;
    @BindView(R.id.id_recyclerview_horizontal)
    public RecyclerView mRecyclerView;
    Unbinder unbinder;
    private GalleryAdapter mAdapter;
    private MainActivitys mContext;

    private List<PostCateItem> postCates;

    private ArrayList<SearchPosts.SearchPost> datas = new ArrayList<>();
    private int listRows = 10;
    private int currentPage = 1;
    private int loadPostFlag = 1; // 0: loadmore; 1: refresh; 2: next20; 3: next30; 4 next40; 5 next50
    private int loadPostCateId = -2;
    private int nextDownCount;

    public MainFragment(){

    }

    public MainFragment(MainActivitys mContext) {
        this.mContext = mContext;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        unbinder = ButterKnife.bind(this, view);

        initViews();
        initDate();
        return view;
    }

    private void initViews() {
        postCates = new ArrayList<>();
        PostCateItem followCate = new PostCateItem();
        followCate.setId(-4);
        followCate.setSelected(false);
        followCate.setTitle("关注");
        PostCateItem hobbyCate = new PostCateItem();
        hobbyCate.setId(-3);
        hobbyCate.setSelected(false);
        hobbyCate.setTitle("爱好");
        PostCateItem recommendCate = new PostCateItem();
        recommendCate.setId(-2);
        recommendCate.setTitle("推荐");
        recommendCate.setSelected(true);
        PostCateItem timeCate = new PostCateItem();
        timeCate.setId(-1);
        timeCate.setTitle("时间");
        timeCate.setSelected(false);
        postCates.add(followCate);
        postCates.add(hobbyCate);
        postCates.add(recommendCate);
        postCates.add(timeCate);
        reqPostCates();

        mAdapter = new GalleryAdapter(getActivity(), postCates);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                PostCateItem postCate = postCates.get(position);
                myAdapter.clearDatas();
                loadPostFlag = 1;
                loadPostCateId = postCate.getId();
                reqPostList();
            }

            @Override
            public void onLongClick(int position) {

            }
        });
        myAdapter = new CardAdapter(datas, getActivity());
        myAdapter.setOnItemClickListener(new com.yjyc.zhoubian.adapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                int postId = myAdapter.getPostId(position);
                if(postId == -1 || postId == -2){
                    //showShortToast("数据错误");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt("PostId", postId);
                ((MainActivitys)getActivity()).startActivityAni(PostDetailsActivity.class, bundle);
            }

            @Override
            public void onLongClick(int position) {

            }

            @Override
            public void onDeleteClick(ImageView iv_delete, boolean isDown, int[] position) {
            }
        });

        //设置 Footer 为 经典样式
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        refreshLayout.setOnLoadmoreListener(refreshlayout -> {
            loadPostFlag = 0;
            reqPostList();
        });
        refreshLayout.setOnRefreshListener(refreshlayout -> {
            myAdapter.clearDatas();
            loadPostFlag = 1;
            reqPostList();
        });
        refreshLayout.autoLoadmore();//自动加载
    }

    private void reqPostList(){
        int reqpage = 1;
        if(loadPostFlag != 1){
            reqpage = currentPage + 1;
        }
        Login login = Hawk.get("LoginModel");
        OkhttpUtils okhttpUtils =OkhttpUtils.with()
                .get()
                .url(HttpUrl.POSTS)
                .addParams("page", ("" + reqpage))
                .addParams("listRows", ("" + listRows));
        if(login == null && loadPostCateId == -4){
            startActivity(new Intent(getActivity(), LoginActivity.class));
            showShortToast("请先登录");
            return;
        }
        if(login != null){
            okhttpUtils.addParams("uid", ("" + login.uid));
            okhttpUtils.addParams("token", ("" + login.token));
        }
        if(loadPostCateId == -4){
            okhttpUtils.post()
                    .url(HttpUrl.FOLLOWUSERPOSTS)
                    .addParams("follow", "true");
        }else if(loadPostCateId == -3){
            okhttpUtils.post()
                    .url(HttpUrl.HOBBYPOSTS);
        }else if(loadPostCateId == -2){
        }else if(loadPostCateId == -1){
            okhttpUtils.addParams("order", "create_time");
        }else{
            okhttpUtils.addParams("postCateId", ("" + loadPostCateId));
        }
        LoadingDialog.showLoading(getActivity());
        okhttpUtils.execute(new AbsJsonCallBack<SearchPostModel, SearchPosts>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showShortToast(errorMsg);
                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefresh();
                    }
                    @Override
                    public void onSuccess(SearchPosts body) {
                        if(loadPostFlag <= 1 || nextDownCount <= 1){
                            LoadingDialog.closeLoading();
                        }
                        refreshLayout.finishLoadmore();
                        refreshLayout.finishRefresh();
                        if(body == null || body.list == null || body.list.size() <= 0){
                            LoadingDialog.closeLoading();
                            if(loadPostFlag == 0){
                                showShortToast("没有更多了");
                            }
                            if(loadPostFlag > 1){
                                showShortToast("已经到底了");
                                int smothTo = recyclerview.getAdapter().getItemCount() - (recyclerview.getAdapter().getItemCount() % 10) + 1;
                                if(smothTo > recyclerview.getAdapter().getItemCount()){
                                    smothTo = recyclerview.getAdapter().getItemCount();
                                }
                                if(nextDownCount > 0){
                                    recyclerview.smoothScrollToPosition(recyclerview.getAdapter().getItemCount());
                                }else{
                                    recyclerview.smoothScrollToPosition(smothTo);
                                }
                                nextDownCount = 0;
                            }
                            return;
                        }
                        if(loadPostFlag == 1){
                            currentPage = 1;
                        }else{
                            currentPage = currentPage + 1;
                        }
                        myAdapter.add(body.list);
                        if(loadPostFlag > 1){
                            if(nextDownCount >= 1){
                                nextDownCount = nextDownCount - 1;
                                reqPostList();
                            }else{
                                recyclerview.smoothScrollToPosition((recyclerview.getAdapter().getItemCount() - 9));
                            }
                        }
                    }
                });
    }


    public void postDownturn(int currentPos, int downturnNum){
        int nextPage = datas.size() - (currentPos + 10 + downturnNum);
        if(nextPage > -10){
            recyclerview.smoothScrollToPosition((currentPos + currentPos / 20 + downturnNum + downturnNum / 20));
            return;
        }else{
            nextDownCount = Math.abs(nextPage) / 10;
        }
        if(downturnNum == 20){
            loadPostFlag = 2;
        }else if(downturnNum == 30){
            loadPostFlag = 3;
        }else if (downturnNum == 50){
            loadPostFlag = 5;
        }
        reqPostList();
    }

    private void reqPostCates() {
        LoadingDialog.showLoading(getActivity());
        OkhttpUtils.with()
                .get()
                .url(HttpUrl.POSTCATE)
                .execute(new AbsJsonCallBack<PostCateModel, PostCate>() {

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                        LoadingDialog.closeLoading();
                    }

                    @Override
                    public void onSuccess(PostCate body) {
                        List<PostCateItem> tempDatas = new ArrayList<>();
                        for (int i = 0; i < body.list.size(); i++) {
                            PostCate.Data data = body.list.get(i);
                            if (data == null){
                                continue;
                            }
                            PostCateItem postCateItem = new PostCateItem();
                            postCateItem.setId(data.getId());
                            postCateItem.setTitle(data.getTitle());
                            postCateItem.setSelected(false);
                            tempDatas.add(postCateItem);
                        }
                        mAdapter.addDatas(tempDatas);
                        LoadingDialog.closeLoading();
                    }
                });
    }

    CardAdapter myAdapter;
    private void initDate(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//纵向线性布局

        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(myAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //设置适配器
        mRecyclerView.setAdapter(mAdapter);

        reqPostList();
    }

    @OnClick(R.id.ll_hobby_setting)
    public void ll_hobby_setting(){
        if(!Hawk.contains("LoginModel")){
            startActivity(new Intent(getActivity(), LoginActivity.class));
            return;
        }

        if(!Hawk.contains("pcs")){
            postCate();
        }else {
            ArrayList<PostCate> pcs = Hawk.get("pcs");
            Intent intent = new Intent(getActivity(), HobbySettingActivity.class);
            startActivity(intent);
        }
    }

    private void postCate() {
        if(!ProgressDialog.isShowing()){
            ProgressDialog.showDialog(mContext);
        }
        OkhttpUtils.with()
                .get()
                .url(HttpUrl.POSTCATE)
                .execute(new AbsJsonCallBack<PostCateModel, PostCate[]>() {
                    @Override
                    public void onSuccess(PostCate[] body) {
                        ArrayList<PostCate> pcs = new ArrayList<>();
                        for (PostCate pc : body){
                            pcs.add(pc);
                        }
                        Hawk.put("pcs", pcs);
                        Intent intent = new Intent(getActivity(), HobbySettingActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        //ToastUtils.showShort(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                    }

                    @Override
                    public void onFinish() {
                        ProgressDialog.dismiss();
                    }
                });
    }

    @OnClick(R.id.ll_search)
    public void ll_search(){
        startActivity(new Intent(getActivity(), SearchActivity.class));
    }

    @OnClick(R.id.ll_loc_set)
    public void LocSet(){
        startActivity(new Intent(getActivity(), LocationSettingActivity.class));
    }

    public interface OnItemClickListener{
        void onClick( int position);
        void onLongClick( int position);
    }
    public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> implements OnItemClickListener{
        private LayoutInflater mInflater;
        private OnItemClickListener onItemClickListener;
        private List<PostCateItem> postCates;
        public GalleryAdapter(Context context, List<PostCateItem> postCates){
            mInflater = LayoutInflater.from(context);
            this.postCates = postCates;
        }

        @Override
        public void onClick(int position) {

        }

        @Override
        public void onLongClick(int position) {

        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView mTxt;
            public ViewHolder(View itemView){
                super(itemView);
                mTxt = itemView.findViewById(R.id.tv);
            }
        }

        @Override
        public int getItemCount(){
            return postCates.size();
        }

        /**
         * 创建ViewHolder
         */
        @Override
        public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
        {
            View view = mInflater.inflate(R.layout.fragment_main_titlebar_item,
                    viewGroup, false);
            GalleryAdapter.ViewHolder viewHolder = new GalleryAdapter.ViewHolder(view);

            return viewHolder;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        /**
         * 设置值
         */
        @Override
        public void onBindViewHolder(final GalleryAdapter.ViewHolder holder, int position){
            PostCateItem postCate = postCates.get(position);
            holder.mTxt.setText(postCate.getTitle());
            holder.mTxt.setSelected(postCate.isSelected());
            holder.mTxt.setOnClickListener(v->{
                int selected = getSelectedPos();
                if(selected != position){
                    postCates.get(position).setSelected(true);
                    postCates.get(selected).setSelected(false);
                    notifyDataSetChanged();
                }

                if(onItemClickListener != null){
                    onItemClickListener.onClick(position);
                }
            });
        }

        public int getSelectedPos(){
            for (int i = 0; i < postCates.size(); i++) {
                if(postCates.get(i).isSelected()){
                    return i;
                }
            }
            return -2;
        }

        public void addDatas(List<PostCateItem> postCates){
            this.postCates.addAll(postCates);
            notifyItemRangeInserted(this.postCates.size(), postCates.size());
        }

        public void clearDatas(){
            synchronized (postCates){
                postCates.clear();
                notifyDataSetChanged();
            }
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
            this.onItemClickListener=onItemClickListener;
        }

    }

    public void showShortToast(String msg){
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
