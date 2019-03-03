package com.cp2y.cube.custom;

import java.util.HashMap;
import java.util.Map;

/**存储过滤结果  点击保存按钮到过滤结果页面之间 公用
 * Created by admin on 2016/12/20.
 */
public class SingletonMapFilterResult {
    //定义单例map对象
    private static Map<String, Object> objMap = new HashMap<String, Object>();
    private SingletonMapFilterResult(){ }
    public static void registerService(String key, Object instance)
    {
        if(!objMap.containsKey(key)){
            objMap.put(key, instance);
        }
    }
    public static Object getService(String key){
        return objMap.get(key);
    }
    public static int getMapSize(){
        return objMap.size();
    }
    public static Map getMap(){
        return  objMap;
    }
}