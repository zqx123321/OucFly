package cn.ouctechnology.oucfly.operator.login;

import cn.ouctechnology.oucfly.exception.OucException;
import cn.ouctechnology.oucfly.http.OkHttpUtil;
import cn.ouctechnology.oucfly.operator.Operator;
import cn.ouctechnology.oucfly.result.Result;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import okhttp3.Cookie;

import java.util.List;

import static cn.ouctechnology.oucfly.encryption.EncryptionUtil.encodeBase64;
import static cn.ouctechnology.oucfly.encryption.EncryptionUtil.getTripleMD5;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 13:47
 * @description: 登录教务处，获取cookie
 **/
public class Login extends Operator<String> {

    //用户名
    private String userName;
    //密码
    private String passWord;

    public Login(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }


    @Override
    public Result<String> run(String host) {
        //获取sessionId
        String username = userName;
        String password = passWord;
        OkHttpUtil.get(host + "cas/login.action");
        //读取cookie
        List<Cookie> cookies = OkHttpUtil.getOkCookie().getCookieStore().get(host.substring(7, host.length() - 1));
        if (cookies == null || cookies.size() <= 0) throw new OucException("can get the cookie");
        String sessionId = cookies.get(0).value();
        logger.debug("get sessionId: {}", sessionId);
        //加密
        username = encodeBase64((username + ";;" + sessionId));
        password = getTripleMD5(password, "");
        //设置参数
        LoginParams params = LoginParams.builder()
                ._u(username)
                ._p(password)
                .randnumber("")
                .isPasswordPolicy("1")
                .build();
        //执行登陆操作
        try {
            String content = OkHttpUtil.post(host + "cas/logon.action", params);
            logger.trace("get the response: {}", content);
            JSONObject jsonObject = JSON.parseObject(content);
            String message = jsonObject.get("message").toString();
            if (message.equals("操作成功!")) return Result.success(message);
            return Result.fail(message);
        } catch (OucException e) {
            logger.error("login fail", e);
            return Result.fail("login fail: " + e);
        } catch (JSONException e) {
            logger.error("the response format is error", e);
            return Result.fail("the response format is error: " + e);
        }
    }
}
