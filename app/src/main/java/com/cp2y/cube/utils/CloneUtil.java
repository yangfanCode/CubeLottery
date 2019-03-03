package com.cp2y.cube.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by js on 2017/1/10.
 */
public class CloneUtil {

    public static void cloneObjectOrginal(Object src, Object dst) {
        Map<String, Object> map = new HashMap<>();
        for(Method m : src.getClass().getMethods()) {
            String name = m.getName();
            if(name.startsWith("get")) {//获取get方法
                try {
                    Object val = m.invoke(src);
                    map.put(name.substring(3), val);//放到Map中去
                }  catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (name.startsWith("is")) {//获取is方法
                try {
                    Object val = m.invoke(src);
                    map.put(name.substring(2), val);//放到Map中去
                }  catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        for (Method m : MethodCache.getMethods(dst.getClass())) {
            String name = m.getName();
            if (m.getParameterTypes().length != 1 || !name.startsWith("set"))
                continue;
            name = name.substring(3);
            try {
                if (map.containsKey(name)) {
                    Object args = map.get(name);
                    m.invoke(dst, args);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 对象复制方法
     * @param src
     * @param dst
     */
    public static void cloneObject(Object src, Object dst) {
        if (src == null || dst == null || (src.getClass() != dst.getClass() && !src.getClass().isAssignableFrom(dst.getClass()))) return;//not same type return
        cloneObjectOrginal(src, dst);
    }

    /**
     * 深复制
     * @param <T>
     * @param obj
     * @return
     */
    public static <T extends Serializable> T clone(Object obj){
        T cloneObj = null;
        try {
            //写入字节流
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream obs = new ObjectOutputStream(out);
            obs.writeObject(obj);
            obs.close();
            //分配内存，写入原始对象，生成新对象
            ByteArrayInputStream ios = new ByteArrayInputStream(out.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(ios);
            //返回生成的新对象
            cloneObj = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cloneObj;
    }

}

