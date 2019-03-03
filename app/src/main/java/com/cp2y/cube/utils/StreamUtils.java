package com.cp2y.cube.utils;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yangfan on 2017/4/17.
 */
public class StreamUtils {

    /**
     * 序列化,List
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static <T> boolean writeObject(List<T> list, File file)
    {
        T[] array = (T[]) list.toArray();
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file)))
        {
            out.writeObject(array);
            out.flush();
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 反序列化,List
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static <E> List<E> readObjectForList(File file)
    {
        E[] object;
        try(ObjectInputStream out = new ObjectInputStream(new FileInputStream(file)))
        {
            object = (E[]) out.readObject();
            return Arrays.asList(object);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }
//    //序列化
//    StreamUtils.<TestObject>writeObject(list, new File("object.adt"));
//    //反序列化
//    List<TestObject> re = StreamOfByte.<TestObject>readObjectForList(new File("object.txt"));


}
