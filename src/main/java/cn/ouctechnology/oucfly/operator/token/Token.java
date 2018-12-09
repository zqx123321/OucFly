package cn.ouctechnology.oucfly.operator.token;

import cn.ouctechnology.oucfly.exception.OucException;
import cn.ouctechnology.oucfly.http.OkHttpUtil;
import cn.ouctechnology.oucfly.operator.Operator;
import cn.ouctechnology.oucfly.result.Result;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 11:21
 * @description: 获取token，用于des加密
 **/
public class Token extends Operator<TokenEntity> {

    @Override
    public Result<TokenEntity> run(String host) {
        String url = host + "custom/js/SetKingoEncypt.jsp";
        try {
            String content = OkHttpUtil.get(url);
            //正则匹配，包含一个捕获组
            String regex = "= '(.*)';";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content);
            TokenEntity tokenEntity = new TokenEntity();
            if (matcher.find()) {
                //desKey
                tokenEntity.setDesKey(matcher.group(1));
                if (matcher.find()) {
                    //nowTime
                    tokenEntity.setNowTime(matcher.group(1));
                    return Result.success(tokenEntity);
                }
            }
            return Result.fail("get token fail,the content is missed");
        } catch (OucException e) {
            logger.error("get token fail: {}", e);
            return Result.fail("get token fail:" + e);
        }
    }
}
