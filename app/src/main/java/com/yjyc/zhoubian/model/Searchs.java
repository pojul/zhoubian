package com.yjyc.zhoubian.model;

import java.util.List;

/**
 * Created by Administrator on 2018/10/29/029.
 */

public class Searchs {
    public List<Search> list;

    public class Search{
        public int num;
        public String unit;

        public int getIsChecked() {
            return isChecked;
        }

        public void setIsChecked(int isChecked) {
            this.isChecked = isChecked;
        }

        private int isChecked;
    }
}
