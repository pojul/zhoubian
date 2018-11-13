package com.yjyc.zhoubian.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.RedEnvelopeSetting;
import com.yjyc.zhoubian.ui.activity.SearchActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/10/12/012.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> implements OnItemClickListener{
    private SearchActivity searchActivity;
    private ArrayList<RedEnvelopeSetting> datas;
    private OnItemClickListener onItemClickListener;

    public SearchAdapter(ArrayList<RedEnvelopeSetting> datas, SearchActivity searchActivity) {
        this.datas = datas;
        this.searchActivity = searchActivity;
    }


    public void setOnItemClickListener(OnItemClickListener onItemClickListener ){
        this. onItemClickListener =onItemClickListener;
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

    class ViewHolder extends RecyclerView.ViewHolder{
        View myView;
        TextView tv;
        public ViewHolder(View itemView) {
            super(itemView);
            myView = itemView;
            tv = itemView.findViewById(R.id.tv);
        }
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_search_distance_item,null);
        final SearchAdapter.ViewHolder holder = new SearchAdapter.ViewHolder(view);
        return holder;
    }

    //将数据绑定到控件上
    @Override
    public void onBindViewHolder(SearchAdapter.ViewHolder holder, final int position) {
        holder.tv.setText(datas.get(position).title);

        if(datas.get(position).isChecked == 1){
            holder.tv.setTextColor(searchActivity.getResources().getColor(R.color.main_bg));
        }else {
            holder.tv.setTextColor(searchActivity.getResources().getColor(R.color.color080808));
        }
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
        return datas.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

}