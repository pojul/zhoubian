package com.yjyc.zhoubian.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.yuqian.mncommonlibrary.utils.ToastUtils;


/**
 * Created by Administrator on 2018/8/1/001.
 */

public abstract class BaseFragment extends Fragment {

    //错误消息弹窗

    abstract View initView();
    protected boolean isVisible;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {//用户可见时调用数据
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            isVisible=true;
            onVisible();
        }else {
            isVisible=false;
            onInVisible();
        }
    }

    protected View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView =initView();
        return mView;
    }

    public void showShortToats(String msg){
        ToastUtils.show(msg);
    }

    protected void onVisible() {        //可见时加载数据
        Loading();
    }
    protected abstract void Loading();
    protected void onInVisible() {  //不可见时不加载数据
    }

}