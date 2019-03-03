package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by admin on 2017/3/23.
 */
public class BaiduCheckLotteryModel {
    private List<lotteryData>retData;

    public List<lotteryData> getRetData() {
        return retData;
    }

    public void setRetData(List<lotteryData> retData) {
        this.retData = retData;
    }
    public static class lotteryData{
        private String word;

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }
    }
}
