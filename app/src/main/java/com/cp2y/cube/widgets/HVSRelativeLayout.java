package com.cp2y.cube.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.cp2y.cube.adapter.TrendSimpleAdapter;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.widgets.listener.ScrollViewListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by js on 2016/12/27.
 */
public class HVSRelativeLayout extends RelativeLayout {

    private int hvX, hvY, hsX, hsY;
    private int width, height;
    private List<VScrollView> vList = new ArrayList<>();
    private List<HScrollView> hList = new ArrayList<>();
    private HVListView dateList = null, dataList = null;
    private HVScrollView hvScrollView;

    private ScrollViewListener listener = new ScrollViewListener() {
        @Override
        public void onScrollChanged(View scrollView, int x, int y, int oldx, int oldy) {
            if (scrollView instanceof VScrollView) {
                if (hvScrollView != null) hvScrollView.scrollTo(hvX, y);
                for (VScrollView v:vList) {
                    if (v != scrollView) {
                        v.scrollTo(x, y);
                    }
                }
                hsY = scrollView.getScrollY();
            }  else if (scrollView instanceof HScrollView) {
                if (hvScrollView != null) hvScrollView.scrollTo(x, hvY);
                for (HScrollView v:hList) {
                    if (v != scrollView) {
                        v.scrollTo(x, y);
                    }
                }
                hsX = scrollView.getScrollX();
            } else if (scrollView == dateList){
                hvScrollView.scrollTo(hvX, dateList.getListScrollY());
            } else {
                hvX = x;
                hvY = y;
                for (VScrollView v:vList) {
                    v.scrollTo(0, y);
                }
                for (HScrollView v:hList
                        ) {
                    v.scrollTo(x, 0);
                }
                int deltaY1 = hvY - dateList.getListScrollY();
                int deltaY2 = hvY - dataList.getListScrollY();
                if (dateList != null && deltaY1 != 0)  {
                    dateList.smoothScrollBy(deltaY1, 0);
                }
                if (dataList != null)  {
                    if (deltaY2 != 0) dataList.smoothScrollBy(deltaY2, 0);
                    try {
                        TrendSimpleAdapter adapter = null;
                        if (dataList.getAdapter() instanceof TrendSimpleAdapter) {
                            adapter = (TrendSimpleAdapter) dataList.getAdapter();
                            adapter.setXoffset(x);
                        }
                        for (int i = 0; i < dataList.getChildCount(); i++) {
                            View rootView = dataList.getChildAt(i);
                            rootView.scrollTo(x, 0);
                            if (adapter != null) adapter.offsetSeprateLine(rootView);
                        }
                    } catch (Exception e) {
                        TipsToast.showTips(e.getMessage());
                    }
                }
            }
        }
    };

    public void keepOnHorizontal() {
        ContextHelper.getApplication().runDelay(()->{
            dateList.smoothScrollBy(1,0);//下滑一下
            dateList.smoothScrollBy(-1,0);//上滑一下
        },100);
    }

    public void setDateList(HVListView dateList) {
        this.dateList = dateList;
        dateList.setScrollViewListener(listener);
    }

    public void scrollBottom() {
        dateList.setSelection(dateList.getCount() - 1);//listview定位到最后
        dataList.setSelection(dataList.getCount() - 1);//listview定位到最后
        try {//scrollview也需要定位到最后
            ContextHelper.getApplication().runDelay(()->{
                dataList.smoothScrollBy(dateList.getListScrollY() - dataList.getListScrollY(), 0); hvScrollView.scrollTo(hvX, height);
            },100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDataList(HVListView dataList) {
        this.dataList = dataList;
    }

    public HVSRelativeLayout(Context context) {
        super(context);
    }

    public HVSRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HVSRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(View child) {
        if (child instanceof VScrollView) {
            VScrollView vScrollView = (VScrollView) child;
            vScrollView.setScrollViewListener(listener);
            vList.add(vScrollView);
        } else if (child instanceof HScrollView) {
            HScrollView hScrollView = (HScrollView) child;
            hScrollView.setScrollViewListener(listener);
            hList.add(hScrollView);
        } else if (child instanceof  HVScrollView) {
            hvScrollView = (HVScrollView) child;
            hvScrollView.setScrollViewListener(listener);
        }
    }

    /**
     * 设置滚动主页内容宽高
     * @param width
     * @param height
     */
    public void setHsvViewSize(int width, int height) {
        try {
            ViewGroup viewGroup = (ViewGroup) hvScrollView.getChildAt(0);
            View view = viewGroup.getChildAt(0);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            this.width = width;
            this.height = height;
            view.setLayoutParams(layoutParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addView(View child) {
        initView(child);
        super.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        initView(child);
        super.addView(child, index);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        initView(child);
        super.addView(child, params);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        initView(child);
        super.addView(child, index, params);
    }
}
