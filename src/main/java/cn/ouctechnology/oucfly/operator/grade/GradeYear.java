package cn.ouctechnology.oucfly.operator.grade;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 15:18
 * @description: 获取指定学年的成绩信息，用于奖学金评奖
 **/
public class GradeYear extends GradeScore {
    //指定学年
    private int year;

    public GradeYear(String userCode, int year) {
        super(userCode);
        this.year = year;
    }

    public GradeYear(int year) {
        super();
        this.year = year;
    }

    @Override
    public Map<String, GradeCalculator.GCEntry> getMap(Document document) {
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

}
