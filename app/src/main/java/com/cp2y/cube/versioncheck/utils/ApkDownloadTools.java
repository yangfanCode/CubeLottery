package com.cp2y.cube.versioncheck.utils;
import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.app.AlertDialog.Builder;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.MyListView;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.model.VersionControlModel;
import com.cp2y.cube.utils.LogUtil;
import com.cp2y.cube.versioncheck.NewVersionInfoAdapter;
import com.cp2y.cube.versioncheck.interfaces.ApkDownloadListener;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.yangfan.utils.CommonUtils;
import com.yangfan.utils.ToastTools;
import com.yangfan.widget.CustomDialog;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;


/**
 * Created by yangfan on 2017/9/23.
 * nrainyseason@163.com
 */

public class ApkDownloadTools {
    private static final String TAG = "ApkDownloadTools";
    private Context mContext;
    private Dialog downloadDialog;
    private ProgressBar mProgress;
    private int progress;
    private String path;//下载链接
    private static final int DOWN_UPDATE = 1;
    private static final int DOWN_UPDATE2 = 11;
    private static final int DOWN_OVER = 2;
    private static final int DOWN_ERROR = 3;
    /* 下载包安装路径 */
    private static final String savePath = FilePathUtils.getUpdateSavePath();
    private static final String fileName = "android.apk";
    private static final String saveFileName = savePath + fileName;

    private CustomDialog dialog;

    private boolean showToast = false;
    private NotificationManager notificationManager;
    private Notification notification;
    private PendingIntent pendingIntent;
    private RemoteViews contentView;
    private ApkDownloadListener apkDownloadListener = null;
    private boolean isDownloading = false;// true  正在下载


    public ApkDownloadTools(Context c) {
        mContext = c;
        apkDownloadListener = null;
    }

