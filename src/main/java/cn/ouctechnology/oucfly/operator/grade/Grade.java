package cn.ouctechnology.oucfly.operator.grade;

import cn.ouctechnology.oucfly.core.Host;
import cn.ouctechnology.oucfly.encryption.EncryptionUtil;
import cn.ouctechnology.oucfly.exception.OucException;
import cn.ouctechnology.oucfly.http.OkHttpUtil;
import cn.ouctechnology.oucfly.operator.Operator;
import cn.ouctechnology.oucfly.operator.XnXq;
import cn.ouctechnology.oucfly.operator.token.Token;
import cn.ouctechnology.oucfly.operator.token.TokenEntity;
import cn.ouctechnology.oucfly.result.Result;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 10:58
 * @description: 获取成绩基类，抽象
 **/
public abstract class Grade<T> extends Operator<T> implements Cloneable {

    /**
     * 学期类型
     */
    public enum XnXqType {
        //单个学期
        SINGLE {
            @Override
            public String toString() {
                return "sjxz3";
            }
        },
        //入学以来
        ALL {
            @Override
            public String toString() {
                return "sjxz1";
            }
        };

        public abstract String toString();
    }

    //序号
    protected String userCode;
    protected XnXqType type;
    protected XnXq xnXq;


    /**
     * 构造函数1，指定学期型
     *
     * @param userCode
     * @param xnXq
     */
    public Grade(String userCode, XnXq xnXq) {
        this.userCode = userCode;
        this.xnXq = xnXq;
        this.type = XnXqType.SINGLE;
    }

    /**
     * 构造函数2，不指定学期，即入学以来
     *
     * @param userCode
     */
    public Grade(String userCode) {
        this.userCode = userCode;
        this.type = XnXqType.ALL;
        //无用赋值
        this.xnXq = new XnXq(1, XnXq.Xq.AUTUMN);
    }

    /**
     * 构造函数3，指定学期，没有学号，用于后续setCode
     *
     * @param xnXq
     */
    public Grade(XnXq xnXq) {
        this.xnXq = xnXq;
        this.type = XnXqType.SINGLE;
    }

    public Grade() {
        this.type = XnXqType.ALL;
        //无用赋值
        this.xnXq = new XnXq(1, XnXq.Xq.AUTUMN);
    }

    @Override
    public Result<T> run(String host) {
        if (userCode == null) throw new OucException("must set the userCode");
        String url = host + "student/xscj.stuckcj_data.jsp";
        String refer = host + "student/xscj.stuckcj.jsp?menucode=JW130705";
        String param = "xn=" + xnXq.getXn() + "&xn1=" + xnXq.getXn() + "&xq=" + xnXq.getXq().ordinal() +
                "&ysyx=yscj&sjxz=" + type.toString() + "&userCode=" + userCode;
        String content;
        //获取DesKey加密发送请求这一系列过程在一个host的一次执行中，应该是原子性的
        ReentrantLock lock = Host.getReentrantLock(host);
        lock.lock();
        try {
            Result<TokenEntity> tokenRes = oucFly.run(new Token(), host);
            if (!tokenRes.isSuccess()) {
                return Result.fail(tokenRes.getErrorMsg());
            }
            TokenEntity tokenEntity = tokenRes.getContent();
            logger.debug("origin params: {}", param);
            param = EncryptionUtil.getEncParams(param, tokenEntity.getNowTime(), tokenEntity.getDesKey());
            logger.debug("encode params: {}", param);
            content = OkHttpUtil.get(url, refer, param);
        } catch (OucException e) {
            logger.error("get grade error: {}", e);
            return Result.fail("get grade error: " + e);
        } finally {
            lock.unlock();
        }
        return parserHtml(content);
    }


    /**
     * 解析response的方法在子类中实现
     */
    public abstract Result<T> parserHtml(String content);

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

}
