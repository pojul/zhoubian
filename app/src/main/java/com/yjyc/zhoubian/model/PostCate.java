package com.yjyc.zhoubian.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Administrator on 2018/10/16/016.
 */

public class PostCate {
    public List<PostCate.Data> list;
    public class Data{
        private int id;
        private String title;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getIsChecked() {
            return isChecked;
        }

        public void setIsChecked(int isChecked) {
            this.isChecked = isChecked;
        }

        private int isChecked; //1选中 2未选中

    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(id);
//        dest.writeString(title);
//        dest.writeInt(isChecked);
//    }
//
//    protected PostCate(Parcel in) {
//        id = in.readInt();
//        title = in.readString();
//        isChecked = in.readInt();
//    }
//
//    public static final Creator<PostCate> CREATOR = new Creator<PostCate>() {
//        @Override
//        public PostCate createFromParcel(Parcel in) {
//            return new PostCate(in);
//        }
//
//        @Override
//        public PostCate[] newArray(int size) {
//            return new PostCate[size];
//        }
//    };

}
