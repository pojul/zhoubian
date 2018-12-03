package com.yjyc.zhoubian.ui.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.scwang.smartrefresh.layout.header.ClassicsHeader;

public class MyClassicsHeader extends ClassicsHeader {
    public MyClassicsHeader(Context context) {
        super(context);
    }

    public MyClassicsHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyClassicsHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void showLoadOnly(){
        this.getChildAt(0).setVisibility(GONE);
        ((RelativeLayout.LayoutParams)this.getChildAt(1).getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT);
        ((RelativeLayout.LayoutParams)this.getChildAt(2).getLayoutParams()).addRule(RelativeLayout.CENTER_IN_PARENT);
    }

}
