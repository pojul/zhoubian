package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
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
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.UserInfo;
import com.yjyc.zhoubian.model.UserInfoModel;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yjyc.zhoubian.ui.fragment.DopeFragment;
import com.yjyc.zhoubian.ui.fragment.MeFragment;
import com.yjyc.zhoubian.ui.fragment.MyEvaluationFragment;
import com.yjyc.zhoubian.ui.fragment.MyEvaluationsFragment;
import com.yjyc.zhoubian.ui.fragment.MyExposeFragment;
import com.yjyc.zhoubian.ui.fragment.MyExposesFragment;
import com.yjyc.zhoubian.ui.fragment.MyPublishFragment;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;
import com.yuqian.mncommonlibrary.refresh.header.MaterialHeader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 个人中心
 * Created by Administrator on 2018/10/10/010.
 */

public class MyPublishActivity extends BaseActivity {
    @BindView(R.id.tvviewpager)
    public ViewPager mViewPager ;

    @BindView(R.id.tvtablayout)
    public TabLayout mTabLayout;

    @BindView(R.id.iv_headUrl)
    RoundedImageView iv_headUrl;

    @BindView(R.id.tv_nickname)
    TextView tv_nickname;

    @BindView(R.id.tv_sex)
    TextView tv_sex;

    @BindView(R.id.tv_follow)
    TextView tv_follow;

    @BindView(R.id.tv_fans)
    TextView tv_fans;

    @BindView(R.id.tv_cty)
    TextView tv_cty;

    @BindView(R.id.tv_phone)
    TextView tv_phone;

    @BindView(R.id.tv_sign)
    TextView tv_sign;

