package com.cp2y.cube.model;

/**
 * Created by admin on 2017/2/8.
 */
public class BaiduMapPoiModel {

    private poiResult result;

    public poiResult getResult() {
        return result;
    }

    public void setResult(poiResult result) {
        this.result = result;
    }

    public static class AddressPoi{
        private String province;

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }
    }

    public static class poiResult{
        private AddressPoi addressComponent;

        public AddressPoi getAddressComponent() {
            return addressComponent;
        }

        public void setAddressComponent(AddressPoi addressComponent) {
            this.addressComponent = addressComponent;
        }


    }
}
