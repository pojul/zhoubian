package com.yjyc.zhoubian.ui.view.pickpicview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

public class PickPicView extends RecyclerView{

    private List<LocalMedia> picList = new ArrayList<>();
    private PickPicAdapter adapter;

    public PickPicView(Context context) {
        super(context);
        init();
    }

    public PickPicView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PickPicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 4);
        setLayoutManager(layoutManager);
        setNestedScrollingEnabled(false);
        adapter = new PickPicAdapter(getContext(), picList, 5);
        setAdapter(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new PicTouchCallBack(adapter));
        itemTouchHelper.attachToRecyclerView(this);
        adapter.setItemTouchHelper(itemTouchHelper);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    List<LocalMedia> selectList;
                    selectList = PictureSelector.obtainMultipleResult(data);
                    adapter.addDatas(selectList);
                    break;
            }
        }
    }

    public List<String> getPics(){
        List<String> pics = new ArrayList<>();


        return pics;
    }

}
