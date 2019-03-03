package com.cp2y.cube.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.cp2y.cube.helper.ContextHelper;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by admin on 2016/12/15.
 */
public class FileUtils {

    public static final String PATH = ContextHelper.getApplication().getFilesDir() + "/CubeLottery/";
    public static final String DOUBLE_PATH = ContextHelper.getApplication().getFilesDir() + "/CubeLottery/DoubleBall/";
    public static final String LOTTO_PATH = ContextHelper.getApplication().getFilesDir() + "/CubeLottery/Lotto/";
    public static final String D3_PATH = ContextHelper.getApplication().getFilesDir() + "/CubeLottery/Fucai3D/";
    public static final String P3_PATH = ContextHelper.getApplication().getFilesDir() + "/CubeLottery/PaiLie3/";
    public static final String P5_PATH = ContextHelper.getApplication().getFilesDir() + "/CubeLottery/PaiLie5/";
    public static final String CQSSC_PATH = ContextHelper.getApplication().getFilesDir() + "/CubeLottery/ChongQingShiShiCai/";
    public static final String CACHE_PATH = ContextHelper.getApplication().getCacheDir() + "/";
    public static final String CALC_PATH=ContextHelper.getApplication().getFilesDir()+"/calcCash.txt";//计奖文件
    public static final Map<Integer, String> PATH_MAP = new HashMap<Integer, String>() {{
        put(0, DOUBLE_PATH);
        put(1, LOTTO_PATH);
        put(2, D3_PATH);
        put(3, P3_PATH);
        put(4, P5_PATH);
        put(5, CQSSC_PATH);
    }};


    public static File getLibraryFile(String issue, int id, int flag) {
        return new File(PATH_MAP.get(flag) + issue, String.valueOf(id));
    }

    public static File getLibraryCacheFile(int id) {
        return new File(CACHE_PATH, String.valueOf(id) + ".tmp");
    }

