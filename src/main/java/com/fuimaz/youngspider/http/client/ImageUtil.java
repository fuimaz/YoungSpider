package com.fuimaz.youngspider.http.client;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by Fuimaz on 2016/11/22.
 */
public class ImageUtil {

    public static void showImageInDifferentPlatform(String path) {
        if (StringUtils.isEmpty(path)) {
            return ;
        }

        Properties props=System.getProperties(); //获得系统属性集
        String osName = props.getProperty("os.name"); //操作系统名称

        String command;
        if (osName.startsWith("Windows")) {
            command = String.format("cmd /c %s", path);
        } else {
            command = String.format("eog %s", path);
        }

        try {
            Runtime.getRuntime().exec(command);//ubuntu下看图片的命令是eog
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
