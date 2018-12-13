package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.yjyc.zhoubian.adapter.CardAdapter;
import com.yjyc.zhoubian.adapter.SearchAdapter;
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.PostCate;
import com.yjyc.zhoubian.model.RedEnvelopeSetting;
import com.yjyc.zhoubian.model.SearchPostModel;
import com.yjyc.zhoubian.model.SearchPosts;
import com.yjyc.zhoubian.model.Searchs;
import com.yjyc.zhoubian.model.UserGroups;
import com.yjyc.zhoubian.ui.dialog.ProgressDialog;
import com.yjyc.zhoubian.ui.view.SwipeBackLayout;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;
import com.yuqian.mncommonlibrary.refresh.header.MaterialHeader;

import net.masonliu.multipletextview.library.MultipleTextViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/10/11/011.
 */

public class SearchActivity extends BaseActivity {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    @BindView(R.id.recyclerview2)
    RecyclerView recyclerView2;

    @BindView(R.id.rg)
    RadioGroup rg;

    @BindView(R.id.et_keyWord)
    EditText et_keyWord;

    @BindView(R.id.rb_comprehensive)
    RadioButton rb_comprehensive;

    @BindView(R.id.ll_no_data)
    LinearLayout ll_no_data;

    @BindView(R.id.ll)
    LinearLayout ll;

    @BindView(R.id.rl_bg)
    public RelativeLayout rl_bg;

    private Context mContext;

    /*private PopupWindow createTimePop;
    private RecyclerView createTimeRecyclerview;
    private SearchAdapter createTimeAdapter;*/

    //private PopupWindow distancePop;
    //private RecyclerView distanceRecyclerview;
    //private SearchAdapter distanceAdapter;

    /*private PopupWindow pricePop;
    private RecyclerView pricePopRecyclerview;
    private SearchAdapter priceAdapter;*/

    private PopupWindow screenPop;

