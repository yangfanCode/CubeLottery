package com.cp2y.cube.custom;

import com.cp2y.cube.util.CombineAlgorithm;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**排列5选号存储
 * Created by admin on 2016/12/20.
 */
public class SingletonMapP5 {
    //号码存储_前排序key,例1_1001
    public static int sign_sort = 1;
    //定义单例map对象
    private static Map<String, List<String>> objMap = new TreeMap<>();

    private SingletonMapP5() {
    }

    public static void registerService(String key, List<String> instance) {

        objMap.put(key, instance);

    }

    public static List<String> getService(String key) {
        return objMap.get(key);
    }

    public static int getMapSize() {
        return objMap.size();
    }

    public static Map<String, List<String>> getMap() {
        return objMap;
    }

    public static void removeMap(String key) {
        objMap.remove(key);
    }

    public static void editDelete(String key, List<String> list) {
        //编辑map中数据
        objMap.put(key, list);
    }

    /**
     * 搜索直选复式千位百位十位个位起始位置
     *
     * @param list
     * @return
     */
    public static int[] findMutiIndex(List<String> list) {
        if (list == null || list.size() == 0) return new int[]{0, 0, 0, 0};
        int i = 0, j = 0, k = 0, l = 0;
        for (String num : list) {
            if (Integer.valueOf(num) >= 50 && j == 0) {
                j = i;//十位位置
            } else if (Integer.valueOf(num) >= 100 && k == 0) {
                k = i;
            } else if (Integer.valueOf(num) >= 150 && l == 0) {
                l = i;
            } else if (Integer.valueOf(num) >= 200) {
                break;
            }
            i++;
        }
        return new int[]{j, k, l, i};
    }

    public static int calcLotteryCount() {
        int totalCount = 0;
        for (Iterator<Map.Entry<String, List<String>>> it = SingletonMapP5.getMap().entrySet().iterator(); it.hasNext(); ) {//优先计算实际彩票数量
            Map.Entry<String, List<String>> entry = it.next();
            List<String> list = entry.getValue();
            if (list == null || list.size() == 0) continue;
            int key = Integer.valueOf(entry.getKey());
            if (key < 1000) {
                if (list.size() == 7) {
                    totalCount++;
                } else {
                    int blueCount = 0;
                    for (String num : list) {
                        if (Integer.parseInt(num) > 50) {
                            blueCount++;
                        }
                    }
                    totalCount += CombineAlgorithm.combination(list.size() - blueCount, 6) * blueCount;
                }
            } else {
                int danCount = 0, tuoCount = 0;
                for (String num : list) {
                    if (Integer.parseInt(num) < 50) {
                        danCount++;
                    } else if (Integer.parseInt(num) > 50 && Integer.parseInt(num) < 100) {
                        tuoCount++;
                    }
                }
                totalCount += CombineAlgorithm.combination(tuoCount, 6 - danCount) * (list.size() - danCount - tuoCount);
            }
        }
        return totalCount;
    }
}