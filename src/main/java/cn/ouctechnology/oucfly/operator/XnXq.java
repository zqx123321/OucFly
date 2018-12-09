package cn.ouctechnology.oucfly.operator;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 10:10
 * @description: 公共组件，用于设置学年学期
 **/
public class XnXq {

    private int xn;

    private Xq xq;

    public XnXq(int xn, Xq xq) {
        this.xn = xn;
        this.xq = xq;
    }

    public int getXn() {
        //春季学期对应学年需要-1
        if (xq == Xq.SPRING)
            return xn - 1;
        return xn;
    }

    public void setXn(int xn) {
        this.xn = xn;
    }

    public Xq getXq() {
        return xq;
    }

    public void setXq(Xq xq) {
        this.xq = xq;
    }

    @Override
    public String toString() {
        return getXn() + "-" + getXq().ordinal();
    }

    public enum Xq {
        SUMMER, AUTUMN, SPRING;
    }
}
