package com.fuimaz.youngspider;

import org.apache.http.*;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Hello world!
 */
public class App {
    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static Map<String,String> cookieMap = new HashMap<String, String>(64);

    public static void main(String[] args)
    {

        //创建一个HttpClient
        RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD_STRICT).build();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();

        try {
            //创建一个get请求用来接收_xsrf信息
            HttpGet get = new HttpGet("http://www.zhihu.com/");
            HttpContext context = new HttpClientContext();
            //获取_xsrf
            CloseableHttpResponse response = httpClient.execute(get,context);
            setCookie(response);
            String responseHtml = EntityUtils.toString(response.getEntity());
            String xsrfValue = responseHtml.split("<input type=\"hidden\" name=\"_xsrf\" value=\"")[1].split("\"/>")[0];
            System.out.println("xsrfValue:" + xsrfValue);
            response.close();

            //构造post数据
            List<NameValuePair> valuePairs = new LinkedList<NameValuePair>();
            valuePairs.add(new BasicNameValuePair("_xsrf", xsrfValue));
            valuePairs.add(new BasicNameValuePair("email", "785328089@qq.com"));
            valuePairs.add(new BasicNameValuePair("password", "31577380"));
            valuePairs.add(new BasicNameValuePair("remember_me", "true"));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);

            //创建一个post请求
            HttpPost post = new HttpPost("http://www.zhihu.com/login/email");
            post.setHeader("Cookie", "d_c0=\"ABCAOBCztAmPTnJrEGeaGq49ReKHWh8QJWk=|1459525357\"; _za=32c0b817-7c3b-4f44-b6b4-408b35eafdf0; _zap=d1c5cbd8-5580-4af1-bf65-f18d3b10eeed; q_c1=7aece427900d4c3fbd8714364daec2e5|1478582736000|1459525357000; _xsrf=93af2e9fb521187556cb5ffe552ff0f3; l_cap_id=\"Yzg1YzI5NDM5OWJmNGIyNjliNjlhNWVjNjhiZDBhYzc=|1479616238|daba432472781e0f7c77d276ea4d7bf76ce6f6f9\"; cap_id=\"MzE4ZjI0MTdiNzMzNDRkZDljNjYxY2I1ZDhlZTk5Nzc=|1479616238|969835207d34789fe8b60fc737868d80d042bbf9\"; __utmt=1; r_cap_id=\"NWMwYjU1NjJkN2ZiNDAxM2E5OTYyZmMyZjJkNDIxNGQ=|1479616239|643047769b72c67ff33140e755366a2122b306a0\"; login=\"ZDNkYzNkYzE1MDMxNGE2ZjgxNzA3MDhmYzVlYWMyNmY=|1479616315|a8e7295611ac7c9492e57be9cb89631d7a0a6c6e\"; __utma=51854390.1802840126.1466692310.1479608613.1479616242.14; __utmb=51854390.6.10.1479616242; __utmc=51854390; __utmz=51854390.1479616242.14.12.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; __utmv=51854390.000--|2=registration_date=20130513=1^3=entry_date=20160401=1; n_c=1");
//请求Header
            post.setHeader("(Request-Line)","POST /login HTTP/1.1");
            post.setHeader("Referer", "http://www.zhihu.com/");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            post.setHeader("Accept-Language","zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
            post.setHeader("Accept-Encoding","gzip, deflate");
            post.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            post.setHeader("Connection","keep-alive");
            post.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64; rv:15.0) Gecko/20100101 Firefox/15.0.1");

            //注入post数据
            post.setEntity(entity);
            HttpResponse httpResponse = httpClient.execute(post);
            //打印登录是否成功信息
            printResponse(httpResponse);

            //构造一个get请求，用来测试登录cookie是否拿到
            HttpGet g = new HttpGet("http://www.zhihu.com/question/following");
            //得到post请求返回的cookie信息
            String c = setCookie(httpResponse);
            //将cookie注入到get请求头当中
            g.setHeader("Cookie",c);
            CloseableHttpResponse r = httpClient.execute(g);
            String content = EntityUtils.toString(r.getEntity());
            logger.info("content: {}", content);
            r.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void printResponse(HttpResponse httpResponse)
            throws ParseException, IOException {
        // 获取响应消息实体
        HttpEntity entity = httpResponse.getEntity();
        // 响应状态
        System.out.println("status:" + httpResponse.getStatusLine());
        System.out.println("headers:");
        HeaderIterator iterator = httpResponse.headerIterator();
        while (iterator.hasNext()) {
            System.out.println("\t" + iterator.next());
        }
        // 判断响应实体是否为空
        if (entity == null) {
            logger.error("return entity is empty");
            return;
        }

        String responseString = EntityUtils.toString(entity);
        System.out.println("response length:" + responseString.length());
        logger.info("response content: {}", responseString.replace("\r\n", ""));
    }

    //从响应信息中获取cookie
    public static String setCookie(HttpResponse httpResponse)
    {
        System.out.println("----setCookieStore");
        Header headers[] = httpResponse.getHeaders("Set-Cookie");
        if (headers == null || headers.length == 0) {
            logger.error("----there are no cookies");
            return null;
        }
        String cookie = "";
        for (int i = 0; i < headers.length; i++) {
            cookie += headers[i].getValue();
            if(i != headers.length-1)
            {
                cookie += ";";
            }
        }

        String cookies[] = cookie.split(";");
        for (String c : cookies) {
            c = c.trim();
            if(cookieMap.containsKey(c.split("=")[0]))
            {
                cookieMap.remove(c.split("=")[0]);
            }
            cookieMap.put(c.split("=")[0], c.split("=").length == 1 ? "" : (c.split("=").length == 2 ? c.split("=")[1] : c.split("=",2)[1]));
        }
        System.out.println("----setCookieStore success");
        String cookiesTmp = "";
        for (String key : cookieMap.keySet()) {
            cookiesTmp += key + "=" + cookieMap.get(key) + ";";
        }

        return cookiesTmp.substring(0, cookiesTmp.length() - 2);
    }
}
