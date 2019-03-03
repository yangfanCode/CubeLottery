package com.cp2y.cube.model;

/**
 * Created by yangfan on 2018/2/26.
 */

public class PersonalModel {
    public int flag;
    public Detail user;
    public static class Detail{
        public String attentionNum;
        public String fansNum;
        public int iD;
        public String interest;
        public String name;
        public int subscribeType;
        public String pushSingleNum;
        public String url;
    }
}
