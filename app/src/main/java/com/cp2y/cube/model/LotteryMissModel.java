package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by js on 2017/1/10.
 */
public class LotteryMissModel {

    private int flag;
    private LotteryMiss data;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public LotteryMiss getData() {
        return data;
    }

    public void setData(LotteryMiss data) {
        this.data = data;
    }

    public static class LotteryMiss {
        private List<List<Integer>> bnc;
        private List<List<Integer>> snc;

        public List<List<Integer>> getBnc() {
            return bnc;
        }

        public void setBnc(List<List<Integer>> bnc) {
            this.bnc = bnc;
        }

        public List<List<Integer>> getSnc() {
            return snc;
        }

        public void setSnc(List<List<Integer>> snc) {
            this.snc = snc;
        }
    }
}
