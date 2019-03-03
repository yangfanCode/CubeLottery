package com.cp2y.cube.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.dialog.ConditionDialog;
import com.cp2y.cube.enums.AppEnvEnum;
import com.cp2y.cube.enums.AppEnvHelper;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.network.NetConst;
import com.cp2y.cube.struct.Range;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.cp2y.cube.utils.TagAliasOperatorHelper.ACTION_DELETE;
import static com.cp2y.cube.utils.TagAliasOperatorHelper.ACTION_SET;
import static com.cp2y.cube.utils.TagAliasOperatorHelper.TagAliasBean;
import static com.cp2y.cube.utils.TagAliasOperatorHelper.sequence;

/**
 * Created by js on 2016/12/29.
 */
public class CommonUtil {

    /**
     * 解析Int
     *
     * @param obj
     * @return
     */
    public static int parseInt(Object obj) {
        int result = 0;
        try {
            result = Integer.parseInt(obj.toString());
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 计算范围
     *
     * @param list
     * @return
     */
    public static Range calcRangeFromList(List<Integer> list) {
        int max = 0, min = Integer.MAX_VALUE;
        for (Integer num : list) {
            if (num > max) {
                max = num;
            }
            if (num < min) {
                min = num;
            }
        }
        return new Range(min, max);
    }

    /**
     * 链接字符串
     *
     * @param strs
     * @param splitter
     * @return
     */
    public static String join(String[] strs, String splitter) {
        StringBuffer sb = new StringBuffer();
        for (String s : strs) {
            sb.append(s + splitter);
        }
        String result = sb.toString();
        if (result.length() > 0) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 增加前缀0
     *
     * @param text
     * @return
     */
    public static String preZeroForBall(Object text) {
        String str = String.valueOf(text);
        str = "0".concat(str);
        return str.substring(str.length() - 2);
    }

    /**
     * 连接字符串数组
     *
     * @param list
     * @return
     */
    public static String joinStringList(List<String> list) {
        StringBuilder sb = new StringBuilder();
        try {
            for (String str : list) {
                sb.append(str).append("\n");
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }

    /**
     * 连接字节数组
     *
     * @param list
     * @return
     */
    public static String joinByteList(List<byte[]> list) {
        StringBuilder sb = new StringBuilder();
        int size = list.size() > 5 ? 5 : list.size();
        try {
            for (int j = 0; j < size; j++) {
                for (int i = 0; i < list.get(j).length; i++) {
                    sb.append(CommonUtil.preZeroForBall(list.get(j)[i]) + " ");
                }
                sb.append("\n");
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }

    /**
     * 除3余余2个数计算
     *
     * @param yu0_start
     * @param yu0_end
     * @param yu1_start
     * @param yu1_end
     * @return
     */
    public static List<String> DivideThree(int yu0_start, int yu0_end, int yu1_start, int yu1_end) {
        List<String> list = new ArrayList<>();
        int yu0Min = Math.min(yu0_start, yu0_end);
        int yu1Min = Math.min(yu1_start, yu1_end);
        int yu0Max = Math.max(yu0_start, yu0_end);
        int yu1Max = Math.max(yu1_start, yu1_end);
        int tempMin = (yu0Min + yu1Min) > 7 ? 7 : yu0Min + yu1Min;
        int tempMax = (yu0Max + yu1Max) > 7 ? 7 : yu0Max + yu1Max;
        list.add(String.valueOf(Math.min((7 - tempMin), (7 - tempMax))));
        list.add(String.valueOf(Math.max((7 - tempMin), (7 - tempMax))));
        return list;
    }

    /**
     * 判断集合是否相同
     *
     * @param list1
     * @param list2
     * @return
     */
    public static boolean ListCheck(List<String> list1, List<String> list2) {
        if (list1.size() == 0 && list2.size() == 0) return false;
        if (list1.size() != list2.size()) return false;
        if (list2.containsAll(list1))
            return true;
        return false;
    }
    /**
     * 判断集合是否相同
     *
     * @param list1
     * @param list2
     * @return
     */
    public static boolean CheckList(List<Object> list1, List<Object> list2) {
        if (list1.size() == 0 && list2.size() == 0) return false;
        if (list1.size() != list2.size()) return false;
        if (list2.containsAll(list1))
            return true;
        return false;
    }

    /**
     * 计算双色球和值是否符合规范
     *
     * @param sum
     * @return
     */
    public static boolean isValidSumValue(String sum) {
        if (TextUtils.isEmpty(sum)) return true;
        Pattern p = Pattern.compile("^([2-9]\\d[,，]|[0-1]\\d{2}[,，])*([2-9]\\d|[0-1]\\d{2})$");
        Matcher m = p.matcher(sum);
        return m.find();
    }

    /**
     * 计算大乐透和值是否符合规范
     * 前段匹配多个数字带逗号 0次或多次,后端匹配一个号码不带逗号
     *
     * @param sum
     * @return
     */
    public static boolean isLottoValidSumValue(String sum) {
        if (TextUtils.isEmpty(sum)) return true;
        Pattern p = Pattern.compile("^(1[8-9][,，]|[2-9]\\d[,，]|1[0-7]\\d[,，]|18[0-8][,，])*(1[8-9]|[2-9]\\d|1[0-7]\\d|18[0-8])$");
        Matcher m = p.matcher(sum);
        return m.find();
    }

    /**
     * 计算3D和值是否符合规范
     *
     * @param sum
     * @return
     */
    public static boolean is3DValidSumValue(String sum) {
        if (TextUtils.isEmpty(sum)) return true;
        Pattern p = Pattern.compile("^([0-9][,，]|1[0-9][,，]|2[0-7][,，])*([0-9]|1[0-9]|2[0-7])$");
        Matcher m = p.matcher(sum);
        return m.find();
    }
    /**
     * 计算排列5和值是否符合规范
     *
     * @param sum
     * @return
     */
    public static boolean isP5ValidSumValue(String sum) {
        if (TextUtils.isEmpty(sum)) return true;
        Pattern p = Pattern.compile("^([0-9][,，]|[1-3]\\d[,，]|4[0-5][,，])*([0-9]|[1-3]\\d|4[0-5])$");
        Matcher m = p.matcher(sum);
        return m.find();
    }
    /**
     * 计算重庆时时彩2星和值是否符合规范
     *
     * @param sum
     * @return
     */
    public static boolean isCQ2ValidSumValue(String sum) {
        if (TextUtils.isEmpty(sum)) return true;
        Pattern p = Pattern.compile("^([0-9][,，]|1[0-8][,，])*([0-9]|1[0-8])$");
        Matcher m = p.matcher(sum);
        return m.find();
    }

    /**
     * 计算3D双码是否符合规范
     *
     * @param sum
     * @return
     */
    public static boolean is3DValidDoubleNumValue(String sum) {
        if (TextUtils.isEmpty(sum)) return false;
        Pattern p = Pattern.compile("^(0[0-9][,，]|[1-9][0-9][,，])*(0[0-9]|[1-9][0-9])$");
        Matcher m = p.matcher(sum);
        return m.find();
    }

    /**
     * 排序
     *
     * @param list
     */
    public static void SortCollection(List<String> list) {
        Collections.sort(list, (s, t) -> {
            int s1 = Integer.valueOf(s);
            int s2 = Integer.valueOf(t);
            if (s1 < s2) return -1;
            if (s1 == s2) return 0;
            return 1;
        });
    }

    /**
     * byte[]排序
     *
     * @param list
     */
    public static void SortListByteCollection(List<byte[]> list) {
        Collections.sort(list, new Comparator<byte[]>() {
            @Override
            public int compare(byte[] o1, byte[] o2) {
                for (int i = 0; i < o1.length; i++) {
                    if (o1[i] < o2[i]) {
                        return -1;
                    } else {
                        if (o1[i] > o2[i]) {
                            return 1;
                        } else {
                            continue;
                        }
                    }
                }
                return 0;
            }
        });
    }

    /**
     * double一位小数
     *
     * @param dou
     * @return
     */
    public static double changeDouble(Double dou) {
        NumberFormat nf = new DecimalFormat("0.0 ");
        dou = Double.parseDouble(nf.format(dou));
        return dou;
    }

    /**
     * double两位小数
     *
     * @param dou
     * @return
     */
    public static String changeDoubleTwo(Double dou) {
        NumberFormat nf = new DecimalFormat("0.00 ");
        return nf.format(dou);
    }

    /**
     * double三位小数
     *
     * @param dou
     * @return
     */
    public static String changeDoubleThree(Double dou) {
        NumberFormat nf = new DecimalFormat("0.000 ");
        return nf.format(dou);
    }

    /**
     * double四位小数
     *
     * @param dou
     * @return
     */
    public static String changeDoubleFour(Double dou) {
        NumberFormat nf = new DecimalFormat("0.0000 ");
        return nf.format(dou);
    }

    /**
     * double整数数无小数点,小数时有
     * @param d
     * @return
     */
    public static String doubleTrans(double d) {
        if (Math.round(d) - d == 0) {
            return String.valueOf((long) d);
        }
        return String.valueOf(d);
    }

    /**
     * 获取当前版本号
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static int getVersionId(Context context) {
        //获取实例
        PackageManager manager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = manager.getPackageInfo(context.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packageInfo.versionName;
        int versionNo = packageInfo.versionCode;
        return versionNo;
    }

    /**
     * 获取当前版本号
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static String getVersionName(Context context) {
        //获取实例
        PackageManager manager = context.getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = manager.getPackageInfo(context.getPackageName(), 0);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = packageInfo.versionName;
        int versionNo = packageInfo.versionCode;
        return version;
    }

    /**
     * 判断手机是否联网
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                //mNetworkInfo.isAvailable();
                return true;//有网
            } else {
                return false;//没有网
            }
        }
        return false;

    }

    /**
     * 如果断网展示提示
     */
    public static void showNetTipToast(){
        if(!CommonUtil.isNetworkConnected(ContextHelper.getApplication())){
            TipsToast.showTips(ContextHelper.getApplication().getString(R.string.netOff));
        }
    }

    public static String getAppChannel() {
        String channel = "android3601002";
        try {
            ApplicationInfo appInfo = ContextHelper.getApplication().getPackageManager()
                    .getApplicationInfo(ContextHelper.getApplication().getPackageName(),
                            PackageManager.GET_META_DATA);
            channel = appInfo.metaData.getString("UMENG_CHANNEL");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Log.d("Channel", channel);
        return channel;
    }

    public static double getTimeCalc(String start, String end) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");//输入日期的格式
        Date date1 = null;
        try {
            date1 = simpleDateFormat.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = simpleDateFormat.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        GregorianCalendar cal1 = new GregorianCalendar();
        GregorianCalendar cal2 = new GregorianCalendar();
        cal1.setTime(date1);
        cal2.setTime(date2);
        double dayCount = (cal2.getTimeInMillis() - cal1.getTimeInMillis()) / (1000 * 3600 * 24);//从间隔毫秒变成间隔天数
        return dayCount;
    }

    /**
     * 获取截取后的key
     *
     * @param key
     * @return
     */
    public static String cutKey(String key) {
        return key.substring(key.indexOf("_") + 1);
    }

    /**
     * 获取截取后的标识key
     *
     * @param key
     * @return
     */
    public static String cutFirstKey(String key) {
        return key.substring(0, key.indexOf("_"));
    }

    /**
     * 获得dialog集合中 dialog位置
     *
     * @param data
     * @param conditionDialog
     * @return
     */
    public static int getListPos(List<ConditionDialog> data, ConditionDialog conditionDialog) {
        int Pidex = 0;
        for (ConditionDialog dialog : data) {
            if (dialog == conditionDialog) {
                break;
            } else {
                Pidex++;
            }
        }
        return Pidex;
    }

    /**
     * 判断字符串是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 判断是否为11位电话
     *
     * @return
     */
    public static boolean isMobileNO(String mobiles) {

        Pattern p = Pattern.compile("^((13[0-9])|(15[0-3|5-9])|(18[0-9])|(16[6])|(14[5|7|9])|(17[1|3|6-8]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 判断JSONObject是否为空
     */
    public static boolean isObjectEmpty(Object Object) {
        try {
            if (Object == null)
                return false;
            else
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getIMEI() {
        try {
            TelephonyManager tm = (TelephonyManager) ContextHelper.getApplication().getSystemService(Context.TELEPHONY_SERVICE);
            String DEVICE_ID = tm.getDeviceId();
            if (DEVICE_ID != null) {
                return DEVICE_ID;
            } else {
                return SystemUtil.UUID();//IMEI为空随机
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean DoubleClick() {
        long firstClick = 0;
        long lastClick = 0;
        int count = 0;
        // 如果第二次点击 距离第一次点击时间过长 那么将第二次点击看为第一次点击
        if (firstClick != 0 && System.currentTimeMillis() - firstClick > 300) {
            count = 0;
        }
        count++;
        if (count == 1) {
            firstClick = System.currentTimeMillis();
        } else if (count == 2) {
            lastClick = System.currentTimeMillis();
            // 两次点击小于300ms 也就是连续点击
            if (lastClick - firstClick < 300) {// 判断是否是执行了双击事件

                return true;
            }
        }
        return false;
    }

    /**
     * 计算数值最近被5整除
     *
     * @param num
     * @return
     */
    public static int calcDivide5(int num) {
        while (num % 5 != 0) {
            num++;
        }
        return num;
    }

    public static boolean checkSame(List<String> list1, List<String> list_check) {
        if (list1.size() != list_check.size()) return false;
        for (int i = 0; i < list1.size(); i++) {
            boolean isSame = list1.get(i).equals(list_check.get(i));
            if (!isSame) {
                break;
            } else {
                if (i == list1.size() - 1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * byte转stringList
     *
     * @return
     */
    public static List<String> bytetoList(byte[] byteData) {
        List<String> list = new ArrayList<>();
        for (byte data : byteData) {
            list.add(String.valueOf(data));
        }
        return list;
    }

    /**
     * byte包含特定值
     *
     * @return
     */
    public static boolean byteContains(byte[] byteData, byte data, int pos) {
        for (int i = 0; i < byteData.length; i++) {
            if (i == pos) {
                continue;
            } else {
                if (byteData[i] == data) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 单式重复判断
     *
     * @param byteData
     * @param data
     * @return
     */
    public static boolean SingleSame(byte[] byteData, byte data, boolean isSingelRed, int pos, int start, int mid, int end) {
        //红篮球数据
        byte[] newbyte = isSingelRed ? Arrays.copyOfRange(byteData, start, mid) : Arrays.copyOfRange(byteData, mid, end);
        for (int i = 0; i < newbyte.length; i++) {
            if (i == pos) {
                continue;//跳过自身元素
            } else {
                if (newbyte[i] == data) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 大乐透胆拖去重
     *
     * @param byteData
     * @param data
     * @param pos      横行球的位置
     * @param position 第几条数据listview
     * @return
     */
    public static boolean lottoDanTuoSame(byte[] byteData, byte data, int pos, int position, List<byte[]> mTickets) {
        byte[] combinByte;
        if (position == 0 || position == 1) {//前区判断
            combinByte = position == 0 ? mTickets.get(1) : mTickets.get(0);//获取相对比数据
        } else {//后区判断
            combinByte = position == 2 ? mTickets.get(3) : mTickets.get(2);//获取相对比数据
        }
        for (int i = 0; i < byteData.length; i++) {//自身数据判断重复
            if (i == pos) {
                continue;
            } else {
                if (byteData[i] == data) {
                    return true;
                }
            }
        }
        return byteContains(combinByte, data, Integer.MAX_VALUE);//胆拖比较重复
    }

    /**
     * 双色球胆拖去重
     *
     * @param byteData
     * @param data
     * @param pos      横行球的位置
     * @param position 第几条数据listview
     * @return
     */
    public static boolean DoubleDanTuoSame(byte[] byteData, byte data, int pos, int position, List<byte[]> mTickets) {
        byte[] combinByte;
        if (position == 0 || position == 1) {//红球判断
            combinByte = position == 0 ? mTickets.get(1) : mTickets.get(0);//获取相对比数据
        } else {//后区判断
            return byteContains(byteData, data, 0);//胆拖比较重复
        }
        for (int i = 0; i < byteData.length; i++) {//自身数据判断重复
            if (i == pos) {
                continue;
            } else {
                if (byteData[i] == data) {
                    return true;
                }
            }
        }
        return byteContains(combinByte, data, Integer.MAX_VALUE);//胆拖比较重复
    }

    /*时间戳转换成字符窜*/
    public static String getDateToString(long time) {
        SimpleDateFormat sf = null;
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyy年MM月dd日");
        return sf.format(d);
    }

    /*时间戳转换成字符窜*/
    public static String getDateToString2(long time) {
        SimpleDateFormat sf = null;
        Date d = new Date(time);
        sf = new SimpleDateFormat("yyyy-MM-dd");
        return sf.format(d);
    }
    /*自定义格式时间戳转换成字符窜*/
    public static String getDateToString3(long time,String format) {
        SimpleDateFormat sf = null;
        Date d = new Date(time);
        sf = new SimpleDateFormat(format);
        return sf.format(d);
    }

    /**
     * 获取手机品牌
     *
     * @return
     */
    public static String getPhoneBrand() {
        String brand = "";
        try {//厂商和品牌
            if (!TextUtils.isEmpty(Build.BRAND)) {//厂商不为空
                if (!TextUtils.isEmpty(Build.MODEL)) {//型号不为空
                    brand = android.os.Build.BRAND.concat(" ").concat(Build.MODEL);
                } else {
                    brand = android.os.Build.BRAND;
                }
            } else {
                brand = "其他";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return brand;
    }

    /**
     * 关闭软键盘
     */
    public static void hintKbOne() {
        InputMethodManager imm = (InputMethodManager) ContextHelper.getApplication().getSystemService(Context.INPUT_METHOD_SERVICE);
        // 得到InputMethodManager的实例
        if (imm.isActive()) {
            // 如果开启
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
                    InputMethodManager.HIDE_NOT_ALWAYS);

        }
    }

    //拼接数组
    public static byte[] ArrayConcat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    /**
     * 字符串转数组
     *
     * @param str
     * @return
     */
    public static String[] stringToArray(String str) {
        String s = str.trim();
        String[] arrays = new String[s.length()];
        for (int i = 0; i < s.length(); i++) {
            arrays[i] = s.substring(i, i + 1);
        }
        return arrays;
    }

    /*根据号码设定格式*/
    public static int check3DNumTypeColor(String num) {
        String nums[] = num.split(" ");
        Set<String> check = new HashSet<>();
        for (String n : nums) {
            check.add(n);
        }
        if (check.size() == 3) {//3个都不同
            return 0;
        } else if (check.size() == 2) {//2个号码相同
            return 2;
        } else {//3个号码相同
            return 3;
        }
    }

    /*根据号码设定格式*/
    public static int check3DNumType(String num) {
        String nums[] = new String[3];
        for (int i = 0; i < num.length(); i++) {
            nums[i] = num.substring(i, i + 1);
        }
        Set<String> check = new HashSet<>();
        for (String n : nums) {
            check.add(n);
        }
        if (check.size() == 3) {//3个都不同
            return 0;
        } else if (check.size() == 2) {//2个号码相同
            return 2;
        } else {//3个号码相同
            return 3;
        }
    }

    /**
     * 日期转星期
     *
     * @param sdate
     * @return
     */
    public static String getWeek(String sdate) {
        // 再转换为时间
        Date date = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        //int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return new SimpleDateFormat("EEEE").format(c.getTime());
    }

    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 获得系统状态栏高度
     **/
    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = ContextHelper.getApplication().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ContextHelper.getApplication().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 拼接定制彩种图片地址
     *
     * @param LotteryId
     * @return
     */
    public static String concatImgUrl(int LotteryId) {
        String url = "";
        url = NetConst.dynamicBaseUrlForLottery().concat(NetConst.CUSTOM_ICON_URL).concat(LotteryId + ".png");
        return url;
    }
    /**
     * 拼接走势彩种图片地址
     *
     * @param LotteryId
     * @return
     */
    public static String concatTrendImgUrl(int LotteryId) {
        String url = "";
        url = NetConst.dynamicBaseUrlForLottery().concat(NetConst.TREND_ICON_URL).concat(LotteryId + ".png");
        return url;
    }

    /**
     * 获得当前时间
     * @return
     */
    public static String getSystemDate(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        return str;
    }

    /**
     * 获得阶段性时间
     * @return
     */
    public static String getRageDate(int days,String format){
        Calendar c = Calendar.getInstance(); // 当时的日期和时间
        int day; // 需要更改的天数
        day = c.get(Calendar.DAY_OF_MONTH) - days;
        c.set(Calendar.DAY_OF_MONTH, day);
        return getDateToString3(c.getTimeInMillis(),format);
    }

    /**
     * 是否开启GPS开关
     * @return
     */
    public static boolean hasGPSDevice() {
        LocationManager locationManager
                = (LocationManager) ContextHelper.getApplication().getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (gps || network) {
            return true;
        }

        return false;
    }

    /**
     * 设置屏幕半透明
     * @param activity
     * @param bgAlpha
     */
    public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
        if(activity != null) {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.alpha = bgAlpha;
            activity.getWindow().setAttributes(lp);
        }
    }

    public static void openActicity(Context context, Class<?> class1, Bundle pBundle) {
        openActicity(context, class1, pBundle, 0);
    }

    public static void openActicity(Context context, Class<?> class1, Bundle pBundle, int flags) {
        Intent intent = new Intent(context, class1);
        if(pBundle != null) {
            intent.putExtras(pBundle);
        }

        if(flags > 0) {
            intent.setFlags(flags);
        }

        context.startActivity(intent);
    }

    /**
     * 登录状态
     * @return
     */
    public static boolean isLogin(){
        return LoginSPUtils.isLogin();
    }

    /**
     * 登录userId
     * @return
     */
    public static int getUserId(){
        return LoginSPUtils.getInt("id",0);
    }
    public static void openActicity(Activity activity, Class<?> class1, Bundle pBundle, boolean closeActivity) {
        Intent intent = new Intent(activity, class1);
        if(pBundle != null) {
            intent.putExtras(pBundle);
        }

        activity.startActivity(intent);
        if(closeActivity) {
            activity.finish();
        }

    }

    /**
     * 极光 alias
     * boolean isSetAlias  ; true 设置 alias，false 清空
     */
    public static void jPushAlias(boolean isSetAlias, String alias) {
        LogUtil.LogE(CommonUtil.class, "alias =" + alias);
        TagAliasBean tagAliasBean = new TagAliasBean();
        if (isSetAlias)
            tagAliasBean.action = ACTION_SET;
        else
            tagAliasBean.action = ACTION_DELETE;
        sequence++;
        tagAliasBean.alias = alias;

        tagAliasBean.isAliasAction = true;
        TagAliasOperatorHelper.getInstance().handleAction(ContextHelper.getApplication().getApplicationContext(), sequence, tagAliasBean);

    }

    public static String getHxImUserIdByAppUserId(String userId) {
//        return userId;
        return (AppEnvHelper.currentEnv() == AppEnvEnum.ONLINE ? "" : "") + userId;
    }

    public static String getAppUserIdByHxImUserId(String hxImUserId) {
        if (hxImUserId == null) return "";
//        return hxImUserId;
        return AppEnvHelper.currentEnv() == AppEnvEnum.ONLINE ? hxImUserId : (hxImUserId.startsWith("test") ? hxImUserId.substring(4) : hxImUserId);
    }

    /**
     * 动画移动view并摆放至相应的位置
     *
     * @param view               控件
     * @param xFromDeltaDistance x起始位置的偏移量
     * @param xToDeltaDistance   x终止位置的偏移量
     * @param yFromDeltaDistance y起始位置的偏移量
     * @param yToDeltaDistance   y终止位置的偏移量
     * @param duration           动画的播放时间
     * @param delay              延迟播放时间
     * @param isBack             是否需要返回到开始位置
     */
    public static void moveViewWithAnimation(final View view, final float xFromDeltaDistance, final float xToDeltaDistance, final float yFromDeltaDistance, final float yToDeltaDistance, int duration, int delay, final boolean isBack) {
        //创建位移动画
        TranslateAnimation ani = new TranslateAnimation(xFromDeltaDistance, xToDeltaDistance, yFromDeltaDistance, yToDeltaDistance);
        ani.setInterpolator(new AccelerateInterpolator());//设置加速器
        ani.setDuration(duration);//设置动画时间
        ani.setStartOffset(delay);//设置动画延迟时间
        //监听动画播放状态
        ani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                int deltaX = (int) (xToDeltaDistance - xFromDeltaDistance);
                int deltaY = (int) (yToDeltaDistance - yFromDeltaDistance);
                int layoutX = view.getLeft();
                int layoutY = view.getTop();
                int tempWidth = view.getWidth();
                int tempHeight = view.getHeight();
                view.clearAnimation();
                if (!isBack) {
                    layoutX += deltaX;
                    layoutY += deltaY;
                    view.layout(layoutX, layoutY, layoutX + tempWidth, layoutY + tempHeight);
                } else {
                    view.layout(layoutX, layoutY, layoutX + tempWidth, layoutY + tempHeight);
                }
            }
        });
        view.startAnimation(ani);
    }
    /**
     * 动画移动view并摆放至相应的位置
     *
     * @param view               控件
     * @param duration           动画的播放时间
     * @param delay              延迟播放时间
     */
    public static void alphaViewWithAnimation(final View view, int duration, int delay) {
        //创建位移动画
        AlphaAnimation ani = new AlphaAnimation(0.1f, 1.0f);
        ani.setInterpolator(new AccelerateInterpolator());//设置加速器
        ani.setDuration(duration);//设置动画时间
        ani.setStartOffset(delay);//设置动画延迟时间
        //监听动画播放状态
        ani.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.clearAnimation();
            }
        });
        view.startAnimation(ani);
    }

    /**
     * 未登录获取定制结果
     * @return
     */
    public static String getNoLoginLotteryId(){
        String customLottery="";
        String custom = CommonSPUtils.getString("customLottery");
        if (!TextUtils.isEmpty(custom)) {
            StringBuilder stringBuilder = new StringBuilder();
            String[] customs = custom.split(",");
            for (int i = 0, size = customs.length; i < size; i++) {
                int key = Integer.valueOf(customs[i].substring(0, customs[i].indexOf("_")));
                stringBuilder.append(i == 0 ? String.valueOf(key) : String.valueOf("," + String.valueOf(key)));
            }
            customLottery = stringBuilder.toString();
        }
        return customLottery;
    }

    /**
     * 福彩3D排列3 组选 输入框换行正则规范
     *
     * @param sum
     * @return
     */
    public static boolean D3P3ZuXuanInputValue(String sum) {
        if (TextUtils.isEmpty(sum)) return false;
        if(sum.endsWith("\n"))return false;
        Pattern p = Pattern.compile("^(\\d{3}[\\n])*(\\d{3})$");
        Matcher m = p.matcher(sum);
        return m.find();
    }
}
