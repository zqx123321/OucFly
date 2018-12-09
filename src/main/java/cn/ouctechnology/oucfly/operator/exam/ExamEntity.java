package cn.ouctechnology.oucfly.operator.exam;

import lombok.Data;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 20:31
 * @description: TODO
 **/
@Data
public class ExamEntity {
    //课程名称
    private String className;
    //课程学分
    private String classScore;
    //课程类型
    private String classType;
    //课程号
    private String classCode;
    //考核方式
    private String examMethod;
    //考试时间
    private String examTime;
    //考试地点
    private String examPlace;
    //考试座号
    private String examSeat;
    //考试方式（统一、非同一、提前等）
    private String examType;
}
