package com.yjyc.zhoubian.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.widget.PopupWindowCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
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
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.PullUserBlack;
import com.yjyc.zhoubian.model.PullUserBlackModel;
import com.yjyc.zhoubian.model.SearchPosts;
import com.yjyc.zhoubian.ui.activity.BaseActivity;
import com.yjyc.zhoubian.ui.activity.LoginActivity;
import com.yjyc.zhoubian.ui.activity.MyFootprintActivity;
import com.yjyc.zhoubian.ui.activity.PostDetailsActivity;
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

public  class InterestPostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<SearchPosts.SearchPost> datas = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    public static final int TYPE_ONE = 0;
    public static final int TYPE_TWO = 1;
    public static final int TYPE_THREE = 2;//三种不同的布局
    RequestOptions options;
    private PopupWindow popWindow;
    private int offsetX;
    private int offsetY;
    public InterestPostAdapter(List<SearchPosts.SearchPost> datas, Context mCcontext) {
        this.datas = datas;
        this.mContext = mCcontext;
        options = new RequestOptions()
                .centerCrop().placeholder(R.drawable.img_bg).error(R.drawable.img_bg);
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
        SearchPosts.SearchPost search = datas.get(position);
        if(search.pic != null && search.pic.size() > 1){
            return TYPE_THREE;
        }else if(search.pic != null && search.pic.size() == 1){
            return TYPE_TWO;
        }else {
            return TYPE_ONE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
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
        }

        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putInt("PostId", datas.get(position).id);
            ((BaseActivity)mContext).startActivityAni(PostDetailsActivity.class, bundle);
        });

        if( mOnItemClickListener!= null){
            holder. itemView.setOnLongClickListener( new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onLongClick(position);
                    return false;
                }
            });
        }
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
        SearchPosts.SearchPost sp = datas.get(itemPosition);
        String title= sp.title;
        if(sp.custom_post_cate != null && !sp.custom_post_cate.isEmpty()){
            title = "【" + sp.custom_post_cate + "】" + title;
        }else if(sp.post_cate_title != null && !sp.post_cate_title.isEmpty()){
            title = "【" + sp.post_cate_title + "】" + title;
        }
        String price = "";
        if(sp.price != null && !sp.price.isEmpty()){
            price = " ¥" + sp.price + "";
        }
        if(sp.price_unit != null && !sp.price_unit.isEmpty()){
            price = price + "" + sp.price_unit;
        }
        if(!price.isEmpty()){
            title = title + " " + price;
            SpannableString spannableString = new SpannableString(title);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#d53c3c")),
                    (title.length() - price.length()), title.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holderOne.tv_title.setText(spannableString);
        }else{
            holderOne.tv_title.setText(title);
        }

        if(!StringUtils.isEmpty(sp.user_name)){
            holderOne.tv_user_name.setText(sp.user_name);
        }

        holderOne.tv_view.setText(sp.view + "阅读");

        if(!StringUtils.isEmpty(sp.time)){
            holderOne.tv_time.setText(sp.time + "来过");
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
            SearchPosts.SearchPost post = datas.get(itemPosition);
            if(post != null){
                Intent intent = new Intent(mContext, ReportActivity.class);
                intent.putExtra("report_uid", post.user_id);
                mContext.startActivity(intent);
            }
        });
        notInterested.setOnClickListener(v->{
            synchronized (datas){
                datas.remove(itemPosition);
                notifyItemRemoved(itemPosition);
                notifyItemRangeChanged(0, datas.size());
            }
            popWindow.dismiss();
        });
        pullBlack.setOnClickListener(v->{
            popWindow.dismiss();
            SearchPosts.SearchPost sp = datas.get(itemPosition);
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
            }else if(mContext instanceof PostDetailsActivity){
                ((PostDetailsActivity)mContext).rl_bg.setVisibility(View.GONE);
            }else if(mContext instanceof MyFootprintActivity){
                ((MyFootprintActivity)mContext).rl_bg.setVisibility(View.GONE);
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
        }else if(mContext instanceof PostDetailsActivity){
            ((PostDetailsActivity)mContext).rl_bg.setVisibility(View.VISIBLE);
        }else if(mContext instanceof MyFootprintActivity){
            ((MyFootprintActivity)mContext).rl_bg.setVisibility(View.VISIBLE);
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
        new OkhttpUtils().with()
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

        SearchPosts.SearchPost sp = datas.get(itemPosition );
        String title= sp.title;
        if(sp.custom_post_cate != null && !sp.custom_post_cate.isEmpty()){
            title = "【" + sp.custom_post_cate + "】" + title;
        }else if(sp.post_cate_title != null && !sp.post_cate_title.isEmpty()){
            title = "【" + sp.post_cate_title + "】" + title;
        }
        String price = "";
        if(sp.price != null && !sp.price.isEmpty()){
            price = " ¥" + sp.price + "";
        }
        if(sp.price_unit != null && !sp.price_unit.isEmpty()){
            price = price + "" + sp.price_unit;
        }
        if(!price.isEmpty()){
            title = title + " " + price;
            SpannableString spannableString = new SpannableString(title);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#d53c3c")),
                    (title.length() - price.length()), title.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holderTwo.tv_title.setText(spannableString);
        }else{
            holderTwo.tv_title.setText(title);
        }

        if(!StringUtils.isEmpty(sp.user_name)){
            holderTwo.tv_user_name.setText(sp.user_name);
        }

        holderTwo.tv_view.setText(sp.view + "阅读");

        if(!StringUtils.isEmpty(sp.time)){
            holderTwo.tv_time.setText(sp.time + "来过");
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

        SearchPosts.SearchPost sp = datas.get(itemPosition);
        String title= sp.title;
        if(sp.custom_post_cate != null && !sp.custom_post_cate.isEmpty()){
            title = "【" + sp.custom_post_cate + "】" + title;
        }else if(sp.post_cate_title != null && !sp.post_cate_title.isEmpty()){
            title = "【" + sp.post_cate_title + "】" + title;
        }
        String price = "";
        if(sp.price != null && !sp.price.isEmpty()){
            price = " ¥" + sp.price + "";
        }
        if(sp.price_unit != null && !sp.price_unit.isEmpty()){
            price = price + "" + sp.price_unit;
        }
        if(!price.isEmpty()){
            title = title + " " + price;
            SpannableString spannableString = new SpannableString(title);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#d53c3c")),
                    (title.length() - price.length()), title.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.tv_title.setText(spannableString);
        }else{
            holder.tv_title.setText(title);
        }

        if(!StringUtils.isEmpty(sp.user_name)){
            holder.tv_user_name.setText(sp.user_name);
        }

        holder.tv_view.setText(sp.view + "阅读");

        if(!StringUtils.isEmpty(sp.time)){
            holder.tv_time.setText(sp.time + "来过");
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
        return (datas==null?0:datas.size());
    }


    //下面两个方法提供给页面刷新和加载时调用
    public void add(List<SearchPosts.SearchPost> addMessageList) {
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

}