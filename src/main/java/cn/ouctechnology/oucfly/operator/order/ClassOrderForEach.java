package cn.ouctechnology.oucfly.operator.order;

import cn.ouctechnology.oucfly.operator.Operator;
import cn.ouctechnology.oucfly.operator.XnXq;
import cn.ouctechnology.oucfly.operator.grade.GradeDetail;
import cn.ouctechnology.oucfly.operator.grade.GradeDetailEntity;
import cn.ouctechnology.oucfly.operator.grade.GradeScoreEntity;
import cn.ouctechnology.oucfly.operator.student.Student;
import cn.ouctechnology.oucfly.operator.student.StudentClassFilter;
import cn.ouctechnology.oucfly.operator.student.StudentEntity;
import cn.ouctechnology.oucfly.operator.student.StudentList;
import cn.ouctechnology.oucfly.result.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-08 11:53
 * @description: 获取某门课的所有成绩
 **/
public class ClassOrderForEach extends Operator<OrderEntity> {
    //学年学期
    private XnXq xnXq;
    //选课号
    private String classCode;

    private String kcdm;

    public ClassOrderForEach(XnXq xnXq, String classCode, String kcdm) {
        this.xnXq = xnXq;
        this.kcdm = kcdm;
        this.classCode = classCode;
    }

    @Override
    public Result<OrderEntity> run(String host) {
        if (kcdm == null) return Result.fail("fail to get the kcdm of class: " + classCode);
        StudentList student = new StudentList(new StudentClassFilter(xnXq, classCode));
        Result<List<StudentEntity>> resStudent = oucFly.run(student);
        if (resStudent.isSuccess()) {
            List<StudentEntity> students = resStudent.getContent();
            Queue<GradeScoreEntity> gradeScoreEntities = new ConcurrentLinkedQueue<>();
            GradeDetail gradeDetail = new GradeDetail(xnXq);
            students.parallelStream().forEach(code -> {
                try {
                    gradeDetail.setUserCode(code.getCode());
                    Result<List<GradeDetailEntity>> resGrade = oucFly.run(gradeDetail);
                    if (resGrade.isSuccess()) {
                        List<GradeDetailEntity> grades = resGrade.getContent();
                        grades.forEach(grade -> {
                            if (grade.getName().substring(1, 13).equals(kcdm)) {
                                GradeScoreEntity gradeScoreEntity = new GradeScoreEntity();
                                gradeScoreEntity.setUserName(code.getName());
                                gradeScoreEntity.setUserCode(code.getCode());
                                gradeScoreEntity.setMajor(code.getMajor());
                                gradeScoreEntity.setGrade(grade.getGrade());
                                gradeScoreEntity.setScore(grade.getScore());
                                gradeScoreEntities.offer(gradeScoreEntity);
                                return;
                            }
                        });
                    }
                } catch (Exception ignored) {
                }
            });
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setAll(students.size());
            orderEntity.setSuccess(gradeScoreEntities.size());
            if (gradeScoreEntities.size() > 0) {
                ArrayList<GradeScoreEntity> list = new ArrayList<>(gradeScoreEntities);
                orderEntity.setData(list);
            }
            return Result.success(orderEntity);
        }
        return Result.fail("get the student codes of class: " + classCode + " fail");
    }
}