    private Handler mHandler = new Handler() {
        @SuppressWarnings("deprecation")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    if (mProgress != null)
                        mProgress.setProgress(progress);
                    else
                        mHandler.sendEmptyMessage(DOWN_UPDATE2);
                    break;

                case DOWN_UPDATE2:
                    if (notification != null) {
                        // 改变通知栏
                        contentView.setTextViewText(R.id.notificationPercent, progress + "%");
                        contentView.setProgressBar(R.id.notificationProgress, 100, progress, false);
                        notification.contentView = contentView;
                        notificationManager.notify(R.layout.notification_item, notification);
                    }
                    break;
                case DOWN_OVER:
                    LogUtil.LogE(ApkDownloadTools.class, "DOWN_OVER");
                    // 添加声音效果
                    // notification.defaults |= Notification.DEFAULT_SOUND;

                    // 添加震动,后来得知需要添加震动权限 : Virbate Permission
                    // mNotification.defaults |= Notification.DEFAULT_VIBRATE ;

                    //添加状态标志
                    //FLAG_AUTO_CANCEL        该通知能被状态栏的清除按钮给清除掉
                    //FLAG_NO_CLEAR           该通知能被状态栏的清除按钮给清除掉
                    //FLAG_ONGOING_EVENT      通知放置在正在运行
                    //FLAG_INSISTENT          通知的音乐效果一直播放

                    if (downloadDialog != null) {//强制更新
                        downloadDialog.dismiss();
                    } else {
                        if (notification != null) {
                            /*********下载完成，点击安装***********/
                            Intent installApkIntent = getInstallApkIntent();
                            if (installApkIntent != null) {
                                pendingIntent = PendingIntent.getActivity(mContext, 0, installApkIntent, 0);
                                notification.contentIntent = pendingIntent;
                            }
                            notification.flags = Notification.FLAG_AUTO_CANCEL;
                            notification.defaults = Notification.DEFAULT_SOUND;//铃声提醒
                            contentView.setTextViewText(R.id.notificationPercent, mContext.getString(R.string.down_sucess));
                            notification.contentView = contentView;
                            notificationManager.notify(R.layout.notification_item, notification);
                        }

                    }
                    installApk();//直接安装
                    break;

                case DOWN_ERROR:
                    if (notification != null) {
                        notification.flags = Notification.FLAG_AUTO_CANCEL;
                        contentView.setTextViewText(R.id.notificationPercent, mContext.getString(R.string.down_fail));
                        notification.contentView = contentView;
                        notificationManager.notify(R.layout.notification_item, notification);
                    }

                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 安装 apk 文件
     */
    private void installApk() {
        Intent installApkIntent = getInstallApkIntent();
        if (installApkIntent != null)
            mContext.startActivity(installApkIntent);
    }

    private Intent getInstallApkIntent() {
        File apkFile = new File(saveFileName);
        if (!apkFile.exists()) {
            return null;
        }
        Intent installApkIntent = new Intent();
        installApkIntent.setAction(Intent.ACTION_VIEW);
        installApkIntent.addCategory(Intent.CATEGORY_DEFAULT);
        installApkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            installApkIntent.setDataAndType(FileProvider.getUriForFile(mContext, ContextHelper.getApplication().getPackageName() + ".fileProvider", apkFile), "application/vnd.android.package-archive");
            installApkIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            installApkIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        return installApkIntent;
    }

    public void setShowToast(boolean showToast) {
        this.showToast = showToast;
    }

    /**
     * 轮训请求 检查更新
     * boolean isRequested   true 网络请求的
     */
//    public void setUpdateInfo(UpdateInfo info, boolean isRequested) {
//
//        if (info != null) {
//            if (info.buildVersion <= CommonUtil.getVersionId(mContext)) {
//                if (showToast) {
//                    TipsToast.showTips("当前已是最新版本，无需升级");
//                }
//            } else {
//                path = info.downloadUrl;
//                if (info.forceUpdate == 1) {
//                    showUpdateDialog(info, true);
//                } else {
//                    if (SPUtil
//                            .getString(SPUtil.KEY_UPDATE_TIME, "")
//                            .equals(CommonUtils.date2String(new Date(),
//                                    CommonUtils.DEFAULT_FORMAT))) {
//                        LogUtil.LogI(ApkDownloadTools.class, " 今天已经检查更新");
//                        if (!isRequested)
//                            showUpdateDialog(info, false);
//                    } else {
//                        SPUtil.put(
//                                SPUtil.KEY_UPDATE_TIME,
//                                CommonUtils.date2String(new Date(),
//                                        CommonUtils.DEFAULT_FORMAT));
//                        showUpdateDialog(info, false);
//                    }
//                }
//
//            }
//        }
//
//    }

    /**
     *
     * @param model
     * @param isForcedUpdate 是否强制更新
     */
    public void showUpdateDialog(VersionControlModel model, final boolean isForcedUpdate) {
        if (mContext == null) return;
        View view;
        ViewHolder viewHolder;
        view = LayoutInflater.from(mContext).inflate(
                R.layout.pop_new_version, null);
        viewHolder = new ViewHolder(view);
//        String ss = "1.这软件牛逼<br>\r\n2.非常牛逼<br>\r\n3.此版本无bug<br>\r\n4.记得点赞";
//
//        ss.split("<br>");
//        "description":"1.这软件牛逼<br>\r\n2.非常牛逼<br>\r\n3.此版本无bug<br>\r\n4.记得点赞"
        if (!TextUtils.isEmpty(model.getItem().getDesc())) {
//            if (info.description.contains("<br>"))
//                viewHolder.listContent.setAdapter(new NewVersionInfoAdapter(mContext, info.description.split("<br>")));
//            else
//                viewHolder.listContent.setAdapter(new NewVersionInfoAdapter(mContext, info.description.split("<br/>")));
                viewHolder.listContent.setAdapter(new NewVersionInfoAdapter(mContext, new String[]{model.getItem().getDesc()}));
        } else {
            viewHolder.listContent.setVisibility(View.GONE);
        }
        path=model.getItem().getUrl();//下载链接
        //viewHolder.tvCancelPop.setVisibility(isForcedUpdate ? View.GONE : View.VISIBLE);//强制更新取消按钮隐藏
        //viewHolder.imvClose.setVisibility(isForcedUpdate ? View.GONE : View.VISIBLE);

        viewHolder.tvOkPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDownloadDialog(isForcedUpdate);
                dialog.dismiss();
                if (apkDownloadListener != null)
                    apkDownloadListener.onOkClick();
            }
        });
        viewHolder.tvTitle.setText(model.getItem().getCurrentVersiontitle());//标题
        viewHolder.tvCancelPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