    public static void removeLibraryFile(String issue, int id, int flag) {
        try {
            getLibraryFile(issue, id, flag).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建文件夹
     *
     * @param file
     */
    public static void makeDirs(File file) {
        if (file != null && !file.exists()) {
            file.mkdirs();
        }
    }

    //读取选择号码
    public static List<String> readSelectNum(File file) {
        List<String> data = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String recordStr;
            while ((recordStr = reader.readLine()) != null) {
                data.add(recordStr);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 读取最大的行数
     *
     * @param file
     * @param max
     * @return
     */
    public static List<String> readMaxLine(File file, int max) {
        List<String> data = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String recordStr;
            int line = 0;
            while (line < max && (recordStr = reader.readLine()) != null) {
                line++;
                data.add(recordStr);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    //获取缓存大小
    public static long getFolderSize(File cacheDir) {
        long size = 0;
        //指定文件夹中， 所有的File对象
        File[] files = cacheDir.listFiles();
        if (files != null && files.length > 0) {
            for (File cacheFile : files) {
                if (cacheFile.isDirectory()) {
                    size += getFolderSize(cacheFile);
                } else {
                    //获得文件字节大小
                    size += cacheFile.length();
                }
            }
        }
        return size;
    }

    public static void getFilesName(File file) {
        // TODO Auto-generated method stub
        if (file.listFiles() != null) {
            File[] files = file.listFiles();
            for (int j = 0; j < files.length; j++) {
                // String name = files[j].getName();
                if (files[j].isDirectory()) {
                    String dirPath = files[j].toString().toLowerCase();
                    System.out.println(dirPath);
                    getFilesName(new File(dirPath + "/"));
                } else {
                    Log.e("yangfan", "getFilesName: " + "FileName===" + files[j].getName());
                }
            }
        }

    }

    //文件数量
    public static int getFilesCount(File file) {
        // TODO Auto-generated method stub
        if (file.listFiles() != null) {
            File[] files = file.listFiles();
            return files.length;
        }
        return 0;
    }


    /**
     * 把Bitmap转Byte
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    //图片到byte数组
    public static byte[] image2byte(String path) {
        byte[] data = null;
        FileInputStream input = null;
        try {
            input = new FileInputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * 移动下载文件到files
     *
     * @param fileName
     * @param issue
     */
    public static void moveToFiles(String fileName, String issue, int flag) {
        try {
            File afile = new File(CACHE_PATH + fileName + ".tmp");
            //创建文件夹
            makeDirs(new File(PATH_MAP.get(flag) + issue));
            if (afile.renameTo(new File(PATH_MAP.get(flag) + issue + "/" + fileName))) {
                // TipsToast.showTips("成功");
            } else {
                // TipsToast.showTips("失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 全部清理cache文件夹
     *
     * @param
     */
    public static void cleanAllInternalCache() {
        deleteDirectory(ContextHelper.getApplication().getCacheDir());
    }

    /**
     * 全部清理files文件夹
     *
     * @param
     */
    public static void cleanAllInternalFiles() {
        deleteFilesDirectory(new File(ContextHelper.getApplication().getFilesDir() + "/CubeLottery"));
    }

    /**
     * 清理files号码库文件夹
     *
     * @param
     */
    public static void cleanInternalFiles(File file) {
        deleteFilesByDirectory(file);
    }

    /**
     * 删除操作留3个文件夹
     *
     * @param directory
     */
    public static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            File[] childFiles = directory.listFiles();//文件数组
            if (childFiles.length < 4) return;//有3条以下文件跳出
            //按文件名奖期排序
            TreeMap<String, File> treeMap2 = new TreeMap<>(new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    //按照key排序
                    return String.valueOf(o1).compareTo(String.valueOf(o2));
                }
            });
            for (File fs : childFiles) {
                treeMap2.put(fs.getName(), fs);//map添加数据
            }
            int i = 0;//遍历map筛选
            for (Iterator<Map.Entry<String, File>> it = treeMap2.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, File> entry = it.next();
                if (childFiles.length - i > 3) {
                    if (entry.getValue().isDirectory()) {
                        for (File files : entry.getValue().listFiles()) {
                            files.delete();
                        }
                    }
                    entry.getValue().delete();
                } else {
                    break;
                }
                i++;
            }
            ;
        }
    }

    /**
     * 清理缓存操作
     *
     * @param directory
     */
    private static void deleteDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }

    /**
     * 清理files缓存操作
     *
     * @param directory
     */
    private static void deleteFilesDirectory(File directory) {
        if (directory.isFile()) {
            directory.delete();
            return;
        }
        if (directory != null && directory.exists() && directory.isDirectory()) {
            File[] childFiles = directory.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                directory.delete();
                return;
            }

            for (int i = 0; i < childFiles.length; i++) {
                deleteFilesDirectory(childFiles[i]);
            }
            directory.delete();
        }
    }

    /**
     * 统一单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return "0.00M";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(megaByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "M";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "M";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "G";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "T";
    }

    /**
     * 缓存大小
     *
     * @return
     */
    public static String getCacheSize() {
        String size = "0.00M";
        try {
            //size = getFormatSize(getFolderSize(ContextHelper.getApplication().getCacheDir())+getFolderSize(new File(PATH)));
            size = getFormatSize(getFolderSize(new File(PATH)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 　　* 保存文件
     * 　　* @param toSaveString
     * 　　* @param filePath
     *
     */


    public static void saveFile(String toSaveString, String filePath) {
        try {
            File saveFile = new File(filePath);
            if (!saveFile.exists()) {
                File dir = new File(saveFile.getParent());
                dir.mkdirs();
                saveFile.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(saveFile);
            outStream.write(toSaveString.getBytes());
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 　　* 读取文件内容
     * 　　* @param filePath
     * 　　* @return 文件内容
     *
     */

    public static String readFile(String filePath) {
        String str = "";
        try {
            File readFile = new File(filePath);
            if (!readFile.exists()) {
                return null;
            }
            FileInputStream inStream = new FileInputStream(readFile);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = inStream.read(buffer)) != -1) {
                stream.write(buffer, 0, length);
            }
            str = stream.toString();
            stream.close();
            inStream.close();
            return str;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
