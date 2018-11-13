package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.yanzhenjie.permission.AndPermission;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.JsonBean;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.UploadModel;
import com.yjyc.zhoubian.model.UserInfo;
import com.yjyc.zhoubian.model.UserInfoModel;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yjyc.zhoubian.utils.PermissionUtils;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;
import com.yuqian.mncommonlibrary.utils.ToastUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by Administrator on 2018/10/10/010.
 */

public class EditProfileActivity extends BaseActivity {
    @BindView(R.id.tv_sex)
    TextView tv_sex;

    @BindView(R.id.tv_city)
    TextView tv_city;

    @BindView(R.id.tv_birthday)
    TextView tv_birthday;

    @BindView(R.id.et_sign)
    EditText et_sign;

    @BindView(R.id.et_nickname)
    EditText et_nickname;

    @BindView(R.id.iv_headUrl)
    RoundedImageView iv_headUrl;

    private Context mContext;
    private TimePickerView pvCustomLunar;
    private OptionsPickerView pvOptions;
    private OptionsPickerView cityOptions;
    private List<LocalMedia> selectList = new ArrayList<>();
    private String currentPath;
    private ArrayList<String> options1Items = new ArrayList<>();//省
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();//市
    private ArrayList<JsonBean> jsonBean;
    private  RequestOptions options;
    private boolean isUpload;
    private boolean picTag;
    private UserInfo userInfo;
    Login loginModel;
    String head_url = "";
    private Gson gson;
    private GsonBuilder builder;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mContext = this;
        ButterKnife.bind(this);
        initView();
        initLunarPicker();
        getOptionData();
        initOptionPicker();
        initJsonData();
        showCityPicker();
    }

    private void initView() {
        builder=new GsonBuilder();
        gson=builder.create();
        loginModel = Hawk.get("LoginModel");
        options = new RequestOptions()
                .centerCrop();

        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("编辑资料", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("userInfo", userInfo);
                setResult(200, data);
                onBackPressed();
            }
        });

        userInfo = (UserInfo) getIntent().getSerializableExtra("userInfo");
        if(userInfo == null){
            userInfo = new UserInfo();
            userInfo();
        }else {
            setView(userInfo);
        }


        et_sign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    String[] str = s.toString().split(" ");
                    String str1 = "";
                    for (int i = 0; i < str.length; i++) {
                        str1 += str[i];
                    }
                    et_sign.setText(str1);

                    et_sign.setSelection(start);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        et_nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    String[] str = s.toString().split(" ");
                    String str1 = "";
                    for (int i = 0; i < str.length; i++) {
                        str1 += str[i];
                    }
                    et_nickname.setText(str1);

                    et_nickname.setSelection(start);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void userInfo() {
        Login loginModel = Hawk.get("LoginModel");
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.USERINFO)
                .addParams("uid", loginModel.uid + "")
                .addParams("token", loginModel.token)
                .execute(new AbsJsonCallBack<UserInfoModel, UserInfo>() {

                    @Override
                    public void onSuccess(UserInfo body) {
                        setView(body);
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

    private void setView(UserInfo body) {
        if(!StringUtils.isEmpty(body.head_url_img)){
            head_url = body.head_url_img;
            url = body.head_url;
            isUpload = false;
            picTag = true;
            Glide.with(context)
                    .load(body.head_url_img)
                    .apply(options)
                    .into(iv_headUrl);
        }

        et_nickname.setText(StringUtils.isEmpty(body.nickname) ? "" : body.nickname);

        if(body.sex == 1){
            tv_sex.setText("男");
        }else if(body.sex == 2){
            tv_sex.setText("女");
        }

        StringBuilder city = new StringBuilder();
        city.append(StringUtils.isEmpty(body.provinces) ? "" : body.provinces).append("  ").append
                (StringUtils.isEmpty(body.city) ? "" : body.city);
        tv_city.setText(city.toString());

        et_sign.setText(StringUtils.isEmpty(body.sign) ? "" : body.sign);

        tv_birthday.setText(StringUtils.isEmpty(body.birthday) ? "" : body.birthday);
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

//        pvOptions.setPicker(cardItem);//添加数据

//        pvOptions.setSelectOptions(1,1);
        cityOptions.setPicker(options1Items);//一级选择器
        cityOptions.setPicker(options1Items, options2Items);//二级选择器
        /*pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器*/
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

    @OnClick(R.id.iv_headUrl)
    public void iv_headUrl(){
        selectPhoto();
    }

    @OnClick(R.id.ll_birthday)
    public void ll_birthday(){
        pvCustomLunar.show();
    }

    @OnClick(R.id.ll_sex)
    public void ll_sex(){
        pvOptions.show();
    }

    @OnClick(R.id.ll_city)
    public void ll_city(){
        cityOptions.show();
    }

    @OnClick(R.id.tv_next)
    public void tv_next(){
        if(!picTag){
            com.blankj.utilcode.util.ToastUtils.showShort("请选择头像");
            return;
        }

        if(StringUtils.isEmpty(et_nickname.getText().toString())){
            ToastUtils.show("请输入用户名");
            return;
        }

        if(StringUtils.isEmpty(tv_city.getText().toString())){
            ToastUtils.show("请选择地区");
            return;
        }

        if(StringUtils.isEmpty(tv_sex.getText().toString())){
            ToastUtils.show("请选择性别");
            return;
        }

        if(StringUtils.isEmpty(tv_birthday.getText().toString())){
            ToastUtils.show("请选择出生日期");
            return;
        }

        if(StringUtils.isEmpty(et_sign.getText().toString())){
            ToastUtils.show("请填写个性签名");
            return;
        }

        if(isUpload){
            upLoad();
        }else {
            userInfoUpdate();
        }

    }

    private void userInfoUpdate() {

        Login loginModel = Hawk.get("LoginModel");
        int sex = tv_sex.getText().toString().equals("女") ? 2 : 1;
        String[] strs = tv_city.getText().toString().split("  ");
        userInfo.nickname = et_nickname.getText().toString();
        userInfo.sex = sex;
        userInfo.provinces = strs != null && strs.length > 0 ?strs[0] : "";
        userInfo.city = strs != null && strs.length > 1 ?strs[1] : "";
        userInfo.sign = et_sign.getText().toString();
        userInfo.head_url_img = head_url;
        userInfo.birthday = tv_birthday.getText().toString();
                OkhttpUtils.with()
                .post()
                .url(HttpUrl.USERINFOUPDATE)
                .addParams("uid", loginModel.uid + "")
                .addParams("token", loginModel.token)
                .addParams("nickname", et_nickname.getText().toString())
                .addParams("sex", sex + "")
                .addParams("provinces", strs != null && strs.length > 0 ?strs[0] : "")
                .addParams("city", strs != null && strs.length > 1 ?strs[1] : "")
                .addParams("head_url", url)
                .addParams("sign", et_sign.getText().toString())
                        .addParams("birthday", tv_birthday.getText().toString())
                .execute(new AbsJsonCallBack<UserInfoModel, UserInfo>() {

                    @Override
                    public void onSuccess(UserInfo body) {
                        userInfo.uid = loginModel.uid;
                        Hawk.put("userInfo", userInfo);
                        ToastUtils.show("编辑成功");
                        Intent data = new Intent();
                        data.putExtra("userInfo", userInfo);
                        setResult(200, data);
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

    private void upLoad() {
        ProgressDialog.showDialog(mContext);
        Luban.with(this)
                .load(new File(currentPath))
                .ignoreBy(30)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        setImgByStr( File2StrByBase64(file),
                                loginModel.token, loginModel.uid + "");
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                        setImgByStr( Bitmap2StrByBase64(currentPath),
                                loginModel.token, loginModel.uid + "");
                    }
                }).launch();
    }

    public String File2StrByBase64(File f){
        ByteArrayOutputStream out= null;
        try {
            FileInputStream stream = new FileInputStream(f);
            out = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            byte[] imgBytes = out.toByteArray();
            return "data:image/jpeg;base64," + Base64.encodeToString(imgBytes, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过Base32将Bitmap转换成Base64字符串
     * @return
     */
    public String Bitmap2StrByBase64(String imgPath){
//        ByteArrayOutputStream bos=new ByteArrayOutputStream();
//        bit.compress(Bitmap.CompressFormat.JPEG, 40, bos);//参数100表示不压缩
//        byte[] bytes=bos.toByteArray();
//        return Base64.encodeToString(bytes, Base64.DEFAULT);

        Bitmap bitmap = null;
        if (imgPath !=null && imgPath.length() > 0) {
            bitmap =  BitmapFactory.decodeFile(imgPath);
        }
        if(bitmap == null){
            return null;
        }
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 30, out);
            out.flush();
            out.close();

            byte[] imgBytes = out.toByteArray();
            return "data:image/jpeg;base64," + Base64.encodeToString(imgBytes, Base64.DEFAULT);
        } catch (Exception e) {
            return null;
        } finally {
            try {
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setImgByStr(String imgStr,String token, String uid){
        String url =  HttpUrl.UPLOADSIMGFORSTRING;
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("file", imgStr);
        params.put("token", token);
        params.put("uid", uid);
        post(params, url);
//        return getValues(params, url);
    }

//    public static Object getValues(Map<String, Object> params, String url) {
//        String token = "";
//        HttpResponse response = post(params, url);
//        if (response != null) {
//            try {
//                token = EntityUtils.toString(response.getEntity());
//                response.removeHeaders("operator");
//            } catch (ParseException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return token;
//    }

    public void post(final Map<String, Object> params, final String url) {
        new Thread(new Runnable() {

                     @Override
             public void run() {
                         HttpClient client = new DefaultHttpClient();
                         HttpPost httpPost = new HttpPost(url);
                         httpPost.addHeader("charset", HTTP.UTF_8);
                         httpPost.setHeader("Content-Type",
                                 "application/x-www-form-urlencoded; charset=utf-8");
                         HttpResponse response = null;
                         if (params != null && params.size() > 0) {
                             List<NameValuePair> nameValuepairs = new ArrayList<NameValuePair>();
                             for (String key : params.keySet()) {
                                 nameValuepairs.add(new BasicNameValuePair(key, (String) params
                                         .get(key)));
                             }
                             try {
                                 httpPost.setEntity(new UrlEncodedFormEntity(nameValuepairs,
                                         HTTP.UTF_8));
                                 response = client.execute(httpPost);
                                 String token = EntityUtils.toString(response.getEntity());
                                 jsonToObject(token);
                                 Logger.i(token);
                                 ProgressDialog.dismiss();
                             } catch (Exception e) {
                                 Logger.i(e.toString());
                                 ProgressDialog.dismiss();
                                 ToastUtils.show("编辑失败");
                                 e.printStackTrace();
                             }
                         } else {
                             try {
                                 response = client.execute(httpPost);
                             } catch (ClientProtocolException e) {
                                 e.printStackTrace();
                             } catch (IOException e) {
                                 e.printStackTrace();
                             }
                         }

                             }
         }).start();//这个start()方法不要忘记了

    }

    private void jsonToObject(String token) {
      try {
          Logger.i(token);
          UploadModel um= gson.fromJson(token, UploadModel.class);
          Logger.i("123");
          if(um.getHeader() != null && "success".equals(um.getHeader().getStatus()) && um.getBody() != null
                  && !StringUtils.isEmpty(um.getBody().url)){
              url = um.getBody().url;
              head_url = um.getBody().httpUrl;
              userInfoUpdate();
          }else {
              ToastUtils.show("编辑失败");
          }
      }catch (Exception e){
          ToastUtils.show("编辑失败");
          Logger.i(e.toString());
      }
    }



    private ArrayList<String> sexItems = new ArrayList<>();
    private void getOptionData() {

        /**
         * 注意：如果是添加JavaBean实体数据，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */

        //选项1
        sexItems.add("男");
        sexItems.add("女");


        /*--------数据源添加完毕---------*/
    }

    private void initOptionPicker() {//条件选择器初始化

        /**
         * 注意 ：如果是三级联动的数据(省市区等)，请参照 JsonDataActivity 类里面的写法。
         */

        pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = sexItems.get(options1);
                tv_sex.setText(tx);
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
                                pvOptions.returnData();
                                pvOptions.dismiss();
                            }
                        });

                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvOptions.dismiss();
                            }
                        });


                    }
                })
                .build();

//        pvOptions.setPicker(cardItem);//添加数据

//        pvOptions.setSelectOptions(1,1);
        pvOptions.setPicker(sexItems);//一级选择器
//        pvOptions.setPicker(options1Items, options2Items);//二级选择器
        /*pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器*/
    }

    /**
     * 农历时间已扩展至 ： 1900 - 2100年
     */
    private void initLunarPicker() {
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(1900, 1, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2069, 2, 28);
        //时间选择器 ，自定义布局
        pvCustomLunar = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                tv_birthday.setText(getTime(date));
            }
        })
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_lunar, new CustomListener() {

                    @Override
                    public void customLayout(final View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomLunar.returnData();
                                pvCustomLunar.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomLunar.dismiss();
                            }
                        });
                        //公农历切换
