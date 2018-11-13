package com.yjyc.zhoubian.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.ui.activity.MyPublishActivity;
import com.yjyc.zhoubian.ui.activity.PublishValuableBookActivity;
import com.yjyc.zhoubian.ui.activity.ValuableBookDetailActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 宝典
 * Created by Administrator on 2018/10/9/009.
 */

public class ValuableBookFragment extends Fragment{

    @BindView(R.id.recyclerview)
    public RecyclerView recyclerview;

    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_valuable_book, container, false);

        unbinder = ButterKnife.bind(this, view);


        initViews();
        initDate();
        return view;
    }

    private void initViews() {
        myAdapter = new MyAdapter();
    }

    MyAdapter myAdapter;
    private void initDate(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());//纵向线性布局

        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setAdapter(myAdapter);

        myAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int position) {
                startActivity(new Intent(getActivity(), ValuableBookDetailActivity.class));
            }

            @Override
            public void onLongClick(int position) {

            }
        });
    }

    @OnClick(R.id.tv_publish_valuable_book)
    public void tv_publish_valuable_book(){
        startActivity(new Intent(getActivity(), PublishValuableBookActivity.class));
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

        public class MyViewHolderThree extends RecyclerView.ViewHolder {


            public MyViewHolderThree(View itemView) {
                super(itemView);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(position % 3 == 1){
                return TYPE_TWO;//第一种布局
            }else if(position % 3 == 2){
                return TYPE_THREE;//第一种布局
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
                            .fragment_valuable_book_item_img0, parent, false));
                case TYPE_TWO:
                    return new MyViewHolderTwo(LayoutInflater.from(parent.getContext()).inflate(R.layout.
                            fragment_valuable_book_item_img1, parent, false));
                case TYPE_THREE:
                    return new MyViewHolderThree(LayoutInflater.from(parent.getContext()).inflate(R.layout.
                            fragment_valuable_book_item_img3, parent, false));
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

        private void bindTypeThree(MyViewHolderThree holder, int position) {  //在其中镶嵌一个RecyclerView

        }

        @Override
        public int getItemCount() {
            return 20;
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

    public interface OnItemClickListener{
        void onClick( int position);
        void onLongClick( int position);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}