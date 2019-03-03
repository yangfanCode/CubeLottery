package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by js on 2017/1/3.
 */
public class IssueNumberDataModel extends BaseModel {

    private List<Byte> bnc;
    private int bnl;
    private List<IssueNumber> data;
    private List<Byte> dw;
    private int isl;
    private int lt;
    private boolean mc;
    private boolean n3;
    private boolean nc;
    private int nsl;
    private List<Byte> snc;
    private int snl;
    private List<Integer> ts;

    public List<Byte> getBnc() {
        return bnc;
    }

    public void setBnc(List<Byte> bnc) {
        this.bnc = bnc;
    }

    public int getBnl() {
        return bnl;
    }

    public void setBnl(int bnl) {
        this.bnl = bnl;
    }

    public List<IssueNumber> getData() {
        return data;
    }

    public void setData(List<IssueNumber> data) {
        this.data = data;
    }

    public List<Byte> getDw() {
        return dw;
    }

    public void setDw(List<Byte> dw) {
        this.dw = dw;
    }

    public int getIsl() {
        return isl;
    }

    public void setIsl(int isl) {
        this.isl = isl;
    }

    public int getLt() {
        return lt;
    }

    public void setLt(int lt) {
        this.lt = lt;
    }

    public boolean isMc() {
        return mc;
    }

    public void setMc(boolean mc) {
        this.mc = mc;
    }

    public boolean isN3() {
        return n3;
    }

    public void setN3(boolean n3) {
        this.n3 = n3;
    }

    public boolean isNc() {
        return nc;
    }

    public void setNc(boolean nc) {
        this.nc = nc;
    }

    public int getNsl() {
        return nsl;
    }

    public void setNsl(int nsl) {
        this.nsl = nsl;
    }

    public List<Byte> getSnc() {
        return snc;
    }

    public void setSnc(List<Byte> snc) {
        this.snc = snc;
    }

    public int getSnl() {
        return snl;
    }

    public void setSnl(int snl) {
        this.snl = snl;
    }

    public List<Integer> getTs() {
        return ts;
    }

    public void setTs(List<Integer> ts) {
        this.ts = ts;
    }

    public static class IssueNumber {
        private List<Integer> bn;
        private int io;
        private String iss;
        private String it;
        private List<List<Integer>> ms;
        private String n;
        private String nr;
        private List<Integer> sn;
        private List<List<String>> ts;

        public List<Integer> getBn() {
            return bn;
        }

        public void setBn(List<Integer> bn) {
            this.bn = bn;
        }

        public int getIo() {
            return io;
        }

        public void setIo(int io) {
            this.io = io;
        }

        public String getIss() {
            return iss;
        }

        public void setIss(String iss) {
            this.iss = iss;
        }

        public String getIt() {
            return it;
        }

        public void setIt(String it) {
            this.it = it;
        }

        public List<List<Integer>> getMs() {
            return ms;
        }

        public void setMs(List<List<Integer>> ms) {
            this.ms = ms;
        }

        public String getN() {
            return n;
        }

        public void setN(String n) {
            this.n = n;
        }

        public String getNr() {
            return nr;
        }

        public void setNr(String nr) {
            this.nr = nr;
        }

        public List<Integer> getSn() {
            return sn;
        }

        public void setSn(List<Integer> sn) {
            this.sn = sn;
        }

        public List<List<String>> getTs() {
            return ts;
        }

        public void setTs(List<List<String>> ts) {
            this.ts = ts;
        }
    }
}
