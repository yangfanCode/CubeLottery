package com.cp2y.cube.custom;

import java.util.HashSet;
import java.util.Set;

/**存储选单checkbox
 * Created by admin on 2016/12/20.
 */
public class SingletonSelectSingleCheck {
    private static Set<Integer> objDoubleList=new HashSet<>();
    public static void registerDoubleService(Integer id) {objDoubleList.add(id);}//存
    public static void removeDoubleMap(Integer key){objDoubleList.remove(key);}//删
    public static boolean isDoubleContains(Integer id){return  objDoubleList.contains(id);};//判断存在
    public static void clearDoubleData(){objDoubleList.clear();}
    public static Set<Integer> getDoubleObj(){return objDoubleList;}

    private static Set<Integer> objLottoList=new HashSet<>();
    public static void registerLottoService(Integer id) {objLottoList.add(id);}//存
    public static void removeLottoMap(Integer key){objLottoList.remove(key);}//删
    public static boolean isLottoContains(Integer id){return  objLottoList.contains(id);};//判断存在
    public static void clearLottoData(){objLottoList.clear();}
    public static Set<Integer> getLottoObj(){return objLottoList;}
    
    private static Set<Integer> objD3List=new HashSet<>();
    public static void registerD3Service(Integer id) {objD3List.add(id);}//存
    public static void removeD3Map(Integer key){objD3List.remove(key);}//删
    public static boolean isD3Contains(Integer id){return  objD3List.contains(id);};//判断存在
    public static void clearD3Data(){objD3List.clear();}
    public static Set<Integer> getD3Obj(){return objD3List;}
    
    private static Set<Integer> objP3List=new HashSet<>();
    public static void registerP3Service(Integer id) {objP3List.add(id);}//存
    public static void removeP3Map(Integer key){objP3List.remove(key);}//删
    public static boolean isP3Contains(Integer id){return  objP3List.contains(id);};//判断存在
    public static void clearP3Data(){objP3List.clear();}
    public static Set<Integer> getP3Obj(){return objP3List;}
    
    private static Set<Integer> objP5List=new HashSet<>();
    public static void registerP5Service(Integer id) {objP5List.add(id);}//存
    public static void removeP5Map(Integer key){objP5List.remove(key);}//删
    public static boolean isP5Contains(Integer id){return  objP5List.contains(id);};//判断存在
    public static void clearP5Data(){objP5List.clear();}
    public static Set<Integer> getP5Obj(){return objP5List;}

    public static void clearAll(){//全部清空
        objDoubleList.clear();
        objLottoList.clear();
        objD3List.clear();
        objP3List.clear();
        objP5List.clear();
    }
}