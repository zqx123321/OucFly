package cn.ouctechnology.oucfly.operator.student;

import cn.ouctechnology.oucfly.operator.XnXq;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 10:07
 * @description: 课程过滤器，用于获取某门课的学生
 **/
public class StudentClassFilter implements StudentFilter {

    //学年学期
    private XnXq xnXq;
    //选课号
    private String classCode;

    public StudentClassFilter(XnXq xnXq, String classCode) {
        this.xnXq = xnXq;
        this.classCode = classCode;
    }


    @Override
    public StudentParams getParams(String host) {
        StudentParams.StudentParamsBuilder builder = StudentParams.builder()
                .xnxq(xnXq)
                .sel_skbjdm(classCode);
        return builder.build();
    }

    @Override
    public String getUrl() {
        return "taglib/DataTable.jsp?tableId=3241&type=skbjdm";
    }
}
