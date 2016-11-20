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

    /**
     * 都需要在外部手动关闭CloseableHttpResponse实例
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
                                          HttpPost httpPost,
                                          UrlEncodedFormEntity urlEncodedFormEntity) {
        httpPost.setEntity(urlEncodedFormEntity);
        return httpPost(url, httpPost);
    }

    public CloseableHttpResponse httpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        return httpExecute(httpGet);
    }

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
        httpPost.setHeader("Cookie", "d_c0=\"ABCAOBCztAmPTnJrEGeaGq49ReKHWh8QJWk=|1459525357\"; _za=32c0b817-7c3b-4f44-b6b4-408b35eafdf0; _zap=d1c5cbd8-5580-4af1-bf65-f18d3b10eeed; q_c1=7aece427900d4c3fbd8714364daec2e5|1478582736000|1459525357000; _xsrf=93af2e9fb521187556cb5ffe552ff0f3; l_cap_id=\"Yzg1YzI5NDM5OWJmNGIyNjliNjlhNWVjNjhiZDBhYzc=|1479616238|daba432472781e0f7c77d276ea4d7bf76ce6f6f9\"; cap_id=\"MzE4ZjI0MTdiNzMzNDRkZDljNjYxY2I1ZDhlZTk5Nzc=|1479616238|969835207d34789fe8b60fc737868d80d042bbf9\"; __utmt=1; r_cap_id=\"NWMwYjU1NjJkN2ZiNDAxM2E5OTYyZmMyZjJkNDIxNGQ=|1479616239|643047769b72c67ff33140e755366a2122b306a0\"; login=\"ZDNkYzNkYzE1MDMxNGE2ZjgxNzA3MDhmYzVlYWMyNmY=|1479616315|a8e7295611ac7c9492e57be9cb89631d7a0a6c6e\"; __utma=51854390.1802840126.1466692310.1479608613.1479616242.14; __utmb=51854390.6.10.1479616242; __utmc=51854390; __utmz=51854390.1479616242.14.12.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; __utmv=51854390.000--|2=registration_date=20130513=1^3=entry_date=20160401=1; n_c=1");
        httpPost.setHeader("(Request-Line)","POST /login HTTP/1.1");
        httpPost.setHeader("Referer", "http://www.zhihu.com/");
        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Accept-Language","zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        httpPost.setHeader("Accept-Encoding","gzip, deflate");
        httpPost.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpPost.setHeader("Connection","keep-alive");
        httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:15.0) Gecko/20100101 Firefox/15.0.1");
    }
}
