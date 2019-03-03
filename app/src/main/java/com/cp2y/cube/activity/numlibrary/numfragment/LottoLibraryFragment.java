package com.cp2y.cube.activity.numlibrary.numfragment;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.cp2y.cube.R;
import com.cp2y.cube.activity.NumLibraryDetailActivity;
import com.cp2y.cube.activity.numlibrary.adapter.LottoNumberAdapter;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.DeleteLibraryModel;
import com.cp2y.cube.model.NewNumberModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.FileUtils;
import com.cp2y.cube.utils.LoginSPUtils;
import com.cp2y.cube.widgets.SwipeMenuHelper;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class LottoLibraryFragment extends Fragment {
    private LottoNumberAdapter adapter = null;
    private int currPage, totalPage = 1;
    private MaterialRefreshLayout refreshLayout;
    private SwipeMenuListView content_list;
    private AVLoadingIndicatorView AVLoading;
    private RelativeLayout num_library_layout;
    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_number_library, container, false);
        //删除号码库
        FileUtils.cleanInternalFiles(new File(FileUtils.LOTTO_PATH));
        //initChangeNumLibraryState();//改变号码库样式
        refreshLayout = (MaterialRefreshLayout) rootView.findViewById(R.id.library_refresh_layout);
        refreshLayout.setLoadMore(true);
        //refreshLayout.enableAutoRefreshLoadMore();
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshLayout.setLoadMore(true);
                refreshData();
            }
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (currPage < totalPage) {
                    super.onRefreshLoadMore(materialRefreshLayout);
                    loadData();
                } else {
                    ContextHelper.getApplication().runDelay(materialRefreshLayout::finishRefreshLoadMore, 200);
                    refreshLayout.setLoadMore(false);
                    if(content_list.getFooterViewsCount()==0){
                        content_list.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.numlibrary_foot,null));
                    }
                    //TipsToast.showTips("暂无更多");
                }
            }
        });
        content_list = (SwipeMenuListView) rootView.findViewById(R.id.list_content);
        AVLoading = (AVLoadingIndicatorView) rootView.findViewById(R.id.AVLoadingIndicator);
        adapter = new LottoNumberAdapter(getActivity());
        //添加头布局
        View head= LayoutInflater.from(getActivity()).inflate(R.layout.mynum_layout,null);
        num_library_layout = (RelativeLayout) head.findViewById(R.id.num_library_layout);
        content_list.addHeaderView(head);
        content_list.setAdapter(adapter);
        content_list.setOnItemClickListener((adapterView, view, i, l) -> {
            if(i==0)return;//点击头布局无效
            if(content_list.getFooterViewsCount()==1&&i==content_list.getLastVisiblePosition())return;
            int type = adapter.getItemViewType(i-1);//排除头布局
            if (type == 5) {//跳转下一个页面
                NewNumberModel.NumberData number = adapter.getItem(i-1);
                Intent it = new Intent(getActivity(), NumLibraryDetailActivity.class);
                it.putExtra("id", number.getId());
                it.putExtra("issue", number.getIssue());
                it.putExtra("flag",1);
                startActivity(it);
            }
        });
        content_list.setMenuCreator(menu -> {
            int type = menu.getViewType();
            int width = DisplayUtil.dip2px(40);
            if (type == 0) {
                return;
            } else if (type != 5) {
                SwipeMenuItem copyItem = new SwipeMenuItem(
                        getActivity().getApplicationContext());
                copyItem.setTitle("复制");
                copyItem.setTitleSize(12);
                copyItem.setTitleColor(ColorUtils.WHITE);
                copyItem.setBackground(new ColorDrawable(ColorUtils.NORMAL_GREEN));
                copyItem.setWidth(width);
                copyItem.setIcon(R.mipmap.icon_copy);
                menu.addMenuItem(copyItem);
            }
            menu.addMenuItem(SwipeMenuHelper.generateDeleteItem());
        });
        content_list.setOnMenuItemClickListener((position, menu, index) -> {
            int type = menu.getViewType();
            NewNumberModel.NumberData number = adapter.getItem(position);
            if (index == 1||type==5) {//删除
                deleteData(position);
            } else if (type != 0){//复制
                ClipboardManager copy = (ClipboardManager) getActivity().getApplicationContext().getSystemService(Context.CLIPBOARD_SERVICE);
                copy.setPrimaryClip(ClipData.newPlainText(null, number.getFiveNumber()));
                TipsToast.showTips("已成功复制至剪贴板");
            }
            return false;
        });
        loadData();
        return rootView;
    }

    /**
     * 删除记录
     * @param position
     */
    private void deleteData(int position) {
        NewNumberModel.NumberData number = adapter.getItem(position);
        NetHelper.LOTTERY_API.deleteNumberLibrary(number.getId(), number.getUserId())
                .subscribe(new SafeOnlyNextSubscriber<DeleteLibraryModel>(){
                    @Override
                    public void onNext(DeleteLibraryModel args) {
                        super.onNext(args);
                        adapter.removeData(position);
                        TipsToast.showTips("已删除");
                        if (adapter.getCount() == 0) refreshData();
                    }
                });
    }


    private void refreshData() {
        currPage = 0;
        NetHelper.LOTTERY_API.getNumberLibrary(LoginSPUtils.getInt("id",0), 10088, 0, ++currPage, 0)
                .doOnTerminate(() -> refreshLayout.finishRefresh())
                .subscribe(new SafeOnlyNextSubscriber<NewNumberModel>(){
                    @Override
                    public void onNext(NewNumberModel args) {
                        super.onNext(args);
                        if(args.getList()!=null&&args.getList().size()>0){
                            totalPage = args.getPageSize();
                            adapter.reloadData(args.getList(), args.getTlist());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        TipsToast.showTips("网络加载异常");
                    }
                });
    }

    private void loadData() {
        NetHelper.LOTTERY_API.getNumberLibrary(LoginSPUtils.getInt("id",0), 10088, 0, ++currPage, 0)
                .doOnTerminate(() -> refreshLayout.finishRefreshLoadMore())
                .subscribe(new SafeOnlyNextSubscriber<NewNumberModel>(){
                    @Override
                    public void onNext(NewNumberModel args) {
                        super.onNext(args);
                        totalPage = args.getPageSize();
                        adapter.addData(args.getList(), args.getTlist());
                        AVLoading.setVisibility(View.GONE);
                        content_list.setVisibility(View.VISIBLE);
                        content_list.setEmptyView(rootView.findViewById(R.id.numLibrary_tip));
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        TipsToast.showTips("网络加载异常");
                        AVLoading.setVisibility(View.GONE);
                        content_list.setEmptyView(rootView.findViewById(R.id.numLibrary_tip));
                        num_library_layout.setVisibility(View.GONE);
                    }
                });
    }

}
