package com.cp2y.cube.bean;

/**
 * Created by admin on 2017/3/13.
 */
public class TrendIgnore {
    public TrendIgnore(String trend, String ignore) {
        Trend = trend;
        Ignore = ignore;
    }

    private String Trend;
    private String Ignore;

    public String getTrend() {
        return Trend;
    }

    public void setTrend(String trend) {
        Trend = trend;
    }

    public String getIgnore() {
        return Ignore;
    }

    public void setIgnore(String ignore) {
        Ignore = ignore;
    }
}
