package cn.ouctechnology.oucfly.operator.student;

import lombok.Data;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 17:35
 * @description: 学生信息实体
 **/
@Data
public class StudentEntity {
    //年级
    private int grade;
    //学号
    private String code;
    //姓名
    private String name;
    //性别
    private String sex;
    //所在学院
    private String dept;
    //所在专业
    private String major;
}
