package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.blankj.utilcode.util.BarUtils;
import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.EmptyEntity;
import com.yjyc.zhoubian.model.EmptyEntityModel;
import com.yjyc.zhoubian.model.InfoPost;
import com.yjyc.zhoubian.model.InfoPostModel;
import com.yjyc.zhoubian.model.JsonBean;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.LoginModel;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InvestCooperateActivity extends BaseActivity {

    @BindView(R.id.tv_city)
    TextView tv_city;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.phone)
    TextView phone;
    @BindView(R.id.company_name)
    TextView company_name;
    @BindView(R.id.remark)
    EditText remark;
    @BindView(R.id.submit)
    TextView submit;
    @BindView(R.id.text)
    TextView text;

    private OptionsPickerView cityOptions;
    private ArrayList<JsonBean> jsonBean;
    private ArrayList<String> options1Items = new ArrayList<>();//省
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();//市
    private Login login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invest_cooperate);
        ButterKnife.bind(this);
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("建议、申诉、招商", v -> onBackPressed());

        login = Hawk.get("LoginModel");
        if(login == null){
            showToast("请先登录");
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        initJsonData();
        showCityPicker();
        reqData();
    }


    @OnClick({R.id.ll_city, R.id.submit})
    void onclick(View view){
        switch (view.getId()){
            case R.id.ll_city:
                cityOptions.show();
                break;
            case R.id.submit:
                submit();
                break;
        }
    }

    private void submit() {
        /*if(name.getText().toString().isEmpty()){
            showToast("姓名不能为空");
            return;
        }
        if(phone.getText().toString().isEmpty()){
            showToast("联系电话不能为空");
            return;
        }
        if(company_name.getText().toString().isEmpty()){
            showToast("公司名称不能为空");
            return;
        }
        if(tv_city.getText().toString().isEmpty()){
            showToast("请选择所在地区");
            return;
        }*/
        if(remark.getText().toString().isEmpty()){
            showToast("详细信息不能为空");
            return;
        }
        /*String nameStr = name.getText().toString();
        String phoneStr = phone.getText().toString();
        String companyNameStr = company_name.getText().toString();
        String provinces = tv_city.getText().toString().split("  ")[0];
        String city = tv_city.getText().toString().split("  ")[1];*/
        String remarkStr = remark.getText().toString();
        LoadingDialog.showLoading(this);
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.INFOPOST)
                .addParams("uid", login.uid + "")
                .addParams("token", login.token)
               /* .addParams("nickname", nameStr)
                .addParams("phone", phoneStr)
                .addParams("provinces", provinces)
                .addParams("city", city)
                .addParams("company_name", companyNameStr)*/
                .addParams("remark", remarkStr)
                .execute(new AbsJsonCallBack<EmptyEntityModel, EmptyEntity>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        showToast(errorMsg);
                    }

                    @Override
                    public void onSuccess(EmptyEntity body) {
                        showToast("发布成功");
                        finish();
                    }
                });
    }

    private void reqData(){
        new OkhttpUtils().with()
                .get()
                .url(HttpUrl.INFOPOST)
                .addParams("uid", login.uid + "")
                .addParams("token", login.token)
                .execute(new AbsJsonCallBack<InfoPostModel, InfoPost>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                    }

                    @Override
                    public void onSuccess(InfoPost body) {
                        if(body != null && body.sys_content != null){
                            text.setText(body.sys_content);
                        }
                    }
                });
    }

    public String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return detail;
    }

    private void initJsonData() {//解析数据
        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData =  getJson(this,"province.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        ArrayList<String> AreaList = new ArrayList<>();
        for (int i=0;i<jsonBean.size();i++){//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）
            this.jsonBean = jsonBean;
            for (int c=0; c<jsonBean.get(i).getCityList().size(); c++){//遍历该省份的所有城市
                String CityName = jsonBean.get(i).getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        ||jsonBean.get(i).getCityList().get(c).getArea().size()==0) {
                    City_AreaList.add("");
                }else {

                    for (int d=0; d < jsonBean.get(i).getCityList().get(c).getArea().size(); d++) {//该城市对应地区所有数据
                        String AreaName = jsonBean.get(i).getCityList().get(c).getArea().get(d);

                        City_AreaList.add(AreaName);//添加该城市所有地区数据
                    }
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(CityList);
            AreaList.add(jsonBean.get(i).getName());
        }
        options1Items.addAll(AreaList);
    }

    private void showCityPicker(){
        cityOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                JsonBean jb = jsonBean.get(options1);
                String area = options1Items.get(options1);
                String city = jb.getCityList().get(option2).getName();
                tv_city.setText(area + "  " + city);
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cityOptions.returnData();
                                cityOptions.dismiss();
                            }
                        });

                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                cityOptions.dismiss();
                            }
                        });


                    }
                })
                .build();

        cityOptions.setPicker(options1Items);
        cityOptions.setPicker(options1Items, options2Items);
    }


}
