package cn.ouctechnology.oucfly.operator.grade;

import cn.ouctechnology.oucfly.operator.XnXq;
import cn.ouctechnology.oucfly.result.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 12:12
 * @description: 获取指定学期加权平均分
 **/
public class GradeScore extends Grade<GradeScoreEntity> {

    private Integer year;

    /**
     * 3个构造函数
     *
     * @see Grade
     */

    public GradeScore(String userCode, XnXq xnXq) {
        super(userCode, xnXq);
    }

    public GradeScore(String userCode) {
        super(userCode);
    }

    public GradeScore(XnXq xnXq) {
        super(xnXq);
    }

    public GradeScore() {
        super();
    }

    public GradeScore(Integer year) {
        this.year = year;
    }

    public GradeScore(String userCode, Integer year) {
        super(userCode);
        this.year = year;
    }

    @Override
    public Result<GradeScoreEntity> parserHtml(String content) {
        try {
            GradeScoreEntity gradeScoreEntity = new GradeScoreEntity();
            Document document = Jsoup.parse(content);
            Element div = document.select("[group]").get(0);
            Elements infos = div.getElementsByTag("div");
            String major = infos.get(2).text();
            gradeScoreEntity.setMajor(major.substring(major.indexOf("：") + 1));
            String userCode = infos.get(3).text();
            gradeScoreEntity.setUserCode(userCode.substring(userCode.indexOf("：") + 1));
            String userName = infos.get(4).text();
            gradeScoreEntity.setUserName(userName.substring(userName.indexOf("：") + 1));
            Map<String, GradeCalculator.GCEntry> gradeScoreEntryMap;
            //遍历所有学期
            if (year == null)
                gradeScoreEntryMap = getMap(document);
            else
                gradeScoreEntryMap = getMapYear(document);
            //转换成数组
            List<GradeCalculator.GCEntry> scoreEntryList = gradeScoreEntryMap.entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());
            //计算加权平均分
            GradeCalculator.GCEntry calculatorRes = GradeCalculator.calculator(scoreEntryList);
            gradeScoreEntity.setScore(calculatorRes.score);
            gradeScoreEntity.setGrade(calculatorRes.grade);
            return Result.success(gradeScoreEntity);

        } catch (Exception e) {
            logger.error("parser the response error", e);
            return Result.fail("parser the response error: " + e);
        }
    }

    /**
     * 将所有的成绩以课程名称为Key存入Map
     */
    private Map<String, GradeCalculator.GCEntry> getMap(Document document) {
        Elements tbodys = document.getElementsByTag("tbody");
        Map<String, GradeCalculator.GCEntry> gradeScoreEntryMap = new HashMap<>();
        for (Element tbody : tbodys) {
            //取出该学期所有成绩
            Elements trs = tbody.getElementsByTag("tr");
            transferTr(gradeScoreEntryMap, trs);
        }
        return gradeScoreEntryMap;
    }

    /**
     * 将所有的成绩以课程名称为Key存入Map,学年
     */
    private Map<String, GradeCalculator.GCEntry> getMapYear(Document document) {
        //遍历所有学期
        Elements tables = document.getElementsByTag("table");
        Map<String, GradeCalculator.GCEntry> gradeScoreEntryMap = new HashMap<>();
        for (int i = 0; i < tables.size(); i += 2) {
            Element table = tables.get(i);
            Element td = table.getElementsByTag("td").get(0);
            if (td.text().contains(year + "")) {
                Element nextTable = tables.get(i + 1);
                Element tbody = nextTable.getElementsByTag("tbody").get(0);
                //取出该学期所有成绩
                Elements trs = tbody.getElementsByTag("tr");
                transferTr(gradeScoreEntryMap, trs);
            }
        }
        return gradeScoreEntryMap;
    }


    /**
     * 转化每一条成绩信息
     *
     * @param gradeScoreEntryMap
     * @param trs
     */
    private void transferTr(Map<String, GradeCalculator.GCEntry> gradeScoreEntryMap, Elements trs) {
        for (Element tr : trs) {
            Elements tds = tr.getElementsByTag("td");
            //剔除不正确的tbody
            if (tds.size() < 8) break;
            String name = tds.get(1).text();
            float score = Float.parseFloat(tds.get(2).text());
            float grade;
            String gradeStr = tds.get(6).text();
            //转换等级为分数
            try {
                grade = Float.parseFloat(gradeStr);
            } catch (NumberFormatException e) {
                grade = GradeCalculator.transferGrade(gradeStr);
            }
            //刷分取最高
            if (gradeScoreEntryMap.containsKey(name)) {
                GradeCalculator.GCEntry gradeScoreEntry = gradeScoreEntryMap.get(name);
                float gradeIn = gradeScoreEntry.getGrade();
                if (grade < gradeIn) continue;
            }
            GradeCalculator.GCEntry gradeScoreEntry = new GradeCalculator.GCEntry(score, grade);
            gradeScoreEntryMap.put(name, gradeScoreEntry);
        }
    }
}
