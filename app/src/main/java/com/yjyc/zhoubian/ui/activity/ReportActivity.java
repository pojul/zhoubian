package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.BarUtils;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.adapter.ReportCateAdapter;
import com.yjyc.zhoubian.model.EmptyEntity;
import com.yjyc.zhoubian.model.EmptyEntityModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.LoginModel;
import com.yjyc.zhoubian.model.ReportCate;
import com.yjyc.zhoubian.model.ReportCateModel;
import com.yjyc.zhoubian.ui.view.pickpicview.PickPicView;
import com.yjyc.zhoubian.utils.UploadFileUtil;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;
import com.yuqian.mncommonlibrary.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/10/12/012.
 */

public class ReportActivity extends BaseActivity {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.body)
    TextView body;
    @BindView(R.id.pick_pic_view)
    PickPicView pickPicView;
    @BindView(R.id.submit)
    TextView submit;

    private Context mContext;
    private ReportCateAdapter adapter;
    private List<ReportCate> cates = new ArrayList<>();
    private Login login;
    private int report_uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        mContext = this;
        ButterKnife.bind(this);
        login = Hawk.get("LoginModel");
        report_uid = getIntent().getIntExtra("report_uid", -1);
        if(report_uid < 0){
            showToast("数据错误");
            finish();
            return;
        }
        if(login == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }
        initView();
    }

    private void initView() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("举报", v -> onBackPressed());
        adapter = new ReportCateAdapter(this, cates);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//纵向线性布局
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        reqCates();
    }

    @OnClick({R.id.submit})
    void onclick(View view){
        switch (view.getId()){
            case R.id.submit:
                submit();
                break;
        }
    }

    private void submit() {
        if(adapter.getSelecterCate() < 0){
            showToast("请选择举报类型");
            return;
        }
        if(body.getText().toString().isEmpty()){
            showToast("请填写举报信息");
            return;
        }
        LoadingDialog.showLoading(this);
        List<String> pics = pickPicView.getPics();
        new UploadFileUtil().uploadFiles(pics, this, new UploadFileUtil.UploadFileCallBack() {
            @Override
            public void finish(List<String> strs) {
                LogUtil.e(new Gson().toJson(strs));
                report(strs);
            }

            @Override
            public void error(String msg) {
                LoadingDialog.closeLoading();
                showToast(msg);
            }
        });

    }

    public String getUploadPics(List<String> pics){
        StringBuffer picStrs = new StringBuffer();
        for (int i = 0; i < pics.size(); i++) {
            if(i != 0){
                picStrs.append(",");
            }
            picStrs.append(pics.get(i));
        }
        return picStrs.toString();
    }

    private void report(List<String> pics) {
        String picStrs = getUploadPics(pics);
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.USERREPORT)
                .addParams("uid", login.uid + "")
                .addParams("token", (login.token + ""))
                .addParams("report_uid", (report_uid + ""))
                .addParams("report_cate_id", (adapter.getSelecterCate() + ""))
                .addParams("pic", picStrs)
                .addParams("body", body.getText().toString())
                .execute(new AbsJsonCallBack<EmptyEntityModel, EmptyEntity>(){
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showToast(errorMsg);
                    }
                    @Override
                    public void onSuccess(EmptyEntity body) {
                        LoadingDialog.closeLoading();
                        showToast("举报成功");
                        finish();
                    }
                });
    }

    private void reqCates() {
        LoadingDialog.showLoading(this);
        new OkhttpUtils().with()
                .get()
                .url(HttpUrl.REPORTCATE)
                .execute(new AbsJsonCallBack<ReportCateModel, List<ReportCate>>(){
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        com.yuqian.mncommonlibrary.utils.ToastUtils.show(errorMsg);
                    }
                    @Override
                    public void onSuccess(List<ReportCate> body) {
                        LoadingDialog.closeLoading();
                        adapter.addData(body);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pickPicView.onActivityResult(requestCode, resultCode, data);
    }

}