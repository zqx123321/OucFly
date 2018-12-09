package cn.ouctechnology.oucfly.operator.table;

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

import java.util.ArrayList;
import java.util.List;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 15:44
 * @description: 获取课程表，此方法返回一个三维数据，
 * 第一维代表行，第二维代表列，第三维代表一个格子中都有哪些课
 **/
public class ClassTable extends Operator<List<List<List<ClassTableEntity>>>> {
    //学号
    private String userCode;
    //学年学期
    private XnXq xnXq;

    public ClassTable(String userCode, XnXq xnXq) {
        this.userCode = userCode;
        this.xnXq = xnXq;
    }

    @Override
    public Result<List<List<List<ClassTableEntity>>>> run(String host) {
        String url = host + "student/wsxk.xskcb.jsp";
        String refer = host + "student/xkjg.wdkb.jsp?menucode=JW130416";
        String params = "xn=" + xnXq.getXn() + "&xq=" + xnXq.getXq().ordinal() + "&xh=" + userCode;
        logger.debug("origin params: {}", params);
        params = "params=" + EncryptionUtil.encodeBase64(params);
        logger.debug("encode params: {}", params);
        try {
            String content = OkHttpUtil.get(url, refer, params);
            Document document = Jsoup.parse(content);
            Elements elements = document.select("[id^=\"k\"]");
            List<List<List<ClassTableEntity>>> classTable = new ArrayList<>();
            List<List<ClassTableEntity>> rowTable = new ArrayList<>();
            for (int i = 0; i < elements.size(); i++) {
                //取出每一行
                Element row = elements.get(i);
                Elements spans = row.getElementsByTag("span");
                List<ClassTableEntity> singleTable = new ArrayList<>();
                //这个格子有课
                if (!spans.text().equals("")) {
                    Element span = spans.get(0);
                    Elements divs = span.getElementsByTag("div");
                    //取出每一个格子
                    for (Element div : divs) {
                        //此处包含&nbsp不间断空格
                        String[] infos = div.text().split("(\\s|\\u00A0)+");
                        if (infos[infos.length - 1].equals("选中")) {
                            //填充属性
                            ClassTableEntity classTableEntity = new ClassTableEntity();
                            classTableEntity.setClassName(infos[0]);
                            classTableEntity.setClassTeacher(infos[1]);
                            classTableEntity.setClassTime(infos[2]);
                            classTableEntity.setClassPlace(infos[3].replaceAll("\\*", ""));
                            //如果存在特殊描述
                            if (infos.length > 5) {
                                classTableEntity.setClassInfo(infos[4].replaceAll("\\*", ""));
                            }
                            singleTable.add(classTableEntity);
                        }
                    }
                }
                rowTable.add(singleTable);
                if ((i + 1) % 7 == 0) {
                    classTable.add(rowTable);
                    rowTable = new ArrayList<>();
                }
            }
            return Result.success(classTable);
        } catch (OucException e) {
            return Result.fail("get class table error" + e);
        } catch (Exception e) {
            return Result.fail("parse response error" + e);
        }
    }
}
