package com.yjyc.zhoubian.ui.view.pickmoney;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.yjyc.zhoubian.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PickMoneyView extends LinearLayout {

    @BindView(R.id.moneys)
    RecyclerView moneys;

    private PickMoneyAdapter adapter;
    private List<Money> moneyList;

    public PickMoneyView(Context context) {
        super(context);
        init();
    }

    public PickMoneyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PickMoneyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.include_chose_money, this);
        ButterKnife.bind(this);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        moneys.setLayoutManager(layoutManager);
        adapter = new PickMoneyAdapter(getContext(), moneyList);
        moneys.setAdapter(adapter);

    }

    public void setMoneys(List<Money> moneyList) {
        this.moneyList = moneyList;
        adapter = new PickMoneyAdapter(getContext(), moneyList);
        moneys.setAdapter(adapter);
    }

    public int gtPickedMoney(){
        return adapter.getSelectedMoney();
    }

}
