package cn.ouctechnology.oucfly.operator.coin;

import cn.ouctechnology.oucfly.encryption.EncryptionUtil;
import cn.ouctechnology.oucfly.exception.OucException;
import cn.ouctechnology.oucfly.http.OkHttpUtil;
import cn.ouctechnology.oucfly.operator.Operator;
import cn.ouctechnology.oucfly.operator.XnXq;
import cn.ouctechnology.oucfly.result.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 20:39
 * @description: 获取指定课程指定学号的选课币
 **/
public class Coin extends Operator<CoinEntity> {
    //学号
    private String userCode;
    //8位选课号
    private String classCode;
    //学年学期
    private XnXq xnXq;

    public Coin(String userCode, String classCode, XnXq xnXq) {
        this.userCode = userCode;
        this.classCode = classCode;
        this.xnXq = xnXq;
    }

    public Coin(String classCode, XnXq xnXq) {
        this.classCode = classCode;
        this.xnXq = xnXq;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    @Override
    public Result<CoinEntity> run(String host) {
        if (userCode == null) throw new OucException("must set the userCode");
        String url = host + "wsxk/xkjg.ckdgxsxdkchj_data.jsp";
        String refer = host + "student / xscj.stuckcj.jsp?menucode = JW130705";
        String params = "xn=" + xnXq.getXn() + "&xq=" + xnXq.getXq().ordinal() + "&xh=" + userCode;
        logger.debug("origin params: {}", params);
        params = "params=" + EncryptionUtil.encodeBase64(params);
        logger.debug("encode params: {}", params);
        try {
            String content = OkHttpUtil.get(url, refer, params);
            Document document = Jsoup.parse(content);
            Element tbody = document.getElementsByTag("tbody").get(0);
            Elements trs = tbody.getElementsByTag("tr");
            for (Element tr : trs) {
                Elements tds = tr.getElementsByTag("td");
                String code = tds.get(0).text();
                if (code.equals(classCode)) {
                    String coin = tds.get(8).text();
                    return Result.success(new CoinEntity(userCode, Integer.parseInt(coin)));
                }
            }
            //该学生没选这门课
            return Result.fail("can not find the class: " + classCode);
        } catch (OucException e) {
            return Result.fail("get class table error" + e);
        } catch (Exception e) {
            return Result.fail("parse response error" + e);
        }
    }
}
