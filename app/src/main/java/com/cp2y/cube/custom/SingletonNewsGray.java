package com.cp2y.cube.custom;

import java.util.HashSet;
import java.util.Set;

/**新闻置灰
 * Created by yangfan on 2017/7/14.
 */
public class SingletonNewsGray {
    private static Set<String> objList=new HashSet<>();
    public static void registerService(String id) {objList.add(id);}//存
    public static void removeMap(String key){objList.remove(key);}//删
    public static boolean isContains(String id){return  objList.contains(id);};//判断存在
    public static void clearData(){objList.clear();}
}
