package com.yjyc.zhoubian.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.BarUtils;
import com.yjyc.zhoubian.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 充值记录
 * Created by Administrator on 2018/10/11/011.
 */

public class RechargeRecordActivity extends BaseActivity {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge_record);
        mContext = this;
        ButterKnife.bind(this);
        initView();
        initDate();
    }

    private void initView() {
        BarUtils.setStatusBarColor(this, getResources().getColor(R.color.main_bg));
        initTitleBar("充值记录", new View.OnClickListener() {
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
            public ViewHolder(View itemView) {
                super(itemView);
                myView = itemView;
            }
        }

//        public MyAdapter(List<ItemBean> list){
//            this.mList = list;
//        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recharge_record_item,null);
            final MyAdapter.ViewHolder holder = new MyAdapter.ViewHolder(view);
            return holder;
        }

        //将数据绑定到控件上
        @Override
        public void onBindViewHolder(MyAdapter.ViewHolder holder, final int position) {
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

        @Override
        public int getItemViewType(int position) {
            return position;
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
    MyAdapter myAdapter;
}