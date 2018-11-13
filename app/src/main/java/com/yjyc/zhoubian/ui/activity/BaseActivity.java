package com.yjyc.zhoubian.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;

import com.blankj.utilcode.util.ToastUtils;
import com.yjyc.zhoubian.R;

/**
 * <pre>
 *     author : maning
 *     e-mail : xxx@xx
 *     time   : 2018/01/05
 *     desc   : BaseActivity
 *     version: 1.0
 * </pre>
 */
public class BaseActivity extends BaseMvpActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public void showToast(String msg) {
        ToastUtils.setGravity(Gravity.CENTER, 0, 0);
        ToastUtils.showShort(msg);
    }

    public void startActivityAni(Class cls, Bundle bundle){
        Intent intent = new Intent(this, cls);
        if(bundle != null){
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.activity_move_enter_anim, R.anim.activity_scale_out_anim);
    }

}
