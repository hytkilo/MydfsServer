package cn.hyt.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by hyt on 2016/6/27.
 */
public class PropertiesUtil {

    public static String getValue(String key) {
        String value = "";
        // 老子说的  上士问道 勤而行之
        Properties properties = new Properties();
        //人很人的差别 记忆力
        InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream("project.properties");
        try {
            properties.load(in);
            value = properties.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return value;

    }

    public static void main(String[] args) {
        String value=PropertiesUtil.getValue("mydfs.port");
        System.out.println(value);
    }
}


