package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2017/4/27.
 */
public class NewsModel  {
    private int flag;
    private String buyTotal;//投注总额
    private String remainTotal;//奖池累积
    private int noteNum;//中奖注数
    private String price;//中奖金额
    private String drawNumber;//开奖号码
    private String issue;//奖期
    private String cidPicture;//背景图
    private String homePicture;
    private List<News> list;
    private String listsize;
    private String pageSize;

    public String getBuyTotal() {
        return buyTotal;
    }

    public void setBuyTotal(String buyTotal) {
        this.buyTotal = buyTotal;
    }

    public String getRemainTotal() {
        return remainTotal;
    }

    public void setRemainTotal(String remainTotal) {
        this.remainTotal = remainTotal;
    }

    public int getNoteNum() {
        return noteNum;
    }

    public void setNoteNum(int noteNum) {
        this.noteNum = noteNum;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getCidPicture() {
        return cidPicture;
    }

    public void setCidPicture(String cidPicture) {
        this.cidPicture = cidPicture;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getHomePicture() {
        return homePicture;
    }

    public void setHomePicture(String homePicture) {
        this.homePicture = homePicture;
    }

    public List<News> getList() {
        return list;
    }

    public void setList(List<News> list) {
        this.list = list;
    }

    public String getListsize() {
        return listsize;
    }

    public void setListsize(String listsize) {
        this.listsize = listsize;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public static class News{
        private String id;
        private String date;
        private String downs;
        private String likeNum;
        private String title;
        private String lotteryId;
        private String titlePicture;


        public String getTitlePicture() {
            return titlePicture;
        }

        public void setTitlePicture(String titlePicture) {
            this.titlePicture = titlePicture;
        }

        public String getLotteryId() {
            return lotteryId;
        }

        public void setLotteryId(String lotteryId) {
            this.lotteryId = lotteryId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getDowns() {
            return downs;
        }

        public void setDowns(String downs) {
            this.downs = downs;
        }

        public String getLikeNum() {
            return likeNum;
        }

        public void setLikeNum(String likeNum) {
            this.likeNum = likeNum;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
