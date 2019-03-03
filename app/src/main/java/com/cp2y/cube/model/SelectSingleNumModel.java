package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2018/1/10.
 */
public class SelectSingleNumModel {
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
        private String fiveNumber;
        private int id;
        private String issue;
        private int lotteryId;
        private int noteNumber;
        private int playType;
        private int status;

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

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
