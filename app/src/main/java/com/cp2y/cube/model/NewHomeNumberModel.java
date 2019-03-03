package com.cp2y.cube.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by js on 2017/1/9.
 */
public class NewHomeNumberModel extends BaseModel implements Serializable {

    private int flag;
    @SerializedName("10002")
    private DoubleBall list_doubleBall;
    @SerializedName("10088")
    private Lotto list_lotto;
    @SerializedName("10001")
    private D3 list_d3;
    @SerializedName("10003")
    private P3 list_p3;
    @SerializedName("10004")
    private P3 list_p5;
    @SerializedName("10089")
    private P3 list_cqssc;
    private int listsize;
    private int pageSize;
    private List<String>keySort;


    public P3 getList_cqssc() {
        return list_cqssc;
    }

    public void setList_cqssc(P3 list_cqssc) {
        this.list_cqssc = list_cqssc;
    }

    public P3 getList_p5() {
        return list_p5;
    }

    public void setList_p5(P3 list_p5) {
        this.list_p5 = list_p5;
    }

    public P3 getList_p3() {
        return list_p3;
    }

    public void setList_p3(P3 list_p3) {
        this.list_p3 = list_p3;
    }

    public D3 getList_d3() {
        return list_d3;
    }

    public void setList_d3(D3 list_d3) {
        this.list_d3 = list_d3;
    }

    public List<String> getKeySort() {
        return keySort;
    }

    public void setKeySort(List<String> keySort) {
        this.keySort = keySort;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public DoubleBall getList_doubleBall() {
        return list_doubleBall;
    }

    public void setList_doubleBall(DoubleBall list_doubleBall) {
        this.list_doubleBall = list_doubleBall;
    }

    public Lotto getList_lotto() {
        return list_lotto;
    }

    public void setList_lotto(Lotto list_lotto) {
        this.list_lotto = list_lotto;
    }

    public int getListsize() {
        return listsize;
    }

    public void setListsize(int listsize) {
        this.listsize = listsize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public static class Drawer {
        private long createtime;
        private String drawNumber;
        private String issue;
        private String prize;

        public long getCreateTime() {
            return createtime;
        }

        public void setCreateTime(long createtime) {
            this.createtime = createtime;
        }

        public String getDrawNumber() {
            return drawNumber;
        }

        public void setDrawNumber(String drawNumber) {
            this.drawNumber = drawNumber;
        }

        public String getIssue() {
            return issue;
        }

        public void setIssue(String issue) {
            this.issue = issue;
        }

        public String getPrize() {
            return prize;
        }

        public void setPrize(String prize) {
            this.prize = prize;
        }
    }
    public static class NumberData{
        private int id;
        private long createtime;
        private int draw;
        private String issue;
        private int lotteryId;
        private String pin;
        private int userId;
        private boolean head;
        private String fiveNumber;
        private int playType;


        public int getPlayType() {
            return playType;
        }

        public void setPlayType(int playType) {
            this.playType = playType;
        }

        public String getFiveNumber() {
            return fiveNumber;
        }

        public void setFiveNumber(String fiveNumber) {
            this.fiveNumber = fiveNumber;
        }

        public boolean isHead() {
            return head;
        }

        public void setHead(boolean head) {
            this.head = head;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public long getCreatetime() {
            return createtime;
        }

        public void setCreatetime(long createtime) {
            this.createtime = createtime;
        }

        public int getDraw() {
            return draw;
        }

        public void setDraw(int draw) {
            this.draw = draw;
        }

        public String getIssue() {
            return issue;
        }

        public void setIssue(String issue) {
            this.issue = issue;
        }

        public int getLotteryId() {
            return lotteryId;
        }

        public void setLotteryId(int lotteryId) {
            this.lotteryId = lotteryId;
        }

        public String getPin() {
            return pin;
        }

        public void setPin(String pin) {
            this.pin = pin;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

    }

    public static class DoubleBall implements Serializable{
        private List<NumberData> list;
        private Drawer tlist;

        public Drawer getTlist() {
            return tlist;
        }

        public void setTlist(Drawer tlist) {
            this.tlist = tlist;
        }

        public List<NumberData> getList() {
            return list;
        }

        public void setList(List<NumberData> list) {
            this.list = list;
        }
    }
    public static class Lotto implements Serializable{
        private List<NumberData> list;
        private Drawer tlist;

        public Drawer getTlist() {
            return tlist;
        }

        public void setTlist(Drawer tlist) {
            this.tlist = tlist;
        }

        public List<NumberData> getList() {
            return list;
        }

        public void setList(List<NumberData> list) {
            this.list = list;
        }
    }
    public static class D3 implements Serializable{
        private List<NumberData> list;
        private Drawer tlist;

        public Drawer getTlist() {
            return tlist;
        }

        public void setTlist(Drawer tlist) {
            this.tlist = tlist;
        }

        public List<NumberData> getList() {
            return list;
        }

        public void setList(List<NumberData> list) {
            this.list = list;
        }
    }
    public static class P3 implements Serializable{
        private List<NumberData> list;
        private Drawer tlist;

        public Drawer getTlist() {
            return tlist;
        }

        public void setTlist(Drawer tlist) {
            this.tlist = tlist;
        }

        public List<NumberData> getList() {
            return list;
        }

        public void setList(List<NumberData> list) {
            this.list = list;
        }
    }

}