    @BindView(R.id.refreshLayout)
    public RefreshLayout refreshLayout;
    private Context mContext;
    private UserInfo body;
    RequestOptions options;
    Login loginModel;
    private MyPublishFragment myPublishFragment;
    public String uid;
    private MyEvaluationsFragment myEvaluationsFragment;
    private MyExposesFragment myExposesFragment;
    private MyEvaluationFragment myEvaluationFragment;
    private MyExposeFragment myExposeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_publish);
        mContext = this;
        ButterKnife.bind(this);
        initView();
        setPullRefresher();
        initViewPager();
    }

    private void initView() {
        uid = getIntent().getStringExtra("uid");
        loginModel = Hawk.get("LoginModel");
        options = new RequestOptions()
                .centerCrop().error(R.drawable.head_url).placeholder(R.drawable.head_url);
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("个人中心", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if(Hawk.contains("userInfo")){
            UserInfo userInfo = Hawk.get("userInfo");
            setView(userInfo);
        }
    }

    private void userInfo() {
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.USERINFO)
                .addParams("uid", loginModel.uid + "")
                .addParams("token", loginModel.token)
                .execute(new AbsJsonCallBack<UserInfoModel, UserInfo>() {


                    @Override
                    public void onSuccess(UserInfo body) {
                        body.uid = loginModel.uid;
                        Hawk.put("userInfo", body);
                        MyPublishActivity.this.body = body;
                        setView(body);
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

    @OnClick(R.id.tv_fans)
    public void tv_fans(){
        startActivity(new Intent(mContext,VermicelliListActivity.class));
    }

    @OnClick(R.id.tv_follow)
    public void tv_follow(){
        startActivity(new Intent(mContext,FollowListActivity.class));
    }

    private void setView(UserInfo body) {
        if(!StringUtils.isEmpty(body.head_url_img)){
            Glide.with(this)
                    .load(body.head_url_img)
                    .apply(options)
                    .into(iv_headUrl);
        }

        tv_nickname.setText(StringUtils.isEmpty(body.nickname) ? "" : body.nickname);

        if(body.sex == 1){
            tv_sex.setText("男");
        }else if(body.sex == 2){
            tv_sex.setText("女");
        }

        StringBuilder city = new StringBuilder();
        city.append(StringUtils.isEmpty(body.provinces) ? "" : body.provinces).append
                (StringUtils.isEmpty(body.city) ? "" : body.city);
        tv_cty.setText("85-90年   " + city.toString());

        tv_sign.setText(StringUtils.isEmpty(body.sign) ? "" : body.sign);

        tv_follow.setText("关注" + body.follow);

        tv_fans.setText("粉丝" + body.fans);

        String phone = loginModel.phone;
        tv_phone.setText("（" + phone.substring(0, 3)+ "****" + phone.substring(7, phone.length()) + "）");
    }

    private void initViewPager() {
        // 创建一个集合,装填Fragment
        ArrayList<Fragment> fragments = new ArrayList<>();
        // 装填
        myPublishFragment = new MyPublishFragment(this);
        myEvaluationsFragment = new MyEvaluationsFragment(this);
        myExposesFragment = new MyExposesFragment(this);
        myEvaluationFragment = new MyEvaluationFragment(this);
        myExposeFragment = new MyExposeFragment(this);
        fragments.add(myPublishFragment);
        fragments.add(myEvaluationsFragment);
        fragments.add(myExposesFragment);
        fragments.add(myEvaluationFragment);
        fragments.add(myExposeFragment);
        // 创建ViewPager适配器
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        myPagerAdapter.setFragments(fragments);
        // 给ViewPager设置适配器
        mViewPager.setAdapter(myPagerAdapter);
        // TabLayout 指示器 (记得自己手动创建4个Fragment,注意是 app包下的Fragment 还是 V4包下的 Fragment)
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.addTab(mTabLayout.newTab());
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setText("我的\n发布");
        mTabLayout.getTabAt(1).setText("我收到\n的评价");
        mTabLayout.getTabAt(2).setText("我被别\n人揭露");
        mTabLayout.getTabAt(3).setText("我给出\n的评价");
        mTabLayout.getTabAt(4).setText("我揭露\n的别人");
        mTabLayout.getTabAt(0).select();

        mViewPager.setOffscreenPageLimit(4);
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {

        private List<Fragment> mFragmentList;

        public void setFragments(ArrayList<Fragment> fragments) {
            mFragmentList = fragments;
        }

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = mFragmentList.get(position);

            return fragment;
        }

        @Override
        public int getCount() {

            return mFragmentList.size();
        }
    }

    private void setPullRefresher(){
        //设置 Header 为 MaterialHeader
        refreshLayout.setRefreshHeader(new MaterialHeader(this));
        //设置 Footer 为 经典样式
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
//                get_me();
                switch (mViewPager.getCurrentItem()){
                    case 0:
                        myPublishFragment.page = 1;
                        myPublishFragment.userPostList();
                        break;
                    case 1:
                        myEvaluationsFragment.page = 1;
                        myEvaluationsFragment.acceptEvaluationExpose();
                        break;
                    case 2:
                        myExposesFragment.page = 1;
                        myExposesFragment.acceptEvaluationExpose();
                        break;
                }

            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                switch (mViewPager.getCurrentItem()){
                    case 0:
                        if(myPublishFragment.body != null){
                            if(myPublishFragment.body.hasNextPages){
                                myPublishFragment.page++;
                                myPublishFragment.userPostList();
                            }else {
                                refreshLayout.finishLoadmore();
                                ToastUtils.showShort("没有更多");
                            }
                        }else {
                            refreshLayout.finishLoadmore();
                        }
                        break;
                    case 1:
                        if(myEvaluationsFragment.body != null){
                            if(myEvaluationsFragment.body.hasNextPages){
                                myEvaluationsFragment.page++;
                                myEvaluationsFragment.acceptEvaluationExpose();
                            }else {
                                refreshLayout.finishLoadmore();
                                ToastUtils.showShort("没有更多");
                            }
                        }else {
                            refreshLayout.finishLoadmore();
                        }
                        break;
                    case 2:
                        if(myExposesFragment.body != null){
                            if(myExposesFragment.body.hasNextPages){
                                myExposesFragment.page++;
                                myExposesFragment.acceptEvaluationExpose();
                            }else {
                                refreshLayout.finishLoadmore();
                                ToastUtils.showShort("没有更多");
                            }
                        }else {
                            refreshLayout.finishLoadmore();
                        }
                        break;
                    case 3:
                        if(myEvaluationFragment.body != null){
                            if(myEvaluationFragment.body.hasNextPages){
                                myEvaluationFragment.page++;
                                myEvaluationFragment.giveEvaluationExpose();
                            }else {
                                refreshLayout.finishLoadmore();
                                ToastUtils.showShort("没有更多");
                            }
                        }else {
                            refreshLayout.finishLoadmore();
                        }
                        break;
                    case 4:
                        if(myExposeFragment.body != null){
                            if(myExposeFragment.body.hasNextPages){
                                myExposeFragment.page++;
                                myExposeFragment.giveEvaluationExpose();
                            }else {
                                refreshLayout.finishLoadmore();
                                ToastUtils.showShort("没有更多");
                            }
                        }else {
                            refreshLayout.finishLoadmore();
                        }
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userInfo();
    }
}
