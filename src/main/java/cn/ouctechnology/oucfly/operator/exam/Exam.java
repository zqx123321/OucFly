package cn.ouctechnology.oucfly.operator.exam;

import cn.ouctechnology.oucfly.exception.OucException;
import cn.ouctechnology.oucfly.http.OkHttpUtil;
import cn.ouctechnology.oucfly.operator.Operator;
import cn.ouctechnology.oucfly.operator.XnXq;
import cn.ouctechnology.oucfly.result.Result;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 19:30
 * @description: 获取考试安排
 **/
public class Exam extends Operator<List<ExamEntity>> {
    //期中期末缓考类型枚举
    public enum ExamType {
        MID(3),     //期中考试
        FINAL(2),   //期末考试
        MAKE_UP(1); //开学初补缓考
        private int code;

        ExamType(int code) {
            this.code = code;
        }
    }

    //学号
    private String userCode;

    private XnXq xnXq;

    private ExamType examType;

    public Exam(String userCode, XnXq xnXq, ExamType examType) {
        this.userCode = userCode;
        this.xnXq = xnXq;
        this.examType = examType;
    }

    @Override
    public Result<List<ExamEntity>> run(String host) {
        String url = host + "taglib/DataTable.jsp?tableId=2538";
        String refer = host + "student/ksap.ksapb.html?menucode=JW130603";
        ExamParams params = ExamParams.builder()
                .xh(userCode)
                .xn(xnXq.getXn())
                .xq(xnXq.getXq().ordinal())
                .kslcdm(examType.code)
                .build();
        try {
            String content = OkHttpUtil.post(url, refer, params);
            logger.trace("get the response: {}", content);
            Document document = Jsoup.parse(content);
            Elements classNames = document.select("[name=kc]");
            Elements classScores = document.select("[name=xf]");
            Elements classTypes = document.select("[name=lb]");
            Elements classCodes = document.select("[name=skbh]");
            Elements examMethods = document.select("[name=khfs]");
            Elements examTime = document.select("[name=kssj]");
            Elements examPlace = document.select("[name=ksdd]");
            Elements examSeat = document.select("[name=zwh]");
            Elements examTypes = document.select("[name=tkfs]");
            List<ExamEntity> examEntities = new ArrayList<>();
            for (int i = 0; i < classNames.size(); i++) {
                ExamEntity examEntity = new ExamEntity();
                examEntity.setClassCode(classCodes.get(i).text());
                examEntity.setClassName(classNames.get(i).text());
                examEntity.setClassScore(classScores.get(i).text());
                examEntity.setClassType(classTypes.get(i).text());
                examEntity.setExamMethod(examMethods.get(i).text());
                examEntity.setExamTime(examTime.get(i).text());
                examEntity.setExamPlace(examPlace.get(i).text());
                examEntity.setExamSeat(examSeat.get(i).text());
                examEntity.setExamType(examTypes.get(i).text());
                examEntities.add(examEntity);
            }
            return Result.success(examEntities);
        } catch (OucException e) {
            return Result.fail("get class table error: " + e);
        } catch (Exception e) {
            return Result.fail("parse response error: " + e);
        }
    }
}
