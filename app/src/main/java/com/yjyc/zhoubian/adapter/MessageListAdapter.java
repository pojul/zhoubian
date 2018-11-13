package com.yjyc.zhoubian.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.im.entity.ChatMessage;
import com.yjyc.zhoubian.model.UserInfo;
import com.yuntongxun.ecsdk.ECMessage;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.BaseViewHolder> {

    private List<ChatMessage> datas;
    private Context mContext;
    private UserInfo owner;
    private UserInfo friend;


    public MessageListAdapter(List<ChatMessage> datas, Context mContext, UserInfo owner, UserInfo friend) {
        this.datas = datas;
        this.mContext = mContext;
        this.friend = friend;
        this.owner = owner;
    }

    @Override
    public MessageListAdapter.BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ChatMessage.MESS_TYPE_TIME:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_mess_time, parent, false);
                break;
            case ChatMessage.MESS_TYPE_TEXT:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_mess_text, parent, false);
                break;
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_mess_text, parent, false);
                break;
        }
        return new MessageListAdapter.BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageListAdapter.BaseViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case ChatMessage.MESS_TYPE_TIME:
                bindTimeViewHolder((TimeViewHolder)holder, position);
                break;
            case ChatMessage.MESS_TYPE_TEXT:
                bindTextViewHolder((TextViewHolder)holder, position);
                break;
            default:
                bindTextViewHolder((TextViewHolder)holder, position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return (datas == null ? 0 : datas.size());
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = datas.get(position);
        return message.getMessageType();
    }

    private void bindTimeViewHolder(TimeViewHolder holder, int position) {
    }

    private void bindTextViewHolder(TextViewHolder holder, int position) {
        bindBaseViewHolder(holder, position);
    }

    private void bindBaseViewHolder(BaseViewHolder holder, int position){
        ECMessage message = datas.get(position).getMessage();
        String from = message.getForm();
        if(from.equals(("" + owner.uid))){
            holder.messLeftPan.setVisibility(View.GONE);
            holder.messRightPan.setVisibility(View.VISIBLE);
            if(owner.head_url_img != null && !owner.head_url_img.isEmpty()){
                Glide.with(mContext).load(owner.head_url_img).into(holder.photo);
            }else{
                Glide.with(mContext).load(R.drawable.test_me).into(holder.photo);
            }

        }else{
            holder.messLeftPan.setVisibility(View.VISIBLE);
            holder.messRightPan.setVisibility(View.GONE);
            if(friend.head_url_img != null && !friend.head_url_img.isEmpty()){
                Glide.with(mContext).load(friend.head_url_img).into(holder.friendPhoto);
            }else{
                Glide.with(mContext).load(R.drawable.test_me).into(holder.friendPhoto);
            }

        }
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.mess_left_pan)
        RelativeLayout messLeftPan;
        @BindView(R.id.friend_photo)
        RoundedImageView friendPhoto;
        @BindView(R.id.friend_nick_name)
        TextView friendNickName;
        @BindView(R.id.friend_text)
        TextView friendText;
        @BindView(R.id.mess_right_pan)
        RelativeLayout messRightPan;
        @BindView(R.id.photo)
        RoundedImageView photo;
        @BindView(R.id.nick_name)
        TextView nickName;
        @BindView(R.id.text)
        TextView text;


        public BaseViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class TextViewHolder extends BaseViewHolder {

        public TextViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class TimeViewHolder extends BaseViewHolder {
        @BindView(R.id.time)
        TextView time;
        public TimeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
