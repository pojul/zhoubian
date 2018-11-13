package com.yjyc.zhoubian.adapter;

import android.widget.ImageView;

import com.yjyc.zhoubian.model.SearchPosts;

/**
 * Created by Administrator on 2018/10/12/012.
 */

public interface OnItemClickListener{
    void onClick( int position);
    void onLongClick( int position);
    void onDeleteClick(ImageView iv_delete, boolean isDown, int[] position);
}
