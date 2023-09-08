package com.canvs.ssm.ioc;

import java.lang.reflect.Field;

public class BaseDAOPackageInjection {
    public static void injection(String packageName,String baseDAOType) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        Class<?> baseDAO = Class.forName(baseDAOType);
        Field field = baseDAO.getDeclaredField("packageName");
        field.setAccessible(true);
        field.set(baseDAO,packageName);
    }
}
