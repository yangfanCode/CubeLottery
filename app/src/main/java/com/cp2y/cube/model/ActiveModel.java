package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2018/1/12.
 */
public class ActiveModel {
    private List<Detail>list;
    private int pageSize;

    public List<Detail> getList() {
        return list;
    }

    public void setList(List<Detail> list) {
        this.list = list;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public static class Detail{
        private String changeNum;
        private String commentsNum;
        private int iD;
        private String issue;
        private int lotteryID;
        private String lotteryName;
        private List<Item>nnList;
        private String portraitUrl;
        private String returnRate;
        private String shows;
        private int status;
        private String time;
        private String title;
        private int type;
        private int userID;
        private String userName;
        private String byUserName;
        private String byTitle;

        public String getByTitle() {
            return byTitle;
        }

        public void setByTitle(String byTitle) {
            this.byTitle = byTitle;
        }

        public String getByUserName() {
            return byUserName;
        }

        public void setByUserName(String byUserName) {
            this.byUserName = byUserName;
        }

        public String getChangeNum() {
            return changeNum;
        }

        public void setChangeNum(String changeNum) {
            this.changeNum = changeNum;
        }

        public String getCommentsNum() {
            return commentsNum;
        }

        public void setCommentsNum(String commentsNum) {
            this.commentsNum = commentsNum;
        }

        public int getiD() {
            return iD;
        }

        public void setiD(int iD) {
            this.iD = iD;
        }

        public String getIssue() {
            return issue;
        }

        public void setIssue(String issue) {
            this.issue = issue;
        }

        public int getLotteryID() {
            return lotteryID;
        }

        public void setLotteryID(int lotteryID) {
            this.lotteryID = lotteryID;
        }

        public String getLotteryName() {
            return lotteryName;
        }

        public void setLotteryName(String lotteryName) {
            this.lotteryName = lotteryName;
        }

        public List<Item> getNnList() {
            return nnList;
        }

        public void setNnList(List<Item> nnList) {
            this.nnList = nnList;
        }

        public String getPortraitUrl() {
            return portraitUrl;
        }

        public void setPortraitUrl(String portraitUrl) {
            this.portraitUrl = portraitUrl;
        }

        public String getReturnRate() {
            return returnRate;
        }

        public void setReturnRate(String returnRate) {
            this.returnRate = returnRate;
        }

        public String getShows() {
            return shows;
        }

        public void setShows(String shows) {
            this.shows = shows;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getUserID() {
            return userID;
        }

        public void setUserID(int userID) {
            this.userID = userID;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
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
}
