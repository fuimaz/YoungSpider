package com.fuimaz.youngspider.http.client;

import org.apache.http.Consts;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static com.fuimaz.youngspider.http.client.DefaultConstants.ZHI_HU_EMAIL_LOGIN;
import static com.fuimaz.youngspider.http.client.DefaultConstants.ZHI_HU_HOME;


/**
 * Created by Fuimaz on 2016/11/20.
 */
public class ZhihuLogin {
    private Logger logger = LoggerFactory.getLogger(ZhihuLogin.class);

    private HTTPRequstClient httpRequstClient = new HTTPRequstClient();

    /**
     *
     * @return cookies
     */
    public String login() {
        //获取_xsrf
        String xsrfValue = fecthXsrf();


        // 获取验证码
        String captcha = fetchCaptcha();

        //构造post数据
        List<NameValuePair> valuePairs = new LinkedList<NameValuePair>();
        valuePairs.add(new BasicNameValuePair("_xsrf", xsrfValue));
        valuePairs.add(new BasicNameValuePair("email", "785328089@qq.com"));
        valuePairs.add(new BasicNameValuePair("password", ""));
        valuePairs.add(new BasicNameValuePair("remember_me", "true"));
        valuePairs.add(new BasicNameValuePair("captcha", captcha));

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);
        CloseableHttpResponse closeableHttpResponse = httpRequstClient.httpPost(ZHI_HU_EMAIL_LOGIN, entity);

        String cookie = HttpResponseDecoder.getCookieString(closeableHttpResponse);
        logger.info("cookie={}", cookie);

        // 获取cookie后验证URL
        closeableHttpResponse = httpRequstClient.httpGet("http://www.zhihu.com/question/following", cookie);
        String content = HttpResponseDecoder.getEntityString(closeableHttpResponse);

        logger.info("content={}", content);

        return null;
    }

    //获取_xsrf
    private String fecthXsrf() {
        CloseableHttpResponse response = httpRequstClient.httpGet(ZHI_HU_HOME);
        String responseHtml = HttpResponseDecoder.getEntityString(response);
        String xsrfValue = responseHtml.split("<input type=\"hidden\" name=\"_xsrf\" value=\"")[1].split("\"/>")[0];

        logger.info("xsrf={}", xsrfValue);
        return xsrfValue;
    }

    private String fetchCaptcha() {
        //获取验证码
        CloseableHttpResponse imageResponse = httpRequstClient.httpGet("http://www.zhihu.com/captcha.gif?r="
                + System.currentTimeMillis() + "&type=login");

        FileOutputStream out;
        try {
            out = new FileOutputStream("conf\\zhihu.gif");

            byte[] imageByte;
            while ((imageByte = HttpResponseDecoder.getBytesStream(imageResponse)) != null) {
                out.write(imageByte, 0, imageByte.length);
            }

            out.close();
            ImageUtil.showImageInDifferentPlatform("conf\\zhihu.gif");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //请用户输入验证码
        System.out.println("请输入验证码：");
        Scanner scanner = new Scanner(System.in);
        String captcha = scanner.next();

        return captcha;
    }


}
