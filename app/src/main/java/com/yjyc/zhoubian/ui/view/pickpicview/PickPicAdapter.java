package com.yjyc.zhoubian.ui.view.pickpicview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.makeramen.roundedimageview.RoundedImageView;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.yanzhenjie.permission.AndPermission;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.utils.ArrayUtil;
import com.yjyc.zhoubian.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PickPicAdapter extends RecyclerView.Adapter<PickPicAdapter.MyViewHolder> implements CallbackItemTouch{

    private Context mContext;
    private List<LocalMedia> datas;
    private ItemTouchHelper itemTouchHelper;
    private int maxSize = 10;

    public PickPicAdapter(Context mContext, List<LocalMedia> datas, int maxSize) {
        this.mContext = mContext;
        this.datas = datas;
        this.maxSize = maxSize;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_pick_pic, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if(position >= datas.size()){
            holder.delete.setVisibility(View.GONE);
            holder.tv.setVisibility(View.GONE);
            holder.pic.setVisibility(View.GONE);
            holder.picAdd.setVisibility(View.VISIBLE);
        }else{
            LocalMedia pic = datas.get(position);
            holder.delete.setVisibility(View.VISIBLE);
            holder.tv.setVisibility(View.VISIBLE);
            holder.pic.setVisibility(View.VISIBLE);
            holder.picAdd.setVisibility(View.GONE);
            String path = pic.getPath();
            if(FileUtil.isNetUrl(path)){
                Glide.with(mContext).load(path).into(holder.pic);
            }else{
                File file = new File(pic.getPath());
                Glide.with(mContext).load(file).into(holder.pic);
            }
        }
        holder.delete.setOnClickListener(v->{
            datas.remove(position);
            notifyDataSetChanged();
        });
        holder.rootRl.setOnLongClickListener(v -> {
            if(datas.size() != 0 && itemTouchHelper != null && position < datas.size()){
                itemTouchHelper.startDrag(holder);
                Vibrator vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
                vib.vibrate(70);
            }
            return false;
        });
        holder.rootRl.setOnClickListener(v->{
            if(position >= datas.size()){
                holder.picAdd.performClick();
                pickPic();
            }
        });
    }

    @SuppressLint("CheckResult")
    private void pickPic() {
        new RxPermissions((Activity) mContext)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if(!granted){
                        new MaterialDialog.Builder(mContext)
                                .title("提示")
                                .content("当前权限被拒绝导致功能不能正常使用，请到设置界面修改相机和存储权限允许访问")
                                .positiveText("去设置")
                                .negativeText("取消")
                                .onPositive((dialog, which) -> AndPermission.permissionSetting(mContext)
                                        .execute())
                                .show();
                    }else{
                        PictureSelector.create((Activity) mContext)
                                .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                                .maxSelectNum((maxSize - datas.size()))// 最大图片选择数量 int
                                .minSelectNum(1)// 最小选择数量 int
                                .imageSpanCount(4)// 每行显示个数 int
                                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                                .previewImage(true)// 是否可预览图片 true or false
                                .isCamera(true)// 是否显示拍照按钮 true or false
                                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                                .sizeMultiplier(0.8f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                                .compress(true)// 是否压缩 true or false
                                .previewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                                .minimumCompressSize(300)// 小于300kb的图片不压缩
                                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                    }
                });
    }

    @Override
    public int getItemCount() {
        if(datas.size() >= maxSize){
            return datas.size();
        }else{
            return (datas == null?1:(datas.size() + 1));
        }
    }

    @Override
    public void itemTouchOnMove(int oldPosition, int newPosition) {
        if(newPosition >= datas.size()){
            return;
        }
        datas.add(newPosition,datas.remove(oldPosition));// change position
        notifyItemMoved(oldPosition, newPosition); //notifies changes in adapter, in this case use the notifyItemMoved
    }

    public void addDatas(List<LocalMedia> pics){
        synchronized (datas){
            datas.addAll(pics);
            notifyDataSetChanged();
        }
    }

    public List<String> getPics(){
        List<String> pics = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++) {
            pics.add(datas.get(i).getPath());
        }
        return pics;
    }

    public void clearData(){
        synchronized (datas){
            datas.clear();
            notifyDataSetChanged();
        }
    }

    public void setdata(List<String> pics) {
        List<LocalMedia> localMedias = new ArrayList<>();
        for (int i = 0; i < pics.size(); i++) {
            LocalMedia localMedia = new LocalMedia();
            localMedia.setPath(pics.get(i));
            localMedias.add(localMedia);
        }
        this.datas = localMedias;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.root_rl)
        RelativeLayout rootRl;
        @BindView(R.id.pic)
        RoundedImageView pic;
        @BindView(R.id.tv)
        TextView tv;
        @BindView(R.id.delete)
        ImageView delete;
        @BindView(R.id.pic_add)
        ImageView picAdd;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setItemTouchHelper(ItemTouchHelper itemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper;
    }
}