    CardAdapter myAdapter;
    ArrayList<PostCate.Data> pcs;
    ArrayList<Searchs.Search> searchTimeForDays;
    ArrayList<Searchs.Search> searchDistances;
    ArrayList<UserGroups.UserGroup> userGroups;
    private SearchPosts body;
    private ArrayList<SearchPosts.SearchPost> datas = new ArrayList<>();
    private int page = 1;
    private int dayTime = -1;
    private int distance = -1;
    private int post_cate_id = -1;
    private int userGroupsId = -1;
    private String order = "";
    private int tag = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBackLayout layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
                R.layout.swipe_back_layout, null);
        layout.attachToActivity(this);
        setContentView(R.layout.activity_search);
        mContext = this;
        ButterKnife.bind(this);
        initView();
        initDate();
        setPullRefresher();
    }

    private void initView() {
        rb_comprehensive.setChecked(true);
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        myAdapter = new CardAdapter(datas, this);
        myAdapter2 = new MyAdapter2();

        myAdapter.setOnItemClickListener(new com.yjyc.zhoubian.adapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                int postId = myAdapter.getPostId(position);
                if(postId == -1 || postId == -2){
                    //showToast("数据错误");
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putInt("PostId", postId);
                startActivityAni(PostDetailsActivity.class, bundle);
                BaseApplication.getIntstance().addViewedPost(myAdapter.getSearchPost(position));
            }

            @Override
            public void onLongClick(int position) {
            }

            @Override
            public void onDeleteClick(ImageView iv_delete, boolean isDown, int[] position) {
            }
        });

        et_keyWord.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                ((InputMethodManager) mContext.getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                String inputContent = et_keyWord.getText().toString().trim();
                if (inputContent.isEmpty()) {
                } else {
                    searchPost();
                }
            }
            return false;
        });
    }

    private void initDate(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//纵向线性布局
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);//纵向线性布局
        recyclerView2.setLayoutManager(layoutManager2);
        recyclerView2.setAdapter(myAdapter2);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                    if(lastVisiblePosition >= layoutManager.getItemCount() - 1){
                        if(body != null){
                            if(body.hasNextPages){
                                page++;
                                searchPost();
                            }else {
                                refreshLayout.finishLoadmore();
                                ToastUtils.showShort("没有更多");
                            }
                        }else {
                            refreshLayout.finishLoadmore();
                        }
                        //模拟网络请求到的数据
                    }
                }
            }
        });
    }

    @OnClick(R.id.btn_left)
    public void btn_left(){
        onBackPressed();
    }

    @OnClick(R.id.rb_comprehensive)
    public void rb_comprehensive(){
        tag = 1;
        for (int i = 1; i <= rg.getChildCount(); i++){
            if(tag == i){
                ((RadioButton)rg.getChildAt(i - 1)).setChecked(true);
            }else {
                ((RadioButton)rg.getChildAt(i - 1)).setChecked(false);
            }
        }
        order = "";
        distance = -1;
        dayTime = -1;
        userGroupsId = -1;
        post_cate_id = -1;
        if(!ProgressDialog.isShowing()){
            ProgressDialog.showDialog(SearchActivity.this);
        }
        page = 1;
        searchPost();
    }

    public void postDownturn(int currentPos, int downturnNum){

    }

    @OnClick(R.id.rb_distance)
    public void rb_distance(){
        showDistancePop();
    }

    @OnClick(R.id.rb_create_time)
    public void rb_create_time(){
        showCreateTimePop();
    }

    @OnClick(R.id.rb_price)
    public void rb_price(){
        showPricePop();
    }

    @OnClick(R.id.rb_screen)
    public void rb_screen(){
        showScreenPop();
    }

    @OnClick(R.id.tv_search)
    public void tv_search(){
        searchPost();
    }

    MultipleTextViewGroup mt_distance;
    MultipleTextViewGroup mt_create_time;
    MultipleTextViewGroup mt_identity;
    MultipleTextViewGroup mt_type;
    private void showScreenPop() {
        View contentView = LayoutInflater.from(SearchActivity.this).inflate(R.layout.screenpopuplayout, null);
        TextView tv_next = contentView.findViewById(R.id.tv_next);

        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setChecked();
                if(distance == -1 && dayTime == -1 && userGroupsId == -1 && post_cate_id == -1){
                    tag = 1;
                }else {
                    tag = 5;
                }
                page = 1;
                order = "";
                screenPop.dismiss();
                if(!ProgressDialog.isShowing()){
                    ProgressDialog.showDialog(SearchActivity.this);
                }
                searchPost();
            }
        });

        TextView tv_resetting = contentView.findViewById(R.id.tv_resetting);
        tv_resetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int j = 0; j < searchDistances.size(); j++){
                    mt_distance.getChildAt(j).setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
                    ((TextView) mt_distance.getChildAt(j)).setTextColor(getResources().getColor(R.color.color080808));
                    searchDistances.get(j).setIsChecked(2);
                }
                distance = -1;

                for (int j = 0; j < searchTimeForDays.size(); j++){
                    mt_create_time.getChildAt(j).setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
                    ((TextView) mt_create_time.getChildAt(j)).setTextColor(getResources().getColor(R.color.color080808));
                    searchTimeForDays.get(j).setIsChecked(2);
                }
                dayTime = -1;

                for (int j = 0; j < userGroups.size(); j++){
                    mt_identity.getChildAt(j).setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
                    ((TextView) mt_identity.getChildAt(j)).setTextColor(getResources().getColor(R.color.color080808));
                    userGroups.get(j).isChecked = 2;
                }
                userGroupsId = -1;

                for (int j = 0; j < pcs.size(); j++){
                    mt_type.getChildAt(j).setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
                    ((TextView) mt_type.getChildAt(j)).setTextColor(getResources().getColor(R.color.color080808));
                    pcs.get(j).setIsChecked(2);
                }
                post_cate_id = -1;
            }
        });

        if(screenPop == null){
            screenPop = new PopupWindow(contentView,
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            mt_distance = contentView.findViewById(R.id.mt_distance);
            List<String> dataList = new ArrayList<String>();
            if(Hawk.contains("searchTimeForDays")){
                searchDistances = Hawk.get("searchDistances");
                for (Searchs.Search pc : searchDistances){
                    dataList.add(pc.num + pc.unit);
                }

                mt_distance.setTextViews(dataList);

                mt_distance.setOnMultipleTVItemClickListener(new MultipleTextViewGroup.OnMultipleTVItemClickListener() {
                    @Override
                    public void onMultipleTVItemClick(View view, int i) {
                        Searchs.Search pc = searchDistances.get(i);
                        if(pc.getIsChecked() == 1){
                        }else {
                            mt_distance.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                            ((TextView) mt_distance.getChildAt(i)).setTextColor(getResources().getColor(R.color.white));
                            searchDistances.get(i).setIsChecked(1);
                            for (int j = 0; j < searchDistances.size(); j++){
                                if(j == i){
                                    continue;
                                }

                                mt_distance.getChildAt(j).setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
                                ((TextView) mt_distance.getChildAt(j)).setTextColor(getResources().getColor(R.color.color080808));
                                searchDistances.get(j).setIsChecked(2);
                            }
                            distance = searchDistances.get(i).num;
                        }
                    }
                });
            }

            mt_create_time = contentView.findViewById(R.id.mt_create_time);
            List<String> dataList2 = new ArrayList<String>();

            if(Hawk.contains("searchTimeForDays")){
                searchTimeForDays = Hawk.get("searchTimeForDays");
                for (Searchs.Search pc : searchTimeForDays){
                    dataList2.add(pc.num + pc.unit);
                }

                mt_create_time.setTextViews(dataList2);

                mt_create_time.setOnMultipleTVItemClickListener(new MultipleTextViewGroup.OnMultipleTVItemClickListener() {
                    @Override
                    public void onMultipleTVItemClick(View view, int i) {
                        Searchs.Search pc = searchTimeForDays.get(i);
                        if(pc.getIsChecked() == 1){
                        }else {
                            mt_create_time.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                            ((TextView) mt_create_time.getChildAt(i)).setTextColor(getResources().getColor(R.color.white));
                            searchTimeForDays.get(i).setIsChecked(1);
                            for (int j = 0; j < searchTimeForDays.size(); j++){
                                if(j == i){
                                    continue;
                                }

                                mt_create_time.getChildAt(j).setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
                                ((TextView) mt_create_time.getChildAt(j)).setTextColor(getResources().getColor(R.color.color080808));
                                searchTimeForDays.get(j).setIsChecked(2);
                            }
                            dayTime = searchTimeForDays.get(i).num;
                        }
                    }
                });
            }

            mt_identity = contentView.findViewById(R.id.mt_identity);
            List<String> dataList3 = new ArrayList<String>();

            if(Hawk.contains("userGroups")){
                userGroups = Hawk.get("userGroups");
                for (UserGroups.UserGroup pc : userGroups){
                    dataList3.add(pc.title);
                }

                mt_identity.setTextViews(dataList3);

                mt_identity.setOnMultipleTVItemClickListener(new MultipleTextViewGroup.OnMultipleTVItemClickListener() {
                    @Override
                    public void onMultipleTVItemClick(View view, int i) {
                        UserGroups.UserGroup pc = userGroups.get(i);
                        if(pc.isChecked == 1){
                        }else {
                            mt_identity.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                            ((TextView) mt_identity.getChildAt(i)).setTextColor(getResources().getColor(R.color.white));
                            userGroups.get(i).isChecked = 1;
                            for (int j = 0; j < userGroups.size(); j++){
                                if(j == i){
                                    continue;
                                }

                                mt_identity.getChildAt(j).setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
                                ((TextView) mt_identity.getChildAt(j)).setTextColor(getResources().getColor(R.color.color080808));
                                userGroups.get(j).isChecked = 2;
                            }
                            userGroupsId = userGroups.get(i).id;
                        }
                    }
                });
            }

            /*MultipleTextViewGroup mt_address = contentView.findViewById(R.id.mt_address);
            List<String> dataList4 = new ArrayList<String>();

            dataList4.add("当前");
            dataList4.add("A");
            dataList4.add("B");
            dataList4.add("C");

            mt_address.setTextViews(dataList4);*/

            mt_type = contentView.findViewById(R.id.mt_type);
            List<String> dataList5 = new ArrayList<String>();


            if(Hawk.contains("pcs")){
                pcs = Hawk.get("pcs");
                for (PostCate.Data pc : pcs){
                    dataList5.add(pc.getTitle());
                }

                mt_type.setTextViews(dataList5);

                mt_type.setOnMultipleTVItemClickListener(new MultipleTextViewGroup.OnMultipleTVItemClickListener() {
                    @Override
                    public void onMultipleTVItemClick(View view, int i) {
                        PostCate.Data pc = pcs.get(i);
                        if(pc.getIsChecked() == 1){
                        }else {
                            mt_type.getChildAt(i).setBackground(getResources().getDrawable(R.drawable.d53c3c_3bg));
                            ((TextView) mt_type.getChildAt(i)).setTextColor(getResources().getColor(R.color.white));
                            pcs.get(i).setIsChecked(1);
                            for (int j = 0; j < pcs.size(); j++){
                                if(j == i){
                                    continue;
                                }

                                mt_type.getChildAt(j).setBackground(getResources().getDrawable(R.drawable.fff_3_stroke_1bg));
                                ((TextView) mt_type.getChildAt(j)).setTextColor(getResources().getColor(R.color.color080808));
                                pcs.get(j).setIsChecked(2);
                            }
                            post_cate_id = pcs.get(i).getId();
                        }
                    }
                });
            }

            screenPop.setContentView(contentView);
        }

        screenPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                for (int i = 1; i <= rg.getChildCount(); i++){
                    if(tag == i){
                        ((RadioButton)rg.getChildAt(i - 1)).setChecked(true);
                    }else {
                        ((RadioButton)rg.getChildAt(i - 1)).setChecked(false);
                    }
                }
            }
        });

        //显示PopupWindow
        screenPop.showAsDropDown(rg);
    }

    //ArrayList<RedEnvelopeSetting> pricedatas = new ArrayList<>();
    private void showPricePop() {
       /* View contentView = LayoutInflater.from(SearchActivity.this).inflate(R.layout.searchpopuplayout, null);
        if(pricePopRecyclerview ==  null){
            pricePopRecyclerview = contentView.findViewById(R.id.recyclerview);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);//纵向线性布局

            pricePopRecyclerview.setLayoutManager(layoutManager);
            pricedatas.add(new RedEnvelopeSetting("由低到高", 2));
            pricedatas.add(new RedEnvelopeSetting("由高到低", 2));
            priceAdapter = new SearchAdapter(pricedatas, this);
            pricePopRecyclerview.setAdapter(priceAdapter);

            priceAdapter.setOnItemClickListener(new com.yjyc.zhoubian.adapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {*/
                    setChecked();
                    tag = 4;
                    order = "由低到高";
                    /*pricedatas.get(position).isChecked = 1;
                    priceAdapter.notifyDataSetChanged();
                    pricePop.dismiss();*/
                    if(!ProgressDialog.isShowing()){
                        ProgressDialog.showDialog(SearchActivity.this);
                    }
                    page = 1;
                    searchPost();
               /* }

                @Override
                public void onLongClick(int position) {

                }

                @Override
                public void onDeleteClick(ImageView iv_delete, boolean isDown, int[] position) {

                }
            });
        }else {
            priceAdapter.notifyDataSetChanged();
        }
        if(pricePop == null){
            pricePop = new PopupWindow(contentView,
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            pricePop.setContentView(contentView);
        }

        pricePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {*/
                for (int i = 1; i <= rg.getChildCount(); i++){
                    if(tag == i){
                        ((RadioButton)rg.getChildAt(i - 1)).setChecked(true);
                    }else {
                        ((RadioButton)rg.getChildAt(i - 1)).setChecked(false);
                    }
                }
           /* }
        });

        //显示PopupWindow
        pricePop.showAsDropDown(rg);*/
    }

    private void setChecked() {
        /*for (RedEnvelopeSetting red : pricedatas){
            red.isChecked = 2;
        }

        for (RedEnvelopeSetting red : distancedatas){
            red.isChecked = 2;
        }

        for (RedEnvelopeSetting red : timedatas){
            red.isChecked = 2;
        }*/
    }

    //final ArrayList<RedEnvelopeSetting> timedatas = new ArrayList<>();
    private void showCreateTimePop() {
        /*View contentView = LayoutInflater.from(SearchActivity.this).inflate(R.layout.searchpopuplayout, null);
        if(createTimeRecyclerview ==  null){
            createTimeRecyclerview = contentView.findViewById(R.id.recyclerview);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);//纵向线性布局

            createTimeRecyclerview.setLayoutManager(layoutManager);
            timedatas.add(new RedEnvelopeSetting("时间由现在到以前", 2));
            timedatas.add(new RedEnvelopeSetting("时间由以前到到现在", 2));
            createTimeAdapter = new SearchAdapter(timedatas, this);
            createTimeRecyclerview.setAdapter(createTimeAdapter);

            createTimeAdapter.setOnItemClickListener(new com.yjyc.zhoubian.adapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {*/
                    setChecked();
                    tag = 3;
                    order = "时间由现在到以前";
                    //timedatas.get(position).isChecked = 1;
                    /*createTimeAdapter.notifyDataSetChanged();
                    createTimePop.dismiss();*/
                    if(!ProgressDialog.isShowing()){
                        ProgressDialog.showDialog(SearchActivity.this);
                    }
                    page = 1;
                    searchPost();
                /*}

                @Override
                public void onLongClick(int position) {

                }

                @Override
                public void onDeleteClick(ImageView iv_delete, boolean isDown, int[] position) {

                }
            });
        }else {
            createTimeAdapter.notifyDataSetChanged();
        }
        if(createTimePop == null){
            createTimePop = new PopupWindow(contentView,
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            createTimePop.setContentView(contentView);
        }*/

        /*createTimePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {*/
                for (int i = 1; i <= rg.getChildCount(); i++){
                    if(tag == i){
                        ((RadioButton)rg.getChildAt(i - 1)).setChecked(true);
                    }else {
                        ((RadioButton)rg.getChildAt(i - 1)).setChecked(false);
                    }
                }
           /* }
        });

        //显示PopupWindow
        createTimePop.showAsDropDown(rg);*/
    }
    //ArrayList<RedEnvelopeSetting> distancedatas = new ArrayList<>();
    private void showDistancePop() {
       /* View contentView = LayoutInflater.from(SearchActivity.this).inflate(R.layout.searchpopuplayout, null);
        if(distanceRecyclerview ==  null){
            distanceRecyclerview = contentView.findViewById(R.id.recyclerview);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);//纵向线性布局

            distanceRecyclerview.setLayoutManager(layoutManager);

            distancedatas.add(new RedEnvelopeSetting("由远到近", 2));
            distancedatas.add(new RedEnvelopeSetting("由近到远", 2));
            distanceAdapter = new SearchAdapter(distancedatas, this);
            distanceRecyclerview.setAdapter(distanceAdapter);

            distanceAdapter.setOnItemClickListener(new com.yjyc.zhoubian.adapter.OnItemClickListener() {
                @Override
                public void onClick(int position) {*/
                    setChecked();
                    tag = 2;
                    order = "由近到远";//distancedatas.get(position).title;
                    //distancedatas.get(position).isChecked = 1;
                    //distanceAdapter.notifyDataSetChanged();
                    //distancePop.dismiss();
                    if(!ProgressDialog.isShowing()){
                        ProgressDialog.showDialog(SearchActivity.this);
                    }
                    page = 1;
                    searchPost();
                /*}

                @Override
                public void onLongClick(int position) {

                }

                @Override
                public void onDeleteClick(ImageView iv_delete, boolean isDown, int[] position) {

                }
            });
        }else {
            distanceAdapter.notifyDataSetChanged();
        }*/
        /*if(distancePop == null){
            distancePop = new PopupWindow(contentView,
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
            distancePop.setContentView(contentView);
        }*/

        /*distancePop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {*/
                for (int i = 1; i <= rg.getChildCount(); i++){
                    if(tag == i){
                        ((RadioButton)rg.getChildAt(i - 1)).setChecked(true);
                    }else {
                        ((RadioButton)rg.getChildAt(i - 1)).setChecked(false);
                    }
                }
            /*}
        });

        //显示PopupWindow
        distancePop.showAsDropDown(rg);*/
    }

    private void searchPost() {
        String keyWord = et_keyWord.getText().toString().trim();
        String str = "";
        if("由远到近".equals(order)){
            str = "distance desc";
        }else if("由近到远".equals(order)){
            str = "distance asc";
        }else if("由高到低".equals(order)){
            str = "price desc";
        }else if("由低到高".equals(order)){
            str = "price asc";
        }else if("时间由现在到以前".equals(order)){
            str = "create_time desc";
        }else if("时间由以前到到现在".equals(order)){
            str = "create_time asc";
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("listRows", "10");
        params.put("page", page + "");
        params.put("keyWord", keyWord);
        if(!StringUtils.isEmpty(str)){
            params.put("order", str);
        }else {
            params.put("distance", distance == -1 ? "" : distance + "");
            params.put("dayTime", dayTime == -1 ? "" : dayTime + "");
            params.put("userGroup", userGroupsId == -1 ? "" : userGroupsId + "");
            params.put("postCateId", post_cate_id == -1 ? "" : post_cate_id + "");
        }
        if(BaseApplication.myLocation != null){
            params.put("lat", "" + BaseApplication.myLocation.getLatitude());
            params.put("lon", "" + BaseApplication.myLocation.getLongitude());
        }
        Login login = Hawk.get("LoginModel");
        if(login != null){
            params.put("uid", ("" + login.uid));
            params.put("token", ("" + login.token));
        }
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.SEARCHPOST)
                .params(params)
//                .addParams("listRows", "10")
//                .addParams("page", page + "")
//                .addParams("order", str)
//                .addParams("distance", distance == -1 ? "" : distance + "")
//                .addParams("dayTime", dayTime == -1 ? "" : dayTime + "")
//                .addParams("userGroup", userGroupsId == -1 ? "" : userGroupsId + "")
//                .addParams("postCateId", post_cate_id == -1 ? "" : post_cate_id + "")
                .execute(new AbsJsonCallBack<SearchPostModel, SearchPosts>() {


                    @Override
                    public void onSuccess(SearchPosts body) {
                        if(body.list == null ){
                            ToastUtils.showShort("网络异常,请稍后重试" );
                            return;
                        }
                        if(body.list.size() < 1){
                            ll_no_data.setVisibility(View.VISIBLE);
                            ll.setVisibility(View.GONE);
                            return;
                        }else {
                            ll_no_data.setVisibility(View.GONE);
                            ll.setVisibility(View.VISIBLE);
                        }
                        SearchActivity.this.body = body;
                        datas = (ArrayList<SearchPosts.SearchPost>) body.list;
                        if(page == 1){
                            myAdapter.refresh(datas);
                        }else {
                            myAdapter.add(datas);
                        }
                        finishRefresh();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        ToastUtils.showShort(StringUtils.isEmpty(errorMsg) ? "网络异常,请稍后重试" : errorMsg);
                        finishRefresh();
                    }

                    @Override
                    public void onFinish() {
                        ProgressDialog.dismiss();
                    }
                });
    }

    public void finishRefresh() {
        if(refreshLayout != null){
            refreshLayout.finishRefresh();
            refreshLayout.finishLoadmore();
        }
    }

    public interface OnItemClickListener{
        void onClick( int position);
        void onLongClick( int position);
    }

    public class MyAdapter2 extends RecyclerView.Adapter<MyAdapter2.ViewHolder> implements OnItemClickListener{
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
            public ViewHolder(View itemView) {
                super(itemView);
                myView = itemView;
            }
        }

