package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */

public class CalcLotteryModel {
    public int flag;
    public List<Detail>redeemLottery;
    public static class Detail{
        public int lotteryID;
        public String lotteryName;
    }
}
