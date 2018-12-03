package com.yjyc.zhoubian.im.chat.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.im.chat.ui.ChatActivity;
import com.yjyc.zhoubian.im.entity.ChatMessage;
import com.yjyc.zhoubian.model.UserInfo;
import com.yjyc.zhoubian.ui.activity.MyPublishActivity;
import com.yjyc.zhoubian.utils.DateUtil;
import com.yjyc.zhoubian.utils.DialogUtil;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.im.ECImageMessageBody;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.BaseViewHolder> {

    public List<ChatMessage> datas;
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
                return new TimeViewHolder(view);
            case ChatMessage.MESS_TYPE_TEXT:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_mess_text, parent, false);
                return new TextViewHolder(view);
            case ChatMessage.MESS_TYPE_PIC:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_mess_pic, parent, false);
                return new PicViewHolder(view);
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_mess_text, parent, false);
                return new TextViewHolder(view);
        }
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
            case ChatMessage.MESS_TYPE_PIC:
                bindPicViewHolder((PicViewHolder)holder, position);
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
        ChatMessage message = datas.get(position);
        holder.time.setText(DateUtil.getTimemess(message.getTime()));
    }

    private void bindTextViewHolder(TextViewHolder holder, int position) {
        ChatMessage message = datas.get(position);
        ECMessage ecMessage = message.getMessage();
        ECTextMessageBody textMessageBody = (ECTextMessageBody) ecMessage.getBody();
        String from = ecMessage.getForm();
        bindBaseViewHolder(holder, position);
        if(from.equals(("" + owner.uid))){
            holder.text.setSelected(true);
            holder.text.setText(textMessageBody.getMessage());
        }else{
            holder.friendText.setSelected(false);
            holder.friendText.setText(textMessageBody.getMessage());
        }
    }

    private void bindPicViewHolder(PicViewHolder holder, int position) {
        ChatMessage message = datas.get(position);
        ECMessage ecMessage = message.getMessage();
        ECImageMessageBody picMessageBody = (ECImageMessageBody) ecMessage.getBody();
        String from = ecMessage.getForm();
        bindBaseViewHolder(holder, position);
        if(from.equals(("" + owner.uid))){
            if(picMessageBody.getLocalUrl() != null && !picMessageBody.getLocalUrl().isEmpty() && !"null".equals(picMessageBody.getLocalUrl())){
                Glide.with(mContext).load(new File(picMessageBody.getLocalUrl())).into(holder.pic);
            }else{
                Glide.with(mContext).load(picMessageBody.getThumbnailFileUrl()).into(holder.pic);
            }
            holder.friendPic.setOnClickListener(null);
            holder.pic.setOnClickListener(v->{
                viewPicDetail(picMessageBody, holder.pic);
            });
        }else{
            if(picMessageBody.getLocalUrl() != null && !picMessageBody.getLocalUrl().isEmpty() && !"null".equals(picMessageBody.getLocalUrl())){
                Glide.with(mContext).load(new File(picMessageBody.getLocalUrl())).into(holder.friendPic);
            }else{
                Glide.with(mContext).load(picMessageBody.getThumbnailFileUrl()).into(holder.friendPic);
            }
            holder.pic.setOnClickListener(null);
            holder.friendPic.setOnClickListener(v->{
                viewPicDetail(picMessageBody, holder.friendPic);
            });
        }
    }

    private void viewPicDetail(ECImageMessageBody picMessageBody, ImageView rawView) {
        String rawPath;
        String path;
        if(picMessageBody.getLocalUrl() != null && !picMessageBody.getLocalUrl().isEmpty() && !"null".equals(picMessageBody.getLocalUrl())){
            rawPath = picMessageBody.getLocalUrl();
            path = picMessageBody.getLocalUrl();
        }else{
            rawPath = picMessageBody.getThumbnailFileUrl();
            if(picMessageBody.getHDImageURL() == null || picMessageBody.getHDImageURL().isEmpty() || "null".equals(picMessageBody.getHDImageURL())){
                path = rawPath;
            }else{
                path = picMessageBody.getHDImageURL();
            }
        }
        DialogUtil.getInstance().showDetailImgDialogPop(mContext,rawPath, path, rawView);
    }

    private void bindBaseViewHolder(NormalViewHolder holder, int position){
        ChatMessage message = datas.get(position);
        ECMessage ecMessage = message.getMessage();
        String from = ecMessage.getForm();
        if(from.equals(("" + owner.uid))){
            holder.messLeftPan.setVisibility(View.GONE);
            holder.messRightPan.setVisibility(View.VISIBLE);
            if(owner.head_url_img != null && !owner.head_url_img.isEmpty()){
                Glide.with(mContext).load(owner.head_url_img).into(holder.photo);
            }else{
                Glide.with(mContext).load(R.drawable.test_me).into(holder.photo);
            }
            if(ecMessage.getMsgStatus() == ECMessage.MessageStatus.FAILED){
                holder.progress.setVisibility(View.GONE);
                holder.sendFail.setVisibility(View.VISIBLE);
            }else if(ecMessage.getMsgStatus() == ECMessage.MessageStatus.SENDING){
                holder.progress.setVisibility(View.VISIBLE);
                holder.sendFail.setVisibility(View.GONE);
            }else{
                holder.progress.setVisibility(View.GONE);
                holder.sendFail.setVisibility(View.GONE);
            }
            if(owner.nickname != null){
                holder.nickName.setText(owner.nickname);
            }else{
                holder.nickName.setText("佚名");
            }
        }else{
            holder.messLeftPan.setVisibility(View.VISIBLE);
            holder.messRightPan.setVisibility(View.GONE);
            if(friend.head_url_img != null && !friend.head_url_img.isEmpty()){
                Glide.with(mContext).load(friend.head_url_img).into(holder.friendPhoto);
            }else{
                Glide.with(mContext).load(R.drawable.test_me).into(holder.friendPhoto);
            }
            if(ecMessage.getMsgStatus() == ECMessage.MessageStatus.FAILED){

            }else if(ecMessage.getMsgStatus() == ECMessage.MessageStatus.SENDING){
                holder.friendProgress.setVisibility(View.VISIBLE);
            }else{
                holder.friendProgress.setVisibility(View.GONE);
            }
            if(friend.nickname != null){
                holder.friendNickName.setText(friend.nickname);
            }else{
                holder.friendNickName.setText("佚名");
            }
        }
    }

    public void addMessage(ECMessage ecMessage){
        ChatMessage chatMessage = new ChatMessage(ecMessage);
        synchronized (datas){
            if(datas.size() == 0 || ( ecMessage.getMsgTime() - datas.get((datas.size() - 1)).getMessage().getMsgTime() ) > 10 * 60 * 1000 ){
                int position = datas.size();
                datas.add(new ChatMessage(ecMessage.getMsgTime()));
                datas.add(chatMessage);
                notifyItemRangeInserted(position, 2);
                return;
            }
            datas.add(chatMessage);
            notifyItemInserted(datas.size());
            ((ChatActivity)mContext).smoothToBottom(datas.size());
        }
    }

    public void addHistoryMessage(List<ECMessage> list) {
        synchronized (datas){
            int rawSize = datas.size();
            for (int i = 0; i < list.size(); i++) {
                ECMessage ecMessage = list.get(i);
                ChatMessage chatMessage = new ChatMessage(ecMessage);
                if(datas.size() == 0){
                    datas.add(0, chatMessage);
                    datas.add(0,new ChatMessage(ecMessage.getMsgTime()));
                }else if(datas.size() >= 2 && datas.get(0).getMessageType() == ChatMessage.MESS_TYPE_TIME
                        && datas.get(1).getMessageType() != ChatMessage.MESS_TYPE_TIME){
                    if( (datas.get(1).getMessage().getMsgTime() - ecMessage.getMsgTime()) > 10 * 60 * 1000){
                        datas.add(0, chatMessage);
                        datas.add(0,new ChatMessage(ecMessage.getMsgTime()));
                    }else{
                        datas.add(1, chatMessage);
                    }
                }
            }
            //notifyItemRangeChanged(0, (datas.size() - rawSize));
            notifyDataSetChanged();
        }
    }

    private List<ChatMessage> getMessageWidthTime(List<ChatMessage> messages) {
        return messages;
    }

    public void notifyMessageStatus(ECMessage message) {
        for (int i = 0; i < datas.size(); i++) {
            ECMessage ecMessage = datas.get(i).getMessage();
            if(ecMessage == null){
                continue;
            }
            if(ecMessage.getMsgId() == message.getMsgId()){
                notifyItemChanged(i);
                return;
            }
        }
    }

    class BaseViewHolder extends RecyclerView.ViewHolder {

        public BaseViewHolder(View itemView) {
            super(itemView);
        }
    }

    class NormalViewHolder extends BaseViewHolder {

        @BindView(R.id.mess_left_pan)
        RelativeLayout messLeftPan;
        @BindView(R.id.friend_photo)
        RoundedImageView friendPhoto;
        @BindView(R.id.friend_nick_name)
        TextView friendNickName;
        @BindView(R.id.mess_right_pan)
        RelativeLayout messRightPan;
        @BindView(R.id.photo)
        RoundedImageView photo;
        @BindView(R.id.nick_name)
        TextView nickName;
        @BindView(R.id.progress)
        ProgressBar progress;
        @BindView(R.id.friend_progress)
        ProgressBar friendProgress;
        @BindView(R.id.send_fail)
        TextView sendFail;
        @BindView(R.id.friend_send_fail)
        TextView friendSendFail;

        public NormalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class TextViewHolder extends NormalViewHolder {

        @BindView(R.id.text)
        TextView text;
        @BindView(R.id.friend_text)
        TextView friendText;

        public TextViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class PicViewHolder extends NormalViewHolder {

        @BindView(R.id.pic)
        RoundedImageView pic;
        @BindView(R.id.friend_pic)
        RoundedImageView friendPic;

        public PicViewHolder(View itemView) {
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

    public long getTopTime() {
        if(datas.size() <= 0){
            return System.currentTimeMillis();
        }
        if(datas.get(0).getMessageType() != ChatMessage.MESS_TYPE_TIME){
            return datas.get(0).getMessage().getMsgTime();
        }else if(datas.size() >= 2 && datas.get(1).getMessageType() != ChatMessage.MESS_TYPE_TIME){
            return datas.get(1).getMessage().getMsgTime();
        }
        return System.currentTimeMillis();
    }

}
