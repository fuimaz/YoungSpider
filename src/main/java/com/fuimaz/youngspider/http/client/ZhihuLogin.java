package com.fuimaz.youngspider.http.client;

import org.apache.http.client.methods.CloseableHttpResponse;

import static com.fuimaz.youngspider.http.client.DefaultConstants.ZHI_HU_HOME;


/**
 * Created by Fuimaz on 2016/11/20.
 */
public class ZhihuLogin {
    private HTTPRequstClient httpRequstClient = new HTTPRequstClient();

    /**
     *
     * @return cookies
     */
    public String login() {

        return null;
    }

    //获取_xsrf
    private String fecthXsrf() {
        CloseableHttpResponse response = httpRequstClient.httpGet(ZHI_HU_HOME);
        String responseHtml = HttpResponseDecoder.getEntityString(response);
        String xsrfValue = responseHtml.split("<input type=\"hidden\" name=\"_xsrf\" value=\"")[1].split("\"/>")[0];
        return xsrfValue;
    }
}
