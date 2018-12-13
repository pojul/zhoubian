package com.yjyc.zhoubian.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.blankj.utilcode.util.StringUtils;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.MainActivitys;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.PostCate;
import com.yjyc.zhoubian.model.PostCateItem;
import com.yjyc.zhoubian.model.PostCateModel;
import com.yjyc.zhoubian.ui.activity.HobbySettingActivity;
import com.yjyc.zhoubian.ui.activity.LocationSettingActivity;
import com.yjyc.zhoubian.ui.activity.LoginActivity;
import com.yjyc.zhoubian.ui.activity.SearchActivity;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
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

public class MainsFragment extends Fragment {

    @BindView(R.id.tablayout)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    Unbinder unbinder;

    private List<MainFragment> mainFragments = new ArrayList<>();
    private MyAdapter adapter;
    private boolean hasInit = false;

    private List<PostCateItem> postCates = new ArrayList<>();

    public MainsFragment() {
        // Required empty public constructor
    }

    /*@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mains, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
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
    }

    private void initData() {
        if(hasInit){
            return;
        }
        hasInit = true;
        for (int i = 0; i < postCates.size(); i++) {
            MainFragment mainFragment = new MainFragment((MainActivitys) getActivity(), postCates.get(i).getId());
            mainFragments.add(mainFragment);
        }
        viewPager.setOffscreenPageLimit(1);
        adapter = new MyAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(2);
    }

    private void reqPostCates() {
        LoadingDialog.showLoading(getActivity());
        new OkhttpUtils().with()
                .get()
                .url(HttpUrl.POSTCATE)
                .execute(new AbsJsonCallBack<PostCateModel, PostCate>() {

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        com.yuqian.mncommonlibrary.utils.ToastUtils.show(errorMsg);
                        LoadingDialog.closeLoading();
                        initData();
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
                        postCates.addAll(tempDatas);
                        LoadingDialog.closeLoading();
                        initData();
                    }
                });
    }

    public void postDownturn(int currentPos, int downturnNum) {
        /**
         *postdownturn
         * */
        MainFragment mainFragment = mainFragments.get(viewPager.getCurrentItem());
        if(mainFragment != null){
            mainFragment.postDownturn(currentPos, downturnNum);
        }
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
            ProgressDialog.showDialog(getActivity());
        }
        new OkhttpUtils().with()
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

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mainFragments.get(position);
        }

        @Override
        public int getCount() {
            return mainFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            LogUtil.e(postCates.size() + "::getPageTitle::" + position);
            return postCates.get(position).getTitle();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
