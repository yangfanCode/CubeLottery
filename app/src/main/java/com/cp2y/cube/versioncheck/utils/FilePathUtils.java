package com.cp2y.cube.versioncheck.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.cp2y.cube.utils.LogUtil;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * Created by yangfan on 2017/9/23.
 * nrainyseason@163.com
 */

public class FilePathUtils {

    /**
     * @param context 上下文对象
     * @param dir     存储目录
     * @return
     */
    public static String getFilePath(Context context, String dir) {
        String directoryPath = "";
        //判断SD卡是否可用
        if (MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            directoryPath = context.getExternalFilesDir(dir).getAbsolutePath();
//            getExternalFilesDir(“test”) = /mnt/sdcard/Android/data/com.my.app/files/test
//            getExternalFilesDir(null) = /mnt/sdcard/Android/data/com.my.app/files

//            directoryPath =context.getExternalCacheDir().getAbsolutePath() ;
//            getExternalCacheDir() = /mnt/sdcard/Android/data/com.my.app/cache
        } else {
            directoryPath = context.getFilesDir() + File.separator + (TextUtils.isEmpty(dir) ? "" : dir);
//            directoryPath=context.getCacheDir()+File.separator+dir;

//            getFilesDir() = /data/data/com.my.app/files
//            getCacheDir() = /data/data/com.my.app/cache
        }
        File file = new File(directoryPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        LogUtil.LogE(FilePathUtils.class, "filePath====>" + directoryPath);
        return directoryPath;
    }

    //  文件存储根目录
    private static final String rootSavePath = (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) ? Environment.getExternalStorageDirectory() + File.separator
            + "cp2y" + File.separator + "cubeLottery" + File.separator : null;

    //  图片文件存储路径
    private static final String imageSavePath = rootSavePath + "image" + File.separator;
    //  图片文件存储路径


    //  更新包文件存储路径
    private static final String updateSavePath = rootSavePath + "update" + File.separator;

    //
    public static String getRootSavePath() {
        return mkdirs(rootSavePath) ? rootSavePath : null;
    }

    public static String getImageSavePath() {
        return mkdirs(imageSavePath) ? imageSavePath : null;
    }

    public static String getUpdateSavePath() {
        return mkdirs(updateSavePath) ? updateSavePath : null;
    }

    public static boolean mkdirs(String pathname) {
        if (pathname == null) {
            throw new NullPointerException();
        }
        File file = new File(pathname);
        if (!file.exists()) {
            return file.mkdirs();
        } else
            return true;
    }

    public static boolean deleteDirectory(String dir, boolean deleteDir) {
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                flag = deleteFile(file.getAbsolutePath());
                if (!flag) {
                    break;
                }
            } else {
                flag = deleteDirectory(file.getAbsolutePath(), true);
                if (!flag) {
                    break;
                }
            }
        }

        if (!flag) {
            return false;
        }

        return !deleteDir || dirFile.delete();
    }

    public static boolean deleteFile(String strFullFileName) {
        if (null == strFullFileName || 0 >= strFullFileName.length())
            return false;

        File file = new File(strFullFileName);
        return file.isFile() && file.delete();
    }

    public static void deleteFileAtDirByExt(String dir, String ext) {
        if (!dir.endsWith(File.separator)) {
            dir = dir + File.separator;
        }
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return;
        }

        File[] files = dirFile.listFiles();
        if (null != files) {
            for (File file : files) {
                String filePath = file.getAbsolutePath();
                if (file.isFile()
                        && (filePath.endsWith(ext.toLowerCase())
                        || filePath.endsWith(ext.toUpperCase()))) {

                    deleteFile(filePath);
                }
            }
        }
    }

    public static String convertFileSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        //%.2f 即是保留两位小数的浮点数，后面跟上对应单位就可以了，不得不说java很方便
        if (size >= gb) {
            return String.format("%.2f GB", (float) size / gb);
        } else if (size >= mb) {
            float f = (float) size / mb;
            //如果大于100MB就不用保留小数位啦
            return String.format(f > 100 ? "%.0f MB" : "%.2f MB", f);
        } else if (size >= kb) {
            float f = (float) size / kb;
            //如果大于100kB就不用保留小数位了
            return String.format(f > 100 ? "%.0f KB" : "%.2f KB", f);
        } else
            return String.format("%d B", size);
    }
}
