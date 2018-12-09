package cn.ouctechnology.oucfly.operator.query;

import cn.ouctechnology.oucfly.http.HttpParams;
import lombok.Builder;
import lombok.Getter;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 21:14
 * @description: TODO
 **/
@Builder
@Getter
public class QueryParams extends HttpParams {
    private int xn;

    private int xq;

    private String kcfw;

    private String sel_kc;

}
