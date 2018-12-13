package com.yjyc.zhoubian.ui.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.baidu.location.BDLocation;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.orhanobut.hawk.Hawk;
import com.yanzhenjie.permission.AndPermission;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.event.BaiduEvent;
import com.yjyc.zhoubian.model.EmptyEntity;
import com.yjyc.zhoubian.model.EmptyEntityModel;
import com.yjyc.zhoubian.model.GetPostMsg;
import com.yjyc.zhoubian.model.GetPostMsgModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.LoginCode;
import com.yjyc.zhoubian.model.LoginCodeModel;
import com.yjyc.zhoubian.model.LoginModel;
import com.yjyc.zhoubian.model.PostCate;
import com.yjyc.zhoubian.model.PostCateModel;
import com.yjyc.zhoubian.model.PostDraftDetail;
import com.yjyc.zhoubian.model.PostDraftDetailModel;
import com.yjyc.zhoubian.model.RedEnvelopeDistance;
import com.yjyc.zhoubian.model.RedEnvelopeDistanceModel;
import com.yjyc.zhoubian.model.RedEnvelopeSetting;
import com.yjyc.zhoubian.model.RedEnvelopeSettingModel;
import com.yjyc.zhoubian.model.UserGroupModel;
import com.yjyc.zhoubian.model.UserGroups;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yjyc.zhoubian.ui.view.pickpicview.PickPicView;
import com.yjyc.zhoubian.utils.ArrayUtil;
import com.yjyc.zhoubian.utils.PermissionUtils;
import com.yjyc.zhoubian.utils.UploadFileUtil;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;
import com.yuqian.mncommonlibrary.utils.LogUtil;

