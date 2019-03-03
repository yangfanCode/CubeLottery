package com.cp2y.cube.model;

/**
 * Created by admin on 2017/1/23.
 */
public class VersionControlModel {
    private int flag;
    private VersionControlItem item;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public VersionControlItem getItem() {
        return item;
    }

    public void setItem(VersionControlItem item) {
        this.item = item;
    }

    public static class  VersionControlItem {
        private String createTime;
        private int currentVersionid;
        private String currentVersiontitle;
        private String desc;
        private String generalizeName;
        private String generalizeUid;
        private int id;
        private int requestType;
        private String source;
        private int status;
        private int type;
        private String url;
        private String versionNo;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getCurrentVersionid() {
            return currentVersionid;
        }

        public void setCurrentVersionid(int currentVersionid) {
            this.currentVersionid = currentVersionid;
        }

        public String getCurrentVersiontitle() {
            return currentVersiontitle;
        }

        public void setCurrentVersiontitle(String currentVersiontitle) {
            this.currentVersiontitle = currentVersiontitle;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getGeneralizeName() {
            return generalizeName;
        }

        public void setGeneralizeName(String generalizeName) {
            this.generalizeName = generalizeName;
        }

        public String getGeneralizeUid() {
            return generalizeUid;
        }

        public void setGeneralizeUid(String generalizeUid) {
            this.generalizeUid = generalizeUid;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getRequestType() {
            return requestType;
        }

        public void setRequestType(int requestType) {
            this.requestType = requestType;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
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

        public String getVersionNo() {
            return versionNo;
        }

        public void setVersionNo(String versionNo) {
            this.versionNo = versionNo;
        }
    }

}
