package cn.ouctechnology.oucfly.core;

import cn.ouctechnology.oucfly.exception.OucException;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-08 15:15
 * @description: 存放主机工作时可用的url
 **/
public class HostSet {
    private List<Host> hostList;

    HostSet() {
        this.hostList = new ArrayList<>();
    }

    /**
     * 添加url
     *
     * @param host
     */
    void addHost(Host host) {
        if (hostList.contains(host)) return;
        this.hostList.add(host);
    }

    /**
     * 移除不能联通的url
     *
     * @param oucFly
     */
    void checkConnection(OucFly oucFly) {
        //removeIf厉害厉害
        hostList.removeIf(host -> !host.testConnection(oucFly));
    }

    int getSize() {
        return hostList.size();
    }

    void clear() {
        hostList.clear();
    }

    /**
     * 获取下次请求应该请求到的URL
     * 此处执行最小连接数的负载均衡算法
     * 此方法需要加锁
     *
     * @return
     */
    public synchronized Host getHost() {
        if (hostList == null || hostList.size() == 0) throw new OucException("the host list has not init");
        Host minCount = hostList.get(0);
        for (int i = 1; i < hostList.size(); i++) {
            Host host = hostList.get(i);
            if (minCount.getCount() > host.getCount()) minCount = host;
        }
        return minCount;
    }
}
