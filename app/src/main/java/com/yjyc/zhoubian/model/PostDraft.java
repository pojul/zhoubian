package com.yjyc.zhoubian.model;

import java.util.List;

public class PostDraft {
    public List<DraftData> list;
    public class DraftData {
        public int id;
        public String title;
        public List<String> pic;
        public String custom_post_cate;
    }
    public boolean hasNextPages;
}
