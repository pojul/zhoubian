package com.yjyc.zhoubian.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orhanobut.hawk.Hawk;
import com.yjyc.zhoubian.HttpUrl;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.model.BlackUser;
import com.yjyc.zhoubian.model.BlackUserListModel;
import com.yjyc.zhoubian.model.Login;
import com.yjyc.zhoubian.model.RemoveBlacklist;
import com.yjyc.zhoubian.model.RemoveBlacklistModel;
import com.yjyc.zhoubian.ui.activity.LoginActivity;
import com.yuqian.mncommonlibrary.dialog.LoadingDialog;
import com.yuqian.mncommonlibrary.http.OkhttpUtils;
import com.yuqian.mncommonlibrary.http.callback.AbsJsonCallBack;

import org.w3c.dom.Text;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BlackListAdapter extends RecyclerView.Adapter<BlackListAdapter.MyViewHolder> {

    private Context mContext;
    private List<BlackUser> datas;

    public BlackListAdapter(Context mContext, List<BlackUser> datas) {
        this.mContext = mContext;
        this.datas = datas;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_black_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BlackUser blackUser = datas.get(position);
        if(blackUser.black_info.nickname != null){
            holder.nickName.setText(blackUser.black_info.nickname);
        }else{
            holder.nickName.setText("");
        }
        if(blackUser.black_info.head_url != null && !blackUser.black_info.head_url.isEmpty()){
            Glide.with(mContext).load((HttpUrl.BASE_URL_NOEND + blackUser.black_info.head_url)).into(holder.photo);
        }else{
            Glide.with(mContext).load(R.drawable.test_me).into(holder.photo);
        }
        if(blackUser.black_info.sign != null){
            holder.sign.setText(blackUser.black_info.sign);
        }else{
            holder.sign.setText("");
        }
        holder.removeaBlack.setOnClickListener(v->{
            removeBlack(blackUser.black_uid, position);
        });
    }

    @Override
    public int getItemCount() {
        return (datas == null ? 0 : datas.size());
    }

    private void removeBlack(int blackUid, int position) {
        if(Hawk.get("LoginModel") == null){
            Toast.makeText(mContext, "请先登陆", Toast.LENGTH_SHORT).show();
            mContext.startActivity(new Intent(mContext, LoginActivity.class));
            return;
        }
        LoadingDialog.showLoading(mContext);
        Login login = Hawk.get("LoginModel");
        new OkhttpUtils().with()
                .post()
                .url(HttpUrl.REMOVEBLACKLIST)
                .addParams("uid", ("" + login.uid))
                .addParams("token", login.token)
                .addParams("black_uid", ("" + blackUid))
                .execute(new AbsJsonCallBack<RemoveBlacklistModel, RemoveBlacklist>() {
                    @Override
                    public void onFailure(String errorCode, String errorMsg) {
                        Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                        LoadingDialog.closeLoading();
                    }

                    @Override
                    public void onSuccess(RemoveBlacklist body) {
                        LoadingDialog.closeLoading();
                        synchronized (datas){
                            datas.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(0, datas.size());
                        }
                    }
                });
    }

    public void addData(List<BlackUser> blackUsers){
        if(blackUsers == null){
            return;
        }
        synchronized (datas){
            datas.addAll(blackUsers);
            notifyDataSetChanged();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.photo)
        RoundedImageView photo;
        @BindView(R.id.nick_name)
        TextView nickName;
        @BindView(R.id.sign)
        TextView sign;
        @BindView(R.id.remove_black)
        TextView removeaBlack;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
