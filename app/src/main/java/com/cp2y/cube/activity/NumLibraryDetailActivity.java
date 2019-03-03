package com.cp2y.cube.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.numlibrary.adapter.LibraryDetailAdapter;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.network.NetConst;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.FileUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.util.List;

public class NumLibraryDetailActivity extends BaseActivity {
    private ListView lv;
    private int id,flag;//0双色球,1大乐透,2福彩3D,3排列3,4排列5,5重庆时时彩
    private String issue;
    private LibraryDetailAdapter adapter;
    private AVLoadingIndicatorView AVLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_detail);
        setMainTitle("全部号码");
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        issue = getIntent().getStringExtra("issue");
        id = getIntent().getIntExtra("id", 0);
        flag=getIntent().getIntExtra("flag",0);
        lv = (ListView) findViewById(R.id.filter_result_lv);
        AVLoadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
        adapter = new LibraryDetailAdapter(this, flag);
        lv.setAdapter(adapter);
        initData();
//        Observable.create(new Observable.OnSubscribe<List<String>>() {
//            @Override
//            public void call(Subscriber<? super List<String>> subscriber) {
//
//                //libFile=FileUtils.getLibraryCacheFile(id);
//                //subscriber.onNext();
//            }
//        })
//                .compose(ApiHelper.applySchedulers())
//                .subscribe(new SafeOnlyNextSubscriber<List<String>>(){
//                    @Override
//                    public void onNext(List<String> args) {
//                        super.onNext(args);
//                        TextView textView = (TextView) findViewById(R.id.filter_result_tv_result);
//                        textView.setText("共{1}注".replace("{1}", String.valueOf(args.size())));
//                        adapter.reloadData(args);
//                    }
//                });
    }

    private void initData() {
        File libFile = FileUtils.getLibraryFile(issue, id,flag);
        //本地文件不存在 开启下载
        if(!libFile.exists()){
            NetHelper.LOTTERY_API.downloadLibrary(id).subscribe(new SafeOnlyNextSubscriber<String>(){
                @Override
                public void onNext(String args) {
                    super.onNext(args);
                    //移动下载到cache文件夹的数据到files文件夹奖期内
                    FileUtils.moveToFiles(String.valueOf(id),String.valueOf(issue),flag);
                    //重新读取展示数据
                    File libFile = FileUtils.getLibraryFile(issue, id,flag);
                    List<String> list=FileUtils.readMaxLine(libFile, Integer.MAX_VALUE);
                    TextView textView = (TextView) findViewById(R.id.filter_result_tv_result);
                    AVLoadingIndicator.setVisibility(View.GONE);
                    textView.setText("共{1}注".replace("{1}", String.valueOf(list.size())));
                    //尾部局
                    View footView = LayoutInflater.from(NumLibraryDetailActivity.this).inflate(R.layout.library_number_footer, null, false);
                    TextView tips = (TextView) footView.findViewById(R.id.foot_tips);
                    tips.setText("暂无更多");
                    if(lv.getFooterViewsCount()==0){
                        lv.addFooterView(footView);
                    }
                    adapter.reloadData(list);
                }
            });
        }else{
            List<String>list=FileUtils.readMaxLine(libFile, Integer.MAX_VALUE);
            AVLoadingIndicator.setVisibility(View.GONE);
            TextView textView = (TextView) findViewById(R.id.filter_result_tv_result);
            textView.setText("共{1}注".replace("{1}", String.valueOf(list.size())));
            //尾部局
            View footView = LayoutInflater.from(NumLibraryDetailActivity.this).inflate(R.layout.library_number_footer, null, false);
            TextView tips = (TextView) footView.findViewById(R.id.foot_tips);
            tips.setText("暂无更多");
            if(lv.getFooterViewsCount()==0){
                lv.addFooterView(footView);
            }
            adapter.reloadData(list);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_library_detail,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            String url = NetHelper.getAbsoluteUrl(NetConst.DOWN_NUMBER_TEXT_URL, "id", String.valueOf(id));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity( intent );
        } catch (Exception e) {
            TipsToast.showTips("打开浏览器失败");
        }
        return false;
    }
}
