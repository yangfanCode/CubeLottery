package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by admin on 2016/12/12.
 */
public class LotteryHistoryModel {
    private List<HistoryLottery>list;
    private int pageSize;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public static class HistoryLottery {
        private String drawRedNumber;
        private String drawBlueNumber;
        private String drawTime;
        private String issue;

        public String getDrawRedNumber() {
            return drawRedNumber;
        }

        public void setDrawRedNumber(String drawRedNumber) {
            this.drawRedNumber = drawRedNumber;
        }

        public String getDrawBlueNumber() {
            return drawBlueNumber;
        }

        public void setDrawBlueNumber(String drawBlueNumber) {
            this.drawBlueNumber = drawBlueNumber;
        }

        public String getDrawTime() {
            return drawTime;
        }

        public void setDrawTime(String drawTime) {
            this.drawTime = drawTime;
        }

        public String getIssue() {
            return issue;
        }

        public void setIssue(String issue) {
            this.issue = issue;
        }
    }
    private int lotteryId;
    private String lotteryName;

    public int getLotteryId() {
        return lotteryId;
    }

    public void setLotteryId(int lotteryId) {
        this.lotteryId = lotteryId;
    }

    public String getLotteryName() {
        return lotteryName;
    }

    public void setLotteryName(String lotteryName) {
        this.lotteryName = lotteryName;
    }

    public List<HistoryLottery> getList() {
        return list;
    }

    public void setList(List<HistoryLottery> list) {
        this.list = list;
    }
}
