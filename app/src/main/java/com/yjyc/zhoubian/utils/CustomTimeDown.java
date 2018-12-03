package com.yjyc.zhoubian.utils;

import android.os.CountDownTimer;

public class CustomTimeDown extends CountDownTimer {

    public CustomTimeDown(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long l) {
        if(mOnTimeDownListener!=null)
        mOnTimeDownListener.onTick(l);
    }

    @Override
    public void onFinish() {
        if(mOnTimeDownListener!=null)
            mOnTimeDownListener.OnFinish();
    }

    private OnTimeDownListener mOnTimeDownListener;

    public interface OnTimeDownListener{
        void onTick(long l);
        void OnFinish();
    }

    public void setOnTimeDownListener(OnTimeDownListener onTimeDownListener){
        mOnTimeDownListener = onTimeDownListener;
    }
}
