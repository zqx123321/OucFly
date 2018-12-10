package cn.ouctechnology.oucfly.operator.student;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 19:54
 * @description: 学生信息过滤器，用于选择是获取某个院系的学生数据，还是获取某门课的学生数据
 **/
public interface StudentFilter {

    /**
     * 获取请求参数
     */
    StudentParams getParams(String host);

    String getUrl();

}
