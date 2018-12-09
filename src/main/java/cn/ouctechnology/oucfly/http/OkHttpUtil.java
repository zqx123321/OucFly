package cn.ouctechnology.oucfly.http;

import cn.ouctechnology.oucfly.exception.OucException;
import okhttp3.*;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 11:46
 * @description: Http请求发送的工具类
 **/
public class OkHttpUtil {
    private static Logger logger = LoggerFactory.getLogger(OkHttpUtil.class);
    //创建cookie容器
    private static final OkCookie okCookie = new OkCookie();
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36";
    //设置失败重试retryOnConnectionFailure(true)
    private static OkHttpClient client = new OkHttpClient.Builder().cookieJar(okCookie).retryOnConnectionFailure(true).build();


    public static String get(String url) {
        return get(url, null, "");
    }

    public static String get(String url, String refer) {
        return get(url, refer, "");
    }

    public static String get(String url, HttpParams params) {
        return get(url, null, params);
    }

    public static String get(String url, String refer, HttpParams params) {
        if (params != null)
            return get(url, refer, params.toString());
        return get(url, refer, "");
    }

    /**
     * 发送get请求
     *
     * @param url    请求url
     * @param refer  请求的refer
     * @param params 请求参数
     */
    public static String get(String url, String refer, String params) {
        //线程安全的request
        Request.Builder builder = new Request.Builder().addHeader(HttpHeaders.USER_AGENT, USER_AGENT);

        if (params != null && !params.equals("")) {
            url += ("?" + params);
        }
        logger.trace("do http get,url: {}", url);
        if (refer != null) {
            builder.header(HttpHeaders.REFERER, refer);
        }

        builder.url(url);
        Request request = builder.build();
        return doHttpRequest(request);
    }

    public static String post(String url) {
        return post(url, null, null);
    }

    public static String post(String url, String refer) {
        return post(url, refer, null);
    }

    public static String post(String url, HttpParams params) {
        return post(url, null, params);
    }

    /**
     * 发送post请求
     *
     * @param url
     * @param refer
     * @param params
     */
    public static String post(String url, String refer, HttpParams params) {
        //线程安全的request
        Request.Builder builder = new Request.Builder().addHeader(HttpHeaders.USER_AGENT, USER_AGENT);
        if (params != null) {
            FormBody formBody = params.toFormBody();
            builder.post(formBody);
        }
        if (refer != null) {
            builder.header(HttpHeaders.REFERER, refer);
        }
        logger.trace("do http post,url: {} ,params: {}", url, params == null ? "" : params.toString());
        builder.url(url);
        Request request =builder.build();
        return doHttpRequest(request);
    }

    /**
     * 原生HttpClient发送方式，用于带有中文的GBK编码
     */
    public static String postHttpClient(String url, String refer, String params) {
        // 全局请求设置
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
        // 创建cookie store的本地实例
        CookieStore cookieStore = new BasicCookieStore();
        String host = url.substring(7);
        host = host.substring(0, host.indexOf("/"));
        List<Cookie> cookies = okCookie.getCookieStore().get(host);
        cookies.forEach(c -> {
            BasicClientCookie cookie = new BasicClientCookie(c.name(), c.value());
            cookie.setVersion(0);
            cookie.setDomain(c.domain());   //设置范围
            cookie.setPath(c.path());
            cookieStore.addCookie(cookie);
        });
        // 创建HttpClient上下文
        HttpClientContext context = HttpClientContext.create();
        //设置cookie
        context.setCookieStore(cookieStore);
        // 创建一个HttpClient
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig)
                .setDefaultCookieStore(cookieStore).build();
        HttpPost httpPost = new HttpPost(url);
        //对参数使用GBK编码，contentType不指定编码！！！！！
        StringEntity entity = new StringEntity(params, "gbk");
        entity.setContentType("application/x-www-form-urlencoded");
        httpPost.setEntity(entity);
        httpPost.addHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:22.0) Gecko/20100101 Firefox/22.0");
        httpPost.addHeader(HttpHeaders.REFERER, refer);
        logger.info("Executing request " + httpPost.getRequestLine() + " to " + url + "params: " + params);
        try {
            HttpResponse response = httpClient.execute(httpPost);
            return EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            logger.error("IOError: {}", e);
            throw new OucException("IOError", e);
        }
    }

    private static String doHttpRequest(Request request) {
        try {
            Response response = client.newCall(request).execute();
            return parseResponse(response);
        } catch (IOException e) {
            logger.error("IOError:{}", e);
            throw new OucException("IOError", e);
        }

    }

    private static String parseResponse(Response response) throws IOException {
        int code = response.code();
        ResponseBody responseBody = response.body();
        if (code == 200 && responseBody != null) {
            return responseBody.string();
        }
        if (code != 200) {
            String msg = "http error,code:" + code;
            logger.info(msg);
            throw new OucException(msg);
        }
        throw new OucException("the get result is null");
    }


    public static OkCookie getOkCookie() {
        return okCookie;
    }
}
