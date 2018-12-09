package cn.ouctechnology.oucfly.operator.student;

import cn.ouctechnology.oucfly.exception.OucException;
import cn.ouctechnology.oucfly.operator.dept.DeptFilter;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 19:55
 * @description: 用于获取指定院系的学生数据
 **/
public class StudentDeptFilter extends StudentFilter {
    private DeptFilter deptFilter;

    public StudentDeptFilter(DeptFilter deptFilter) {
        this.deptFilter = deptFilter;
    }

    @Override
    public StudentParams getParams(String host) {
        deptFilter.doFilter(host);
        String dept = deptFilter.getDept();
        if (dept == null) throw new OucException("must set the dept");
        StudentParams.StudentParamsBuilder builder = StudentParams
                .builder()
                .nj(deptFilter.getGrade())
                .yxbdm(dept)
                .zydm(deptFilter.getMajor());
        if (userCode != null) builder.yhdm(userCode);
        if (userName != null) builder.xm(userName);
        return builder.build();
    }

    @Override
    public String getUrl() {
        return "taglib/DataTable.jsp?tableId=3241";
    }
}
