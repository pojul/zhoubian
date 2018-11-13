package com.yjyc.zhoubian.ui.activity;

/**
 * <pre>
 *     author : maning
 *     e-mail : xxx@xx
 *     time   : 2018/01/05
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface BaseView {

    //显示加载框
    void showLoading();

    //隐藏加载框
    void hideLoading();

    //显示Toast
    void showToast(String msg);

}
