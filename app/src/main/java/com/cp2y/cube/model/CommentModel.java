package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2018/2/11.
 */

public class CommentModel {
    public String commentsNum;
    public int flag;
    public int pageSize;
    public List<Detail>list;
    public List<Item>crList;
    public static class Detail{
        public int byCriticsID;
        public String content;
        public long createtime;
        public int criticsID;
        public int iD;
        public String name;
        public int pushSingleID;
        public List<Item>replyList;
        public String url;
        public int type;
        public String tiem;
    }
    public static class Item{
        public int byCriticsID;
        public String byCriticsName;
        public int commentsID;
        public String content;
        public long createtime;
        public int criticsID;
        public String criticsName;
        public int iD;
        public int pushSingleID;
        public int type;
    }
}
