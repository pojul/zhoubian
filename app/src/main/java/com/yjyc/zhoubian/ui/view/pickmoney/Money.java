package com.yjyc.zhoubian.ui.view.pickmoney;

import java.util.ArrayList;
import java.util.List;

public class Money {

    private int money;
    private String note;
    private boolean isSelected;

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public static List<Money> createMoneys(List<Integer> prices){
        List<Money> moneys = new ArrayList<>();
        if(prices == null || prices.size() <= 0){
            return moneys;
        }
        for (int i = 0; i < prices.size(); i++) {
            Money money = new Money();
            money.setMoney(prices.get(i));
            money.setNote((prices.get(i) + "元"));
            moneys.add(money);
        }
        return moneys;
    }

}
