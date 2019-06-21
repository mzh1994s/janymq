package cn.mzhong.janymq.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class FieldUtils {

    private FieldUtils() {
    }

    public static List<Field> getAllFields(Class<?> _class) {
        List<Field> fieldList = new ArrayList<>();
        Class<?> _loopClass = _class;
        do {
            for (Field field : _loopClass.getDeclaredFields()) {
                fieldList.add(field);
            }
            _loopClass = _loopClass.getSuperclass();
        } while (_loopClass != null);
        return fieldList;
    }
}
