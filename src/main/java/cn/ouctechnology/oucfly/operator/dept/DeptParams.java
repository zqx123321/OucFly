package cn.ouctechnology.oucfly.operator.dept;

import cn.ouctechnology.oucfly.http.HttpParams;
import lombok.Builder;
import lombok.Getter;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 15:10
 * @description: TODO
 **/
@Builder
@Getter
public class DeptParams extends HttpParams {
    private String comboBoxName;
    private String paramValue;
}
