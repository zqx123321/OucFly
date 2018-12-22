package cn.ouctechnology.oucfly.operator.student;

import cn.ouctechnology.oucfly.exception.OucException;
import cn.ouctechnology.oucfly.http.OkHttpUtil;
import cn.ouctechnology.oucfly.operator.Operator;
import cn.ouctechnology.oucfly.result.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 14:59
 * @description: 获取学号，获取学生信息的精简版，仅仅获取学号
 **/
public class StudentCode extends Operator<List<String>> {

    private StudentFilter filter;

    public StudentCode(StudentFilter filter) {
        this.filter = filter;
    }

    @Override
    public Result<List<String>> run(String host) {
        if (filter == null) throw new OucException("must set the filter");
        String url = host + filter.getUrl();
        String refer = host + "core/popmsg/popmsg.sendOnlineMessage.jsp";
        String content = "";
        try {
            StudentParams params = filter.getParams(host);
            content = OkHttpUtil.postHttpClient(url, refer, params.toString());
            logger.trace("get the response: {}", content);
            Document document = Jsoup.parse(content);
            Elements codeList = document.select("[name=yhxh]");
            List<String> codes = codeList.stream().map(Element::text).collect(Collectors.toList());
            return Result.success(codes);
        } catch (OucException e) {
            logger.error("get students error", e);
            return Result.fail("get students error: " + e);
        } catch (Exception e) {
            return Result.fail("parse response error:" + e);
        }
    }
}
