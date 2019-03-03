package com.cp2y.cube.utils;

import android.util.SparseArray;
import android.util.SparseIntArray;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by js on 2016/12/30.
 */
public class MapUtils {
    /**原生走势图**/
    public static Set<Integer>TRENDS=new HashSet<Integer>(){{add(10002);add(10088);add(10001);add(10003);add(10004);}};
    /**走势已开发彩种 待更新**/
    public static Set<Integer>TREND_DEVELOP=new HashSet<Integer>(){{add(10002);add(10088);add(10001);add(10003);add(10004);add(10019);add(10083);add(10005);add(10100);add(10165);add(10166);add(10169);add(10151);add(10093);add(10089);add(10084);add(10152);add(10153);add(10154);}};
    /**走势已开发彩种名字**/
    public static Set<String>TREND_DEVELOP_NAME=new HashSet<String>(){{add("双色球");add("超级大乐透");add("福彩3D");add("排列3");add("排列5");add("15选5");add("七乐彩");add("七星彩");add("东方6+1");add("安徽快三");add("广西快三");add("浙江快乐彩");add("内蒙快三");add("重庆时时彩三星");add("重庆时时彩");
                                                                        add("上海时时乐");add("上海11选5");add("上海11选5(前三位)");add("上海11选5(前二位)");}};
    /**过滤已开发彩种**/
    public static Set<Integer> FILTER_DEVELOP=new HashSet<Integer>(){{add(10002);add(10088);add(10001);add(10003);add(10004);add(10093);add(10089);add(10095);}};
    /**过滤已开发彩种名字**/
    public static Set<String> FILTER_DEVELOP_NAME=new HashSet<String>(){{add("双色球");add("超级大乐透");add("福彩3D");add("排列3");add("排列5");add("重庆时时彩三星");add("重庆时时彩");add("重庆时时彩二星");}};
    /**彩种**/
    public static String[] LOTTERY={"双色球","超级大乐透","福彩3D","排列3","排列5","七乐彩","七星彩","华东15选5","浙江6+1","浙江20选5"};
    /**奇偶**/
    public static String[] ODD_EVEN = new String[]{"奇","偶"};
    /**奇偶**/
    public static Map<String, Integer> ODD_EVEN_MAP = new HashMap<String, Integer>(){{put("奇",0);put("偶",1);}};
    /**大小**/
    public static String[] BIG_EVEN = new String[]{"大","小"};
    /**大小**/
    public static Map<String, Integer> BIG_EVEN_MAP = new HashMap<String, Integer>(){{put("大",0);put("小",1);}};
    /**3D奇偶**/
    public static Map<String, Integer> ODD_3D_EVEN_MAP = new HashMap<String, Integer>(){{put("奇奇奇",0);put("奇奇偶",1);put("奇偶奇",2);put("奇偶偶",3);put("偶奇奇",4);put("偶奇偶",5);put("偶偶奇",6);put("偶偶偶",7);}};
    /**重庆2星奇偶**/
    public static Map<String, Integer> ODD_CQ2_EVEN_MAP = new HashMap<String, Integer>(){{put("奇奇",0);put("奇偶",1);put("偶奇",2);put("偶偶",3);}};
    /**3D大小**/
    public static Map<String, Integer> BIG_3D_EVEN_MAP = new HashMap<String, Integer>(){{put("大大大",0);put("大大小",1);put("大小大",2);put("大小小",3);put("小大大",4);put("小大小",5);put("小小大",6);put("小小小",7);}};
    /**重庆2星大小**/
    public static Map<String, Integer> BIG_CQ2_EVEN_MAP = new HashMap<String, Integer>(){{put("大大",0);put("大小",1);put("小大",2);put("小小",3);}};
    /**重庆2星除3余**/
    public static Map<String, Integer> DIVIDE_CQ2_EVEN_MAP = new HashMap<String, Integer>() {{put("00",0);put("01",1);put("02",2);put("10",3);put("11",4);put("12",5);put("20",6);put("21",7);put("22",8);}};
    /**重庆2星除3余个数**/
    public static Map<String, Integer> DIVIDE_NUM_CQ2_EVEN_MAP = new HashMap<String, Integer>() {{put("2:0:0",0);put("1:1:0",1);put("1:0:1",2);put("0:2:0",3);put("0:1:1",4);put("0:0:2",5);}};
    /**常用期数**/
    public static int[] TREND_PERIOD = new int[]{30,50,100,200,300};
    /**常用期数**/
    public static SparseIntArray TREND_PERIOD_MAP = new SparseIntArray(){{put(30,0);put(50,1);put(100,2);put(200,3);put(300,4);}};
    /**常用星期**/
    public static int[] TREND_WEEK = new int[]{0,2,4,7};
    /**福建36选7常用星期 246**/
    public static int[] FJ_TREND_WEEK = new int[]{0,2,4,6};
    /**大乐透常用星期**/
    public static int[] LOTTO_TREND_WEEK = new int[]{0,1,3,6};
    /**福彩3D常用星期**/
    public static int[] D3_TREND_WEEK = new int[]{0,1,2,3,4,5,6,7};
    /**七乐彩常用星期**/
    public static int[] QLC_TREND_WEEK = new int[]{0,1,3,5};
    /**新疆常用星期**/
    public static int[] XJ_TREND_WEEK = new int[]{0,1,5};
    /**深圳常用星期 25**/
    public static int[] SZ_TREND_WEEK = new int[]{0,2,5};
    /**河北数字5常用星期 357**/
    public static int[] HBSZ5_TREND_WEEK = new int[]{0,3,5,7};
    /**七星彩常用星期**/
    public static int[] QXC_TREND_WEEK = new int[]{0,2,5,7};
    /**江苏7位数常用星期2457**/
    public static int[] JS7_TREND_WEEK = new int[]{0,2,4,5,7};
    /**东方6+1常用星期**/
    public static int[] DF61_TREND_WEEK = new int[]{0,1,3,6};
    /**常用星期**/
    public static SparseIntArray TREND_WEEK_MAP = new SparseIntArray(){{put(0,0);put(2,1);put(4,2);put(7,3);}};
    /**福建36选7常用星期**/
    public static SparseIntArray TREND_FJ_WEEK_MAP = new SparseIntArray(){{put(0,0);put(2,1);put(4,2);put(6,3);}};
    /**大乐透常用星期**/
    public static SparseIntArray TREND_LOTTO_WEEK_MAP = new SparseIntArray(){{put(0,0);put(1,1);put(3,2);put(6,3);}};
    /**新疆常用星期**/
    public static SparseIntArray TREND_XJ_WEEK_MAP = new SparseIntArray(){{put(0,0);put(1,1);put(5,2);}};
    /**深圳常用星期**/
    public static SparseIntArray TREND_SZ_WEEK_MAP = new SparseIntArray(){{put(0,0);put(2,1);put(5,2);}};
    /**七乐彩常用星期**/
    public static SparseIntArray TREND_QLC_WEEK_MAP = new SparseIntArray(){{put(0,0);put(1,1);put(3,2);put(5,3);}};
    /**七星彩常用星期**/
    public static SparseIntArray TREND_QXC_WEEK_MAP = new SparseIntArray(){{put(0,0);put(2,1);put(5,2);put(7,3);}};
    /**河北数字5常用星期**/
    public static SparseIntArray TREND_HBSZ5_WEEK_MAP = new SparseIntArray(){{put(0,0);put(3,1);put(5,2);put(7,3);}};
    /**江苏7位数常用星期**/
    public static SparseIntArray TREND_JS7_WEEK_MAP = new SparseIntArray(){{put(0,0);put(2,1);put(4,2);put(5,3);put(7,4);}};
    /**东方6+1常用星期**/
    public static SparseIntArray TREND_DF61_WEEK_MAP = new SparseIntArray(){{put(0,0);put(1,1);put(3,2);put(6,3);}};
    /**福彩3D常用星期**/
    public static SparseIntArray TREND_D3_WEEK_MAP = new SparseIntArray(){{put(0,0);put(1,1);put(2,2);put(3,3);put(4,4);put(5,5);put(6,6);put(7,7);}};
    /**指标**/
    public static String[] NUM_SUPPORT = {"开奖号","重号","连号","边号","串号"};
    /**15选5基本号码**/
    public static String[] HD15X5_NUM_SUPPORT = {"重号","连号","边号","串号","分区线","分段线","遗漏","柱图"};
    /**15选5定位**/
    public static String[] HD15X5_LOCATION_SUPPORT = {"折叠线","平均线","分区线 ","分段线 ","遗漏","遗漏柱图"};
    /**15选5指标**/
    public static String[] HD15X5_TARGET_SUPPORT = {"折叠线","平均线","分段线 ","遗漏","遗漏柱图"};
    /**15选5和值**/
    public static String[] HD15X5_SUM_SUPPORT = {"折叠线","平均线","分段线"};
    /**七乐彩和值**/
    public static String[] QILECAI_SUM_SUPPORT = {"折叠线","平均线","分段线","包含特别号码"};
    /**15选5指标**/
    public static String[] QILECAI_TARGET_SUPPORT = {"折叠线","平均线","分段线 ","遗漏","遗漏柱图","包含特别号码"};
    /**指标**/
    public static String[] LOCATE_SUPPORT = {"遗漏"};
    /**指标**/
    public static String[] D3_LOCATE_SUPPORT = {"开奖号","遗漏"};
    /**指标**/
    public static String[] DIVIDE_SUPPORT = {"开奖号","含蓝球计算","遗漏"};
    /**指标**/
    public static String[] LOTTO_DIVIDE_SUPPORT = {"开奖号","含后区计算","遗漏"};
    /**指标**/
    public static String[] ODD_SUPPORT = {"开奖号", "含蓝球计算", "遗漏"};
    /**指标**/
    public static String[] LOTTO_ODD_SUPPORT = {"开奖号", "含后区计算", "遗漏"};
    /**指标**/
    public static String[] SPAN_SUPPORT = {"开奖号","含蓝球计算","遗漏"};
    /**指标**/
    public static String[] LOTTO_SPAN_SUPPORT = {"开奖号","含后区计算","遗漏"};
    /**指标**/
    public static String[] SUM_SUPPORT = {"开奖号","含蓝球计算"};
    /**指标**/
    public static String[] LOTTO_SUM_SUPPORT = {"开奖号","含后区计算"};
    /**指标**/
    public static String[] P5_SUM_SUPPORT = {"开奖号"};
    /**和值标题**/
    public static int[] SUM_BLUE_NUMS = {22, 40, 58, 76, 94, 112, 130, 148, 166, 184, 199};
    /**乐透和值含篮球标题**/
    public static int[] LOTTO_SUM_NOMAL_BLUE_NUMS = {18, 35, 52, 69, 86, 103, 120, 137, 154, 171, 188};
    /**乐透和值不含篮球标题**/
    public static int[] LOTTO_SUM_BLUE_NUMS = {15, 30, 45, 60, 75, 90, 105, 120, 135, 150, 165};
    /**和值标题**/
    public static int[] SUM_NORMAL_NUMS = {21, 38, 55, 72, 89, 106, 123, 140, 157, 174, 183};
    /**双色球默认值**/
    public static String[] DEF_DOUBLE_BALL = {"-", "-", "-", "-", "-", "-", "-"};
    /**福彩3D默认值**/
    public static String[] DEF_3D_BALL = {"-", "-", "-"};
    /**排列3默认值**/
    public static String[] DEF_P5_BALL = {"-", "-", "-","-", "-"};
    /**左侧菜单默认彩种**/
    public static String[] LEFT_LOTTERY_TYPE = {"双色球","大乐透","福彩3D","排列3","华东15选5"};
    /**双色球个数比**/
    public static String[] BALL_NUM_RATE = {"7:0", "6:1", "5:2", "4:3", "3:4", "2:5", "1:6", "0:7"};
    /**3D个数比**/
    public static String[] D3_NUM_RATE = {"3:0", "2:1", "1:2", "0:3"};
    /**时时彩2星个数比**/
    public static String[] CQ2_NUM_RATE = {"2:0", "1:1", "0:2",};
    /**排列5个数比**/
    public static String[] P5_NUM_RATE = {"5:0", "4:1", "3:2", "2:3","1:4","0:5"};
    /**3D形态**/
    public static String[] D3_PATTERN_RATE = {"大大大", "大大小", "大小大", "大小小","小大大","小大小","小小大","小小小"};
    /**时时彩2星形态**/
    public static String[] CQ2_PATTERN_RATE = {"大大", "大小", "小大", "小小"};
    /**3D奇偶形态**/
    public static String[] D3_ODD_PATTERN_RATE = {"奇奇奇", "奇奇偶", "奇偶奇", "奇偶偶","偶奇奇","偶奇偶","偶偶奇","偶偶偶"};
    /**时时彩2星奇偶形态**/
    public static String[] CQ2_ODD_PATTERN_RATE = {"奇奇", "奇偶", "偶奇", "偶偶"};
    /**大小形态位置**/
    public static SparseArray<String> MAP_BIG_INDEX = new SparseArray<String>(){{put(0,"大小");put(1,"大");put(2,"小");}};
    /**奇偶形态位置**/
    public static SparseArray<String> MAP_ODD_INDEX = new SparseArray<String>(){{put(0,"奇偶");put(1,"奇");put(2,"偶");}};
    /**奇偶形态位置**/
    public static SparseArray<String> MAP_3D_ODD_INDEX = new SparseArray<String>(){{put(0,"奇奇奇");put(1,"奇奇偶");put(2,"奇偶奇");put(3,"奇偶偶");put(4,"偶奇奇");put(5,"偶奇偶");put(6,"偶偶奇");put(7,"偶偶偶");}};
    /**奇偶形态位置**/
    public static SparseArray<String> MAP_CQ2_ODD_INDEX = new SparseArray<String>(){{put(0,"奇奇");put(1,"奇偶");put(2,"偶奇");put(3,"偶偶");}};
    /**除3余个数位置**/
    public static SparseArray<String> MAP_CQ2_DIVIDE_NUM_INDEX = new SparseArray<String>(){{put(0,"2:0:0");put(1,"1:1:0");put(2,"1:0:1");put(3,"0:2:0");put(4,"0:1:1");put(5,"0:0:2");}};
    /**大小形态位置**/
    public static SparseArray<String> MAP_3D_BIG_INDEX = new SparseArray<String>(){{put(0,"大大大");put(1,"大大小");put(2,"大小大");put(3,"大小小");put(4,"小大大");put(5,"小大小");put(6,"小小大");put(7,"小小小");}};
    /**大小形态位置**/
    public static SparseArray<String> MAP_CQ2_BIG_INDEX = new SparseArray<String>(){{put(0,"大大");put(1,"大小");put(2,"小大");put(3,"小小");}};
    /**二星除三余形态位置**/
    public static SparseArray<String> MAP_CQ2_DIVIDE_INDEX = new SparseArray<String>(){{put(0,"00");put(1,"01");put(2,"02");put(3,"10");put(4,"11");put(5,"12");put(6,"20");put(7,"21");put(8,"22");}};
    /**遗漏切换期数**/
    public static int[] DATE_ARRAY={30,50,100,200,300};
    /**遗漏期数位置**/
    public static SparseIntArray DATE_ARRAY_POS=new SparseIntArray(){{put(30,0);put(50,1);put(100,2);put(200,3);put(300,4);}};
    /**3D号码库区分类型**/
    public static SparseIntArray D3_LIBRARY_TYPE=new SparseIntArray(){{put(13,1);put(12,2);put(16,3);put(172,4);put(18,5);put(17,6);put(173,7);}};
    /**排列3号码库区分类型**/
    public static SparseIntArray P3_LIBRARY_TYPE=new SparseIntArray(){{put(1,1);put(0,2);put(6,3);put(168,4);put(5,5);put(7,6);put(169,7);}};
    /**排列5号码库区分类型**/
    public static SparseIntArray P5_LIBRARY_TYPE=new SparseIntArray(){{put(9,1);put(10,2);}};
    /**重庆时时彩3星号码库区分类型**/
    public static SparseIntArray CQ3_LIBRARY_TYPE=new SparseIntArray(){{put(94,1);put(93,2);put(126,3);put(522,4);put(129,5);put(127,6);put(521,7);}};
    /**重庆时时彩5星号码库区分类型**/
    public static SparseIntArray CQ5_LIBRARY_TYPE=new SparseIntArray(){{put(92,1);put(91,2);}};
    /**重庆时时彩号码库区分类型**/
    public static SparseIntArray CQSSC_LIBRARY_TYPE=new SparseIntArray(){{put(92,1);put(91,2);put(94,3);put(93,4);put(126,5);put(522,6);put(129,7);put(127,8);put(521,9);put(98,10);put(97,11);put(101,12);put(100,13);put(103,14);}};
    /**推单详情总类型**/
    public static SparseIntArray PUSH_SINGLE_TYPE=new SparseIntArray(){{put(13,5);put(12,6);put(16,7);put(172,8);put(18,9);put(17,10);put(173,11);put(1,5);put(0,6);put(6,7);put(168,8);put(5,9);put(7,10);put(169,11);put(9,12);put(10,13);}};
    /**彩种flag**/
    public static SparseIntArray LOTTERY_FLAG=new SparseIntArray(){{put(10002,0);put(10088,1);put(10001,2);put(10003,3);put(10004,4);put(10089,5);}};
}
