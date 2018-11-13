package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.BarUtils;
import com.yjyc.zhoubian.R;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 宝典详情
 * Created by Administrator on 2018/10/10/010.
 */

public class ValuableBookDetailActivity extends BaseActivity {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    private Context mContext;
    MyAdapter myAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valuable_book_detail);
        mContext = this;
        ButterKnife.bind(this);
        initView();
        initDate();
    }

    private void initView() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("宝典详情", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        myAdapter = new MyAdapter();
    }

    private void initDate(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);//纵向线性布局

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);//纵向线性布局

    }

    public interface OnItemClickListener{
        void onClick( int position);
        void onLongClick( int position);
    }

    public  class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnItemClickListener{
        private OnItemClickListener mOnItemClickListener;
        public static final int TYPE_ONE = 0;
        public static final int TYPE_TWO = 1;
        public static final int TYPE_THREE = 2;//三种不同的布局

        public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
            this. mOnItemClickListener=onItemClickListener;
        }

        @Override
        public void onClick(int position) {

        }

        @Override
        public void onLongClick(int position) {

        }

        public class MyViewHolderOne extends RecyclerView.ViewHolder {

            public MyViewHolderOne(View itemView) {
                super(itemView);
            }
        }

        public class MyViewHolderTwo extends RecyclerView.ViewHolder {

            public MyViewHolderTwo(View itemView) {
                super(itemView);
            }
        }

        public class MyFooterHolder extends RecyclerView.ViewHolder {


            public MyFooterHolder(View itemView) {
                super(itemView);
            }
        }
        @Override
        public int getItemViewType(int position) {
            if(position % 3 == 1){
                return TYPE_TWO;//第一种布局
            }else {
                return TYPE_ONE;//第一种布局
            }
        }
//        public MyAdapter(List<ItemBean> list){
//            this.mList = list;
//        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_ONE:
                    return new MyViewHolderOne(LayoutInflater.from(parent.getContext()).inflate(R.layout
                            .fragment_post_details_item_img0, parent, false));
                case TYPE_TWO:
                    return new MyViewHolderTwo(LayoutInflater.from(parent.getContext()).inflate(R.layout.
                            fragment_post_details_item_img1, parent, false));
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
            } else if (holder instanceof MyFooterHolder) {
                bindTypeThree((MyFooterHolder) holder, position);
            }

            if( mOnItemClickListener!= null){
                holder.itemView.setOnClickListener( new View.OnClickListener() {
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

        private void bindTypeOne(MyViewHolderOne holderOne, int position) {
        }

        private void bindTypeTwo(MyViewHolderTwo holderTwo, int position) {
        }

        private void bindTypeThree(MyFooterHolder holder, int position) {  //在其中镶嵌一个RecyclerView

        }

        @Override
        public int getItemCount() {
            return 4;
        }


//        //下面两个方法提供给页面刷新和加载时调用
//        public void add(List<PlanetsActivityModel.PlanetsActivity> addMessageList) {
//            //增加数据
//            int position = mList.size();
//            mList.addAll(position, addMessageList);
//            notifyItemInserted(position);
//        }
//
//        public void refresh(List<PlanetsActivityModel.PlanetsActivity> newList) {
//            //刷新数据
//            mList.removeAll(mList);
//            mList.addAll(newList);
//            notifyDataSetChanged();
//        }
    }
}