package com.yjyc.zhoubian.utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.yjyc.zhoubian.R;

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
        TextView next20 = popView.findViewById(R.id.next_20);
        TextView next30 = popView.findViewById(R.id.next_30);
        TextView next50 = popView.findViewById(R.id.next_50);
        next20.setOnClickListener(v->{
            if (dialogClick != null) {
                dialogClick.onclick("20");
            }
            setDialogClick(null);
            shutDownPop.dismiss();
        });
        next30.setOnClickListener(v->{
            if (dialogClick != null) {
                dialogClick.onclick("30");
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

    public void setDialogClick(DialogClick dialogClick) {
        this.dialogClick = dialogClick;
    }
    
    public interface DialogClick {
        void onclick(String str);
    }


}