//                        CheckBox cb_lunar = (CheckBox) v.findViewById(R.id.cb_lunar);
//                        cb_lunar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                            @Override
//                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                                pvCustomLunar.setLunarCalendar(!pvCustomLunar.isLunarCalendar());
//                                //自适应宽
//                                setTimePickerChildWeight(v, isChecked ? 0.8f : 1f, isChecked ? 1f : 1.1f);
//                            }
//                        });

                    }

                    /**
                     * 公农历切换后调整宽
                     * @param v
                     * @param yearWeight
                     * @param weight
                     */
                    private void setTimePickerChildWeight(View v, float yearWeight, float weight) {
                        ViewGroup timepicker = (ViewGroup) v.findViewById(R.id.timepicker);
                        View year = timepicker.getChildAt(0);
                        LinearLayout.LayoutParams lp = ((LinearLayout.LayoutParams) year.getLayoutParams());
                        lp.weight = yearWeight;
                        year.setLayoutParams(lp);
                        for (int i = 1; i < timepicker.getChildCount(); i++) {
                            View childAt = timepicker.getChildAt(i);
                            LinearLayout.LayoutParams childLp = ((LinearLayout.LayoutParams) childAt.getLayoutParams());
                            childLp.weight = weight;
                            childAt.setLayoutParams(childLp);
                        }
                    }
                })
                .setType(new boolean[]{true, true, true, false, false, false})
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(Color.RED)
                .build();
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private void selectPhoto() {
        PermissionUtils.checkCameraPermission(this, new PermissionUtils.PermissionCallBack() {
            @Override
            public void onGranted() {
                PictureSelector.create(EditProfileActivity.this)
                        .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                        .maxSelectNum(1)// 最大图片选择数量 int
                        .minSelectNum(1)// 最小选择数量 int
                        .imageSpanCount(4)// 每行显示个数 int
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(true)// 是否可预览图片 true or false
                        .isCamera(true)// 是否显示拍照按钮 true or false
                        .selectionMedia(selectList)
                        .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                        .sizeMultiplier(0.8f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        .compress(true)// 是否压缩 true or false
                        .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                        .minimumCompressSize(30)// 小于300kb的图片不压缩
                        .circleDimmedLayer(true)
                        .previewImage(true)// 是否可预览图片 true or false
                        .enableCrop(true)// 是否裁剪 true or false
                        .rotateEnabled(false) // 裁剪是否可旋转图片 true or false
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            }

            @Override
            public void onDenied() {
                new MaterialDialog.Builder(EditProfileActivity.this)
                        .title("提示")
                        .content("当前权限被拒绝导致功能不能正常使用，请到设置界面修改相机和存储权限允许访问")
                        .positiveText("去设置")
                        .negativeText("取消")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                AndPermission.permissionSetting(EditProfileActivity.this)
                                        .execute();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    isUpload = true;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    LocalMedia localMedia = selectList.get(0);
                    RequestOptions options = new RequestOptions()
                            .centerCrop();
                    if (localMedia.isCompressed()) {
                        currentPath = localMedia.getCompressPath();
                    } else {
                        currentPath = localMedia.getPath();
                    }
                    picTag = true;
                    Glide.with(context)
                            .load(currentPath)
                            .apply(options)
                            .into(iv_headUrl);
                    break;
            }
        }
    }
}