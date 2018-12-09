package cn.ouctechnology.oucfly.core;

import cn.ouctechnology.oucfly.exception.OucException;
import cn.ouctechnology.oucfly.operator.login.Login;
import cn.ouctechnology.oucfly.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-08 15:15
 * @description: 教务处可用的URL
 **/
public enum Host {
    JWGL_OUC_EDU_CN("http://jwgl.ouc.edu.cn/"),
    JWGL_2_OUC_EDU_CN("http://jwgl2.ouc.edu.cn/"),
    I_222_195_158_225("http://222.195.158.225/"),//只可以校内访问
    I_222_195_158_206("http://222.195.158.206/");//只可以校内访问

    //提供通过url得到枚举的方式
    private static Map<String, Host> hostMap = new HashMap<>();

    static {
        hostMap.put(JWGL_OUC_EDU_CN.value, JWGL_OUC_EDU_CN);
        hostMap.put(JWGL_2_OUC_EDU_CN.value, JWGL_OUC_EDU_CN);
        hostMap.put(I_222_195_158_225.value, JWGL_OUC_EDU_CN);
        hostMap.put(I_222_195_158_206.value, JWGL_OUC_EDU_CN);
    }

    private String value;

    //当前连接数，用于负载均衡
    private AtomicInteger count;

    /**
     * 每个URL上有一把锁，用于获取成绩时的原子性操作
     *
     * @see cn.ouctechnology.oucfly.operator.grade.Grade
     */
    private ReentrantLock reentrantLock = new ReentrantLock();

    private static Logger logger = LoggerFactory.getLogger(Host.class);

    Host(String value) {
        this.value = value;
        this.count = new AtomicInteger(0);
    }

    public String getValue() {
        return value;
    }

    public int getCount() {
        return count.get();
    }

    /**
     * 增加连接
     */
    public void incr() {
        this.count.incrementAndGet();
    }

    /**
     * 减少连接
     */
    public void decr() {
        this.count.decrementAndGet();
    }

    public static ReentrantLock getReentrantLock(String host) {
        if (!host.contains(host)) throw new OucException("the host is not corrected");
        return hostMap.get(host).reentrantLock;
    }

    /**
     * 测试是否联通，并登陆获取操作，放于OkCookie中
     *
     * @param oucFly
     * @see cn.ouctechnology.oucfly.http.OkCookie
     */
    boolean testConnection(OucFly oucFly) {
        String host = this.value;
        Result<String> result = new Login(oucFly.getUsername(), oucFly.getPassword()).run(host);
        if (!result.isSuccess()) {
            logger.info("login in:{} fail :{}", host, result.getErrorMsg());
            return false;
        }
        logger.info("login in:{} successfully", host);
        return true;
    }
}
