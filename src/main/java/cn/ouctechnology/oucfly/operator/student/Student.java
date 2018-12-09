package cn.ouctechnology.oucfly.operator.student;

import cn.ouctechnology.oucfly.exception.OucException;
import cn.ouctechnology.oucfly.http.OkHttpUtil;
import cn.ouctechnology.oucfly.operator.Operator;
import cn.ouctechnology.oucfly.result.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 14:59
 * @description: 根据院系或者选课号获取学生信息
 **/
public class Student extends Operator<List<StudentEntity>> {

    /**
     * 过滤器
     * @see StudentFilter
     */
    private StudentFilter filter;

    public Student(StudentFilter filter) {
        this.filter = filter;
    }

    @Override
    public Result<List<StudentEntity>> run(String host) {
        if (filter == null) throw new OucException("must set the filter");
        String url = host + filter.getUrl();
        String refer = host + "core/popmsg/popmsg.sendOnlineMessage.jsp";
        String content = "";
        try {
            StudentParams params = filter.getParams(host);
            content = OkHttpUtil.postHttpClient(url, refer, params.toString());
            Document document = Jsoup.parse(content);
            Elements codeList = document.select("[name=yhxh]");
            Elements nameList = document.select("[name=xm]");
            Elements sexList = document.select("[name=xb]");
            Elements deptList = document.select("[name=yxbmc]");
            Elements bjList = document.select("[name=bjmc]");
            Elements majorList = document.select("[name=zymc]");
            List<StudentEntity> studentEntities = new ArrayList<>();
            for (int i = 0; i < codeList.size(); i++) {
                String code = codeList.get(i).text();
                String name = nameList.get(i).text();
                String dept = deptList.get(i).text();
                String major = majorList.get(i).text();
                String sex = sexList.get(i).text();
                String bj = bjList.get(i).text();
                bj = "20" + bj.substring(bj.length() - 2);
                int grade = Integer.parseInt(bj);
                StudentEntity student = new StudentEntity();
                student.setCode(code);
                student.setName(name);
                student.setDept(dept);
                student.setMajor(major);
                student.setSex(sex);
                student.setGrade(grade);
                studentEntities.add(student);
            }
            return Result.success(studentEntities);
        } catch (OucException e) {
            logger.error("get students error: {}", e);
            return Result.fail("get students error" + e);
        } catch (Exception e) {
            return Result.fail("parse response error:" + e);
        }
    }
}
