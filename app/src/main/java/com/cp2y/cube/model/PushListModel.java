package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2018/3/7.
 */

public class PushListModel {
    public int flag;
    public List<Detail>list;
    public static class Detail{
        public int id;
        public int lotteryId;
        public String lotteryName;
        public String lotteryDetail;
        public int platform;
        public long sendTime;
        public int userId;
        public int type;
    }
}