import net.masonliu.multipletextview.library.MultipleTextViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class EditPostDraftActivity extends BaseActivity {

    @BindView(R.id.main_rl)
    public MultipleTextViewGroup main_rl;
    @BindView(R.id.main_rl2)
    public MultipleTextViewGroup main_rl2;
    @BindView(R.id.main_rl3)
    public MultipleTextViewGroup main_rl3;
    @BindView(R.id.main_rl4)
    public MultipleTextViewGroup main_rl4;
    @BindView(R.id.et_title)
    EditText et_title;
    @BindView(R.id.et_body)
    EditText et_body;
    @BindView(R.id.et_custom_post_cate)
    EditText et_custom_post_cate;
    @BindView(R.id.et_user_name)
    EditText et_user_name;
    @BindView(R.id.et_phone)
    EditText et_phone;
    @BindView(R.id.et_code)
    EditText et_code;
    @BindView(R.id.et_price)
    EditText et_price;
    @BindView(R.id.et_key_word)
    EditText et_key_word;
    @BindView(R.id.et_red_package_password)
    EditText et_red_package_password;
    @BindView(R.id.tv1)
    TextView tv1;
    @BindView(R.id.tv2)
    TextView tv2;
    @BindView(R.id.tv_price1)
    TextView tv_price1;
    @BindView(R.id.tv_price2)
    TextView tv_price2;
    @BindView(R.id.tv_price3)
    TextView tv_price3;
    @BindView(R.id.tv_price4)
    TextView tv_price4;
    @BindView(R.id.tv_price5)
    TextView tv_price5;
    @BindView(R.id.tv_price6)
    TextView tv_price6;
    @BindView(R.id.tv_price7)
    TextView tv_price7;
    @BindView(R.id.tv_price8)
    TextView tv_price8;
    @BindView(R.id.tv_price9)
    TextView tv_price9;
    @BindView(R.id.tv_price10)
    TextView tv_price10;
    @BindView(R.id.tv_addtrs)
    TextView tv_addtrs;
    @BindView(R.id.tv_code)
    TextView tv_code;
    @BindView(R.id.tv_red1)
    TextView tv_red1;
    @BindView(R.id.tv_red2)
    TextView tv_red2;
    @BindView(R.id.tv_red_package_money)
    TextView tv_red_package_money;
    @BindView(R.id.tv_single_red_money)
    TextView tv_single_red_money;
    @BindView(R.id.ll_phone)
    LinearLayout ll_phone;
    @BindView(R.id.ll_red_price)
    LinearLayout ll_red_price;
    @BindView(R.id.et_red_price)
    TextView et_red_price;
    @BindView(R.id.pick_pic_view)
    PickPicView pickPicView;
    @BindView(R.id.root_sv)
    ScrollView root_sv;

    Unbinder unbinder;
    Login loginModel;
    private Gson gson;
    private GsonBuilder builder;
    private ArrayList<UserGroups.UserGroup> userGroups;
    private int post_cate_id = -1;
    private int user_group_id = -1;
    private int phone_from = 1;
    private List<String> upLoadPics = new ArrayList<>();
    private String code;
    private GetPostMsg getPostMsg;
    private int red_package_rule = 1;
    private int red_package_money = -1;
    private int rob_red_package_range = -1;
    private int publishTag;
    private String price_unit = "元";

    private int draftId;
    private PostDraftDetail.PostDraft postDraft;
    private List<TextView> tv_prices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post_draft);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        draftId = getIntent().getIntExtra("draftId", -1);
        loginModel = Hawk.get("LoginModel");
        if(draftId < 0 || loginModel == null){
            finish();
        }
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        reqData();
    }

    ArrayList<PostCate.Data> pcs;
    ArrayList<RedEnvelopeSetting> redEnvelopeSettings;
    ArrayList<RedEnvelopeDistance> redEnvelopeDistances;

    public void reqData(){
        getPostMsg();
    }

    private void initViews() {
        root_sv.setVisibility(View.VISIBLE);
        tv_prices.add(tv_price1);tv_prices.add(tv_price2);tv_prices.add(tv_price3);tv_prices.add(tv_price3);
        tv_prices.add(tv_price5);tv_prices.add(tv_price6);tv_prices.add(tv_price7);tv_prices.add(tv_price8);
        tv_prices.add(tv_price9);tv_prices.add(tv_price10);
        if (SPUtils.getInstance().contains("user_name")) {
            user_name = SPUtils.getInstance().getString("user_name");
            et_user_name.setText(user_name);
        }

        BDLocation location = BaseApplication.getIntstance().getLocation();
        if (location != null && !StringUtils.isEmpty(location.getAddrStr())) {
            tv_addtrs.setText("已获取位置：" + location.getAddrStr());
        } else {
            tv_addtrs.setText("获取位置失败，请检查定位权限是否开启");
        }
        builder = new GsonBuilder();
        gson = builder.create();
        loginModel = Hawk.get("LoginModel");

        if (Hawk.contains("pcs")) {
            pcs = Hawk.get("pcs");
            List<String> dataList = new ArrayList<String>();
            for (PostCate.Data pc : pcs) {
                dataList.add(pc.getTitle());
            }
            main_rl.setTextViews(dataList);
            main_rl.requestLayout();
            main_rl.setOnMultipleTVItemClickListener((view, i) -> {
                PostCate.Data pc = pcs.get(i);
                if (pc.getIsChecked() == 1) {
                } else {
                    main_rl.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                    ((TextView) main_rl.getChildAt(i)).setTextColor(getResources().getColor(R.color.white));
                    pcs.get(i).setIsChecked(1);
                    for (int j = 0; j < pcs.size(); j++) {
                        if (j == i) {
                            continue;
                        }
                        main_rl.getChildAt(j).setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
                        ((TextView) main_rl.getChildAt(j)).setTextColor(getResources().getColor(R.color.color080808));
                        pcs.get(j).setIsChecked(2);
                    }
                    post_cate_id = pcs.get(i).getId();
                }
            });
        }

        if (Hawk.contains("redEnvelopeSettings")) {
            redEnvelopeSettings = Hawk.get("redEnvelopeSettings");
            List<String> dataList = new ArrayList<String>();
            for (RedEnvelopeSetting pc : redEnvelopeSettings) {
                dataList.add(pc.title);
            }
            main_rl3.setTextViews(dataList);
            main_rl3.requestLayout();
            main_rl3.setOnMultipleTVItemClickListener((view, i) -> {
                RedEnvelopeSetting pc = redEnvelopeSettings.get(i);
                if (pc.isChecked == 1) {
                } else {
                    main_rl3.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                    ((TextView) main_rl3.getChildAt(i)).setTextColor(getResources().getColor(R.color.white));
                    redEnvelopeSettings.get(i).isChecked = 1;
                    for (int j = 0; j < redEnvelopeSettings.size(); j++) {
                        if (j == i) {
                            continue;
                        }
                        main_rl3.getChildAt(j).setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
                        ((TextView) main_rl3.getChildAt(j)).setTextColor(getResources().getColor(R.color.color080808));
                        redEnvelopeSettings.get(j).isChecked = 2;
                    }
                    if (!StringUtils.isEmpty(pc.title)) {
                        red_package_money = Integer.parseInt(pc.title);
                        tv_red_package_money.setText(red_package_money + "元");
                    }

                }
            });
        }

        if (Hawk.contains("redEnvelopeDistances")) {
            redEnvelopeDistances = Hawk.get("redEnvelopeDistances");
            List<String> dataList = new ArrayList<String>();
            for (RedEnvelopeDistance pc : redEnvelopeDistances) {
                dataList.add(pc.title + (StringUtils.isEmpty(pc.unit) ? "" : pc.unit));
            }
            main_rl4.setTextViews(dataList);
            main_rl4.requestLayout();
            main_rl4.setOnMultipleTVItemClickListener((view, i) -> {
                RedEnvelopeDistance pc = redEnvelopeDistances.get(i);
                if (pc.isChecked == 1) {
                } else {
                    main_rl4.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                    ((TextView) main_rl4.getChildAt(i)).setTextColor(getResources().getColor(R.color.white));
                    redEnvelopeDistances.get(i).isChecked = 1;
                    for (int j = 0; j < redEnvelopeDistances.size(); j++) {
                        if (j == i) {
                            continue;
                        }
                        main_rl4.getChildAt(j).setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
                        ((TextView) main_rl4.getChildAt(j)).setTextColor(getResources().getColor(R.color.color080808));
                        redEnvelopeDistances.get(j).isChecked = 2;
                    }
                    rob_red_package_range = pc.id;
                }
            });
        }

        if (Hawk.contains("userGroups")) {
            userGroups = Hawk.get("userGroups");
            List<String> dataList = new ArrayList<String>();
            for (UserGroups.UserGroup pc : userGroups) {
                dataList.add(pc.title);
            }
            main_rl2.setTextViews(dataList);
            main_rl2.requestLayout();
            main_rl2.setOnMultipleTVItemClickListener((view, i) -> {
                UserGroups.UserGroup pc = userGroups.get(i);
                if (pc.isChecked == 1) {
                } else {
                    main_rl2.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                    ((TextView) main_rl2.getChildAt(i)).setTextColor(getResources().getColor(R.color.white));
                    userGroups.get(i).isChecked = 1;
                    for (int j = 0; j < userGroups.size(); j++) {
                        if (j == i) {
                            continue;
                        }
                        main_rl2.getChildAt(j).setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
                        ((TextView) main_rl2.getChildAt(j)).setTextColor(getResources().getColor(R.color.color080808));
                        userGroups.get(j).isChecked = 2;
                    }
                    user_group_id = userGroups.get(i).id;
                }
            });
        }

        reqDraftDetail();

    }

    public void reqDraftDetail() {
        Login login = Hawk.get("LoginModel");
        if(login == null || draftId == -1){
            ProgressDialog.dismiss();
            return;
        }
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.POSTDRAFTDETAIL)
                .addParams("uid", login.uid + "")
                .addParams("token", login.token + "")
                .addParams("id", draftId + "")
                .execute(new AbsJsonCallBack<PostDraftDetailModel, PostDraftDetail>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        ProgressDialog.dismiss();
                        com.yuqian.mncommonlibrary.utils.ToastUtils.show("errorMsg");
                    }

                    @Override
                    public void onSuccess(PostDraftDetail body) {
                        ProgressDialog.dismiss();
                        if(body == null){
                            com.yuqian.mncommonlibrary.utils.ToastUtils.show("数据错误");
                            finish();
                            return;
                        }
                        postDraft = body.post_draft;
                        setData();
                    }
                });
    }

    private void setData() {
        if(postDraft == null){
            finish();
            return;
        }
        postDraft.pic = ArrayUtil.filterLocalPic(postDraft.pic);
        if(postDraft.pic != null){
            pickPicView.setData(postDraft.pic);
        }
        post_cate_id = postDraft.post_cate_id;
        for (int i = 0; i < main_rl.getChildCount(); i++) {
            PostCate.Data pc = pcs.get(i);
            if(pc.getId() == post_cate_id){
                main_rl.getChildAt(i).performClick();
            }
        }
        if(postDraft.custom_post_cate != null){
            et_custom_post_cate.setText(postDraft.custom_post_cate);
        }
        title = postDraft.title;
        et_title.setText(title);
        body = postDraft.body;
        et_body.setText(body);
        user_group_id = postDraft.user_group_id;
        for (int i = 0; i < main_rl2.getChildCount(); i++) {
            UserGroups.UserGroup userGroup = userGroups.get(i);
            if(userGroup.id == user_group_id){
                main_rl2.getChildAt(i).performClick();
            }
        }
        user_name = postDraft.user_name;
        et_user_name.setText(user_name);
        if(postDraft.phone_from > 0){
            phone_from = postDraft.phone_from;
        }
        if(phone_from == 2){
            tv2.performClick();
        }
        BDLocation location = new BDLocation();
        location.setLatitude(postDraft.lat);
        location.setLongitude(postDraft.lon);
        BaseApplication.getIntstance().setLocation(location);
        BaseApplication.getIntstance().setProvince(postDraft.province);
        BaseApplication.getIntstance().setCity(postDraft.city);
        tv_addtrs.setText("已获取位置：" + postDraft.province + postDraft.city);
        price = postDraft.price + "";
        if(postDraft.price > 0){
            et_price.setText(price);
        }
        price_unit = postDraft.price_unit;
        if(price_unit != null){
            for (int i = 0; i < tv_prices.size(); i++) {
                TextView tv_price = tv_prices.get(i);
                if(price_unit.equals(tv_price.getText().toString())){
                    tv_price.performClick();
                }
            }
        }
        if(postDraft.key_word != null){
            et_key_word.setText(postDraft.key_word);
        }
        red_package_money = ((int)postDraft.red_package_money);
        for (int i = 0; i < main_rl3.getChildCount(); i++) {
            RedEnvelopeSetting redEnvelopeSetting = redEnvelopeSettings.get(i);
            if(("" + red_package_money).equals(redEnvelopeSetting.title)){
                main_rl3.getChildAt(i).performClick();
            }
        }
        if(red_package_money > 0){
            tv_red_package_money.setText((red_package_money + "元"));
        }
        if(postDraft.red_package_password != null){
            et_red_package_password.setText(postDraft.red_package_password);
        }
        if(postDraft.rob_red_package_range != null && !postDraft.rob_red_package_range.isEmpty()){
            int tempRange = Integer.parseInt(postDraft.rob_red_package_range);
            if(tempRange > 0){
                rob_red_package_range = tempRange;
                for (int i = 0; i < main_rl4.getChildCount(); i++) {
                    RedEnvelopeDistance redEnvelopeDistance = redEnvelopeDistances.get(i);
                    if(redEnvelopeDistance.id == rob_red_package_range){
                        main_rl4.getChildAt(i).performClick();
                    }
                }
            }
        }
    }

    private void getPostMsg() {
        if (!ProgressDialog.isShowing()) {
            ProgressDialog.showDialog(this);
        }
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.GETPOSTMSG)
                .addParams("uid", loginModel.uid + "")
                .addParams("token", loginModel.token)
                .execute(new AbsJsonCallBack<GetPostMsgModel, GetPostMsg>() {
                    @Override
                    public void onSuccess(GetPostMsg body) {
                        getPostMsg = body;
                        tv_single_red_money.setText("平均每个红包" + body.single_red_money + "元左右");
                        postCate();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        postCate();
                    }
                });
    }

    @OnClick(R.id.save)
    public void save() {
        savePost();
    }

    @OnClick(R.id.tv_my_publish)
    public void tv_my_publish() {
        Intent intent = new Intent(this, MyPublishActivity.class);
        intent.putExtra("uid", loginModel.uid + "");
        startActivity(intent);
    }

    @OnClick(R.id.tv_price1)
    public void tv_price1() {
        setTvBackground2(R.id.tv_price1);
        price_unit = "元";
    }

    @OnClick(R.id.tv_price2)
    public void tv_price2() {
        setTvBackground2(R.id.tv_price2);
        price_unit = "元/小时";
    }

    @OnClick(R.id.tv_price3)
    public void tv_price3() {
        setTvBackground2(R.id.tv_price3);
        price_unit = "元/天";
    }

    @OnClick(R.id.tv_price4)
    public void tv_price4() {
        setTvBackground2(R.id.tv_price4);
        price_unit = "元/月";
    }

    @OnClick(R.id.tv_price5)
    public void tv_price5() {
        setTvBackground2(R.id.tv_price5);
        price_unit = "元/次";
    }

    @OnClick(R.id.tv_price6)
    public void tv_price6() {
        setTvBackground2(R.id.tv_price6);
        price_unit = "元/M²";
    }

    @OnClick(R.id.tv_price7)
    public void tv_price7() {
        setTvBackground2(R.id.tv_price7);
        price_unit = "元/斤";
    }

    @OnClick(R.id.tv_price8)
    public void tv_price8() {
        setTvBackground2(R.id.tv_price8);
        price_unit = "元/年";
    }

    @OnClick(R.id.tv_price9)
    public void tv_price9() {
        setTvBackground2(R.id.tv_price9);
        price_unit = "元/万";
    }

    @OnClick(R.id.tv_price10)
    public void tv_price10() {
        setTvBackground2(R.id.tv_price10);
        price_unit = "折";
    }

    @OnClick(R.id.tv_red1)
    public void tv_red1() {
        red_package_rule = 1;
        setRedTvBackground(R.id.tv_red1);
        main_rl3.setVisibility(View.VISIBLE);
        ll_red_price.setVisibility(View.GONE);
    }

    @OnClick(R.id.tv_red2)
    public void tv_red2() {
        red_package_rule = 2;
        setRedTvBackground(R.id.tv_red2);
        main_rl3.setVisibility(View.GONE);
        ll_red_price.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.tv1)
    public void tv1(){
        phone_from = 1;
        setTvBackground(R.id.tv1);
    }

    @OnClick(R.id.tv2)
    public void tv2(){
        phone_from = 2;
        setTvBackground(R.id.tv2);
    }

    @OnClick(R.id.tv_code)
    public void tv_code() {
        if (et_phone.getText().toString().length() != 11) {
            showToast("请输入11位手机号");
            return;
        }

        postCode();
    }

    private void postCode() {
        if (!ProgressDialog.isShowing()) {
            ProgressDialog.showDialog(this);
        }
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.POSTCODE)
                .addParams("phone", et_phone.getText().toString())
                .addParams("uid", loginModel.uid + "")
                .addParams("token", loginModel.token)
                .execute(new AbsJsonCallBack<LoginCodeModel, LoginCode>() {

                    @Override
                    public void onSuccess(LoginCode body) {
                        CountDownTimerUtils countDownTimerUtils = new CountDownTimerUtils(60000, 1000);
                        countDownTimerUtils.start();
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

    class CountDownTimerUtils extends CountDownTimer {

        public CountDownTimerUtils(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            tv_code.setClickable(false); //设置不可点击
            tv_code.setText(millisUntilFinished / 1000 + "秒");  //设置倒计时时间
        }

        @Override
        public void onFinish() {
            tv_code.setText("重新获取");
            tv_code.setClickable(true);//重新获得点击
        }
    }

    @OnClick(R.id.tv_baidu)
    public void tv_baidu() {
        PermissionUtils.checkLocationPermission(this, new PermissionUtils.PermissionCallBack() {
            @Override
            public void onGranted() {
                startActivity(new Intent(EditPostDraftActivity.this, BaiDuMapActivity.class));
            }

            @Override
            public void onDenied() {
                new MaterialDialog.Builder(EditPostDraftActivity.this)
                        .title("提示")
                        .content("当前权限被拒绝导致功能不能正常使用，请到设置界面修改定位和存储权限允许访问")
                        .positiveText("去设置")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                AndPermission.permissionSetting(EditPostDraftActivity.this)
                                        .execute();
                            }
                        })
                        .show();
            }
        });
    }

    String title;
    String body;
    String user_name;
    String phone;
    String price;

    @OnClick(R.id.tv_publish3)
    public void tv_publish3() {
        publishTag = 3;
        if (!isFinish()) {
            return;
        }
        /*price = et_price.getText().toString();
        if(StringUtils.isEmpty(price.trim())){
            ToastUtils.showShort("请填写价格");
            return;
        }*/

        /*String keyword = et_key_word.getText().toString();
        if(StringUtils.isEmpty(keyword.trim())){
            ToastUtils.showShort("请输入本帖关键词");
            return;
        }

        if(red_package_rule == 1){
            if(red_package_money == -1){
                ToastUtils.showShort("请选择红包金额");
                return;
            }
        }else if(red_package_rule == 2){
            String str = et_red_price.getText().toString().trim();
            if(StringUtils.isEmpty(str)){
                ToastUtils.showShort("请输入自定义价格");
                return;
            }else {
                red_package_money = Integer.parseInt(str);
            }
        }

        if(StringUtils.isEmpty(getPostMsg.single_red_money) || red_package_money > Double.parseDouble(getPostMsg.user_balance)){
            ToastUtils.showShort("您的余额不足");
            return;
        }

        if(rob_red_package_range == -1){
            ToastUtils.showShort("请选择抢红包权限");
            return;
        }*/
        uploadPics();
    }

    @OnClick(R.id.tv_publish2)
    public void tv_publish2() {
        publishTag = 2;
        if (!isFinish()) {
            return;
        }
        /*if(StringUtils.isEmpty(price.trim())){
            ToastUtils.showShort("请填写价格");
            return;
        }

        String keyword = et_key_word.getText().toString();
        if(StringUtils.isEmpty(keyword.trim())){
            ToastUtils.showShort("请输入本帖关键词");
            return;
        }*/
        uploadPics();

    }

    @OnClick(R.id.tv_publish1)
    public void tv_publish1() {
        publishTag = 1;
        if (!isFinish()) {
            return;
        }
        uploadPics();
    }

    private void uploadPics() {
        ProgressDialog.showDialog(this);
        List<String> pics = pickPicView.getPics();
        upLoadPics = new ArrayList<>();
        new UploadFileUtil().uploadFiles(pics, this, new UploadFileUtil.UploadFileCallBack() {
            @Override
            public void finish(List<String> strs) {
                LogUtil.e(new Gson().toJson(strs));
                upLoadPics = strs;
                userPost();
            }

            @Override
            public void error(String msg) {
                ProgressDialog.dismiss();
                com.yuqian.mncommonlibrary.utils.ToastUtils.show(msg);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        pickPicView.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isFinish() {
        if (post_cate_id == -1) {
            ToastUtils.showShort("请选择帖子类别");
            return false;
        }

        title = et_title.getText().toString();
        if (StringUtils.isEmpty(title.trim())) {
            ToastUtils.showShort("请输入帖子标题");
            return false;
        }

        if (title.length() < 4) {
            ToastUtils.showShort("帖子标题长度必须4-30字");
            return false;
        }

        body = et_body.getText().toString();
        if (StringUtils.isEmpty(body.trim())) {
            ToastUtils.showShort("请输入帖子详情");
            return false;
        }

        if (body.length() < 20) {
            ToastUtils.showShort("帖子详情长度必须20-1500字");
            return false;
        }

        if (BaseApplication.getIntstance().getLocation() == null) {
            ToastUtils.showShort("请选择本帖位置");
            return false;
        }

        if (user_group_id == -1) {
            ToastUtils.showShort("请选择发布者身份");
            return false;
        }
        SPUtils.getInstance().put("user_group_id", user_group_id);

        user_name = et_user_name.getText().toString();
        if (StringUtils.isEmpty(user_name.trim())) {
            ToastUtils.showShort("请输入发布者称呼");
            return false;
        }
        SPUtils.getInstance().put("user_name", user_name);

        if (phone_from == 1) {
            phone = loginModel.phone;
        }

        if (phone_from == 2) {
            phone = et_phone.getText().toString();
            if (StringUtils.isEmpty(phone.trim())) {
                ToastUtils.showShort("请输入手机号");
                return false;
            }

            code = et_code.getText().toString();
            if (StringUtils.isEmpty(code.trim())) {
                ToastUtils.showShort("请输入验证码");
                return false;
            }
        }

        price = et_price.getText().toString();
        try {
            red_package_money = Integer.parseInt(et_red_price.getText().toString().trim());
        } catch (Exception e) {
        }
        if (red_package_money > 0) {
            if (StringUtils.isEmpty(getPostMsg.single_red_money) || red_package_money > Double.parseDouble(getPostMsg.user_balance)) {
                ToastUtils.showShort("您的余额不足");
                return false;
            }

            if (rob_red_package_range == -1) {
                ToastUtils.showShort("请选择抢红包权限");
                return false;
            }
        }
        if( BaseApplication.getIntstance().getLocation() == null){
            ToastUtils.showShort("请选择位置");
            return false;
        }
        if(BaseApplication.getIntstance().getProvince() == null){
            ToastUtils.showShort("请选择位置");
            return false;
        }
        if(BaseApplication.getIntstance().getCity() == null){
            ToastUtils.showShort("请选择位置");
            return false;
        }
        return true;
    }

    private void userPost() {
        BDLocation location = BaseApplication.getIntstance().getLocation();
        Map<String, String> map = new LinkedHashMap<>();
        String pics = ArrayUtil.toCommaSplitStr(upLoadPics);
        map.put("pic", pics);
        map.put("uid", loginModel.uid + "");
        map.put("token", loginModel.token);
        map.put("post_draft_id", postDraft.id + "");
        map.put("post_cate_id", post_cate_id + "");
        map.put("custom_post_cate", et_custom_post_cate.getText().toString().trim());
        map.put("title", title);
        map.put("body", body);
        map.put("user_group_id", user_group_id + "");
        map.put("user_name", user_name);
        map.put("phone_from", phone_from + "");
        if (phone_from == 2) {
            map.put("phone", phone);
            map.put("code", code);
        }
        map.put("lon", location.getLongitude() + "");
        map.put("lat", location.getLatitude() + "");
        map.put("province", StringUtils.isEmpty(BaseApplication.getIntstance().getProvince()) ? "" : BaseApplication.getIntstance().getProvince());
        map.put("city", StringUtils.isEmpty(BaseApplication.getIntstance().getCity()) ? "" : BaseApplication.getIntstance().getCity());
        if (price != null && !price.isEmpty()) {
            map.put("price", price);
            map.put("price_unit", price_unit);
        }
        map.put("key_word", et_key_word.getText().toString().trim());
        if (red_package_money > 0) {
            map.put("red_package_rule", red_package_rule + "");
            map.put("red_package_money", red_package_money + "");
            map.put("single_red_money", getPostMsg.single_red_money + "");
            map.put("red_package_password", et_red_package_password.getText().toString().trim());
            map.put("rob_red_package_range", rob_red_package_range + "");
        }
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.USERPOST)
                .params(map)
                .execute(new AbsJsonCallBack<LoginModel, Login>() {

                    @Override
                    public void onSuccess(Login body) {
                        startActivity(new Intent(EditPostDraftActivity.this, ReleaseSuccessActivity.class));
                        finish();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        com.blankj.utilcode.util.ToastUtils.showShort(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                    }

                    @Override
                    public void onFinish() {
                        ProgressDialog.dismiss();
                    }
                });
    }

    private void savePost() {
        if (!isFinish()) {
            return;
        }
        BDLocation location = BaseApplication.getIntstance().getLocation();
        Map<String, String> map = new LinkedHashMap<>();
        String pics = ArrayUtil.toCommaSplitStr(pickPicView.getPics());
        map.put("pic", pics);
        map.put("uid", loginModel.uid + "");
        map.put("token", loginModel.token);
        map.put("id", postDraft.id + "");
        map.put("post_cate_id", post_cate_id + "");
        map.put("custom_post_cate", et_custom_post_cate.getText().toString().trim());
        map.put("title", title);
        map.put("body", body);
        map.put("user_group_id", user_group_id + "");
        map.put("user_name", user_name);
        map.put("phone_from", phone_from + "");
        if (phone_from == 2) {
            map.put("phone", phone);
            map.put("code", code);
        }
        map.put("lon", location.getLongitude() + "");
        map.put("lat", location.getLatitude() + "");
        map.put("province", StringUtils.isEmpty(BaseApplication.getIntstance().getProvince()) ? "" : BaseApplication.getIntstance().getProvince());
        map.put("city", StringUtils.isEmpty(BaseApplication.getIntstance().getCity()) ? "" : BaseApplication.getIntstance().getCity());
        if (price != null && !price.isEmpty()) {
            map.put("price", price);
            map.put("price_unit", price_unit);
        }
        map.put("key_word", et_key_word.getText().toString().trim());
        if (red_package_money > 0) {
            map.put("red_package_rule", red_package_rule + "");
            map.put("red_package_money", red_package_money + "");
            map.put("single_red_money", getPostMsg.single_red_money + "");
            map.put("red_package_password", et_red_package_password.getText().toString().trim());
            map.put("rob_red_package_range", rob_red_package_range + "");
        }
        LoadingDialog.showLoading(this);
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.EDITPOSTDRAFT)
                .params(map)
                .execute(new AbsJsonCallBack<EmptyEntityModel, EmptyEntity>() {
                    @Override
                    public void onSuccess(EmptyEntity body) {
                        LoadingDialog.closeLoading();
                        com.yuqian.mncommonlibrary.utils.ToastUtils.show("保存成功");
                        finish();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        com.blankj.utilcode.util.ToastUtils.showShort(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                    }
                });
    }

    private void setRedTvBackground(int tag) {
        tv_red1.setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
        tv_red1.setTextColor(getResources().getColor(R.color.color080808));
        tv_red2.setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
        tv_red2.setTextColor(getResources().getColor(R.color.color080808));
        switch (tag) {
            case R.id.tv_red1:
                tv_red1.setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                tv_red1.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.tv_red2:
                tv_red2.setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                tv_red2.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    private void setTvBackground(int tag) {
        tv1.setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
        tv1.setTextColor(getResources().getColor(R.color.color080808));
        tv2.setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
        tv2.setTextColor(getResources().getColor(R.color.color080808));
        switch (tag) {
            case R.id.tv1:
                tv1.setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                tv1.setTextColor(getResources().getColor(R.color.white));
                ll_phone.setVisibility(View.GONE);
                break;
            case R.id.tv2:
                tv2.setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                tv2.setTextColor(getResources().getColor(R.color.white));
                ll_phone.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setTvBackground2(int tag) {
        tv_price1.setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
        tv_price1.setTextColor(getResources().getColor(R.color.color080808));
        tv_price2.setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
        tv_price2.setTextColor(getResources().getColor(R.color.color080808));
        tv_price3.setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
        tv_price3.setTextColor(getResources().getColor(R.color.color080808));
        tv_price4.setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
        tv_price4.setTextColor(getResources().getColor(R.color.color080808));
        tv_price5.setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
        tv_price5.setTextColor(getResources().getColor(R.color.color080808));
        tv_price6.setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
        tv_price6.setTextColor(getResources().getColor(R.color.color080808));
        tv_price7.setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
        tv_price7.setTextColor(getResources().getColor(R.color.color080808));
        tv_price8.setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
        tv_price8.setTextColor(getResources().getColor(R.color.color080808));
        tv_price9.setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
        tv_price9.setTextColor(getResources().getColor(R.color.color080808));
        tv_price10.setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
        tv_price10.setTextColor(getResources().getColor(R.color.color080808));
        switch (tag) {
            case R.id.tv_price1:
                tv_price1.setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                tv_price1.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.tv_price2:
                tv_price2.setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                tv_price2.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.tv_price3:
                tv_price3.setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                tv_price3.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.tv_price4:
                tv_price4.setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                tv_price4.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.tv_price5:
                tv_price5.setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                tv_price5.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.tv_price6:
                tv_price6.setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                tv_price6.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.tv_price7:
                tv_price7.setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                tv_price7.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.tv_price8:
                tv_price8.setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                tv_price8.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.tv_price9:
                tv_price9.setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                tv_price9.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.tv_price10:
                tv_price10.setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                tv_price10.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    private void postCate() {
        new OkhttpUtils().with()
                .get()
                .url(HttpUrl.POSTCATE)
                .execute(new AbsJsonCallBack<PostCateModel, PostCate>() {
                    @Override
                    public void onSuccess(PostCate body) {
                        ArrayList<PostCate.Data> list = (ArrayList<PostCate.Data>) body.list;
                        if (list == null) {
                            return;
                        }
                        Hawk.put("pcs", list);
                        getRedEnvelopeSetting();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        ToastUtils.showShort(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                        getRedEnvelopeSetting();
                    }
                });
    }

    private void getRedEnvelopeSetting() {
        new OkhttpUtils().with()
                .get()
                .url(HttpUrl.GETREDENVELOPESETTING)
                .execute(new AbsJsonCallBack<RedEnvelopeSettingModel, RedEnvelopeSetting[]>() {
                    @Override
                    public void onSuccess(RedEnvelopeSetting[] body) {
                        ArrayList<RedEnvelopeSetting> redEnvelopeSettings = new ArrayList<>();
                        for (RedEnvelopeSetting pc : body) {
                            redEnvelopeSettings.add(pc);
                        }
                        Hawk.put("redEnvelopeSettings", redEnvelopeSettings);
                        getRedEnvelopeDistance();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        ToastUtils.showShort(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                        getRedEnvelopeDistance();
                    }
                });
    }

    private void userGroup() {
        new OkhttpUtils().with()
                .get()
                .url(HttpUrl.USERGROUP)
                .execute(new AbsJsonCallBack<UserGroupModel, UserGroups>() {
                    @Override
                    public void onSuccess(UserGroups body) {
                        if (body.list == null && body.list.size() > 0) {
                            return;
                        }
                        Hawk.put("userGroups", body.list);
                        initViews();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        ToastUtils.showShort(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                        initViews();
                    }
                });
    }

    private void getRedEnvelopeDistance() {
        new OkhttpUtils().with()
                .get()
                .url(HttpUrl.GETREDENVELOPEDISTANCE)
                .execute(new AbsJsonCallBack<RedEnvelopeDistanceModel, RedEnvelopeDistance[]>() {
                    @Override
                    public void onSuccess(RedEnvelopeDistance[] body) {
                        ArrayList<RedEnvelopeDistance> redEnvelopeSettings = new ArrayList<>();
                        for (RedEnvelopeDistance pc : body) {
                            redEnvelopeSettings.add(pc);
                        }
                        Hawk.put("redEnvelopeDistances", redEnvelopeSettings);
                        userGroup();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        ToastUtils.showShort(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                        userGroup();
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.activity_scale_in_anim, R.anim.activity_move_out_anim);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BaiduEvent event) {
        BDLocation location = BaseApplication.getIntstance().getLocation();
        if(location != null ){
            tv_addtrs.setText("已获取位置：" + BaseApplication.getIntstance().getAddress());
        }else {
            tv_addtrs.setText("获取位置失败，请检查定位权限是否开启");
        }
    }

}
