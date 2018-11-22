package com.yjyc.zhoubian.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.orhanobut.hawk.Hawk;
import com.orhanobut.logger.Logger;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.MainActivitys;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.PullUserBlack;
import com.yjyc.zhoubian.model.PullUserBlackModel;
import com.yjyc.zhoubian.model.SearchPosts;
import com.yjyc.zhoubian.ui.activity.LoginActivity;
import com.yjyc.zhoubian.ui.activity.ReportActivity;
import com.yjyc.zhoubian.ui.activity.SearchActivity;
import com.yjyc.zhoubian.utils.DialogUtil;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/10/13/013.
 */

public  class CardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemClickListener{
    private ArrayList<SearchPosts.SearchPost> datas = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    public static final int TYPE_ONE = 0;
    public static final int TYPE_TWO = 1;
    public static final int TYPE_THREE = 2;//三种不同的布局
    public static final int TYPE_ZERO= 3;
    RequestOptions options;
    private PopupWindow popWindow;
    private int offsetX;
    private int offsetY;
    public CardAdapter(ArrayList<SearchPosts.SearchPost> datas, Context mCcontext) {
        this.datas = datas;
        this.mContext = mCcontext;
        options = new RequestOptions()
                .centerCrop().placeholder( R.drawable.img_bg).error(R.drawable.img_bg);
    }

    public CardAdapter() {
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this. mOnItemClickListener=onItemClickListener;
    }

    @Override
    public void onClick(int position) {

    }

    @Override
    public void onLongClick(int position) {

    }

    @Override
    public void onDeleteClick(ImageView iv_delete, boolean isDown, int[] position) {

    }

    public class MyViewHolderZero extends RecyclerView.ViewHolder {
        View myView;
        TextView tv;
        TextView downturnNum;
        ImageView shutdown;
        LinearLayout rootLl;
        public MyViewHolderZero(View itemView) {
            super(itemView);
            myView = itemView;
            tv = itemView.findViewById(R.id.tv);
            downturnNum = itemView.findViewById(R.id.downturn_num);
            shutdown = itemView.findViewById(R.id.shutdown);
            rootLl = itemView.findViewById(R.id.root_ll);
        }
    }

    public class MyViewHolderOne extends RecyclerView.ViewHolder {
        View myView;
        ImageView iv_delete;
        TextView tv_title;
        TextView tv_user_name;
        TextView tv_view;
        TextView tv_time;
        TextView tv_distance;
        ImageView iv_red_package;
        TextView tv_price;
        public MyViewHolderOne(View itemView) {
            super(itemView);
            myView = itemView;
            iv_delete = itemView.findViewById(R.id.iv_delete);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_view = itemView.findViewById(R.id.tv_view);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            iv_red_package = itemView.findViewById(R.id.iv_red_package);
            tv_price = itemView.findViewById(R.id.tv_price);
        }
    }

    public class MyViewHolderTwo extends RecyclerView.ViewHolder {
        View myView;
        ImageView iv_delete;
        TextView tv_title;
        TextView tv_user_name;
        TextView tv_view;
        TextView tv_time;
        TextView tv_distance;
        ImageView iv1;
        ImageView iv_red_package;
        TextView tv_price;
        public MyViewHolderTwo(View itemView) {
            super(itemView);
            myView = itemView;
            iv_delete = itemView.findViewById(R.id.iv_delete);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_view = itemView.findViewById(R.id.tv_view);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            iv1 = itemView.findViewById(R.id.iv1);
            iv_red_package = itemView.findViewById(R.id.iv_red_package);
            tv_price = itemView.findViewById(R.id.tv_price);
        }
    }

