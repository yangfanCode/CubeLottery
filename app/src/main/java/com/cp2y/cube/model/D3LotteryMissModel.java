package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2017/5/22.
 */
public class D3LotteryMissModel {
    private D3LotteryMiss data;

    public D3LotteryMiss getData() {
        return data;
    }

    public void setData(D3LotteryMiss data) {
        this.data = data;
    }

    public static class D3LotteryMiss{
        private List<List<List<Object>>> decide;
        private List<List<List<Object>>> miss;

        public List<List<List<Object>>> getDecide() {
            return decide;
        }

        public void setDecide(List<List<List<Object>>> decide) {
            this.decide = decide;
        }

        public List<List<List<Object>>> getMiss() {
            return miss;
        }

        public void setMiss(List<List<List<Object>>> miss) {
            this.miss = miss;
        }
    }
}
