package cn.ouctechnology.oucfly.core;

import cn.ouctechnology.oucfly.exception.OucException;
import cn.ouctechnology.oucfly.operator.Operator;
import cn.ouctechnology.oucfly.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 11:51
 * @description: 全局配置对象，所有的操作均需要通过oucFly.run()方法来执行
 * 此对象可以作为全局单例对象，但是系统并不保证长时间不操作知道cookie过期之后
 * 会自动重新获取cookie
 **/
public class OucFly {
    private Logger logger = LoggerFactory.getLogger(OucFly.class);

    //登录学号
    private String username;
    //登录密码
    private String password;
    /**
     * 工作线程数
     */
    private int thread;
    /**
     * 工作时可用的教务处网址
     */
    private HostSet hostSet;

    /**
     * 创建OucFly类的Builder
     */
    public static class OucFlyBuilder {

        private String username;

        private String password;

        private HostSet hostSet;

        /**
         * Builder中必须指明学号和密码
         *
         * @param username
         * @param password
         */
        OucFlyBuilder(String username, String password) {
            this.username = username;
            this.password = password;
            this.hostSet = new HostSet();
        }

        private int thread = 1;

        private Mode mode = Mode.FULL_POWER;

        /**
         * 设置工作线程数
         * 系统只有在获取课程选课币（CoinClass）和获取专业排名（MajorOrder）
         * 时会启动多线程，推荐thread等于系统CPU数量
         *
         * @param thread
         */
        public OucFlyBuilder thread(int thread) {
            thread = thread < 1 ? 1 : thread;
            this.thread = thread;
            return this;
        }

        /**
         * 设置工作模式
         *
         * @see Mode
         */
        public OucFlyBuilder mode(Mode mode) {
            this.mode = mode;
            return this;
        }

        public OucFlyBuilder addHost(Host host) {
            hostSet.addHost(host);
            return this;
        }

        public OucFly build() {
            if (mode == Mode.FULL_POWER) {
                hostSet.clear();
                hostSet.addHost(Host.JWGL_OUC_EDU_CN);
                hostSet.addHost(Host.JWGL_2_OUC_EDU_CN);
                hostSet.addHost(Host.I_222_195_158_206);
                hostSet.addHost(Host.I_222_195_158_225);
            } else if (mode == Mode.OUTSIDE_ONLY) {
                hostSet.clear();
                hostSet.addHost(Host.JWGL_OUC_EDU_CN);
            }
            OucFly oucFly = new OucFly(username, password, thread, hostSet);
            //测试连接
            oucFly.testConnection();
            if (oucFly.hostSet.getSize() <= 0) {
                throw new OucException("these is no available host in the host list, if you user the mode" +
                        "ASSIGN_SELF, please check the host you set is corrected and available, if so, it is" +
                        "probably the network is poor");
            }
            return oucFly;
        }
    }


    public static OucFlyBuilder builder(String username, String password) {
        return new OucFlyBuilder(username, password);
    }

    /**
     * 获取下次请求应该请求的url
     *
     * @see HostSet
     */
    public Host getHost() {
        Host host = hostSet.getHost();
        logger.info("get host: {}, connection count: {}", host.getValue(), host.getCount());
        return host;
    }

    /**
     * 构造器私有，防止外界直接使用new来创建OucFly对象
     *
     * @param username
     * @param password
     * @param thread
     * @param hostSet
     */
    private OucFly(String username, String password, int thread, HostSet hostSet) {
        this.username = username;
        this.password = password;
        this.thread = thread;
        this.hostSet = hostSet;
    }

    /**
     * 核心函数，所有的操作均需要通过此方法来执行
     *
     * @param operator 待执行的操作
     * @param <T>      返回值的类型
     * @return
     */
    public <T> Result<T> run(Operator<T> operator) {
        operator.setOucFly(this);
        Host host = getHost();
        host.incr();
        Result<T> res = operator.run(host.getValue());
        host.decr();
        return res;
    }

    /**
     * 指定host 的run方法
     */
    public <T> Result<T> run(Operator<T> operator, String host) {
        operator.setOucFly(this);
        return operator.run(host);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    private void testConnection() {
        hostSet.checkConnection(this);
    }

    public int getThread() {
        return thread;
    }


}
