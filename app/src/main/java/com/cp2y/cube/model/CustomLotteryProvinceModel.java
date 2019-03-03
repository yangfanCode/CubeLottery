package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2017/8/7.
 */
public class CustomLotteryProvinceModel {
    private List<LotteryDetail> category;
    private List<LotteryDetail> provinceList;

    public List<LotteryDetail> getcategory() {
        return category;
    }

    public void setcategory(List<LotteryDetail> category) {
        this.category = category;
    }

    public List<LotteryDetail> getprovinceList() {
        return provinceList;
    }

    public void setprovinceList(List<LotteryDetail> provinceList) {
        this.provinceList = provinceList;
    }

    public class LotteryDetail{
        private int id;
        private String province;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }
    }
}
