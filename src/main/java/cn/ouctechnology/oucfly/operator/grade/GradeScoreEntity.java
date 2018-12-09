package cn.ouctechnology.oucfly.operator.grade;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 11:00
 * @description: TODO
 **/

import lombok.Data;

@Data
public class GradeScoreEntity {
    //学号
    private String userCode;
    //姓名
    private String userName;
    //专业
    private String major;
    //总学分
    private float score;
    //加权平均分
    private float grade;
}
