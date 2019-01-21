package cn.ouctechnology.oucfly.operator.query;

import lombok.Data;

import java.util.List;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 21:07
 * @description: TODO
 **/
@Data
public class QueryEntity {

    //课程名称
    private String className;
    //课程选课号
    private String classCode;
    //课程所在校区
    private String classSchool;
    //授课老师
    private String classTeacher;
    //起始周
    private String teacherWeek;
    //课程学分
    private Float classScore;
    //总学时
    private Integer classHour;
    //限选人数
    private Integer limitPeople;
    //已选人数
    private Integer allPeople;
    //确定人数
    private Integer countPeople;
    //上课时间，可能有多个时间
    private List<String> classTime;
    //上课地点，可能有多个地点
    private List<String> classPlace;
}
