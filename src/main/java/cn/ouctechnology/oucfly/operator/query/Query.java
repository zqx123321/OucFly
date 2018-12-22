package cn.ouctechnology.oucfly.operator.query;

import cn.ouctechnology.oucfly.exception.OucException;
import cn.ouctechnology.oucfly.http.OkHttpUtil;
import cn.ouctechnology.oucfly.operator.Operator;
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
 * @create: 2018-12-07 21:03
 * @description: 根据课程名称查询课程相关信息，支持模糊搜索
 **/
public class Query extends Operator<List<QueryEntity>> {

    /**
     * 课程类型枚举
     */
    public enum ClassType {
        //公共基础课
        PUBLIC_BASIC("PublicBasic"),
        //专业课
        SPECIALTY("Specialty"),
        //通识选修课
        COMMON("Common");
        private String name;

        ClassType(String name) {
            this.name = name;
        }
    }

    //学年学期
    private XnXq xnXq;
    //课程类型
    private ClassType classType;
    //课程名称
    private String className;

    public Query(XnXq xnXq, ClassType classType, String className) {
        this.xnXq = xnXq;
        this.classType = classType;
        this.className = className;
    }

    //当前页，如果查询结果太多，需要翻页请求
    private Integer currentPage;

    /**
     * 设置当前页
     * @param page
     * @return
     */
    public Query setCurrentPage(Integer page) {
        this.currentPage = page;
        return this;
    }


    @Override
    public Result<List<QueryEntity>> run(String host) {
        QueryParams params = QueryParams.builder()
                .xn(xnXq.getXn())
                .xq(xnXq.getXq().ordinal())
                .kcfw(classType.name)
                .sel_kc(className)
                .build();
        String url;
        String refer;
        String paramStr = params.toString();
        if (currentPage != null) {
            url = host + "taglib/DataTable.jsp?currPageCount=" + currentPage;
            refer = host + "taglib/DataTable.jsp?tableId=6146";
            paramStr += "&tableId=6146";
        } else {
            url = host + "taglib/DataTable.jsp?tableId=6146";
            refer = host + "wsxk.kcbcx.html?menucode=JW130414";
        }

        try {
            String content = OkHttpUtil.postHttpClient(url, refer, paramStr);
            logger.trace("get the response: {}", content);
            Document document = Jsoup.parse(content);
            Elements schools = document.select("[name=xqmc]");
            Elements teachers = document.select("[name=rkjs]");
            Elements codes = document.select("[name=curent_skbjdm]");
            Elements weeks = document.select("[name=qsz]");
            Elements scores = document.select("[name=xf]");
            Elements hours = document.select("[name=zongxs]");
            Elements limits = document.select("[name=xkrssx]");
            Elements alls = document.select("[name=xkrs]");
            Elements counts = document.select("[name=qdrs]");
            Elements times = document.select("[name=sksj]");
            Elements places = document.select("[name=skdd]");

            List<QueryEntity> queryEntities = new ArrayList<>();

            for (int i = 0; i < codes.size(); i++) {
                Element code = codes.get(i);
                if (code == null || code.text().equals("")) {
                    QueryEntity queryEntity = queryEntities.get(queryEntities.size() - 1);
                    queryEntity.getClassTime().add(times.get(i).text());
                    queryEntity.getClassPlace().add(places.get(i).text());
                    continue;
                }
                QueryEntity queryEntity = new QueryEntity();
                queryEntity.setClassCode(code.text());
                queryEntity.setClassSchool(schools.get(i).text());
                queryEntity.setAllPeople(Integer.parseInt(alls.get(i).text()));
                queryEntity.setClassHour(Integer.parseInt(hours.get(i).text()));
                queryEntity.setClassScore(Float.parseFloat(scores.get(i).text()));
                queryEntity.setClassTeacher(teachers.get(i).text());
                queryEntity.setTeacherWeek(weeks.get(i).text());
                queryEntity.setCountPeople(Integer.parseInt(counts.get(i).text()));
                queryEntity.setLimitPeople(Integer.parseInt(limits.get(i).text()));
                List<String> time = new ArrayList<>();
                List<String> place = new ArrayList<>();
                time.add(times.get(i).text());
                place.add(places.get(i).text());
                queryEntity.setClassPlace(place);
                queryEntity.setClassTime(time);
                queryEntities.add(queryEntity);
            }
            return Result.success(queryEntities);
        } catch (OucException e) {
            return Result.fail("get query info error: " + e);
        } catch (Exception e) {
            return Result.fail("parse response error: " + e);
        }
    }
}
