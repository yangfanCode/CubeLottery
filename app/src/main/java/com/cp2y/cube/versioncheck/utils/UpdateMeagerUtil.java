package com.cp2y.cube.versioncheck.utils;

import android.content.Context;

import rx.Subscription;

public class UpdateMeagerUtil {
    private static final String TAG = "Update";
    private Context mContext;
    private Boolean isToast = false;
    public Subscription subscription;

    public UpdateMeagerUtil(Context c) {
        mContext = c;
    }

//    public void checkNewVersion(final Boolean showToash) {
//        this.isToast = showToash;
//        Map<String, Object> map = new HashMap<>();
//        map.put("device", 3);////设备 3：安卓 4：苹果 ~~~
//        map.put("appVersion", CommonUtil2.getVersionCode());
//        map.put("channel", CommonUtil.getChannelName(mContext));
//        subscription = HttpUtils.getInstance().getPost("", false, mContext).version(map).subscribeOn(Schedulers.io()).
//                observeOn(AndroidSchedulers.mainThread())
//                .subscribe(checkUpdateObserver);
//    }
//
//    public void checkNewVersion(Observer<ResultBean<UpdateInfo>> checkUpdateObserver) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("device", 3);////设备 3：安卓 4：苹果 ~~~
//        map.put("appVersion", CommonUtil2.getVersionCode());
//        map.put("channel", CommonUtil.getChannelName(mContext));
//        subscription = HttpUtils.getInstance().getPost("", false, mContext).version(map).subscribeOn(Schedulers.io()).
//                observeOn(AndroidSchedulers.mainThread())
//                .subscribe(checkUpdateObserver);
//    }
//
//    // 回调
//    Observer<ResultBean<UpdateInfo>> checkUpdateObserver = new HttpUtils.RxObserver<ResultBean<UpdateInfo>>(ApiConstant.VERSION) {
//        @Override
//        public void onSuccess(ResultBean<UpdateInfo> resultBean) {
//            if (resultBean == null) return;
//            if (resultBean.success && null != resultBean.data) {
//                UpdateInfo updateInfo = resultBean.data;
//                ApkDownloadTools apkDownloadTools = new ApkDownloadTools(mContext);
//                apkDownloadTools.setShowToast(isToast);
//                apkDownloadTools.setUpdateInfo(updateInfo, !isToast);
//            }
//
//        }
//
//        @Override
//        public void onError(Throwable e) {
//            super.onError(e);
//        }
//    };


}
