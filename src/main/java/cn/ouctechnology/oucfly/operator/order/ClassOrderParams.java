package cn.ouctechnology.oucfly.operator.order;

import cn.ouctechnology.oucfly.http.HttpParams;
import lombok.Builder;
import lombok.Getter;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-08 12:03
 * @description: TODO
 **/
@Getter
@Builder
public class ClassOrderParams extends HttpParams {
    private int xn;
    private int xq;
    private String kcdm;
    private String skbjdm;
}
