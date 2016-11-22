package com.fuimaz.youngspider.http.client;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;

/**
 * Created by Fuimaz on 2016/11/20.
 */
public class HTTPRequstClient {
    private Logger logger = LoggerFactory.getLogger(HTTPRequstClient.class);

    private CloseableHttpClient httpClient;

    private RequestConfig requestConfig;

    public void init(RequestConfig requestConfig){
        this.requestConfig = requestConfig;
        if (httpClient != null) {
            httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        }
    }

    public HTTPRequstClient() {
        //创建一个HttpClient
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(10000)
                .setConnectTimeout(5000)
                .setCookieSpec(CookieSpecs.STANDARD_STRICT)         // 标准cookie策略，在知乎上要设置这个
                .build();
        httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
    }

    public HTTPRequstClient(RequestConfig requestConfig) {
        if (httpClient != null) {
            httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(requestConfig).build();
        }
    }

    // *******************************httpPost******************************************************************
    /**
     *
     * @param url
     * @param httpPost
     * @return
     */
    public CloseableHttpResponse httpPost(String url, HttpPost httpPost) {
        httpPost.setURI(URI.create(url));
        return httpPost(httpPost);
    }

    public CloseableHttpResponse httpPost(HttpPost httpPost) {
        setDefaultPostHeader(httpPost);
        return httpExecute(httpPost);
    }

    public CloseableHttpResponse httpPost(HttpPost httpPost,
                                          UrlEncodedFormEntity urlEncodedFormEntity) {
        httpPost.setEntity(urlEncodedFormEntity);
        return httpPost(httpPost);
    }

    public CloseableHttpResponse httpPost(String url,
                                          UrlEncodedFormEntity urlEncodedFormEntity) {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(urlEncodedFormEntity);
        return httpPost(url, httpPost);
    }

    // *******************************httpGet******************************************************************

    public CloseableHttpResponse httpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        return httpExecute(httpGet);
    }

    public CloseableHttpResponse httpGet(String url, String cookie) {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Cookie", cookie);
        return httpExecute(httpGet);
    }

    // *******************************************************************************************************

    private CloseableHttpResponse httpExecute(HttpUriRequest httpUriRequest) {
        CloseableHttpResponse closeableHttpResponse;
        try {
            closeableHttpResponse = httpClient.execute(httpUriRequest);
            return  closeableHttpResponse;
        } catch (ClientProtocolException ce) {
            logger.error("{}, {}", ce.getCause(), ce.getStackTrace());
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setDefaultPostHeader(HttpPost httpPost) {
        httpPost.setHeader("Referer", "https://www.zhihu.com/");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36");
    }
}
