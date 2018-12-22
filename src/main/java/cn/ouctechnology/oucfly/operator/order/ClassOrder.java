package cn.ouctechnology.oucfly.operator.order;

import cn.ouctechnology.oucfly.encryption.EncryptionUtil;
import cn.ouctechnology.oucfly.exception.OucException;
import cn.ouctechnology.oucfly.http.OkHttpUtil;
import cn.ouctechnology.oucfly.operator.Operator;
import cn.ouctechnology.oucfly.operator.XnXq;
import cn.ouctechnology.oucfly.operator.grade.GradeScoreEntity;
import cn.ouctechnology.oucfly.operator.student.StudentClassFilter;
import cn.ouctechnology.oucfly.operator.student.StudentCode;
import cn.ouctechnology.oucfly.result.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-08 11:53
 * @description: 获取某门课的所有成绩
 **/
public class ClassOrder extends Operator<OrderEntity> {
    //学年学期
    private XnXq xnXq;
    //选课号
    private String classCode;

    public ClassOrder(XnXq xnXq, String classCode) {
        this.xnXq = xnXq;
        this.classCode = classCode;
    }

    @Override
    public Result<OrderEntity> run(String host) {
        String url = host + "wjstgdfw/cjlr.ckxscj.fkcaskbjckcj_rptOrigina_data_exp.jsp";
        String refer = host + "wjstgdfw/cjlr.ckxscj.fkcaskbjckcj_rpt.jsp";
        //自动获取课程代码
        try {
            String kcdm = getKcdm(host);
            if (kcdm == null) return Result.fail("get the kcdm of class: " + classCode + " fail");
            logger.info("success got the kcdm: {}", kcdm);
            ClassOrderParams params = ClassOrderParams.builder()
                    .xn(xnXq.getXn())
                    .xq(xnXq.getXq().ordinal())
                    .kcdm(kcdm)
                    .skbjdm(classCode)
                    .build();

            String content = OkHttpUtil.post(url, refer, params);
            logger.trace("get the response: {}", content);
            Document document = Jsoup.parse(content);
            //获取学分
            Element scoreDiv = document.select("[group]").get(0);
            String scoreText = scoreDiv.getElementsByTag("table").get(0).getElementsByTag("tr")
                    .get(0).getElementsByTag("td").get(3).text();
            float score = Float.parseFloat(scoreText.substring(scoreText.indexOf("：") + 1));


            Element tbody = document.getElementsByTag("tbody").get(1);
            Elements trs = tbody.getElementsByTag("tr");
            List<GradeScoreEntity> gradeScoreEntityList = new ArrayList<>();

            for (Element tr : trs) {
                GradeScoreEntity gradeScoreEntity = new GradeScoreEntity();
                Elements tds = tr.getElementsByTag("td");
                gradeScoreEntity.setUserCode(tds.get(1).text());
                gradeScoreEntity.setUserName(tds.get(2).text());
                gradeScoreEntity.setMajor(tds.get(4).text());
                float grade = 0F;
                String text = tds.get(10).text();
                if (text != null && !Objects.equals("", text)) {
                    grade = Float.parseFloat(text);
                }
                gradeScoreEntity.setGrade(grade);
                gradeScoreEntity.setScore(score);
                gradeScoreEntityList.add(gradeScoreEntity);
            }
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setAll(gradeScoreEntityList.size());
            orderEntity.setSuccess(gradeScoreEntityList.size());
            orderEntity.setData(gradeScoreEntityList);
            return Result.success(orderEntity);
        } catch (OucException e) {
            return Result.fail("get grade list error: " + e);
        } catch (Exception e) {
            return Result.fail("parse response error: " + e);
        }
    }

    /**
     * 自动获取课程代码，通过课程表获取
     */
    private String getKcdm(String host) {
        String url = host + "wsxk/xkjg.ckdgxsxdkchj_data.jsp";
        String refer = host + "student / xscj.stuckcj.jsp?menucode = JW130705";
        String baseParams = "xn=" + xnXq.getXn() + "&xq=" + xnXq.getXq().ordinal() + "&xh=";
        StudentCode studentCode = new StudentCode(new StudentClassFilter(xnXq, classCode));
        Result<List<String>> studentRes = oucFly.run(studentCode);
        if (!studentRes.isSuccess()) throw new OucException("get student code fail " + studentRes.getErrorMsg());
        List<String> codeList = studentRes.getContent();
        for (String stuCode : codeList) {
            try {
                String params = "params=" + EncryptionUtil.encodeBase64(baseParams + stuCode);
                String content = OkHttpUtil.get(url, refer, params);
                logger.trace("get the response: {}", content);
                Document document = Jsoup.parse(content);
                Element tbody = document.getElementsByTag("tbody").get(0);
                Elements trs = tbody.getElementsByTag("tr");
                for (Element tr : trs) {
                    Elements tds = tr.getElementsByTag("td");
                    String code = tds.get(0).text();
                    //成功获取
                    if (code.equals(classCode)) {
                        String className = tds.get(1).text();
                        return className.substring(1, 13);
                    }
                }
            } catch (Exception ignored) {

            }
        }
        //没获取到
        return null;
    }
}
