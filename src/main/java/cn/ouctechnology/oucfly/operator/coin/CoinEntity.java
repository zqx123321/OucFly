package cn.ouctechnology.oucfly.operator.coin;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-08 20:59
 * @description: TODO
 **/
@Data
@AllArgsConstructor
public class CoinEntity {
    //学生学号
    private String userCode;
    //该学生投入的选课币
    private int coin;
}
