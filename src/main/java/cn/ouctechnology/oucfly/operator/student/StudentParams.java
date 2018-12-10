package cn.ouctechnology.oucfly.operator.student;

import cn.ouctechnology.oucfly.http.HttpParams;
import cn.ouctechnology.oucfly.operator.XnXq;
import lombok.Builder;
import lombok.Getter;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 19:34
 * @description: TODO
 **/
@Builder
@Getter
public class StudentParams extends HttpParams {
    private int nj;

    private String yxbdm;

    private String zydm;

    private String yhdm;

    private String sel_skbjdm;

    private XnXq xnxq;

}