//        public MyAdapter(List<ItemBean> list){
//            this.mList = list;
//        }
        @Override
        public MyAdapter2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_FOOTER_VIEW) {
  /*这里返回的是FooterView*/
                final View footerView = LayoutInflater.from(mContext).inflate(R.layout.adapter_foot_view, parent, false);
                return new ViewHolder(footerView);
            }
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_search_item,null);
            final MyAdapter2.ViewHolder holder = new MyAdapter2.ViewHolder(view);
            return holder;
        }

        //将数据绑定到控件上
        @Override
        public void onBindViewHolder(MyAdapter2.ViewHolder holder, final int position) {
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
        }

        @Override
        public int getItemCount() {
            return 20;
        }


        public static final int TYPE_FOOTER_VIEW = 1;
        @Override
        public int getItemViewType(int position) {
     /*当position是最后一个的时候，也就是比list的数量多一个的时候，则表示FooterView*/
//            if (position + 1 == items.size() + 1) {
//                return TYPE_FOOTER_VIEW;
//            }
            if(position == 19){
                return TYPE_FOOTER_VIEW;
            }
            return super.getItemViewType(position);
        }

//        //下面两个方法提供给页面刷新和加载时调用
//        public void add(List<TCVideoInfo> addMessageList) {
//            //增加数据
//            int position = datas.size();
//            datas.addAll(position, addMessageList);
//            notifyItemInserted(position);
//        }
//
//        public void refresh(List<TCVideoInfo> newList) {
//            //刷新数据
//            datas.clear();
//            datas.addAll(newList);
//            notifyDataSetChanged();
//        }
    }
    MyAdapter2 myAdapter2;
    private void setPullRefresher(){
        refreshLayout.setEnableLoadmore(false);
        //设置 Header 为 MaterialHeader
        refreshLayout.setRefreshHeader(new MaterialHeader(this));
        //设置 Footer 为 经典样式
        refreshLayout.setRefreshFooter(new ClassicsFooter(this));

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                searchPost();
            }
        });
        refreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                if(body != null){
                    if(body.hasNextPages){
                        page++;
                        searchPost();
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