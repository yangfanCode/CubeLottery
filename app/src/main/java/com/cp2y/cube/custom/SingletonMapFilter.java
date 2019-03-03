package com.cp2y.cube.custom;

import java.util.HashMap;
import java.util.Map;

/**存储过滤条件记录
 * Created by admin on 2016/12/20.
 */
public class SingletonMapFilter {
    //定义单例map对象
    private static Map<String, Object> objMap = new HashMap<String, Object>();//双色球
    private static Map<String, Object> objLottoMap = new HashMap<String, Object>();//大乐透
    private static Map<String, Object> obj3DMap = new HashMap<String, Object>();//福彩3D
    private static Map<String, Object> objP3Map = new HashMap<String, Object>();//排列3
    private static Map<String, Object> objP5Map = new HashMap<String, Object>();//排列5
    private static Map<String, Object> objCQ3Map = new HashMap<String, Object>();//重庆时时彩3星
    private static Map<String, Object> objCQ5Map = new HashMap<String, Object>();//重庆时时彩5星
    private static Map<String, Object> objCQ2Map = new HashMap<String, Object>();//重庆时时彩2星

    private SingletonMapFilter(){ }
    //保存
    public static void registerService(String key, Object instance) {objMap.put(key, instance);}
    public static void registerLottoService(String key, Object instance) { objLottoMap.put(key, instance); }
    public static void register3DService(String key, Object instance) { obj3DMap.put(key, instance); }
    public static void registerP3Service(String key, Object instance) { objP3Map.put(key, instance); }
    public static void registerP5Service(String key, Object instance) { objP5Map.put(key, instance); }
    public static void registerCQ3Service(String key, Object instance) { objCQ3Map.put(key, instance); }
    public static void registerCQ5Service(String key, Object instance) { objCQ5Map.put(key, instance); }
    public static void registerCQ2Service(String key, Object instance) { objCQ2Map.put(key, instance); }
    //获取
    public static Object getService(String key){
        return objMap.get(key);
    }
    public static Object getLottoService(String key){
        return objLottoMap.get(key);
    }
    public static Object get3dService(String key){
        return obj3DMap.get(key);
    }
    public static Object getP3Service(String key){return objP3Map.get(key);}
    public static Object getP5Service(String key){return objP5Map.get(key);}
    public static Object getCQ3Service(String key){return objCQ3Map.get(key);}
    public static Object getCQ5Service(String key){return objCQ5Map.get(key);}
    public static Object getCQ2Service(String key){return objCQ2Map.get(key);}
    //大小
    public static int getMapSize(){
        return objMap.size();
    }
    public static int getLottoMapSize(){
        return objLottoMap.size();
    }
    public static int get3DMapSize(){
        return obj3DMap.size();
    }
    public static int getP3MapSize(){
        return objP3Map.size();
    }
    public static int getP5MapSize(){return objP5Map.size();}
    public static int getCQ3MapSize(){return objCQ3Map.size();}
    public static int getCQ5MapSize(){return objCQ5Map.size();}
    public static int getCQ2MapSize(){return objCQ2Map.size();}
    //对象
    public static Map getMap(){
        return  objMap;
    }
    public static Map getLottoMap(){
        return  objLottoMap;
    }
    public static Map get3dMap(){return  obj3DMap;}
    public static Map getP3Map(){return  objP3Map;}
    public static Map getP5Map(){return  objP5Map;}
    public static Map getCQ3Map(){return  objCQ3Map;}
    public static Map getCQ5Map(){return  objCQ5Map;}
    public static Map getCQ2Map(){return  objCQ2Map;}
}