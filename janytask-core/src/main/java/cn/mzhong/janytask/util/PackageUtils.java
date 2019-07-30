package cn.mzhong.janytask.util;

public abstract class PackageUtils {
    private PackageUtils() {

    }

    public static String[] parseMultiple(String packageString) {
        String[] packages = new String[0];
        if (StringUtils.isEmpty(packageString)) {
            packages = new String[0];
        } else {
            packages = packageString.split(",|，");
            int index = packages.length;
            while (index-- != 0) {
                packages[index] = packages[index].trim();
            }
        }
        return packages;
    }
}
