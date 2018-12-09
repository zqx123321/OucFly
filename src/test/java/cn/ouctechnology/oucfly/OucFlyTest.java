package cn.ouctechnology.oucfly;

import cn.ouctechnology.oucfly.core.Mode;
import cn.ouctechnology.oucfly.core.OucFly;
import cn.ouctechnology.oucfly.operator.XnXq;
import cn.ouctechnology.oucfly.operator.coin.Coin;
import cn.ouctechnology.oucfly.operator.coin.CoinClass;
import cn.ouctechnology.oucfly.operator.coin.CoinClassEntity;
import cn.ouctechnology.oucfly.operator.dept.Dept;
import cn.ouctechnology.oucfly.operator.dept.DeptFilter;
import cn.ouctechnology.oucfly.operator.exam.Exam;
import cn.ouctechnology.oucfly.operator.grade.*;
import cn.ouctechnology.oucfly.operator.order.ClassOrder;
import cn.ouctechnology.oucfly.operator.order.MajorOrder;
import cn.ouctechnology.oucfly.operator.order.OrderEntity;
import cn.ouctechnology.oucfly.operator.query.Query;
import cn.ouctechnology.oucfly.operator.student.Student;
import cn.ouctechnology.oucfly.operator.student.StudentClassFilter;
import cn.ouctechnology.oucfly.operator.student.StudentCode;
import cn.ouctechnology.oucfly.operator.student.StudentDeptFilter;
import cn.ouctechnology.oucfly.operator.table.ClassTable;
import cn.ouctechnology.oucfly.operator.table.ClassTableEntity;
import cn.ouctechnology.oucfly.operator.token.Token;
import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 14:04
 * @description: TODO
 **/
public class OucFlyTest {
    private OucFly oucFly;

    @Before
    public void init() {
        oucFly = OucFly.builder("15020031106", "19961020.579")
                .thread(4)
                .mode(Mode.FULL_POWER)
                .build();
    }

    @Test
    public void testDeptList() {
        DeptFilter deptFilter = new DeptFilter(2015).filterDept("信息");
        Dept deptList = new Dept(deptFilter);
        System.out.println(oucFly.run(deptList).getContent());
    }

    @Test
    public void testStudent() {
        DeptFilter deptFilter = new DeptFilter(2015).filterDept("信息").filterMajor("保密");
        Student student = new Student(new StudentDeptFilter(deptFilter));

        System.out.println(oucFly.run(student).getContent());
    }

    @Test
    public void testStudentByClass() {
        StudentClassFilter filter = new StudentClassFilter(new XnXq(2018, XnXq.Xq.SPRING), "02003021");
        Student student = new Student(filter);
        System.out.println(oucFly.run(student).getContent());
    }

    @Test
    public void testCode() {
        StudentClassFilter filter = new StudentClassFilter(new XnXq(2018, XnXq.Xq.SPRING), "02003021");
        StudentCode student = new StudentCode(filter);
        System.out.println(oucFly.run(student).getContent());
    }

    @Test
    public void testGetToken() {
        Token token = new Token();
        System.out.println(oucFly.run(token).getContent());
    }

    @Test
    public void testGetGrade() {
//        GradeDetail grade = new GradeDetail("15020031106", GradeDetail.XnXqType.SINGLE, new XnXq(2018, XnXq.Xq.SPRING));
        GradeDetail grade = new GradeDetail("15020031106");
        List<GradeDetailEntity> res = oucFly.run(grade).getContent();
        System.out.println(JSON.toJSONString(res));
    }


    @Test
    public void testGetGradeScore() {
        GradeScore gradeScore = new GradeScore("15020031106");
        System.out.println(oucFly.run(gradeScore).getContent());
    }

    @Test
    public void testGetGradeYear() {
        GradeYear gradeYear = new GradeYear("15020031106", 2016);
        System.out.println(oucFly.run(gradeYear).getContent());
    }

    @Test
    public void testClassTable() {
        ClassTable classTable = new ClassTable("15020031106", new XnXq(2017, XnXq.Xq.AUTUMN));
        List<List<List<ClassTableEntity>>> content = oucFly.run(classTable).getContent();
        System.out.println(JSON.toJSON(content));
    }

    @Test
    public void testExam() {
        Exam exam = new Exam("15020031106", new XnXq(2015, XnXq.Xq.AUTUMN), Exam.ExamType.MID);
        System.out.println(JSON.toJSONString(oucFly.run(exam).getContent()));
    }

    @Test
    public void testCoin() {
        Coin coin = new Coin("15020031106", "18005016", new XnXq(2015, XnXq.Xq.AUTUMN));
        System.out.println(oucFly.run(coin).getContent());
    }

    @Test
    public void testQueryClass() {
        Query query = new Query(new XnXq(2018, XnXq.Xq.AUTUMN), Query.ClassType.PUBLIC_BASIC, "英语").setCurrentPage(2);
        System.out.println(JSON.toJSONString(oucFly.run(query).getContent()));
    }

    @Test
    public void testClassCoin() {
        CoinClass coinClass = new CoinClass("18003020", new XnXq(2018, XnXq.Xq.AUTUMN));
        long l = System.currentTimeMillis();
        CoinClassEntity content = oucFly.run(coinClass).getContent();
        long l1 = System.currentTimeMillis();
        System.out.println(l1 - l);
        System.out.println(content);

    }

    @Test
    public void testOrder() {
        DeptFilter deptFilter = new DeptFilter(2015).filterDept("信息").setMajor("0011");
        MajorOrder order = new MajorOrder(deptFilter);
        long l = System.currentTimeMillis();
        OrderEntity content = oucFly.run(order).getContent();
        long l1 = System.currentTimeMillis();
        List<GradeScoreEntity> data = content.getData();
        data.sort(Comparator.comparingDouble(GradeScoreEntity::getGrade));
        System.out.println(l1 - l);
        System.out.println(JSON.toJSON(data));
    }

    @Test
    public void testClassOrder() {
        XnXq xnXq = new XnXq(2017, XnXq.Xq.AUTUMN);
        ClassOrder classOrder = new ClassOrder(xnXq, "02003025");
        System.out.println(JSON.toJSONString(oucFly.run(classOrder).getContent()));
    }
}