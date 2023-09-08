package com.canvs.ssm.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class PackageScanner {
    public static List<String> getAllClassesInPackage(String packageName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.','/');
        Enumeration<URL> resources = classLoader.getResources(path);
        ArrayList<String> classNames = new ArrayList<>();
        while (resources.hasMoreElements()){
            URL resource = resources.nextElement();
            if (resource.getProtocol().equals("file")){
                File packageDir = new File(resource.getFile());
                classNames.addAll(findClassesInDirectory(packageName, packageDir));
            }
        }
        return classNames;
    }
    public static List<String> findClassesInDirectory(String packageName,File packageDir){
        ArrayList<String> classNames = new ArrayList<>();
        File[] files = packageDir.listFiles();
        if(files != null){
            for (File file : files){
                if (file.isDirectory()){
                    classNames.addAll(findClassesInDirectory(packageName + "." + file.getName(), file));
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                    classNames.add(className);
                }
            }
        }
        return classNames;
    }
}
