package com.cp2y.cube.activity.pushsingle.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.NumLibraryDetailActivity;
import com.cp2y.cube.activity.pushsingle.adapter.LottoSelectAdapter;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.SingletonSelectSingleCheck;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.SelectSingleNumModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.LoginSPUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectLottoFragment extends Fragment implements MyInterface.partNotify{
    private ListView lv;
    private AVLoadingIndicatorView AVLoading;
    private RelativeLayout num_library_layout;
    private LottoSelectAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_select_lotto, container, false);
        initView(rootView);
        initData();
        initLisener();
        return rootView;
    }
    private void initLisener() {
        lv.setOnItemClickListener(((parent, view, position, id) -> {
            if(position==0)return;//点击头布局无效
            if(lv.getFooterViewsCount()==1&&position==lv.getLastVisiblePosition())return;//点击尾部局无效
            int type = adapter.getItemViewType(position-1);//排除头布局
            if (type == 5) {//跳转下一个页面
                SelectSingleNumModel.Detail number = adapter.getItem(position-1);
                Intent it = new Intent(getActivity(), NumLibraryDetailActivity.class);
                it.putExtra("id", number.getId());
                it.putExtra("issue", number.getIssue());
                it.putExtra("flag",0);
                startActivity(it);
            }
        }));
    }

    private void initData() {
        NetHelper.LOTTERY_API.getSelectSingleNumber(LoginSPUtils.getInt("id", 0),10088).subscribe(new SafeOnlyNextSubscriber<SelectSingleNumModel>(){
            @Override
            public void onNext(SelectSingleNumModel args) {
                super.onNext(args);
                AVLoading.setVisibility(View.GONE);
                List<SelectSingleNumModel.Detail> list=new ArrayList<SelectSingleNumModel.Detail>();
                if(args.getList()!=null&&args.getList().size()>0){
                    SelectSingleNumModel.Detail detail=new SelectSingleNumModel.Detail();
                    list.add(detail);//第一条头部
                    list.addAll(args.getList());
                    adapter.loadData(list);
                    lv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                AVLoading.setVisibility(View.GONE);
                TipsToast.showNewTips("请检查网络设置");
            }
        });
    }

    private void initView(View rootView) {
        lv = (ListView)rootView. findViewById(R.id.my_selectSingle_lv);
        AVLoading = (AVLoadingIndicatorView)rootView. findViewById(R.id.AVLoadingIndicator);
        //添加头布局
        View head= LayoutInflater.from(getActivity()).inflate(R.layout.mynum_layout,null);
        num_library_layout = (RelativeLayout) head.findViewById(R.id.num_library_layout);
        lv.addHeaderView(head);
        adapter=new LottoSelectAdapter(getActivity());
        lv.setAdapter(adapter);
        adapter.setNotify(this);
    }

    private void updateSingle(int position) {
        /**第一个可见的位置**/
        int firstVisiblePosition = lv.getFirstVisiblePosition();
        /**最后一个可见的位置**/
        int lastVisiblePosition = lv.getLastVisiblePosition();

        /**在看见范围内才更新，不可见的滑动后自动会调用getView方法更新**/
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            /**获取指定位置view对象**/
            View view = lv.getChildAt(position - firstVisiblePosition);
            CheckBox check= (CheckBox) view.findViewById(R.id.select_single_check);
            //头布局占以为 -1
            if(check!=null)check.setChecked(SingletonSelectSingleCheck.isLottoContains(position-1));//是否选择
        }
    }


    //局部刷新
    @Override
    public void notifyListView() {
        /**第一个可见的位置**/
        int firstVisiblePosition = lv.getFirstVisiblePosition();
        /**最后一个可见的位置**/
        int lastVisiblePosition = lv.getLastVisiblePosition();
        for(int i=firstVisiblePosition;i<=lastVisiblePosition;i++){
            if(i==0)continue;
            updateSingle(i);
        }
    }

    //获取选单数据 用#区分
    public String getSelectSingleData() {
        StringBuilder stringBuilder1 = new StringBuilder();
        StringBuilder stringBuilder2 = new StringBuilder();
        for (Iterator<Integer> iterator = SingletonSelectSingleCheck.getLottoObj().iterator(); iterator.hasNext();) {
            Integer pos = iterator.next();
            SelectSingleNumModel.Detail model = adapter.getItem(pos);
            if (pos == 0) continue;//排除全部 选项
            if (stringBuilder1.toString().length() == 0) {//第一条数据 不拼,
                stringBuilder1.append(String.valueOf(model.getId()));
            } else {
                stringBuilder1.append(",".concat(String.valueOf(model.getId())));
            }
            if (stringBuilder2.toString().length() == 0) {//第一条数据 不拼,
                stringBuilder2.append(String.valueOf(model.getPlayType())+"|"+String.valueOf(model.getNoteNumber()));
            } else {
                stringBuilder2.append(",".concat(String.valueOf(model.getPlayType())+"|"+String.valueOf(model.getNoteNumber())));
            }
        }
        return stringBuilder1.toString().concat("#").concat(stringBuilder2.toString());
    }



    //获得奖期
    public String getIssue() {
        for (int i = 1, size = adapter.getCount(); i < size; i++) {
            String issue = adapter.getItem(i).getIssue();
            if (!TextUtils.isEmpty(issue)) return issue;
        }
        return "";
    }

    //获得彩种id
    public int getLotteryId() {
        return 10088;
    }
}
