package com.cp2y.cube.model;

/**
 * Created by js on 2017/1/9.
 */
public class IssueModel extends BaseModel {

    private int flag;
    private String issue;
    private boolean isDetail;
    private boolean isQuick;
    private long drawTime;

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

    public long getDrawTime() {
        return drawTime;
    }

    public void setDrawTime(long drawTime) {
        this.drawTime = drawTime;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }
}
