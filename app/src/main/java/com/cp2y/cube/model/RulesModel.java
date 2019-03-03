package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2017/8/18.
 */
public class RulesModel {
    private int flag;
    private String drawTime;
    private int lotteryID;
    private String reMoneyRate;
    private String rule;
    private List<detail>winConditions;


    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getDrawTime() {
        return drawTime;
    }

    public void setDrawTime(String drawTime) {
        this.drawTime = drawTime;
    }

    public int getLotteryID() {
        return lotteryID;
    }

    public void setLotteryID(int lotteryID) {
        this.lotteryID = lotteryID;
    }

    public String getReMoneyRate() {
        return reMoneyRate;
    }

    public void setReMoneyRate(String reMoneyRate) {
        this.reMoneyRate = reMoneyRate;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public List<detail> getWinConditions() {
        return winConditions;
    }

    public void setWinConditions(List<detail> winConditions) {
        this.winConditions = winConditions;
    }

    public static class detail{
        private String awardLevel;
        private String bonus;
        private String explain;

        public String getAwardLevel() {
            return awardLevel;
        }

        public void setAwardLevel(String awardLevel) {
            this.awardLevel = awardLevel;
        }

        public String getBonus() {
            return bonus;
        }

        public void setBonus(String bonus) {
            this.bonus = bonus;
        }

        public String getExplain() {
            return explain;
        }

        public void setExplain(String explain) {
            this.explain = explain;
        }
    }
}
