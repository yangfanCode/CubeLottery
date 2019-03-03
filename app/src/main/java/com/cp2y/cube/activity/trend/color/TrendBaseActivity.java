package com.cp2y.cube.activity.trend.color;

import android.os.Bundle;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.adapter.TrendBaseAdapter;
import com.cp2y.cube.adapter.TrendDateAdapter;
import com.cp2y.cube.model.ConditionModel;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.widgets.HVListView;
import com.cp2y.cube.widgets.HVSRelativeLayout;

public class TrendBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend_base);
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((view -> finish()));
        setMainTitle("基本走势");
        HVSRelativeLayout relativeLayout = (HVSRelativeLayout) findViewById(R.id.trend_layout);
        HVListView dateList = (HVListView) findViewById(R.id.scroll_layout1);
        HVListView dataList = (HVListView) findViewById(R.id.scroll_list);
        relativeLayout.setDateList(dateList);
        relativeLayout.setDataList(dataList);
        relativeLayout.setHsvViewSize(DisplayUtil.dip2px(1829f), DisplayUtil.dip2px(9000f));
        TrendDateAdapter dateAdapter = new TrendDateAdapter(this);
        dateList.setAdapter(dateAdapter);
        TrendBaseAdapter dataAdapter = new TrendBaseAdapter(this, new ConditionModel());
        dataList.setAdapter(dataAdapter);

    }

}
