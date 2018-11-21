package com.yjyc.zhoubian.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.BlackUser;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.RemoveBlacklist;
import com.yjyc.zhoubian.model.RemoveBlacklistModel;
import com.yjyc.zhoubian.model.ReportCate;
import com.yjyc.zhoubian.ui.activity.LoginActivity;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportCateAdapter extends RecyclerView.Adapter<ReportCateAdapter.MyViewHolder> {

    private Context mContext;
    private List<ReportCate> datas;

    public ReportCateAdapter(Context mContext, List<ReportCate> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.report_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ReportCate reportCate = datas.get(position);
        holder.radio.setSelected(reportCate.isSelected);
        holder.title.setText(reportCate.title);
        holder.radio.setOnClickListener(v->{
            if(!holder.radio.isSelected()){
                onItemSelected(position);
            }
        });
    }

    private void onItemSelected(int position) {
        synchronized (datas){
            for (int i = 0; i < datas.size(); i++) {
                ReportCate reportCate = datas.get(i);
                if(position == i){
                    reportCate.isSelected = true;
                }else{
                    reportCate.isSelected = false;
                }
            }
            notifyDataSetChanged();
        }
    }

    public int getSelecterCate(){
        for (int i = 0; i < datas.size(); i++) {
            ReportCate reportCate = datas.get(i);
            if(reportCate.isSelected){
                return reportCate.id;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return (datas == null ? 0 : datas.size());
    }

    public void addData(List<ReportCate> dataList){
        if(dataList == null){
            return;
        }
        synchronized (datas){
            datas.addAll(dataList);
            notifyDataSetChanged();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.radio)
        ImageView radio;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
