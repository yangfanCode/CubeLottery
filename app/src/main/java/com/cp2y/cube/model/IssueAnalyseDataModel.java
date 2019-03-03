package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by js on 2017/1/4.
 */
public class IssueAnalyseDataModel extends BaseModel {

    private List<Byte> bnc;
    private int bnl;
    private List<IssueAnalyse> data;
    private List<Byte> dw;
    private int isl;
    private int lt;
    private boolean n3;
    private int nsl;
    private List<Byte> snc;
    private int snl;
    private String tn;
    private List<Object> tpc;
    private List<Object> tps;
    private List<Object> tsc;
    private List<List<Integer>> tvc;

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

    public List<IssueAnalyse> getData() {
        return data;
    }

    public void setData(List<IssueAnalyse> data) {
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

    public boolean isN3() {
        return n3;
    }

    public void setN3(boolean n3) {
        this.n3 = n3;
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

    public String getTn() {
        return tn;
    }

    public void setTn(String tn) {
        this.tn = tn;
    }

    public List<Object> getTpc() {
        return tpc;
    }

    public void setTpc(List<Object> tpc) {
        this.tpc = tpc;
    }

    public List<Object> getTps() {
        return tps;
    }

    public void setTps(List<Object> tps) {
        this.tps = tps;
    }

    public List<Object> getTsc() {
        return tsc;
    }

    public void setTsc(List<Object> tsc) {
        this.tsc = tsc;
    }

    public List<List<Integer>> getTvc() {
        return tvc;
    }

    public void setTvc(List<List<Integer>> tvc) {
        this.tvc = tvc;
    }

    public static class IssueAnalyse {
        private int io;
        private String iss;
        private String it;
        private String n;
        private String nr;
        private List<Object> ta;
        private List<Object> tam;
        private List<String> tp;
        private List<List<Integer>> tpm;
        private List<String> ts;
        private List<List<Integer>> tsm;
        private List<List<Integer>> tv;
        private List<List<List<Integer>>> tvm;
        private List<List<String>> tvn;

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

        public List<Object> getTa() {
            return ta;
        }

        public void setTa(List<Object> ta) {
            this.ta = ta;
        }

        public List<Object> getTam() {
            return tam;
        }

        public void setTam(List<Object> tam) {
            this.tam = tam;
        }

        public List<String> getTp() {
            return tp;
        }

        public void setTp(List<String> tp) {
            this.tp = tp;
        }

        public List<List<Integer>> getTpm() {
            return tpm;
        }

        public void setTpm(List<List<Integer>> tpm) {
            this.tpm = tpm;
        }

        public List<String> getTs() {
            return ts;
        }

        public void setTs(List<String> ts) {
            this.ts = ts;
        }

        public List<List<Integer>> getTsm() {
            return tsm;
        }

        public void setTsm(List<List<Integer>> tsm) {
            this.tsm = tsm;
        }

        public List<List<Integer>> getTv() {
            return tv;
        }

        public void setTv(List<List<Integer>> tv) {
            this.tv = tv;
        }

        public List<List<List<Integer>>> getTvm() {
            return tvm;
        }

        public void setTvm(List<List<List<Integer>>> tvm) {
            this.tvm = tvm;
        }

        public List<List<String>> getTvn() {
            return tvn;
        }

        public void setTvn(List<List<String>> tvn) {
            this.tvn = tvn;
        }
    }

}
