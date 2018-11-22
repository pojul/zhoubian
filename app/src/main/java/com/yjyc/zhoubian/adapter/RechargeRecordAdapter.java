package com.yjyc.zhoubian.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.RechargeLog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RechargeRecordAdapter extends RecyclerView.Adapter<RechargeRecordAdapter.ViewHolder>{

    private Context mContext;
    private List<RechargeLog.RechargeRecord> records;

    public RechargeRecordAdapter(Context mContext, List<RechargeLog.RechargeRecord> records) {
        this.mContext = mContext;
        this.records = records;
    }

    @Override
    public RechargeRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recharge_record_item,null);
        RechargeRecordAdapter.ViewHolder holder = new RechargeRecordAdapter.ViewHolder(view);
        return holder;
    }

    //将数据绑定到控件上
    @Override
    public void onBindViewHolder(RechargeRecordAdapter.ViewHolder holder, final int position) {
        RechargeLog.RechargeRecord record = records.get(position);
        if(record.pay_status == 1){
            holder.payStatus.setText("充值成功");
        }else{
            holder.payStatus.setText("充值失败");
        }
        if (record.pay_time != null){
            holder.payTime.setText(record.pay_time);
        }else{
            holder.payTime.setText("");
        }
        if(record.pay_type == 1){
            holder.payType.setText("微信支付");
        }else{
            holder.payType.setText("支付宝支付");
        }
        if(record.money != null){
            holder.money.setText(record.money);
        }else{
            holder.money.setText("");
        }
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

        @BindView(R.id.pay_status)
        TextView payStatus;
        @BindView(R.id.pay_time)
        TextView payTime;
        @BindView(R.id.pay_type)
        TextView payType;
        @BindView(R.id.money)
        TextView money;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

    public void addData(List<RechargeLog.RechargeRecord> datas){
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
