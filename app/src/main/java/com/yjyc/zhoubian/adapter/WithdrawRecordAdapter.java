package com.yjyc.zhoubian.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.RechargeLog;
import com.yjyc.zhoubian.model.WithdrawLog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WithdrawRecordAdapter extends RecyclerView.Adapter<WithdrawRecordAdapter.ViewHolder>{

    private Context mContext;
    private List<WithdrawLog.WithdrawRecord> records;

    public WithdrawRecordAdapter(Context mContext, List<WithdrawLog.WithdrawRecord> records) {
        this.mContext = mContext;
        this.records = records;
    }

    @Override
    public WithdrawRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_withdraw_record,null);
        WithdrawRecordAdapter.ViewHolder holder = new WithdrawRecordAdapter.ViewHolder(view);
        return holder;
    }

    //将数据绑定到控件上
    @Override
    public void onBindViewHolder(WithdrawRecordAdapter.ViewHolder holder, final int position) {
        WithdrawLog.WithdrawRecord record = records.get(position);
        if(record.check_status == 1){
            holder.withdrawStatus.setText("提现成功");
            holder.withdrawStatus.setTextColor(Color.parseColor("#444444"));
        }else if(record.check_status == 2){
            holder.withdrawStatus.setText("提现失败");
            holder.withdrawStatus.setTextColor(Color.parseColor("#d53c3c"));
        }else{
            holder.withdrawStatus.setText("待审核");
            holder.withdrawStatus.setTextColor(Color.parseColor("#3390E7"));
        }
        if (record.create_time != null){
            holder.withdrawTime.setText(record.create_time);
        }else{
            holder.withdrawTime.setText("");
        }
        holder.money.setText((record.money + ""));
    }

    @Override
    public int getItemCount() {
        return (records == null ? 0 : records.size());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.withdraw_status)
        TextView withdrawStatus;
        @BindView(R.id.withdraw_time)
        TextView withdrawTime;
        @BindView(R.id.money)
        TextView money;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public void addData(List<WithdrawLog.WithdrawRecord> datas){
        if(datas == null || datas.size() <= 0){
            return;
        }
        synchronized (records){
            int position = records.size();
            records.addAll(datas);
            notifyItemRangeInserted(position, datas.size());
        }
    }

    public void clearDatas(){
        synchronized (records){
            records.clear();
            notifyDataSetChanged();
        }
    }

}
