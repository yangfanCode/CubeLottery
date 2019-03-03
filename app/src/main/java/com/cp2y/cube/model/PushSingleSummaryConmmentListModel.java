package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2018/1/17.
 */
public class PushSingleSummaryConmmentListModel {
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
        private int byCriticsID;
        private String content;
        private int criticsID;
        private int iD;
        private List<Item>replyList;
        private String name;
        private int pushSingleID;
        private String tiem;
        private int type;
        private String url;

        public int getByCriticsID() {
            return byCriticsID;
        }

        public void setByCriticsID(int byCriticsID) {
            this.byCriticsID = byCriticsID;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getCriticsID() {
            return criticsID;
        }

        public void setCriticsID(int criticsID) {
            this.criticsID = criticsID;
        }

        public int getiD() {
            return iD;
        }

        public void setiD(int iD) {
            this.iD = iD;
        }

        public List<Item> getReplyList() {
            return replyList;
        }

        public void setReplyList(List<Item> replyList) {
            this.replyList = replyList;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getPushSingleID() {
            return pushSingleID;
        }

        public void setPushSingleID(int pushSingleID) {
            this.pushSingleID = pushSingleID;
        }

        public String getTiem() {
            return tiem;
        }

        public void setTiem(String tiem) {
            this.tiem = tiem;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
    public static class Item{
        private int byCriticsID;
        private String byCriticsName;
        private String content;
        private int criticsID;
        private String criticsName;
        private int iD;
        private int pushSingleID;
        private int type;

        public int getByCriticsID() {
            return byCriticsID;
        }

        public void setByCriticsID(int byCriticsID) {
            this.byCriticsID = byCriticsID;
        }

        public String getByCriticsName() {
            return byCriticsName;
        }

        public void setByCriticsName(String byCriticsName) {
            this.byCriticsName = byCriticsName;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getCriticsID() {
            return criticsID;
        }

        public void setCriticsID(int criticsID) {
            this.criticsID = criticsID;
        }

        public String getCriticsName() {
            return criticsName;
        }

        public void setCriticsName(String criticsName) {
            this.criticsName = criticsName;
        }

        public int getiD() {
            return iD;
        }

        public void setiD(int iD) {
            this.iD = iD;
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
}
