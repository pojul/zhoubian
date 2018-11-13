package com.yjyc.zhoubian.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.im.entity.Conversation;
import com.yuntongxun.ecsdk.ECMessage;

import java.util.List;

import butterknife.ButterKnife;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.MyViewHolder> {

    private Context mContext;
    private List<Conversation> conversations;

    public ConversationAdapter(Context mContext, List<Conversation> conversations) {
        this.mContext = mContext;
        this.conversations = conversations;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_chat_list, parent, false);
        return new ConversationAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
