package cn.ouctechnology.oucfly.operator.exam;

import cn.ouctechnology.oucfly.http.HttpParams;
import lombok.Builder;
import lombok.Getter;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 19:59
 * @description: TODO
 **/
@Getter
@Builder
public class ExamParams extends HttpParams {
    private int xn;

    private int xq;

    private int kslcdm;

    private String xh;
}