//        viewHolder.imvClose.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });

        dialog = new CustomDialog(mContext, R.style.progress_dialog);
        dialog.addContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        dialog.setCancelable(false);
        dialog.show();

        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
//        p.height = (int) (d.getHeight() * 0.3);
        p.width = (int) (CommonUtils.getScreenWidth(mContext) * 0.7);    //宽度设置为屏幕的0.72
        dialog.getWindow().setAttributes(p);     //设置生效
    }

    private void showDownloadDialog(boolean isForcedUpdate) {
        if (isForcedUpdate) {
            Builder builder = new Builder(mContext);
            final LayoutInflater inflater = LayoutInflater.from(mContext);
            View v = inflater.inflate(R.layout.progress, null);
            mProgress = (ProgressBar) v.findViewById(R.id.progress);
            downloadDialog = builder.setTitle(mContext.getString(R.string.app_update)).setView(v).setCancelable(false)
                    .create();
            downloadDialog.show();
        }
        if (!isDownloading) {
            checkStoragePermissinss();
        }

    }

    private void checkStoragePermissinss() {
        new RxPermissions((Activity) mContext).request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    if (downloadDialog == null)
                        createNotification();
                    downloadNewApk();
                } else {
                    ToastTools.showToast(mContext.getApplicationContext(), mContext.getString(R.string.permission_write_external_storage_not_allow));
                }
            }
        });
    }

    public void downloadNewApk() {

        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return;
        }
        if(TextUtils.isEmpty(path)){
            return;
        }
        DownloadUtil.get().download(path, savePath, fileName, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                LogUtil.LogE(ApkDownloadTools.class, "onDownloadSuccess");
                isDownloading = false;
                mHandler.sendEmptyMessageDelayed(DOWN_OVER, 10);
            }

            @Override
            public void onDownloading(int progress) {
                LogUtil.LogE(ApkDownloadTools.class, "progress = " + progress);
                isDownloading = true;

                if (progress > ApkDownloadTools.this.progress) {
                    ApkDownloadTools.this.progress = progress;
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                }

            }

            @Override
            public void onDownloadFailed() {
                LogUtil.LogE(ApkDownloadTools.class, "onDownloadFailed");
                isDownloading = false;
                mHandler.sendEmptyMessage(DOWN_ERROR);
            }
        });

    }

    /**
     * 方法描述：createNotification方法
     *
     * @param
     * @return
     * @see
     */
    public void createNotification() {

        notification = new Notification(
                R.mipmap.logo,//应用的图标
                mContext.getString(R.string.app_name_thf) + mContext.getString(R.string.is_downing),
                System.currentTimeMillis());
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        //notification.flags = Notification.FLAG_AUTO_CANCEL;

        /*** 自定义  Notification 的显示****/
        contentView = new RemoteViews(mContext.getPackageName(), R.layout.notification_item);
        contentView.setTextViewText(R.id.notificationTitle, mContext.getString(R.string.app_name_thf) + mContext.getString(R.string.is_downing));
        contentView.setTextViewText(R.id.notificationPercent, "0%");
        contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);
        notification.contentView = contentView;

        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(R.layout.notification_item, notification);
    }

    public void cancelNotification() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (notificationManager != null)
                    notificationManager.cancel(R.layout.notification_item);
            }
        }, 100);


    }

    public void setApkDownloadListener(ApkDownloadListener apkDownloadListener) {
        this.apkDownloadListener = apkDownloadListener;
    }


    static class ViewHolder {
//        @Bind(R.id.imv_close)
//        ImageView imvClose;
        @Bind(R.id.list_content)
        MyListView listContent;
        @Bind(R.id.tv_cancel_pop)
        TextView tvCancelPop;
        @Bind(R.id.app_update_title)
        TextView tvTitle;
        @Bind(R.id.tv_ok_pop)
        TextView tvOkPop;
        @Bind(R.id.lay_btn)
        LinearLayout layBtn;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
