package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2017/6/2.
 */
public class D3NumberMiss {
    private Num num;
    private List<List<MissData>> special;

    public Num getNum() {
        return num;
    }

    public void setNum(Num num) {
        this.num = num;
    }

    public List<List<MissData>> getSpecial() {
        return special;
    }

    public void setSpecial(List<List<MissData>> special) {
        this.special = special;
    }

    public static class Num{
        private List<List<MissData>>miss;

        public List<List<MissData>> getMiss() {
            return miss;
        }

        public void setMiss(List<List<MissData>> miss) {
            this.miss = miss;
        }
    }
    public static class MissData{
        private int issues;
        private String value;
        private int showBi;
        private int emerge;
        private int miss;
        private int show;

        public int getIssues() {
            return issues;
        }

        public void setIssues(int issues) {
            this.issues = issues;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getShowBi() {
            return showBi;
        }

        public void setShowBi(int showBi) {
            this.showBi = showBi;
        }

        public int getEmerge() {
            return emerge;
        }

        public void setEmerge(int emerge) {
            this.emerge = emerge;
        }

        public int getMiss() {
            return miss;
        }

        public void setMiss(int miss) {
            this.miss = miss;
        }

        public int getShow() {
            return show;
        }

        public void setShow(int show) {
            this.show = show;
        }
    }
}
