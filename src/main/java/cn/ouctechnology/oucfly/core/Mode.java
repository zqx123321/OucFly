package cn.ouctechnology.oucfly.core;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-08 15:49
 * @description: TODO
 **/
public enum Mode {
    FULL_POWER, //全功率工作模式，系统自动测试全部的四个URL，把可用的加入到主机set
    OUTSIDE_ONLY, //校外工作模式，由于四个URL中的后两个只能校外访问，校外全功率不可用
    ASSIGN_SELF;//用于自定义主机列表
}
