package cn.ouctechnology.oucfly.http;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 11:52
 * @description: Cookie存储容器，全局单例
 **/
public class OkCookie implements CookieJar {
    //使用线程安全的map
    private final Map<String, List<Cookie>> cookieStore = new ConcurrentHashMap<>();

    @Override
    public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
        cookieStore.put(httpUrl.host(), list);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl httpUrl) {
        List<Cookie> cookies = cookieStore.get(httpUrl.host());
        return cookies == null ? new ArrayList<>() : cookies;
    }

    public Map<String, List<Cookie>> getCookieStore() {
        return cookieStore;
    }
}
