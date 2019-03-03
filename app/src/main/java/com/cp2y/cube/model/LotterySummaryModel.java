package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by admin on 2016/12/12.
 */
public class LotterySummaryModel {
    private List<SummaryLottery>drawDetail;
    private List<String> entrance;
    public static class SummaryLottery {
        private String drawRedNumber;
        private String drawBlueNumber;
        private String drawTime;
        private String issue;
        private String lotteryName;
        private String remainTotal;
        private String saleTotal;
        private List<Summaryitems>items;
        public static class Summaryitems{
            private String prizeAmount;
            private String prizeItem;
            private String number;

            public String getNumber() {
                return number;
            }

            public void setNumber(String number) {
                this.number = number;
            }

            public String getPrizeAmount() {
                return prizeAmount;
            }

            public void setPrizeAmount(String prizeAmount) {
                this.prizeAmount = prizeAmount;
            }

            public String getPrizeItem() {
                return prizeItem;
            }

            public void setPrizeItem(String prizeItem) {
                this.prizeItem = prizeItem;
            }
        }

        public String getLotteryName() {
            return lotteryName;
        }

        public void setLotteryName(String lotteryName) {
            this.lotteryName = lotteryName;
        }

        public String getRemainTotal() {
            return remainTotal;
        }

        public void setRemainTotal(String remainTotal) {
            this.remainTotal = remainTotal;
        }

        public String getSaleTotal() {
            return saleTotal;
        }

        public void setSaleTotal(String saleTotal) {
            this.saleTotal = saleTotal;
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

        public List<Summaryitems> getItems() {
            return items;
        }

        public void setItems(List<Summaryitems> items) {
            this.items = items;
        }
    }

    public List<String> getEntrance() {
        return entrance;
    }

    public void setEntrance(List<String> entrance) {
        this.entrance = entrance;
    }

    public List<SummaryLottery> getDrawDetail() {
        return drawDetail;
    }

    public void setDrawDetail(List<SummaryLottery> drawDetail) {
        this.drawDetail = drawDetail;
    }
}
