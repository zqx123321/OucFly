package cn.ouctechnology.oucfly.operator.table;

import lombok.Data;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 15:51
 * @description: 课程表实体
 **/
@Data
public class ClassTableEntity {
    //课程名称
    private String className;
    //授课老师
    private String classTeacher;
    //上课时间
    private String classTime;
    //上课地点
    private String classPlace;
    //其他备注信息
    private String classInfo;
}
