package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.BarUtils;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.PostDetail;
import com.yjyc.zhoubian.ui.fragment.PostDetailsFragment;
import com.yjyc.zhoubian.ui.fragment.PostPositionFragment;
import com.yjyc.zhoubian.ui.view.SwipeBackLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/10/12/012.
 */

public class PostDetailsActivity extends BaseActivity {
    @BindView(R.id.tvviewpager)
    public ViewPager mViewPager ;

    @BindView(R.id.rl_bg)
    public RelativeLayout rl_bg;

    private Context mContext;
    private int postId;
    private PostDetail postDetail;
    private PostPositionFragment postPositionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBackLayout layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
                R.layout.swipe_back_layout, null);
        layout.attachToActivity(this);
        setContentView(R.layout.activity_post_details);
        mContext = this;
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        postId = getIntent().getIntExtra("PostId", -1);
        if(postId == -1){
            finish();
            showToast("数据错误");
            return;
        }
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("帖子详情", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initViewPager();
    }

    private void initViewPager() {
        // 创建一个集合,装填Fragment
        ArrayList<Fragment> fragments = new ArrayList<>();
        // 装填
        fragments.add(new PostDetailsFragment(this, postId));
        postPositionFragment = new PostPositionFragment();
        fragments.add(postPositionFragment);

        // 创建ViewPager适配器
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        myPagerAdapter.setFragments(fragments);
        // 给ViewPager设置适配器
        mViewPager.setAdapter(myPagerAdapter);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position == 0){
                    tv_title.setText("帖子详情");
                }else{
                    tv_title.setText("本贴地址");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // TabLayout 指示器 (记得自己手动创建4个Fragment,注意是 app包下的Fragment 还是 V4包下的 Fragment)

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

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }
    }

    public void setPostDeatil(PostDetail postDetail){
        this.postDetail = postDetail;
        postPositionFragment.setPostDeatil(postDetail);
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.activity_scale_in_anim, R.anim.activity_move_out_anim);
    }
}