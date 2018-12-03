package com.yjyc.zhoubian.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.SiteMsgs;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OfficialMsgAdapter extends RecyclerView.Adapter<OfficialMsgAdapter.MyViewHolder>{

    private List<SiteMsgs.SiteMsg> datas;
    private Context mContext;

    public OfficialMsgAdapter(Context mContext, List<SiteMsgs.SiteMsg> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    @Override
    public OfficialMsgAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_official_msg, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SiteMsgs.SiteMsg siteMsg = datas.get(position);
        if(siteMsg.title == null || siteMsg.title.isEmpty()){
            holder.title.setVisibility(View.GONE);
        }else{
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(siteMsg.title);
        }
        if(siteMsg.body != null && !siteMsg.body.isEmpty()){
            holder.body.setVisibility(View.VISIBLE);
            holder.body.setText(siteMsg.body);
        }else{
            holder.body.setVisibility(View.GONE);
        }
        if(siteMsg.pic != null && !siteMsg.pic.isEmpty()){
            holder.img.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(siteMsg.pic).into(holder.img);
        }else{
            holder.img.setVisibility(View.GONE);
        }
        if(siteMsg.create_time != null && !siteMsg.create_time.isEmpty()){
            holder.time.setText(siteMsg.create_time.substring(0, siteMsg.create_time.lastIndexOf(" ")));
        }else{
            holder.time.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return (datas == null ? 0 : datas.size());
    }

    public void addDatas(List<SiteMsgs.SiteMsg> siteMsgs){
        synchronized (datas){
            datas.addAll(siteMsgs);
            notifyDataSetChanged();
        }
    }

    public void clearData() {
        synchronized (datas){
            datas.clear();
            notifyDataSetChanged();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.body)
        TextView body;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.img)
        ImageView img;
        @BindView(R.id.time)
        TextView time;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
