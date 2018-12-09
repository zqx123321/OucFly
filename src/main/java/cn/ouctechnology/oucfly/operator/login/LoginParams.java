package cn.ouctechnology.oucfly.operator.login;

import cn.ouctechnology.oucfly.http.HttpParams;
import lombok.Builder;
import lombok.Getter;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 15:38
 * @description: 登录操作所需参数
 **/
@Builder
@Getter
public class LoginParams extends HttpParams {
    private String _u;

    private String _p;

    private String randnumber;

    private String isPasswordPolicy;
}
