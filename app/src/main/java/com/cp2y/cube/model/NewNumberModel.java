package com.cp2y.cube.model;

import java.util.List;
import java.util.Map;

/**
 * Created by js on 2017/1/9.
 */
public class NewNumberModel extends BaseModel {

    private int flag;
    private Map<String,List<NumberData>> list;
    private int listsize;
    private int pageSize;
    private List<Drawer> tlist;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public Map<String, List<NumberData>> getList() {
        return list;
    }

    public void setList(Map<String, List<NumberData>> list) {
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

    public static class Drawer {
        private String drawNumber;
        private String issue;
        private String prize;

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
        private String drawNumber;
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

        public String getDrawNumber() {
            return drawNumber;
        }

        public void setDrawNumber(String drawNumber) {
            this.drawNumber = drawNumber;
        }

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
}
