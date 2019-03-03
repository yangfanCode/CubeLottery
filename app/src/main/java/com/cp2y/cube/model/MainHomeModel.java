package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2018/1/3.
 */
public class MainHomeModel {
    private List<Detail>list;
    private userDetail user;

    public userDetail getUser() {
        return user;
    }

    public void setUser(userDetail user) {
        this.user = user;
    }

    public List<Detail> getList() {
        return list;
    }

    public void setList(List<Detail> list) {
        this.list = list;
    }

    public static class Detail{
        private LotteryDetail draw;
        private List<PushDetail>pushSingle;

        public LotteryDetail getDraw() {
            return draw;
        }

        public void setDraw(LotteryDetail draw) {
            this.draw = draw;
        }

        public List<PushDetail> getPushSingle() {
            return pushSingle;
        }

        public void setPushSingle(List<PushDetail> pushSingle) {
            this.pushSingle = pushSingle;
        }

    }
    public static class LotteryDetail{
        private String drawBlueNumber;
        private String drawRedNumber;
        private String drawTime;
        private boolean isDetail;
        private boolean isQuick;
        private String issue;
        private int lotteryId;
        private String lotteryName;
        private int type;
        private String week;

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

        public String getDrawTime() {
            return drawTime;
        }

        public void setDrawTime(String drawTime) {
            this.drawTime = drawTime;
        }

        public boolean isDetail() {
            return isDetail;
        }

        public void setDetail(boolean detail) {
            isDetail = detail;
        }

        public boolean isQuick() {
            return isQuick;
        }

        public void setQuick(boolean quick) {
            isQuick = quick;
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

        public String getLotteryName() {
            return lotteryName;
        }

        public void setLotteryName(String lotteryName) {
            this.lotteryName = lotteryName;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }
    }
    public static class PushDetail{
        private List<Item>nnList;
        private String prize;
        private int type;
        private int pushSingleID;

        public List<Item> getNnList() {
            return nnList;
        }

        public void setNnList(List<Item> nnList) {
            this.nnList = nnList;
        }

        public String getPrize() {
            return prize;
        }

        public void setPrize(String prize) {
            this.prize = prize;
        }

        public int getPushSingleID() {
            return pushSingleID;
        }

        public void setPushSingleID(int pushSingleID) {
            this.pushSingleID = pushSingleID;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
    public static class Item{
        private String name;
        private String note;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }
    public static class userDetail{
        private String attentionNum;
        private String fansNum;
        private int iD;
        private String name;
        private String pushSingleNum;
        private String url;

        public String getAttentionNum() {
            return attentionNum;
        }

        public void setAttentionNum(String attentionNum) {
            this.attentionNum = attentionNum;
        }

        public String getFansNum() {
            return fansNum;
        }

        public void setFansNum(String fansNum) {
            this.fansNum = fansNum;
        }

        public int getiD() {
            return iD;
        }

        public void setiD(int iD) {
            this.iD = iD;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPushSingleNum() {
            return pushSingleNum;
        }

        public void setPushSingleNum(String pushSingleNum) {
            this.pushSingleNum = pushSingleNum;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
