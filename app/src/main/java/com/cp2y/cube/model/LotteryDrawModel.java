package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by admin on 2016/12/12.
 */
public class LotteryDrawModel  {
    private List<DrawLottery>drawList;
    public static class DrawLottery{
        private String drawRedNumber;
        private String drawBlueNumber;
        private String drawTime;
        private String issue;
        private Integer issueId;
        private Integer lotteryId;
        private String lotteryName;
        private String threeDNumber;
        private boolean isQuick;
        private boolean isDetail;
        private String week;


        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

        public boolean isQuick() {
            return isQuick;
        }

        public void setQuick(boolean quick) {
            isQuick = quick;
        }

        public boolean isDetail() {
            return isDetail;
        }

        public void setDetail(boolean detail) {
            isDetail = detail;
        }

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

        public Integer getIssueId() {
            return issueId;
        }

        public void setIssueId(Integer issueId) {
            this.issueId = issueId;
        }

        public Integer getLotteryId() {
            return lotteryId;
        }

        public void setLotteryId(Integer lotteryId) {
            this.lotteryId = lotteryId;
        }

        public String getLotteryName() {
            return lotteryName;
        }

        public void setLotteryName(String lotteryName) {
            this.lotteryName = lotteryName;
        }

        public String getThreeDNumber() {
            return threeDNumber;
        }

        public void setThreeDNumber(String threeDNumber) {
            this.threeDNumber = threeDNumber;
        }
    }

    public List<DrawLottery> getDrawList() {
        return drawList;
    }

    public void setDrawList(List<DrawLottery> drawList) {
        this.drawList = drawList;
    }
}
