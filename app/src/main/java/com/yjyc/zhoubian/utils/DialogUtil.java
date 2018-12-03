package com.yjyc.zhoubian.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.yjyc.zhoubian.R;
import com.yjyc.zhoubian.app.BaseApplication;
import com.yjyc.zhoubian.im.entity.ChatMessage;

import java.io.File;

public class DialogUtil {

    private static DialogUtil mDialogUtil;
    private DialogClick dialogClick;
    private final static String TAG = "DialogUtil";

    public static DialogUtil getInstance() {
        if (mDialogUtil == null) {
            synchronized (DialogUtil.class) {
                if (mDialogUtil == null) {
                    mDialogUtil = new DialogUtil();
                }
            }
        }
        return mDialogUtil;
    }


    /**
     * @param context
     * @param view
     * @param type 1: 留言 2: 回复 3: 红包口令
     * */
    public void showCommentDialog(Context context, View view, int type, String raw) {
        View popView = LayoutInflater.from(context).inflate(R.layout.dialog_comment, null);
        PopupWindow subReplyPop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subReplyPop.setBackgroundDrawable(new BitmapDrawable());
        subReplyPop.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        subReplyPop.setOutsideTouchable(false);
        subReplyPop.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        EditText subReplyText = popView.findViewById(R.id.subreply_text);
        if(raw != null && !raw.isEmpty()){
            subReplyText.setText(raw);
        }
        String hint = "";
        if(type == 1){
            hint = "评论";
        }else if(type == 2){
            hint = "回复";
        }else if(type == 3){
            hint = "请输入红包口令";
        }
        subReplyText.setHint(hint);
        String finalHint = hint;
        popView.findViewById(R.id.ok).setOnClickListener(v -> {
            if (subReplyText.getText().toString().isEmpty()) {
                Toast.makeText(context, finalHint + "内容不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dialogClick != null) {
                dialogClick.onclick(subReplyText.getText().toString());
            }
            setDialogClick(null);
            subReplyPop.dismiss();
        });
        popView.findViewById(R.id.cancel).setOnClickListener(v -> {
            setDialogClick(null);
            subReplyPop.dismiss();
        });
    }

    /**
     *显示下翻帖子弹框
     * @param context
     * @param view
     * */
    public void postShutDownPop(Context context, View view){
        View popView = LayoutInflater.from(context).inflate(R.layout.dialog_downturn, null);
        PopupWindow shutDownPop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        shutDownPop.setBackgroundDrawable(new BitmapDrawable());
        shutDownPop.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        shutDownPop.setOutsideTouchable(false);
        shutDownPop.showAsDropDown(view);
        TextView next30 = popView.findViewById(R.id.next_30);
        TextView next40 = popView.findViewById(R.id.next_40);
        TextView next50 = popView.findViewById(R.id.next_50);
        next30.setOnClickListener(v->{
            if (dialogClick != null) {
                dialogClick.onclick("30");
            }
            setDialogClick(null);
            shutDownPop.dismiss();
        });
        next40.setOnClickListener(v->{
            if (dialogClick != null) {
                dialogClick.onclick("40");
            }
            setDialogClick(null);
            shutDownPop.dismiss();
        });
        next50.setOnClickListener(v->{
            if (dialogClick != null) {
                dialogClick.onclick("50");
            }
            setDialogClick(null);
            shutDownPop.dismiss();
        });
    }


    public void showDetailImgDialogPop(Context context,String rawPath, String path, ImageView rawView) {
        try {
            ImageView photoView = (ImageView) LayoutInflater.from(context).inflate(R.layout.dialog_detail_img, null);
            PopupWindow popUpWin1 = new PopupWindow(photoView, BaseApplication.SCREEN_WIDTH, BaseApplication.SCREEN_HEIGHT);
            popUpWin1.setBackgroundDrawable(new BitmapDrawable());
            popUpWin1.setFocusable(true);
            // 设置可以触摸弹出框以外的区域
            popUpWin1.setOutsideTouchable(true);
            if (rawPath != null) {
                if (FileUtil.isNetUrl(rawPath)) {
                    String finalPath = path;
                    Glide.with(context).load(rawPath).listener(new RequestListener<Drawable>() {
                        boolean hasExecute = false;
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //加载失败 移除监听
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //成功 移除监
                            if (hasExecute) {
                                return false;
                            }
                            hasExecute = true;
                            showDetailImgPop(popUpWin1, photoView, context, finalPath, rawView);
                            return false;
                        }
                    }).into(photoView);
                } else {
                    File file = new File(rawPath);
                    Glide.with(context).load(file).into(photoView);
                }
            } else {
                Glide.with(context).load(rawView.getDrawable()).into(photoView);
            }
            popUpWin1.showAtLocation(rawView.getRootView(), Gravity.CENTER, 0, 0);

            int[] positions = new int[2];
            rawView.getLocationInWindow(positions);
            ViewGroup.LayoutParams layoutParams = photoView.getLayoutParams();
            /*if (rawView instanceof RoundedImageView) {
                layoutParams.width = ((RoundedImageView) rawView).drawableWidth;
                layoutParams.height = ((RoundedImageView) rawView).drawableHeight;
                photoView.setX((positions[0] + (rawView.getWidth() - layoutParams.width) * 0.5f));
                photoView.setY((positions[1] - getStatusBarHeight(context) + (rawView.getHeight() - layoutParams.height) * 0.5f));
            } else {*/
                layoutParams.width = rawView.getWidth();
                layoutParams.height = rawView.getHeight();
                photoView.setX(positions[0]);
                photoView.setY((positions[1] - getStatusBarHeight(context)));
            //}
            photoView.setLayoutParams(layoutParams);

            if (rawPath == null || !FileUtil.isNetUrl(rawPath)) {
                showDetailImgPop(popUpWin1, photoView, context, path, rawView);
            }
        } catch (Exception e) {
        }
    }

