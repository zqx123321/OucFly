package cn.ouctechnology.oucfly.operator.order;

import cn.ouctechnology.oucfly.operator.grade.GradeScoreEntity;
import lombok.Data;

import java.util.List;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-08 21:07
 * @description: TODO
 **/
@Data
public class OrderEntity {
    //该专业全部人数
    private int all;
    //获取成功的人数
    private int success;
    //数据
    private List<GradeScoreEntity> data;
}
