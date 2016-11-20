package com.fuimaz.youngspider.http.client;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.fuimaz.youngspider.http.client.DefaultConstants.DEFAULT_BYTE_CACHE_SIZE;


/**
 * Created by Fuimaz on 2016/11/20.
 */
public class HttpResponseDecoder {
    private static Logger logger = LoggerFactory.getLogger(HttpResponseDecoder.class);

    private static byte[] cacheBytes = new byte[DEFAULT_BYTE_CACHE_SIZE];

    public static Map<String,String> cookieMap = new HashMap<>();

    public static byte[] getBytesStream(CloseableHttpResponse closeableHttpResponse) {
        return getBytesStream(closeableHttpResponse, true);
    }

    // 这个是线程不安全的，但性能要好些
    public static byte[] getBytesStream(CloseableHttpResponse closeableHttpResponse, boolean isCloseResponse) {
        if (checkEntityValid(closeableHttpResponse)) {
            return null;
        }

        try {
            if (closeableHttpResponse.getEntity().getContent() == null) {
                logger.error("closeableHttpResponse's entity is null pointer");
                return null;
            }

            int len = closeableHttpResponse.getEntity().getContent().read(cacheBytes);
            logger.info("read bytes length, {}", len);
            if (len == -1) {
                return null;
            }

            return cacheBytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeResponse(closeableHttpResponse, isCloseResponse);
        }
    }

    private static void closeResponse(CloseableHttpResponse closeableHttpResponse, boolean isCloseResponse) {
        if (isCloseResponse) {
            try {
                closeableHttpResponse.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static byte[] getBytesStreamAsync(CloseableHttpResponse closeableHttpResponse, boolean isCloseResponse) {
        if (checkEntityValid(closeableHttpResponse)) {
            return null;
        }

        try {
            if (closeableHttpResponse.getEntity().getContent() == null) {
                logger.error("closeableHttpResponse's entity is null pointer");
                return null;
            }

            byte[] bytes = new byte[DEFAULT_BYTE_CACHE_SIZE];
            int len = closeableHttpResponse.getEntity().getContent().read(bytes);
            logger.info("read bytes length, {}", len);
            if (len == -1) {
                return null;
            }

            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getBytesStreamAsync(CloseableHttpResponse closeableHttpResponse) {
        return getBytesStreamAsync(closeableHttpResponse, true);
    }

    public static String getEntityString(CloseableHttpResponse closeableHttpResponse) {
        if (checkEntityValid(closeableHttpResponse)) {
            return null;
        }

        try {
            return EntityUtils.toString(closeableHttpResponse.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean checkEntityValid(CloseableHttpResponse closeableHttpResponse) {
        if (closeableHttpResponse == null) {
            logger.error("closeableHttpResponse is null pointer");
            return false;
        }

        if (closeableHttpResponse.getStatusLine().getStatusCode() != 200) {
            logger.error("closeableHttpResponse status code isn't 200, status code: {}",
                    closeableHttpResponse.getStatusLine().getStatusCode());
            return false;
        }

        if (closeableHttpResponse.getEntity() == null) {
            logger.error("closeableHttpResponse's entity is null pointer");
            return false;
        }

        return true;
    }

    public static String getCookieString(CloseableHttpResponse closeableHttpResponse) {
        cookieMap.clear();
        return getCookieString(closeableHttpResponse, cookieMap);
    }

    public static String getCookieStringAsync(CloseableHttpResponse closeableHttpResponse) {
        Map<String,String> tmpMap = new HashMap<>();
        return getCookieString(closeableHttpResponse, tmpMap);
    }

    private static String getCookieString(CloseableHttpResponse closeableHttpResponse, Map<String, String> map) {
        Header headers[] = closeableHttpResponse.getHeaders("Set-Cookie");
        if (headers == null || headers.length == 0) {
            logger.error("closeableHttpResponse's headers is empty");
            return null;
        }

        StringBuilder rawCookie = new StringBuilder();
        for (int i = 0; i < headers.length; i++) {
            rawCookie.append(headers[i].getValue());
            if(i != headers.length - 1) {
                rawCookie.append(";");
            }
        }

        String cookies[] = rawCookie.toString().split(";");
        for (String c : cookies) {
            c = c.trim();
            if(map.containsKey(c.split("=")[0])) {
                map.remove(c.split("=")[0]);
            }

            String value = c.split("=").length == 1 ? "" : (c.split("=").length == 2 ? c.split("=")[1] : c.split("=",2)[1]);
            map.put(c.split("=")[0], value);
        }

        StringBuilder cookiesTmp = new StringBuilder();
        for (String key : map.keySet()) {
            String strTmp = String.format(key + "=" + map.get(key) + ";");
            cookiesTmp.append(strTmp);
        }

        return cookiesTmp.substring(0, cookiesTmp.length() - 2);
    }
}
