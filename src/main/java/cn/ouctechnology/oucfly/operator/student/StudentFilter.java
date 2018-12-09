package cn.ouctechnology.oucfly.operator.student;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 19:54
 * @description: 学生信息过滤器，用于选择是获取某个院系的学生数据，还是获取某门课的学生数据
 **/
public abstract class StudentFilter {

    protected String userCode;

    protected String userName;

    /**
     * 可以进一步过滤学号，选出具体某一个学生
     */
    public StudentFilter filterCode(String userCode) {
        this.userCode = userCode;
        return this;
    }

    /**
     * 可以进一步过滤姓名，选出具体某一个学生
     */
    public StudentFilter filterName(String userName) {
        this.userName = userName;
        return this;
    }

    /**
     * 获取请求参数
     */
    public abstract StudentParams getParams(String host);

    public abstract String getUrl();

}
