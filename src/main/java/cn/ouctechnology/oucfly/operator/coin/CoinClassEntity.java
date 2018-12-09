package cn.ouctechnology.oucfly.operator.coin;

import lombok.Data;

import java.util.List;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-08 21:03
 * @description: TODO
 **/
@Data
public class CoinClassEntity {
    //全部选课人数
    private int all;
    //获取成功的人数
    private int success;
    //数据
    private List<CoinEntity> data;
}
