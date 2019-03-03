package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2018/1/9.
 */
public class AttentionFansListModel {
    private int pageSize;

    private List<Detail>item;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<Detail> getItem() {
        return item;
    }

    public void setItem(List<Detail> item) {
        this.item = item;
    }

    public static class Detail{
        private String nickName;
        private String createTime;
        private String headUrl;
        private int id;
        private int subscribeUserId;
        private int userId;
        private int subscribeType;
        private int type;
        private String interest;

        public int getSubscribeType() {
            return subscribeType;
        }

        public void setSubscribeType(int subscribeType) {
            this.subscribeType = subscribeType;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getInterest() {
            return interest;
        }

        public void setInterest(String interest) {
            this.interest = interest;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getCreateTime() {
            return createTime;
        }

        public String getHeadUrl() {
            return headUrl;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getSubscribeUserId() {
            return subscribeUserId;
        }

        public void setSubscribeUserId(int subscribeUserId) {
            this.subscribeUserId = subscribeUserId;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }
    }
}
