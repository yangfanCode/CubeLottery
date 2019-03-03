package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2017/4/7.
 */
public class ScanCashNumModel {
    private CashMoney drawDetail;
    private int flag;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public CashMoney getDrawDetail() {
        return drawDetail;
    }

    public void setDrawDetail(CashMoney drawDetail) {
        this.drawDetail = drawDetail;
    }

    public static class CashMoney{
        private String drawBlueNumber;
        private String drawRedNumber;
        private String total;
        private List<prize>items;

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getDrawBlueNumber() {
            return drawBlueNumber;
        }

        public void setDrawBlueNumber(String drawBlueNumber) {
            this.drawBlueNumber = drawBlueNumber;
        }

        public String getDrawRedNumber() {
            return drawRedNumber;
        }

        public void setDrawRedNumber(String drawRedNumber) {
            this.drawRedNumber = drawRedNumber;
        }

        public List<prize> getItems() {
            return items;
        }

        public void setItems(List<prize> items) {
            this.items = items;
        }
    }
    public static class prize{
        private String money;
        private String number;
        private String prizeAmount;//奖金
        private String prizeItem;//奖项名
        private String moneyAward;//追加中奖奖金
        private String numberAward;//追加注数
        private String prizeAmountAward;//追加奖金

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

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

        public String getMoneyAward() {
            return moneyAward;
        }

        public void setMoneyAward(String moneyAward) {
            this.moneyAward = moneyAward;
        }

        public String getNumberAward() {
            return numberAward;
        }

        public void setNumberAward(String numberAward) {
            this.numberAward = numberAward;
        }

        public String getPrizeAmountAward() {
            return prizeAmountAward;
        }

        public void setPrizeAmountAward(String prizeAmountAward) {
            this.prizeAmountAward = prizeAmountAward;
        }
    }
}
