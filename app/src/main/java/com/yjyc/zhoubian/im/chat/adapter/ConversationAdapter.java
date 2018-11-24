package com.yjyc.zhoubian.im.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.im.chat.ui.ChatActivity;
import com.yjyc.zhoubian.im.entity.Conversation;
import com.yjyc.zhoubian.ui.activity.MyPublishActivity;
import com.yjyc.zhoubian.utils.DateUtil;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> {

    private Context mContext;
    private List<Conversation> datas;

    public ConversationAdapter(Context mContext, List<Conversation> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_list, parent, false);
        return new ConversationAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Conversation conversation = datas.get(position);
        if(conversation.getFriend() != null && conversation.getFriend().head_url_img != null){
            Glide.with(mContext).load((conversation.getFriend().head_url_img)).into(holder.photo);
        }else{
            Glide.with(mContext).load(R.drawable.test_me).into(holder.photo);
        }
        if(conversation.getUnReadMessage() > 0){
            holder.un_red_num.setVisibility(View.VISIBLE);
            holder.un_red_num.setText(("" + conversation.getUnReadMessage()));
        }else{
            holder.un_red_num.setVisibility(View.GONE);
        }
        if(conversation.getFriend() != null && conversation.getFriend().nickname != null){
            holder.nick_name.setText(conversation.getFriend().nickname);
        }else{
            holder.nick_name.setText("佚名");
        }
        holder.autograph.setText(conversation.getLastMessage());
        holder.time.setText(DateUtil.getTimemess(conversation.getLastTimeMilli()));
        if(conversation.isNotTroubled()){
            holder.not_disturb.setVisibility(View.GONE);
        }else{
            holder.not_disturb.setVisibility(View.VISIBLE);
        }
        holder.root_rl.setOnClickListener(v->{
            Intent intent = new Intent(mContext, ChatActivity.class);
            intent.putExtra("frindId", ("" + conversation.getFrom()));
            mContext.startActivity(intent);
        });
        holder.photo.setOnClickListener(v->{
            Intent intent = new Intent(mContext, MyPublishActivity.class);
            intent.putExtra("uid",  conversation.getFrom() + "");
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return (datas==null?0:datas.size());
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.photo)
        RoundedImageView photo;
        @BindView(R.id.un_red_num)
        TextView un_red_num;
        @BindView(R.id.nick_name)
        TextView nick_name;
        @BindView(R.id.autograph)
        TextView autograph;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.not_disturb)
        ImageView not_disturb;
        @BindView(R.id.root_rl)
        RelativeLayout root_rl;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
