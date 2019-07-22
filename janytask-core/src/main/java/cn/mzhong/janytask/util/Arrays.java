package cn.mzhong.janytask.util;

import java.lang.reflect.Array;

@SuppressWarnings("unchecked")
public abstract class Arrays {

    public static <T> T[] add(T[] arr, T e) {
        T[] newArr = (T[]) Array.newInstance(arr.getClass().getComponentType(), arr.length + 1);
        System.arraycopy(arr, 0, newArr, 0, arr.length);
        newArr[arr.length] = e;
        return newArr;
    }
}
