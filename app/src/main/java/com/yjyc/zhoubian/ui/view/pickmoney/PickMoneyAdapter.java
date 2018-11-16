package com.yjyc.zhoubian.ui.view.pickmoney;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yjyc.zhoubian.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PickMoneyAdapter extends RecyclerView.Adapter<PickMoneyAdapter.MyViewHolder> {

    private Context mContext;
    private List<Money> datas;

    public PickMoneyAdapter(Context mContext, List<Money> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pick_money, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Money money = datas.get(position);
        holder.money.setText((money.getMoney() +  "元"));
        if(money.isSelected()){
            holder.money.setSelected(true);
        }else{
            holder.money.setSelected(false);
        }
        holder.money.setOnClickListener(v->{
            if(!money.isSelected()){
                onSelected(position);
            }else{
                onCancelSelected(position);
            }
        });
    }

    private void onCancelSelected(int position) {
        synchronized (datas){
            Money money = datas.get(position);
            money.setSelected(false);
            notifyItemChanged(position);
        }
    }

    private void onSelected(int position) {
        synchronized (datas){
            for (int i = 0; i < datas.size(); i++) {
                Money money = datas.get(i);
                if(money == null){
                    continue;
                }
                if(i == position){
                    money.setSelected(true);
                }else{
                    money.setSelected(false);
                }
            }
            notifyDataSetChanged();
        }
    }

    public int getSelectedMoney(){
        for (int i = 0; i < datas.size(); i++) {
            Money money = datas.get(i);
            if(money == null){
                continue;
            }
            if(money.isSelected()){
                return money.getMoney();
            }
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return (datas == null?0:datas.size());
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.money)
        TextView money;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
