package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by js on 2016/11/29.
 */
public class LotteryListModel {

    private List<Lottery> list;
    private int flag;
    private List<UrllAddress> urlAddress;
    private String found;

    public List<Lottery> getList() {
        return list;
    }

    public void setList(List<Lottery> list) {
        this.list = list;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public List<UrllAddress> getUrlAddress() {
        return urlAddress;
    }

    public void setUrlAddress(List<UrllAddress> urlAddress) {
        this.urlAddress = urlAddress;
    }

    public String getFound() {
        return found;
    }

    public void setFound(String found) {
        this.found = found;
    }

    @Override
    public String toString() {
        return "LotteryListModel{" +
                "list=" + list +
                ", flag=" + flag +
                ", urlAddress=" + urlAddress +
                ", found='" + found + '\'' +
                '}';
    }

    public static class Lottery {
        private String message;
        private String lotteryPic;
        private long remainTime;
        private int lotteryId;
        private String event;
        private String h5Url;
        private int isStop;
        private String issue;
        private int issueId;
        private boolean linkH5;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getLotteryPic() {
            return lotteryPic;
        }

        public void setLotteryPic(String lotteryPic) {
            this.lotteryPic = lotteryPic;
        }

        public long getRemainTime() {
            return remainTime;
        }

        public void setRemainTime(long remainTime) {
            this.remainTime = remainTime;
        }

        public int getLotteryId() {
            return lotteryId;
        }

        public void setLotteryId(int lotteryId) {
            this.lotteryId = lotteryId;
        }

        public String getEvent() {
            return event;
        }

        public void setEvent(String event) {
            this.event = event;
        }

        public String getH5Url() {
            return h5Url;
        }

        public void setH5Url(String h5Url) {
            this.h5Url = h5Url;
        }

        public int getIsStop() {
            return isStop;
        }

        public void setIsStop(int isStop) {
            this.isStop = isStop;
        }

        public String getIssue() {
            return issue;
        }

        public void setIssue(String issue) {
            this.issue = issue;
        }

        public int getIssueId() {
            return issueId;
        }

        public void setIssueId(int issueId) {
            this.issueId = issueId;
        }

        public boolean isLinkH5() {
            return linkH5;
        }

        public void setLinkH5(boolean linkH5) {
            this.linkH5 = linkH5;
        }
    }

    public static class UrllAddress {
        private String pictureUrl;

        public String getPictureUrl() {
            return pictureUrl;
        }

        public void setPictureUrl(String pictureUrl) {
            this.pictureUrl = pictureUrl;
        }

        @Override
        public String toString() {
            return "UrllAddress{" +
                    "pictureUrl='" + pictureUrl + '\'' +
                    '}';
        }
    }

}
