package cn.ouctechnology.oucfly.operator;

import cn.ouctechnology.oucfly.core.OucFly;
import cn.ouctechnology.oucfly.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 11:51
 * @description: 操作组件，所有的操作均是这个组件的子类
 * 需要拓展功能的时候，只需要继承此类，实现run方法即可
 **/
public abstract class Operator<T> {
    protected Logger logger = LoggerFactory.getLogger(Operator.class);
    protected OucFly oucFly;

    public abstract Result<T> run(String host);

    public void setOucFly(OucFly oucFly) {
        this.oucFly = oucFly;
    }
}
