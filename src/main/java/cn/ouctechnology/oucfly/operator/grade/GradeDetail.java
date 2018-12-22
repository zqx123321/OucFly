package cn.ouctechnology.oucfly.operator.grade;

import cn.ouctechnology.oucfly.operator.XnXq;
import cn.ouctechnology.oucfly.result.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 10:58
 * @description: 获取指定学号的详细成绩信息
 **/
public class GradeDetail extends Grade<List<GradeDetailEntity>> {

    /**
     * 多个构造函数
     *
     * @see Grade
     */
    public GradeDetail(String userCode, XnXq xnXq) {
        super(userCode, xnXq);
    }

    public GradeDetail(String userCode) {
        super(userCode);
    }

    public GradeDetail(XnXq xnXq) {
        super(xnXq);
    }

    public GradeDetail() {
    }

    @Override
    public Result<List<GradeDetailEntity>> parserHtml(String content) {
        try {
            Document document = Jsoup.parse(content);
            List<GradeDetailEntity> gradeDetailEntities = new ArrayList<>();
            Elements tbodys = document.getElementsByTag("tbody");
            for (Element tbody : tbodys) {
                Elements trs = tbody.getElementsByTag("tr");
                for (Element tr : trs) {
                    Elements tds = tr.getElementsByTag("td");
                    if (tds.size() < 8) break;
                    GradeDetailEntity gradeDetailEntity = new GradeDetailEntity();
                    gradeDetailEntity.setName(tds.get(1).text());
                    gradeDetailEntity.setScore(Float.parseFloat(tds.get(2).text()));
                    gradeDetailEntity.setType(tds.get(3).text());
                    String gradeStr = tds.get(6).text();
                    float grade;
                    try {
                        grade = Float.parseFloat(gradeStr);
                    } catch (NumberFormatException e) {
                        grade = GradeCalculator.transferGrade(gradeStr);
                    }
                    gradeDetailEntity.setGrade(grade);
                    gradeDetailEntity.setMode(tds.get(7).text());
                    gradeDetailEntities.add(gradeDetailEntity);
                }
            }
            return Result.success(gradeDetailEntities);
        } catch (Exception e) {
            logger.error("parse the response error", e);
            return Result.fail("parse the response error: " + e);
        }
    }
}