    public class MyViewHolderThree extends RecyclerView.ViewHolder {
        View myView;
        ImageView iv_delete;
        TextView tv_title;
        TextView tv_user_name;
        TextView tv_view;
        TextView tv_time;
        TextView tv_distance;
        ImageView iv1;
        ImageView iv2;
        ImageView iv3;
        ImageView iv_red_package;
        TextView tv_price;
        public MyViewHolderThree(View itemView) {
            super(itemView);
            myView = itemView;
            iv_delete = itemView.findViewById(R.id.iv_delete);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_user_name = itemView.findViewById(R.id.tv_user_name);
            tv_view = itemView.findViewById(R.id.tv_view);
            tv_time = itemView.findViewById(R.id.tv_time);
            tv_distance = itemView.findViewById(R.id.tv_distance);
            iv1 = itemView.findViewById(R.id.iv1);
            iv2 = itemView.findViewById(R.id.iv2);
            iv3 = itemView.findViewById(R.id.iv3);
            iv_red_package = itemView.findViewById(R.id.iv_red_package);
            tv_price = itemView.findViewById(R.id.tv_price);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 || (position % 21 == 0)){
            return TYPE_ZERO;//第一种布局
        }
        SearchPosts.SearchPost search = datas.get(position == 0 ? 0 : position -   (position/21 + 1));
        if(search.pic != null && search.pic.size() > 1){
            return TYPE_THREE;
        }else if(search.pic != null && search.pic.size() == 1){
            return TYPE_TWO;
        }else {
            return TYPE_ONE;
        }
    }
//        public MyAdapter(List<ItemBean> list){
//            this.mList = list;
//        }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ZERO:
                return new MyViewHolderZero(LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .fragment_main_item_img, parent, false));
            case TYPE_ONE:
                return new MyViewHolderOne(LayoutInflater.from(parent.getContext()).inflate(R.layout
                        .fragment_main_item_img0, parent, false));
            case TYPE_TWO:
                return new MyViewHolderTwo(LayoutInflater.from(parent.getContext()).inflate(R.layout.
                        fragment_main_item_img1, parent, false));
            case TYPE_THREE:
                return new MyViewHolderThree(LayoutInflater.from(parent.getContext()).inflate(R.layout.
                        fragment_main_item_img3, parent, false));
        }
        return null;

    }

    //将数据绑定到控件上
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolderOne) {
            bindTypeOne((MyViewHolderOne) holder, position);
        } else if (holder instanceof MyViewHolderTwo) {
            bindTypeTwo((MyViewHolderTwo) holder, position);
        } else if (holder instanceof MyViewHolderThree) {
            bindTypeThree((MyViewHolderThree) holder, position);
        }else if (holder instanceof MyViewHolderZero) {
            bindTypeZero((MyViewHolderZero) holder, position);
        }

        if( mOnItemClickListener!= null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
            holder. itemView.setOnLongClickListener( new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onLongClick(position);
                    return false;
                }
            });
        }
    }

    private void bindTypeZero(final MyViewHolderZero holderZero, int position) {
        holderZero.tv.setText((position / 21) * 20 + "m深");
        if(position == 0){
            holderZero.tv.setVisibility(View.INVISIBLE);
        }else{
            holderZero.tv.setVisibility(View.VISIBLE);
        }
        holderZero.downturnNum.setOnClickListener(v->{
            if(mContext instanceof MainActivitys){
                ((MainActivitys)mContext).postDownturn((position == 0 ? 0 : position - (1+ position / 21)), 20);
            }else if(mContext instanceof SearchActivity){
                ((SearchActivity)mContext).postDownturn((position == 0 ? 0 : position - (1 + position / 21)), 20);
            }
        });
        holderZero.shutdown.setOnClickListener(v->{
            if(mContext == null || holderZero.rootLl == null){
                return;
            }
            DialogUtil.getInstance().postShutDownPop(mContext, holderZero.rootLl);
            DialogUtil.getInstance().setDialogClick(str -> {
                int num = 0;
                int currentPos = (position == 0 ? 0 : position - ((1 + position / 21)));
                try{
                    num = Integer.parseInt(str);
                }catch(Exception e){}
                if(num <= 0){
                    return;
                }
                if(mContext instanceof MainActivitys){
                    ((MainActivitys)mContext).postDownturn(currentPos, num);
                }else if(mContext instanceof SearchActivity){
                    ((SearchActivity)mContext).postDownturn(currentPos, num);
                }
            });
        });
    }

    private void bindTypeOne(final MyViewHolderOne holderOne, int itemPosition) {
        holderOne.iv_delete.setOnClickListener(view -> {
            int[] position = new int[2];
            holderOne.iv_delete.getLocationInWindow(position);
            Logger.i("getLocationInWindow:" + position[0] + "," + position[1]);
            Logger.i("height:" + ScreenUtils.getScreenHeight());
            boolean isDown;
            if(position[1] < (ScreenUtils.getScreenHeight() / 2)){
                isDown = true;
            }else {
                isDown = false;
            }
            showPopWindow(holderOne.iv_delete, isDown, position, itemPosition);
        });
        SearchPosts.SearchPost sp = datas.get(itemPosition -  (1 + itemPosition/21));
        if(!StringUtils.isEmpty(sp.title)){
            holderOne.tv_title.setText(sp.title);
        }

        if(!StringUtils.isEmpty(sp.user_name)){
            holderOne.tv_user_name.setText(sp.user_name);
        }

        holderOne.tv_view.setText(sp.view + "阅读");

        if(!StringUtils.isEmpty(sp.time)){
            holderOne.tv_time.setText(sp.time);
        }

        if(!StringUtils.isEmpty(sp.distance)){
            holderOne.tv_distance.setText("距离" + sp.distance);
        }

        if(sp.red_package_rule == 1){
            holderOne.iv_red_package.setVisibility(View.VISIBLE);
        }else {
            holderOne.iv_red_package.setVisibility(View.INVISIBLE);
        }

        if(!StringUtils.isEmpty(sp.price)){
            holderOne.tv_price.setText(sp.price);
        }
    }

    private void showPopWindow(ImageView iv_delete, boolean isDown, int[] location, int itemPosition) {
        View contentView;
        if(isDown){
            contentView = LayoutInflater.from(mContext).inflate(R.layout.deletepopupdownlayout, null);

        }else {
            contentView = LayoutInflater.from(mContext).inflate(R.layout.deletepopuptoplayout, null);
        }
        RelativeLayout rl_dismiss = contentView.findViewById(R.id.rl_dismiss);
        rl_dismiss.setOnClickListener(view -> popWindow.dismiss());

        TextView tv_report = contentView.findViewById(R.id.tv_report);
        TextView notInterested = contentView.findViewById(R.id.not_interested);
        TextView pullBlack = contentView.findViewById(R.id.pull_black);
        tv_report.setOnClickListener(view -> {
            popWindow.dismiss();
            SearchPosts.SearchPost post = getSearchPost(itemPosition);
            if(post != null){
                Intent intent = new Intent(mContext, ReportActivity.class);
                intent.putExtra("report_uid", post.user_id);
                mContext.startActivity(intent);
            }
        });
        notInterested.setOnClickListener(v->{
            synchronized (datas){
                datas.remove(itemPosition -  (1 + itemPosition/21));
                notifyItemRemoved(itemPosition);
                notifyItemRangeChanged(0, datas.size());
            }
            popWindow.dismiss();
        });
        pullBlack.setOnClickListener(v->{
            popWindow.dismiss();
            SearchPosts.SearchPost sp = datas.get(itemPosition -  (1 + itemPosition/21));
            pullBlackUser(sp.user_id);
        });

        popWindow = new PopupWindow(contentView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        popWindow.setBackgroundDrawable(new BitmapDrawable());
        popWindow.setContentView(contentView);
        contentView.measure(makeDropDownMeasureSpec(popWindow.getWidth()),
                makeDropDownMeasureSpec(popWindow.getHeight()));
        popWindow.setOnDismissListener(() -> {
            if(mContext instanceof MainActivitys){
                ((MainActivitys)mContext).rl_bg.setVisibility(View.GONE);
            }else if(mContext instanceof SearchActivity){
                ((SearchActivity)mContext).rl_bg.setVisibility(View.GONE);
            }
        });

        //显示PopupWindow
        if(isDown){
            offsetX = Math.abs(popWindow.getContentView().getMeasuredWidth()-iv_delete.getWidth()) / 2;
            popWindow.showAsDropDown(iv_delete);
        }else {
            offsetX = Math.abs(popWindow.getContentView().getMeasuredWidth()-iv_delete.getWidth()) / 2;
            PopupWindowCompat.showAsDropDown(popWindow, iv_delete, offsetX, 0, Gravity.START);
        }
        if(mContext instanceof MainActivitys){
            ((MainActivitys)mContext).rl_bg.setVisibility(View.VISIBLE);
        }else if(mContext instanceof SearchActivity){
            ((SearchActivity)mContext).rl_bg.setVisibility(View.VISIBLE);
        }
    }

    private void pullBlackUser(int userId) {
        if(!Hawk.contains("LoginModel")){
            Toast.makeText(mContext, "请先登录", Toast.LENGTH_SHORT).show();
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            return;
        }
        Login loginModel = Hawk.get("LoginModel");
        if(loginModel.uid == userId){
            return;
        }
        LoadingDialog.showLoading(mContext);
        OkhttpUtils.with()
                .post()
                .url(HttpUrl.PULLUSERBLACK)
                .addParams("uid", ("" + loginModel.uid))
                .addParams("token", loginModel.token)
                .addParams("black_uid", ("" + userId))
                .execute(new AbsJsonCallBack<PullUserBlackModel, PullUserBlack>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        LoadingDialog.closeLoading();
                        Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onSuccess(PullUserBlack body) {
                        LoadingDialog.closeLoading();
                        Toast.makeText(mContext, "作者已被拉黑", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressWarnings("ResourceType")
    private static int makeDropDownMeasureSpec(int measureSpec) {
        int mode;
        if (measureSpec == ViewGroup.LayoutParams.WRAP_CONTENT) {
            mode = View.MeasureSpec.UNSPECIFIED;
        } else {
            mode = View.MeasureSpec.EXACTLY;
        }
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec), mode);
    }

    private void bindTypeTwo(final MyViewHolderTwo holderTwo, int itemPosition) {
        holderTwo.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] position = new int[2];
                holderTwo.iv_delete.getLocationInWindow(position);
                Logger.i("getLocationInWindow:" + position[0] + "," + position[1]);
                Logger.i("height:" + ScreenUtils.getScreenHeight());
                boolean isDown;
                if(position[1] < (ScreenUtils.getScreenHeight() / 2)){
                    isDown = true;
                }else {
                    isDown = false;
                }
                showPopWindow(holderTwo.iv_delete, isDown, position, itemPosition);
            }
        });

        SearchPosts.SearchPost sp = datas.get(itemPosition -  (1 + itemPosition/21));
        if(!StringUtils.isEmpty(sp.title)){
            holderTwo.tv_title.setText(sp.title);
        }

        if(!StringUtils.isEmpty(sp.user_name)){
            holderTwo.tv_user_name.setText(sp.user_name);
        }

        holderTwo.tv_view.setText(sp.view + "阅读");

        if(!StringUtils.isEmpty(sp.time)){
            holderTwo.tv_time.setText(sp.time);
        }

        if(!StringUtils.isEmpty(sp.distance)){
            holderTwo.tv_distance.setText("距离" + sp.distance);
        }

        if(!StringUtils.isEmpty(sp.pic.get(0))){
            Glide.with(mContext)
                    .load(sp.pic.get(0))
                    .apply(options)
                    .into(holderTwo.iv1);
        }

        if(sp.red_package_rule == 1){
            holderTwo.iv_red_package.setVisibility(View.VISIBLE);
        }else {
            holderTwo.iv_red_package.setVisibility(View.INVISIBLE);
        }

        if(!StringUtils.isEmpty(sp.price)){
            holderTwo.tv_price.setText(sp.price);
        }
    }

    private void bindTypeThree(final MyViewHolderThree holder, int itemPosition) {  //在其中镶嵌一个RecyclerView
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] position = new int[2];
                holder.iv_delete.getLocationInWindow(position);
                boolean isDown;
                if(position[1] < (ScreenUtils.getScreenHeight() / 2)){
                    isDown = true;
                }else {
                    isDown = false;
                }
                showPopWindow(holder.iv_delete, isDown, position, itemPosition);
            }
        });

        SearchPosts.SearchPost sp = datas.get(itemPosition -  (1 + itemPosition/21));
        if(!StringUtils.isEmpty(sp.title)){
            holder.tv_title.setText(sp.title);
        }

        if(!StringUtils.isEmpty(sp.user_name)){
            holder.tv_user_name.setText(sp.user_name);
        }

        holder.tv_view.setText(sp.view + "阅读");

        if(!StringUtils.isEmpty(sp.time)){
            holder.tv_time.setText(sp.time);
        }

        if(!StringUtils.isEmpty(sp.distance)){
            holder.tv_distance.setText("距离" + sp.distance);
        }

        if(!StringUtils.isEmpty(sp.pic.get(0))){
            Glide.with(mContext)
                    .load(sp.pic.get(0))
                    .apply(options)
                    .into(holder.iv1);
        }

        if(!StringUtils.isEmpty(sp.pic.get(1))){
            Glide.with(mContext)
                    .load(sp.pic.get(1))
                    .apply(options)
                    .into(holder.iv2);
        }

        if(sp.pic.size() > 2 && !StringUtils.isEmpty(sp.pic.get(2))){
            holder.iv3.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(sp.pic.get(2))
                    .apply(options)
                    .into(holder.iv3);
        }else{
            holder.iv3.setVisibility(View.INVISIBLE);
        }

        if(sp.red_package_rule == 1){
            holder.iv_red_package.setVisibility(View.VISIBLE);
        }else {
            holder.iv_red_package.setVisibility(View.INVISIBLE);
        }

        if(!StringUtils.isEmpty(sp.price)){
            holder.tv_price.setText(sp.price);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size() + 1 + datas.size()/20 ;
    }


    //下面两个方法提供给页面刷新和加载时调用
    public void add(List<SearchPosts.SearchPost> addMessageList) {
        //增加数据
        int position = datas.size();
        datas.addAll(position, addMessageList);

        notifyItemInserted(getItemCount());
    }

    public void clearDatas(){
        synchronized (datas){
            datas.clear();
            notifyDataSetChanged();
        }
    }

    public void refresh(List<SearchPosts.SearchPost> newList) {
        //刷新数据
        datas.clear();
        datas.addAll(newList);
        notifyDataSetChanged();
    }

    public int getPostId(int position) {
        if(position == 0 || (position % 21 == 0)){
            return -2;
        }
        SearchPosts.SearchPost search = datas.get(position -  (1 + position/21));
        if(search != null){
            return search.id;
        }
        return -1;
    }

    public SearchPosts.SearchPost getSearchPost(int position) {
        if(position == 0 || (position % 21 == 0)){
            return null;
        }
        SearchPosts.SearchPost search = datas.get(position -  (1 + position/21));
        return search;
    }

}