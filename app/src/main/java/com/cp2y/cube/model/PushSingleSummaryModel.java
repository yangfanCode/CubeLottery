package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2018/1/15.
 */
public class PushSingleSummaryModel {
    private String drawNumber;
    private int flag;
    private boolean isChange;
    private int type;
    private Detail pushSingle;
    private User user;
    private String rate;
    private List<NumberDetail>numberLisbrarylist;
    private List<ChangeDetail>changePushSingle;


    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDrawNumber() {
        return drawNumber;
    }

    public void setDrawNumber(String drawNumber) {
        this.drawNumber = drawNumber;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public boolean isChange() {
        return isChange;
    }

    public void setChange(boolean change) {
        isChange = change;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Detail getPushSingle() {
        return pushSingle;
    }

    public void setPushSingle(Detail pushSingle) {
        this.pushSingle = pushSingle;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<NumberDetail> getNumberLisbrarylist() {
        return numberLisbrarylist;
    }

    public void setNumberLisbrarylist(List<NumberDetail> numberLisbrarylist) {
        this.numberLisbrarylist = numberLisbrarylist;
    }

    public List<ChangeDetail> getChangePushSingle() {
        return changePushSingle;
    }

    public void setChangePushSingle(List<ChangeDetail> changePushSingle) {
        this.changePushSingle = changePushSingle;
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
    public static class ChangeDetail{
        private String byPortraitUrl;
        private String byUserName;
        private String byTitle;
        private int commentsNum;
        private int iD;
        private String issue;
        private List<Item>nnList;
        private String returnRate;
        private String shows;
        private int status;
        private String time;
        private String title;
        private int type;
        private int userID;
        private int lotteryID;
        private String lotteryName;
        private int forwardingID;

        public int getForwardingID() {
            return forwardingID;
        }

        public void setForwardingID(int forwardingID) {
            this.forwardingID = forwardingID;
        }

        public String getLotteryName() {
            return lotteryName;
        }

        public void setLotteryName(String lotteryName) {
            this.lotteryName = lotteryName;
        }

        public String getByTitle() {
            return byTitle;
        }

        public void setByTitle(String byTitle) {
            this.byTitle = byTitle;
        }

        public int getLotteryID() {
            return lotteryID;
        }

        public void setLotteryID(int lotteryID) {
            this.lotteryID = lotteryID;
        }

        public String getByPortraitUrl() {
            return byPortraitUrl;
        }

        public void setByPortraitUrl(String byPortraitUrl) {
            this.byPortraitUrl = byPortraitUrl;
        }

        public String getByUserName() {
            return byUserName;
        }

        public void setByUserName(String byUserName) {
            this.byUserName = byUserName;
        }

        public int getCommentsNum() {
            return commentsNum;
        }

        public void setCommentsNum(int commentsNum) {
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

        public List<Item> getNnList() {
            return nnList;
        }

        public void setNnList(List<Item> nnList) {
            this.nnList = nnList;
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
    }
    public static class NumberDetail{
        private int draw;
        private String drawNumber;
        private String fiveNumber;
        private int id;
        private String issue;
        private int lotteryId;
        private int noteNumber;
        private int playType;
        private String prize;
        private int status;
        private int userId;

        public int getDraw() {
            return draw;
        }

        public void setDraw(int draw) {
            this.draw = draw;
        }

        public String getDrawNumber() {
            return drawNumber;
        }

        public void setDrawNumber(String drawNumber) {
            this.drawNumber = drawNumber;
        }

        public String getFiveNumber() {
            return fiveNumber;
        }

        public void setFiveNumber(String fiveNumber) {
            this.fiveNumber = fiveNumber;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public int getNoteNumber() {
            return noteNumber;
        }

        public void setNoteNumber(int noteNumber) {
            this.noteNumber = noteNumber;
        }

        public int getPlayType() {
            return playType;
        }

        public void setPlayType(int playType) {
            this.playType = playType;
        }

        public String getPrize() {
            return prize;
        }

        public void setPrize(String prize) {
            this.prize = prize;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }
    public static class User{
        private String fansNum;
        private int iD;
        private boolean isAttention;
        private String name;
        private String url;

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

        public boolean isAttention() {
            return isAttention;
        }

        public void setAttention(boolean attention) {
            isAttention = attention;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
    public static class Detail{
        private String changeNum;
        private String issue;
        private int lotteryID;
        private int status;
        private String title;
        private int type;
        private String drawNumber;
        private String time;
        private String prize;


        public String getPrize() {
            return prize;
        }

        public void setPrize(String prize) {
            this.prize = prize;
        }

        public String getChangeNum() {
            return changeNum;
        }

        public void setChangeNum(String changeNum) {
            this.changeNum = changeNum;
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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
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

        public String getDrawNumber() {
            return drawNumber;
        }

        public void setDrawNumber(String drawNumber) {
            this.drawNumber = drawNumber;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
