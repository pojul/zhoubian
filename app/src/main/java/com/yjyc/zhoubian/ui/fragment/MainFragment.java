package com.yjyc.zhoubian.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.MainActivitys;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.adapter.CardAdapter;
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.PostCateItem;
import com.yjyc.zhoubian.model.SearchPostModel;
import com.yjyc.zhoubian.model.SearchPosts;
import com.yjyc.zhoubian.ui.activity.LoginActivity;
import com.yjyc.zhoubian.ui.activity.PostDetailsActivity;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;
import com.yuqian.mncommonlibrary.utils.LogUtil;

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
    Unbinder unbinder;
    private MainActivitys mContext;

    private ArrayList<SearchPosts.SearchPost> datas = new ArrayList<>();
    private int listRows = 10;
    private int currentPage = 1;
    private int loadPostFlag = 1; // 0: loadmore; 1: refresh; 2: next20; 3: next30; 4 next40; 5 next50
    //private int loadPostCateId = -2;
    private int cateId;
    private int nextDownCount;
    private boolean hasInit = false;

    public MainFragment(){

    }

    public MainFragment(MainActivitys mContext, int cateId) {
        this.mContext = mContext;
        this.cateId = cateId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Login login = Hawk.get("LoginModel");
        if(login == null && cateId == -4){
            if(!BaseApplication.firstIn){
                startActivity(new Intent(getActivity(), LoginActivity.class));
                showShortToast("请先登录");
            }
            BaseApplication.firstIn = false;
        }
        hasInit = true;
        if(getUserVisibleHint()){
            initViews();
            initDate();
            LogUtil.e(getUserVisibleHint() + "::init onActivityCreated::" + cateId);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.e(getUserVisibleHint() + "::cateId::" + cateId);
        if(hasInit && getUserVisibleHint()){
            initViews();
            initDate();
            LogUtil.e(getUserVisibleHint() + "::init setUserVisibleHint::" + cateId);
        }
    }

    private void initViews() {
        myAdapter = new CardAdapter(datas, getActivity());
        myAdapter.setOnItemClickListener(new com.yjyc.zhoubian.adapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                int postId = myAdapter.getPostId(position);
                if(postId == -1 || postId == -2){
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt("PostId", postId);
                ((MainActivitys)getActivity()).startActivityAni(PostDetailsActivity.class, bundle);
                BaseApplication.getIntstance().addViewedPost(myAdapter.getSearchPost(position));
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
        if(!hasInit || !isVisible()){
            return;
        }
        LogUtil.e( " hasInit:" + hasInit + "; createId: " + cateId + "; visiable: " + getUserVisibleHint());
        int reqpage = 1;
        if(loadPostFlag != 1){
            reqpage = currentPage + 1;
        }
        Login login = Hawk.get("LoginModel");
        OkhttpUtils okhttpUtils =new OkhttpUtils().with()
                .get()
                .url(HttpUrl.POSTS)
                .addParams("page", ("" + reqpage))
                .addParams("listRows", ("" + listRows));
        if(BaseApplication.myLocation != null){
            okhttpUtils.addParams("lat", "" + BaseApplication.myLocation.getLatitude());
            okhttpUtils.addParams("lon", "" + BaseApplication.myLocation.getLongitude());
        }
        if(login == null && cateId == -4){
            return;
        }
        if(login != null){
            okhttpUtils.addParams("uid", ("" + login.uid));
            okhttpUtils.addParams("token", ("" + login.token));
        }
        if(cateId == -4){
            okhttpUtils.post()
                    .url(HttpUrl.FOLLOWUSERPOSTS)
                    .addParams("follow", "true");
        }else if(cateId == -3){
            okhttpUtils.post()
                    .url(HttpUrl.HOBBYPOSTS);
        }else if(cateId == -2){
        }else if(cateId == -1){
            okhttpUtils.addParams("order", "create_time");
        }else{
            okhttpUtils.addParams("postCateId", ("" + cateId));
        }
        LoadingDialog.showLoading(getActivity());
        okhttpUtils.execute(new AbsJsonCallBack<SearchPostModel, SearchPosts>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showShortToast(errorMsg);
                        if(refreshLayout != null){
                            refreshLayout.finishLoadmore();
                            refreshLayout.finishRefresh();
                        }
                    }
                    @Override
                    public void onSuccess(SearchPosts body) {
                        if(loadPostFlag <= 1 || nextDownCount <= 1){
                            LoadingDialog.closeLoading();
                        }
                        if(refreshLayout != null){
                            refreshLayout.finishLoadmore();
                            refreshLayout.finishRefresh();
                        }
                        if(body == null || body.list == null || body.list.size() <= 0){
                            LoadingDialog.closeLoading();
                            if(loadPostFlag == 0){
                                //showShortToast("没有更多了");
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
        }else if(downturnNum == 40){
            loadPostFlag = 4;
        }else if (downturnNum == 50){
            loadPostFlag = 5;
        }
        reqPostList();
    }

    CardAdapter myAdapter;
    private void initDate(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//纵向线性布局

        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(myAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        reqPostList();
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
        hasInit = false;
    }
}