    private void showDetailImgPop(PopupWindow popUpWin1, ImageView photoView, Context context, String path, ImageView rawView) {
        PhotoView photoView2 = (PhotoView) LayoutInflater.from(context).inflate(R.layout.dialog_detail_photoview, null);
        PopupWindow popUpWin2 = new PopupWindow(photoView2, BaseApplication.SCREEN_WIDTH, BaseApplication.SCREEN_HEIGHT);
        popUpWin2.setBackgroundDrawable(new BitmapDrawable());
        popUpWin2.setFocusable(true);
        // 设置可以触摸弹出框以外的区域
        popUpWin2.setOutsideTouchable(true);
        popUpWin2.setAnimationStyle(R.style.showPopupAnimation2);
        photoView2.setOnClickListener(v->{
            popUpWin2.dismiss();
        });

        AnimatorUtil.startPopObjAnimator(photoView, () -> {
            //isAnim = false;
            RequestOptions options = new RequestOptions();
            options.placeholder(photoView.getDrawable())
                    .error(photoView.getDrawable())
                    .fallback(photoView.getDrawable());
            if (!FileUtil.isNetUrl(path)) {
                File file = new File(path);
                Glide.with(context).load(file).apply(options).into(photoView2);
            } else {
                Glide.with(context).load(path).apply(options).into(photoView2);
            }
            popUpWin2.showAtLocation(rawView.getRootView(), Gravity.CENTER, 0, 0);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (popUpWin1 != null) {
                    popUpWin1.dismiss();
                }
            }, 155);
        });
    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void showUpdateAppDialog(Context context, int newVersionCode, String newVersionName, int oldVersionCode, String oldVersionName){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.dialog_check_update, null);
        TextView version = view.findViewById(R.id.version);
        Button leftBt = view.findViewById(R.id.left_button);
        Button rightBt = view.findViewById(R.id.right_button);
        TextView versionInfo = view.findViewById(R.id.version_info);
        if(newVersionCode > oldVersionCode){
            version.setText("检测到新版本");
            versionInfo.setText("最新版本号：" + newVersionCode + "\n最新版本名：" + newVersionName);
            versionInfo.setVisibility(View.VISIBLE);
            leftBt.setText("立即更新");
            rightBt.setText("以后再说");
        }else{
            version.setText("当前已是最新版本");
            versionInfo.setVisibility(View.GONE);
            leftBt.setText("确定");
            rightBt.setText("取消");
        }
        dialogBuilder.setView(view);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
        leftBt.setOnClickListener(v->{
            if(leftBt.getText().toString().equals("立即更新")){
                if(dialogClick != null){
                    dialogClick.onclick("立即更新");
                }
            }
            setDialogClick(null);
            dialog.dismiss();
        });

        rightBt.setOnClickListener(v->{
            setDialogClick(null);
            dialog.dismiss();
        });
    }

    public void setDialogClick(DialogClick dialogClick) {
        this.dialogClick = dialogClick;
    }
    
    public interface DialogClick {
        void onclick(String str);
    }


}
