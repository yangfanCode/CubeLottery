package com.cp2y.cube.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by yangfan on 2018/2/28.
 */

public class RankModel {
    @SerializedName("10002")
    public List<Detail> doubleball;
    @SerializedName("10088")
    public List<Detail> lottoball;
    @SerializedName("10001")
    public List<Detail> d3ball;
    @SerializedName("10003")
    public List<Detail> p3ball;
    @SerializedName("10004")
    public List<Detail> p5ball;
    public List<Integer>lotteryList;
    public int flag;
    public static class Detail implements Serializable{
        public long createtime;
        public String headUrl;
        public int id;
        public int lotteryId;
        public String nickName;
        public String returnRate;
        public int subscribeType;
        public int type;
        public int userId;
    }
}
