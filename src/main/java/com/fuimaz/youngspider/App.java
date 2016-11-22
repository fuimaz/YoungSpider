package com.fuimaz.youngspider;

import com.fuimaz.youngspider.http.client.ZhihuLogin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * Hello world!
 */
public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static Map<String,String> cookieMap = new HashMap<String, String>(64);

    public static void main(String[] args) {
        ZhihuLogin zhihuLogin = new ZhihuLogin();

        zhihuLogin.login();
    }

}
