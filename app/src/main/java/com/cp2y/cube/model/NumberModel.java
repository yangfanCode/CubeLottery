package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by js on 2017/1/9.
 */
public class NumberModel extends BaseModel {

    private int flag;
    private List<Number> list;
    private int listsize;
    private int pageSize;
    private List<Drawer> tlist;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public List<Number> getList() {
        return list;
    }

    public void setList(List<Number> list) {
        this.list = list;
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

    public List<Drawer> getTlist() {
        return tlist;
    }

    public void setTlist(List<Drawer> tlist) {
        this.tlist = tlist;
    }

    public static class Number {
        private int id;
        private long createtime;
        private int draw;
        private int issue;
        private int lotteryId;
        private String pin;
        private int userId;
        private long prize;
        private boolean head;
        private List<String> list;

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
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

        public int getIssue() {
            return issue;
        }

        public void setIssue(int issue) {
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

        public long getPrize() {
            return prize;
        }

        public void setPrize(long prize) {
            this.prize = prize;
        }
    }

    public static class Drawer {
        private String drawNumber;
        private int issue;
        private long prize;

        public String getDrawNumber() {
            return drawNumber;
        }

        public void setDrawNumber(String drawNumber) {
            this.drawNumber = drawNumber;
        }

        public int getIssue() {
            return issue;
        }

        public void setIssue(int issue) {
            this.issue = issue;
        }

        public long getPrize() {
            return prize;
        }

        public void setPrize(long prize) {
            this.prize = prize;
        }
    }
}
