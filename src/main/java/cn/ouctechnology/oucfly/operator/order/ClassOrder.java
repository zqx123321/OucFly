package cn.ouctechnology.oucfly.operator.order;

import cn.ouctechnology.oucfly.operator.Operator;
import cn.ouctechnology.oucfly.operator.XnXq;
import cn.ouctechnology.oucfly.result.Result;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-08 11:53
 * @description: 获取某门课的所有成绩
 **/
public class ClassOrder extends Operator<OrderEntity> {
    //学年学期
    private XnXq xnXq;
    //选课号
    private String classCode;

    public ClassOrder(XnXq xnXq, String classCode) {
        this.xnXq = xnXq;
        this.classCode = classCode;
    }

    @Override
    public Result<OrderEntity> run(String host) {
        ClassOrderDirectly classOrderDirectly = new ClassOrderDirectly(xnXq, classCode);
        Result<OrderEntity> resDirectly = oucFly.run(classOrderDirectly);
        if (resDirectly.isSuccess()) return resDirectly;
        logger.info("get the class order directly fail, get the order for each....");
        ClassOrderForEach classOrderForEach = new ClassOrderForEach(xnXq, classCode, classOrderDirectly.getKcdm());
        return oucFly.run(classOrderForEach);
    }
}
