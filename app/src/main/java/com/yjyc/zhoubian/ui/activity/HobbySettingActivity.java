package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.model.HobbySettingContent;
import com.yjyc.zhoubian.model.HobbySettingContentModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.PostCate;
import com.yjyc.zhoubian.model.PostCateModel;
import com.yjyc.zhoubian.model.UserInfo;
import com.yjyc.zhoubian.model.UserInfoModel;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yjyc.zhoubian.ui.fragment.MeFragment;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import net.masonliu.multipletextview.library.MultipleTextViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/10/11/011.
 */

public class HobbySettingActivity extends BaseActivity {
    @BindView(R.id.main_rl)
    public MultipleTextViewGroup main_rl;

    @BindView(R.id.main_rl2)
    public MultipleTextViewGroup main_rl2;

    @BindView(R.id.et1)
    public EditText et1;

    @BindView(R.id.et2)
    public EditText et2;

    private Context mContext;
    ArrayList<PostCate.Data> pcs1 ;
    ArrayList<PostCate.Data> pcs2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobby_setting);
        mContext = this;
        ButterKnife.bind(this);
        initView();

        hobbySettingContent();
    }

    private void hobbySettingContent() {
        if(!ProgressDialog.isShowing()){
            ProgressDialog.showDialog(mContext);
        }
        Login loginModel = Hawk.get("LoginModel");
        new OkhttpUtils().with()
                .post()
                .addParams("uid", loginModel.uid + "")
                .addParams("token", loginModel.token)
                .url(HttpUrl.HOBBYSETTINGCONTENT)
                .execute(new AbsJsonCallBack<HobbySettingContentModel, HobbySettingContent>() {


                    @Override
                    public void onSuccess(HobbySettingContent body) {
                        if(!StringUtils.isEmpty(body.hobby_post_cate_ids)){
                            String[] strs = body.hobby_post_cate_ids.split(",");
                            ArrayList<String> ids = new ArrayList<>();
                            for (String str : strs){
                                ids.add(str);
                            }
                            for (int i = 0; i < pcs1.size(); i++){
                                    if(ids.contains(pcs1.get(i).getId() + "")){
                                        pcs1.get(i).setIsChecked(1);
                                        main_rl.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                                        ((TextView)main_rl.getChildAt(i)).setTextColor(getResources().getColor(R.color.white));
                                    }
                            }
                        }

                        if(!StringUtils.isEmpty(body.shield_post_cate_ids)){
                            String[] strs = body.shield_post_cate_ids.split(",");
                            ArrayList<String> ids = new ArrayList<>();
                            for (String str : strs){
                                ids.add(str);
                            }
                            for (int i = 0; i < pcs2.size(); i++){
                                if(ids.contains(pcs2.get(i).getId() + "")){
                                    pcs2.get(i).setIsChecked(1);
                                    main_rl2.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                                    ((TextView)main_rl2.getChildAt(i)).setTextColor(getResources().getColor(R.color.white));
                                }
                            }
                        }

                        if(!StringUtils.isEmpty(body.hobby_post_key_words)){
                            String[] strs = body.hobby_post_key_words.split(",");
                            StringBuilder sb = new StringBuilder();
                            for (String str : strs){
                                sb.append(str).append(" ");
                            }
                            et1.setText(sb.toString());
                        }

                        if(!StringUtils.isEmpty(body.shield_post_key_words)){
                            String[] strs = body.shield_post_key_words.split(",");
                            StringBuilder sb = new StringBuilder();
                            for (String str : strs){
                                sb.append(str).append(" ");
                            }
                            et2.setText(sb.toString());
                        }
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        showToast(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                    }

                    @Override
                    public void onFinish() {
                        ProgressDialog.dismiss();
                    }
                });
    }

    private void initView() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("爱好设置", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pcs1 = Hawk.get("pcs");
        pcs2 = Hawk.get("pcs");

        List<String> dataList = new ArrayList<String>();
        List<String> dataList2 = new ArrayList<String>();

        for (PostCate.Data pc : pcs1){
            dataList.add(pc.getTitle());
        }

        for (PostCate.Data pc : pcs2){
            dataList2.add(pc.getTitle());
        }

        main_rl.setTextViews(dataList);
        main_rl2.setTextViews(dataList2);

        main_rl.setOnMultipleTVItemClickListener(new MultipleTextViewGroup.OnMultipleTVItemClickListener() {
            @Override
            public void onMultipleTVItemClick(View view, int i) {
                PostCate.Data pc = pcs1.get(i);
                if(pc.getIsChecked() == 1){
                    main_rl.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
                    ((TextView)main_rl.getChildAt(i)).setTextColor(getResources().getColor(R.color.color080808));
                    pc.setIsChecked(2);
                }else {
                    main_rl.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                    ((TextView)main_rl.getChildAt(i)).setTextColor(getResources().getColor(R.color.white));
                    pc.setIsChecked(1);
                }

                if(pcs2.get(i).getIsChecked() == 1){
                    pcs2.get(i).setIsChecked(2);
                    main_rl2.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
                    ((TextView)main_rl2.getChildAt(i)).setTextColor(getResources().getColor(R.color.color080808));
                }
            }
        });

        main_rl2.setOnMultipleTVItemClickListener(new MultipleTextViewGroup.OnMultipleTVItemClickListener() {
            @Override
            public void onMultipleTVItemClick(View view, int i) {
                PostCate.Data pc = pcs2.get(i);
                if(pc.getIsChecked() == 1){
                    main_rl2.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
                    ((TextView)main_rl2.getChildAt(i)).setTextColor(getResources().getColor(R.color.color080808));
                    pc.setIsChecked(2);
                }else {
                    main_rl2.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                    ((TextView)main_rl2.getChildAt(i)).setTextColor(getResources().getColor(R.color.white));
                    pc.setIsChecked(1);
                }

                if(pcs1.get(i).getIsChecked() == 1){
                    pcs1.get(i).setIsChecked(2);
                    main_rl.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
                    ((TextView)main_rl.getChildAt(i)).setTextColor(getResources().getColor(R.color.color080808));
                }
            }
        });
    }

    @OnClick(R.id.tv_next)
    public void tv_next(){
        boolean tag = false;
        StringBuilder sb1 = new StringBuilder();
        for (PostCate.Data pc : pcs1){
            if(pc.getIsChecked() == 1){
                tag = true;
                sb1.append(pc.getId()).append(",");
            }
        }

        StringBuilder sb2 = new StringBuilder();
        for (PostCate.Data pc : pcs2){
            if(pc.getIsChecked() == 1){
                tag = true;
                sb2.append(pc.getId()).append(",");
            }
        }
        /*if(!tag){
            ToastUtils.showShort("请选择爱好帖子的类别");
            return;
        }*/
        String hobby_post_cate_ids = "";
        if(!StringUtils.isEmpty(sb1.toString())){
            hobby_post_cate_ids = sb1.toString().substring(0, sb1.toString().length() - 1);
        }

        String shield_post_cate_ids = "";
        if(!StringUtils.isEmpty(sb2.toString())){
            shield_post_cate_ids = sb2.toString().substring(0, sb2.toString().length() - 1);
        }

        String hobby_post_key_words = et1.getText().toString();
       /* if(!StringUtils.isEmpty(et1.getText().toString().trim())){
            StringBuilder sb = new StringBuilder();
            String[] strs = et1.getText().toString().split(" ");
            for (String str : strs){
                sb.append(str).append(",");
            }

            hobby_post_key_words = sb.toString().substring(0, sb.toString().length() - 1);
        }*/

        String shield_post_key_words = et2.getText().toString();
        /*if(!StringUtils.isEmpty(et2.getText().toString().trim())){
            StringBuilder sb = new StringBuilder();
            String[] strs = et2.getText().toString().split(" ");
            for (String str : strs){
                sb.append(str).append(",");
            }

            shield_post_key_words = sb.toString().substring(0, sb.toString().length() - 1);
        }*/
        Login loginModel = Hawk.get("LoginModel");
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.HOBBYSETTING)
                .addParams("uid", loginModel.uid + "")
                .addParams("token", loginModel.token)
                .addParams("hobby_post_cate_ids", hobby_post_cate_ids)
                .addParams("shield_post_cate_ids", shield_post_cate_ids)
                .addParams("hobby_post_key_words", hobby_post_key_words)
                .addParams("shield_post_key_words", shield_post_key_words)
                .execute(new AbsJsonCallBack<UserInfoModel, UserInfo>() {


                    @Override
                    public void onSuccess(UserInfo body) {
                        showToast("设置成功");
                        finish();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        showToast(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                    }

                    @Override
                    public void onFinish() {
                        ProgressDialog.dismiss();
                    }
                });
    }
}