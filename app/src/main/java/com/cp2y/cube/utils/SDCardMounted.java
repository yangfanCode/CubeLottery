package com.cp2y.cube.utils;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import com.cp2y.cube.helper.ContextHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**SD卡是否挂载
 * Created by yangfan on 2017/6/16.
 */
public class SDCardMounted {

    public static boolean isSDMounted() {
        boolean isMounted = false;
        StorageManager sm = (StorageManager) ContextHelper.getApplication().getSystemService(Context.STORAGE_SERVICE);

        try {
            Method getVolumList = StorageManager.class.getMethod("getVolumeList",  new Class<?>[]{});
            getVolumList.setAccessible(true);
            Object[] results = (Object[])getVolumList.invoke(sm, new Object[]{});
            if (results != null) {
                for (Object result : results) {
                    Method mRemoveable = result.getClass().getMethod("isRemovable", new Class<?>[]{});
                    Boolean isRemovable = (Boolean) mRemoveable.invoke(result, new Object[]{});
                    if (isRemovable) {
                        Method getPath = result.getClass().getMethod("getPath", new Class<?>[]{});
                        String path = (String) mRemoveable.invoke(result, new Object[]{});
                        Method getState = sm.getClass().getMethod("getVolumeState", String.class);
                        String state = (String)getState.invoke(sm, path);
                        if (state.equals(Environment.MEDIA_MOUNTED)) {
                            isMounted = true;
                            break;
                        }
                    }
                }
            }
        } catch (NoSuchMethodException e){
            e.printStackTrace();
        } catch (IllegalAccessException e){
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return isMounted;
    }
}
