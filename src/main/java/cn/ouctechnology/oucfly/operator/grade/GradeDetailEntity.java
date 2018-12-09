package cn.ouctechnology.oucfly.operator.grade;

import lombok.Data;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 11:57
 * @description: 成绩详细信息
 **/
@Data
public class GradeDetailEntity {
    //课程名称
    private String name;
    //课程学分
    private float score;
    //课程类型
    private String type;
    //取得分数
    private float grade;
    //获取方式（初修、重修、缓考）
    private String mode;
}
