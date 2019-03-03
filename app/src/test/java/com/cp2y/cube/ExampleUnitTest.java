package com.cp2y.cube;

import com.cp2y.cube.helper.LotteryHelper;
import com.cp2y.cube.tool.filter.FilterCollection;
import com.cp2y.cube.util.CombineAlgorithm;
import com.cp2y.cube.utils.CommonUtil;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * To work on unit tests, switch0 the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void test() {
        FilterCollection<byte[]> filterCollection = new FilterCollection.Builder<byte[]>()
//                .addFilter(new SumFilter(true, new Integer[]{}))
//                .addFilter(new SumMantissaFilter(1,2,true))
//                .addFilter(new MantissaSumFilter(true, 23,24))
//                .addFilter(new AverageFilter(true,5,6))
//                .addFilter(new RangeFilter(true, 5,6))
//                .addFilter(new ACValueFiter(true,0, 4))
//                .addFilter(new BigSmallFilter(true, new Range(3,4), new byte[]{0,0,0,0,0,0,0}, true, (byte)18, (byte)9, (byte)6, new Range(1,33), new Range(3,33), true))
//                .addFilter(new OddEvenFilter(true, new Range(0,7), new byte[]{-1,1,-1,-1,1,-1,1}, true))
//                .addFilter(new PrimeCompositeFilter(true, new Range(3,4), new byte[]{0,0,0,0,0,0,0}, true))
//                .addFilter(new DevideThreeFilter(true, new Range(2,2), new Range(2,5), new byte[]{0,1,1,-1,0,1,1}, false))
//                .addFilter(new NewOldSkipFilter(true, new Range(2,3), new Range(2,3), new byte[]{-1,0,-1,-1,-1,-1,-1},true, new ArrayList<byte[]>(){{add(new byte[]{1,2,5,17,26,32});add(new byte[]{1,6,9,26,28,30});}}))
//                .addFilter(new OldLeanLoneFilter(true, new Range(2,3), new Range(2,3), new byte[]{-1,-1,-1,-1,-1,-1,-1},true, new ArrayList<byte[]>(){{add(new byte[]{1,2,5,17,26,32});add(new byte[]{1,6,9,26,28,30});}}))
//                .addFilter(new ConnectNumberFilter(false, new Range(2,4), new Range(0,3)))
//                .addFilter(new SeparateNumberFilter(true, new Range(2,3), new Range(0,3)))
//                .addFilter(new SameMantissaFilter(true, new Range(2,4), new Range(2,2)))
//                .addFilter(new MultipleFilter(false, new Range(2,2)))
//                .addFilter(new HistoryFilter(false, new ArrayList<byte[]>(){{add(new byte[]{1,10,17,21,23,30,12});add(new byte[]{4,13,15,17,21,24,15});}}))
//                .addFilter(new BigSmallFilter(true, new byte[]{-1,-1,-1,0,1,1,1}, false, (byte)18, (byte)9, (byte)6, 6,7))
                .build();
        List<byte[]> data = LotteryHelper.generateLotteryRecordsColorBall(new byte[]{}, new byte[]{1,2,3,4,5,6,7,8,9,10,11,12,13,14,24,25,26,27,28,29,30,31,32,33}, new byte[]{1,2,3,11,12,13}, 7, 1);//双色球复式
//        List<byte[]> data = LotteryHelper.generateLotteryRecordsColorBall(new byte[]{}, new byte[]{1,2,3,4,10,17,21,23,30}, new byte[]{12}, 7, 1);//双色球复式
//        List<byte[]> data = LotteryHelper.generateLotteryRecordsColorBall(new byte[]{1,2,3}, new byte[]{4,5,6,7,8}, new byte[]{1,2,3,4}, 7, 1);//双色球胆拖
//        List<byte[]> data = LotteryHelper.generateLotteryRecordsColorBall(new byte[]{1,2,3}, new byte[]{4,5,6,7,8}, new byte[]{1,2,3,4}, 7, 2);//大乐透
//        List<byte[]> data = LotteryHelper.generateLotteryForPermutation(new ArrayList<byte[]>(){{add(new byte[]{0,1,2});add(new byte[]{2,3,4});add(new byte[]{2,5,6,7,8});}});//7星彩
//        List<byte[]> data = LotteryHelper.generateLotteryRecordsSimpleBall(new byte[]{1,2,3,4,5,6,7,8,9}, 7);//7乐彩
//        List<byte[]> data = LotteryHelper.generateLotteryRecordsSimpleBall(new byte[]{1,2,3,4,5,6,7,8,9}, 8);//7乐彩特别号码
//        List<byte[]> data = LotteryHelper.generateLotteryRecordsAllBall(new byte[]{1,2,3,4,5},3);//M选N排列
//        List<byte[]> data = LotteryHelper.generateLotteryForPermutation(new ArrayList<byte[]>(){{add(new byte[]{0,1,2});add(new byte[]{0,1,2});add(new byte[]{0,1,2});}});//排列3组选
 //       filterCollection.doFilter(data);
        System.out.println("data size is:" + data.size());
//        System.out.println(data);
    }

    @Test
    public void test1() {
        List<String> list = new ArrayList<String>(){{add("1");add("2");add("3");add("51");add("52");add("101");}};
        int i = 0, j = 0;
        for (String num:list)
        {
            if (Integer.valueOf(num) > 100) {
                break;
            } else if (Integer.valueOf(num) > 50 && j == 0) {
                j = i;
            }
            i++;
        }
        List<String> l1 =list.subList(0, j);
        List<String> l2 =list.subList(j, i);
        List<String> l3 =list.subList(i, list.size());
        System.out.println(l1);
    }
    @Test
    public void test2(){
        int yu0_start=1;
        int yu0_end=7;
        int yu1_start=0;
        int yu1_end=7;
        List<String> list=CommonUtil.DivideThree(yu0_start,yu0_end,yu1_start,yu1_end);
        System.out.println("yu2 "+list.get(0)+"-"+list.get(1));
    }

    @Test
    public void test3() {
        Integer[] redBalls = {1,2,3,4,9,10,11,12};//选择的红球
        Integer[] blueBalls = {1,2,3,4,5,6,7,8};//选择的蓝球
        Integer[] selectNums = {1,2,3,4,5,6,1};//开奖号
        Set<Integer> setReds = new HashSet<>();//选择的红球
        setReds.addAll(Arrays.asList(redBalls));
        Set<Integer> setBlues = new HashSet<>();//选择的蓝球
        setBlues.addAll(Arrays.asList(blueBalls));
        Set<Integer> selectReds = new HashSet<>();//中奖的红球
        List<Integer> list = Arrays.asList(selectNums);
        selectReds.addAll(list.subList(0, 6));
        Set<Integer> selectBlues = new HashSet<>();//中奖的蓝球
        selectBlues.addAll(list.subList(6, 7));
        setReds.removeAll(selectReds);//排除掉所有中奖号
        setBlues.removeAll(selectBlues);//排除掉所有的中奖号
        int size = CombineAlgorithm.combination(setReds.size(), 2) * setBlues.size();
        System.out.println("5等奖（4+0）中奖号码个数：" + size);
    }
    @Test
    public void text4(){
        String str="17024";
        System.out.println(str.length());
    }
    @Test
    public void text5(){
        String str=";1;2;3";
        String[]array=str.split(";");
        System.out.println("aa"+array.length);
        System.out.println(array[0]);
        System.out.println(array[1]);
        System.out.println(array[2]);
    }
    @Test
    public void test6(){
        int num=1432;
        while (num%5!=0){
            num++;
        }
        System.out.println(num);
    }
    @Test
    public  void test7(){
        List<Integer>list=new ArrayList<>();
        list.add(123);
        list.add(500);
        list.add(35);
        Collections.sort(list, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1<o2?-1:1;
            }
        });
        System.out.print(list.get(0));
        System.out.print(list.get(1));
        System.out.print(list.get(2));
    }
    @Test
    public void test8(){
        String str="金额420红复0102030405060708蓝单:01;倍数01";
        Pattern p = Pattern.compile("[\\t ]*([蓝球红胆拖单复]{2})(([:\\t ]{0,1}\\d[\\t ]*\\d\\s*)+)");
        Matcher m = p.matcher(str);
        while(m.find()){
            System.out.println(m.group(0));
            System.out.println(m.group(1));
            //System.out.println(m.group(2));
            //System.out.println(m.group(4));
        }
//        System.out.println(m.groupCount());
//        if(m.find()){
//                for(int i=0;i<m.groupCount();i++){
//                    System.out.println(m.group(i));
//                }
//        }


    }
    @Test
    public void cutStr(){
//        String str="如国福利彩票 0FF AB0 AN0 10EA 0FA B0N0站号:3111600222017.04.10-18:38:03操作员:1双色球期号:2017041序号:00111胆拖红球胆163红球拖【030918202126蓝球【0616】开奖0:2017-04-11倍数:001金额:24元站址:新桥路689弄6号111111 11 1111 11111上海市福利彩票发行中心承销";
        String str="sdfsdf111safasd1111sdfsdfsdf11111sdfsd1111111111";
        int index=str.indexOf("11111");
        System.out.println(index);
    }
    @Test
    public void cutStr2(){
        String str="大大大";
        for(int i=0;i<str.length();i++){
            String val=str.substring(i,i+1);
            System.out.println(val);
        }
    }
    @Test
    public void removeList(){
        List<String>list=new ArrayList<>();
        list.add("0");
        list.add("1");
        list.add("1");
        list.remove("1");
        for(String str:list){
            System.out.println(str);
        }
    }

    @Test
    public void num(){
        int n=0xa1;
        int num=30;
        String str=Integer.toHexString(num);
        System.out.println(str);
        byte b0 = Byte.decode("0x" + new String(str)).byteValue();
        System.out.println(b0);
        int iValue = Integer.valueOf(str,16);
        System.out.println(""+(n & 0xff));
}
    @Test
    public void sortNum(){
        String strs="213";
        byte[] num=strs.getBytes();
        Arrays.sort(num);
        String str=new String(num);
        System.out.println(str+",");
    }
    @Test
    public void treeMap(){
        String str="04 11 18#05 12 19#6#05 12";
        String[] s=str.split("#");
        String[] ss=s[2].split(" ");
        System.out.println(ss.length);
    }

    @Test
    public void Test9(){
       String sum="123\n";
        Pattern p = Pattern.compile("^(\\d\\d\\d\\n)*(\\d\\d\\d)$");
        Matcher m = p.matcher(sum);
        System.out.println(m.find());
    }
    @Test
    public void Test10(){
        List<String> A=new ArrayList<>();
        A.add("1");
        A.add("2");
        A.add("2");
        List<String> B=new ArrayList<>();
        B=A;
        B.add("3");
        B.add("4");
    }
}