package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnDismissListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bigkoo.pickerview.view.TimePickerView;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.orhanobut.hawk.Hawk;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.BalanceDetails;
import com.yjyc.zhoubian.model.BalanceDetailsModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;
import com.yuqian.mncommonlibrary.refresh.header.MaterialHeader;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 明细
 * Created by Administrator on 2018/10/11/011.
 */

public class DetailActivity extends BaseActivity {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.rl_title_bg)
    RelativeLayout rl_title_bg;

    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    private Context mContext;
    private PopupWindow mPopWindow;
    private TimePickerView pvCustomLunar;
    private OptionsPickerView pvOptions;
    private TextView tv_time;
    private TextView tv_type;
    private int page = 1;
    private List<BalanceDetails.Data> datas = new ArrayList<>();
    private BalanceDetails body;
    private String type = "";
    private String time = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mContext = this;
        ButterKnife.bind(this);
        initView();
        initDate();
        setPullRefresher();
        initLunarPicker();
        getOptionData();
        initOptionPicker();
    }

    private void initView() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("明细", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        showRightPulishText();
        TextView tv = getRightPulishText();
        tv.setText("筛选");
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopwindow();
            }
        });
        myAdapter = new MyAdapter();
        if(!ProgressDialog.isShowing()){
            ProgressDialog.showDialog(mContext);
        }
        balanceDetails(type, time);
    }

    private void balanceDetails(String type, String time) {

        Login loginModel = Hawk.get("LoginModel");
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.BALANCEDETAILS)
                .addParams("uid", loginModel.uid + "")
                .addParams("token", loginModel.token)
                .addParams("listRows", "10")
                .addParams("page", page + "")
                .addParams("type", type)
                .addParams("create_time", time)
                .execute(new AbsJsonCallBack<BalanceDetailsModel, BalanceDetails>() {


                    @Override
                    public void onSuccess(BalanceDetails body) {
                        if(body.list == null ){
                            ToastUtils.showShort("网络异常,请稍后重试" );
                            return;
                        }
                        DetailActivity.this.body = body;
                        ArrayList<BalanceDetails.Data> datas = (ArrayList<BalanceDetails.Data>) body.list;
                        if(page == 1){
                            myAdapter.refresh(datas);
                        }else {
                            myAdapter.add(datas);
                        }
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        ToastUtils.showShort(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                    }

                    @Override
                    public void onFinish() {
                        ProgressDialog.dismiss();
                    }
                });
    }

    private void initDate(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//纵向线性布局

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);
    }
    View contentView;
    private void showPopwindow() {
        if(mPopWindow == null){
            contentView = LayoutInflater.from(DetailActivity.this).inflate(R.layout.detailpopuplayout, null);
            mPopWindow = new PopupWindow(contentView,
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        }

        LinearLayout ll_time = contentView.findViewById(R.id.ll_time);
        ll_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvCustomLunar.show();
                mPopWindow.dismiss();
            }
        });

        LinearLayout ll_type = contentView.findViewById(R.id.ll_type);
        ll_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pvOptions.show();
                mPopWindow.dismiss();
            }
        });

        tv_time = contentView.findViewById(R.id.tv_time);
        tv_type = contentView.findViewById(R.id.tv_type);
        mPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });

        TextView tv_resetting = contentView.findViewById(R.id.tv_resetting);
        tv_resetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_time.setText("选择时间");
                tv_type.setText("选择类型");
            }
        });

        TextView tv_next = contentView.findViewById(R.id.tv_next);
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time = tv_time.getText().toString();
                String type = tv_type.getText().toString();
                mPopWindow.dismiss();
                if(!type.equals(DetailActivity.this.type) || !time.equals(DetailActivity.this.time)){
                    DetailActivity.this.type = type;
                    DetailActivity.this.time = time;
                    if("充值".equals(type)){
                        type = "1";
                    }else if("提现".equals(type)){
                        type = "2";
                    }else if("抢红包".equals(type)){
                        type = "3";
                    }else if("发红包".equals(type)){
                        type = "4";
                    }else {
                        type = "";
                    }

                    if("选择时间".equals(time)){
                        time = "";
                    }
                    if(!ProgressDialog.isShowing()){
                        ProgressDialog.showDialog(mContext);
                    }
                    balanceDetails(type, time);
                }
            }
        });
        mPopWindow.setContentView(contentView);
        //显示PopupWindow
        mPopWindow.showAsDropDown(rl_title_bg);

    }

    public interface OnItemClickListener{
        void onClick( int position);
        void onLongClick( int position);
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> implements OnItemClickListener{
        //        private List<ItemBean> mList;
        private OnItemClickListener onItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
            this. onItemClickListener =onItemClickListener;
        }

        @Override
        public void onClick(int position) {

        }

        @Override
        public void onLongClick(int position) {

        }

        class ViewHolder extends RecyclerView.ViewHolder{
            View myView;
            TextView tv_create_time;
            TextView tv_type;
            TextView tv_money;
            TextView tv_balance;
            public ViewHolder(View itemView) {
                super(itemView);
                myView = itemView;
                tv_create_time = itemView.findViewById(R.id.tv_create_time);
                tv_type = itemView.findViewById(R.id.tv_type);
                tv_money = itemView.findViewById(R.id.tv_money);
                tv_balance = itemView.findViewById(R.id.tv_balance);
            }
        }

//        public MyAdapter(List<ItemBean> list){
//            this.mList = list;
//        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_item,null);
            final MyAdapter.ViewHolder holder = new MyAdapter.ViewHolder(view);
            return holder;
        }

        //将数据绑定到控件上
        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, final int position) {
            BalanceDetails.Data data = datas.get(position);
            if( onItemClickListener!= null){
                holder.itemView.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onClick(position);
                    }
                });
                holder. itemView.setOnLongClickListener( new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onItemClickListener.onLongClick(position);
                        return false;
                    }
                });
            }

            holder.tv_create_time.setText(StringUtils.isEmpty(data.create_time) ? "" : data.create_time);
            String type = "";
            switch (data.type){
                case 1:
                    type = "充值";
                    break;
                case 2:
                    type = "提现";
                    break;
                case 3:
                    type = "抢红包";
                    break;
                case 4:
                    type = "发红包";
                    break;
            }
            holder.tv_type.setText(type);

            holder.tv_money.setText(StringUtils.isEmpty(data.money) ? "" : data.money);

            holder.tv_balance.setText(StringUtils.isEmpty(data.balance) ? "" : data.balance);
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        //下面两个方法提供给页面刷新和加载时调用
        public void add(List<BalanceDetails.Data> addMessageList) {
            //增加数据
            int position = datas.size();
            datas.addAll(position, addMessageList);
            notifyItemInserted(position);
        }

        public void refresh(List<BalanceDetails.Data> newList) {
            //刷新数据
            datas.clear();
            datas.addAll(newList);
            notifyDataSetChanged();
        }
    }
    MyAdapter myAdapter;

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
                tv_time.setText(getTime(date));
                showPopwindow();
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

        pvCustomLunar.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                showPopwindow();
            }
        });
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private ArrayList<String> options1Items = new ArrayList<>();
    private void getOptionData() {

        /**
         * 注意：如果是添加JavaBean实体数据，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */

        //选项1
        options1Items.add("充值");
        options1Items.add("抢红包");
        options1Items.add("提现");
        options1Items.add("发红包");

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
                String tx = options1Items.get(options1);
                tv_type.setText(tx);
                showPopwindow();
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

                        v.bringToFront();
                    }
                })
                .build();

//        pvOptions.setPicker(cardItem);//添加数据

//        pvOptions.setSelectOptions(1,1);
        pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(Object o) {
                showPopwindow();
            }
        });
//        pvOptions.setPicker(options1Items, options2Items);//二级选择器
        /*pvOptions.setPicker(options1Items, options2Items,options3Items);//三级选择器*/
    }

    private void setPullRefresher(){
        //设置 Header 为 MaterialHeader
        refreshLayout.setRefreshHeader(new MaterialHeader(this));
        //设置 Footer 为 经典样式
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                balanceDetails(type, time);
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if(body != null){
                    if(body.hasNextPages){
                        page++;
                        balanceDetails(type, time);
                    }else {
                        refreshLayout.finishLoadmore();
                        ToastUtils.showShort("没有更多");
                    }
                }else {
                    refreshLayout.finishLoadmore();
                }
                //模拟网络请求到的数据

            }
        });
    }
}