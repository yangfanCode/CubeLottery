package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2017/5/3.行业动态model
 */
public class NewsIndustryModel {
    private int flag;
    private String listsize;
    private String pageSize;
    private List<News>listYW;
    private List<News>listDT;

    public List<News> getListYW() {
        return listYW;
    }

    public void setListYW(List<News> listYW) {
        this.listYW = listYW;
    }

    public List<News> getListDT() {
        return listDT;
    }

    public void setListDT(List<News> listDT) {
        this.listDT = listDT;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
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
