package cn.ouctechnology.oucfly.operator.order;

import cn.ouctechnology.oucfly.operator.Operator;
import cn.ouctechnology.oucfly.operator.XnXq;
import cn.ouctechnology.oucfly.operator.grade.GradeScore;
import cn.ouctechnology.oucfly.operator.grade.GradeScoreEntity;
import cn.ouctechnology.oucfly.operator.student.StudentCode;
import cn.ouctechnology.oucfly.operator.student.StudentDeptFilter;
import cn.ouctechnology.oucfly.result.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-08 10:19
 * @description: 获取专业成绩排名
 * 此过程需要发送大量请求，并且不保证全部成功，请通过返回结果中的all与success来判断成功数
 **/
public class MajorOrder extends Operator<OrderEntity> {

    private StudentDeptFilter deptFilter;

    private XnXq xnXq;

    private Integer year;

    public MajorOrder(StudentDeptFilter deptFilter, XnXq xnXq) {
        this.deptFilter = deptFilter;
        this.xnXq = xnXq;
    }

    public MajorOrder(StudentDeptFilter deptFilter, int year) {
        this.deptFilter = deptFilter;
        this.year = year;
    }

    public MajorOrder(StudentDeptFilter deptFilter) {
        this.deptFilter = deptFilter;
    }

    /**
     * 根据是学年还是学期获取GradeScore实例
     *
     * @return
     */
    private GradeScore getGradeScore() {
        if (year != null) return new GradeScore(year);
        if (xnXq != null) return new GradeScore(xnXq);
        return new GradeScore();
    }

    private GradeScore getGradeScore(String userCode) {
        if (year != null) return new GradeScore(userCode, year);
        if (xnXq != null) return new GradeScore(userCode, xnXq);
        return new GradeScore(userCode);
    }

    @Override
    public Result<OrderEntity> run(String host) {
        StudentCode studentCode = new StudentCode(deptFilter);
        Result<List<String>> studentRes = oucFly.run(studentCode);
        if (!studentRes.isSuccess()) return Result.fail("get student list fail: " + studentRes.getErrorMsg());
        List<String> codeList = studentRes.getContent();
        int thread = oucFly.getThread();
        List<GradeScoreEntity> res;
        //单线程
        if (thread == 1) {
            res = new ArrayList<>();
            GradeScore gradeScore = getGradeScore();
            for (String code : codeList) {
                gradeScore.setUserCode(code);
                Result<GradeScoreEntity> gradeResult = oucFly.run(gradeScore);
                if (gradeResult.isSuccess()) {
                    logger.info("get the student grade of: {} success", code);
                    res.add(gradeResult.getContent());
                } else logger.info("get the student grade of: {} fail", code);
            }
        } else {
            res = runThread(codeList);
        }
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setAll(codeList.size());
        orderEntity.setSuccess(res.size());
        orderEntity.setData(res);
        return Result.success(orderEntity);
    }

    /**
     * 多线程获取数据
     */
    private List<GradeScoreEntity> runThread(List<String> codeList) {
        int thread = oucFly.getThread();
        //线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                thread,
                thread,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(codeList.size()));
        Queue<GradeScoreEntity> resQueue = new ConcurrentLinkedQueue<>();
        for (String code : codeList) {
            GradeTask coinTask = new GradeTask(code, resQueue);
            executor.execute(coinTask);
        }
        //等待所有的任务结束
        executor.shutdown();
        while (!executor.isTerminated()) {
            LockSupport.parkNanos(10000);
        }
        return new ArrayList<>(resQueue);
    }

    private class GradeTask implements Runnable {

        private String userCode;
        private GradeScore gradeScore;
        private Queue<GradeScoreEntity> resQueue;

        private GradeTask(String userCode, Queue<GradeScoreEntity> resQueue) {
            this.userCode = userCode;
            this.resQueue = resQueue;
            this.gradeScore = getGradeScore(userCode);
        }

        @Override
        public void run() {
            Result<GradeScoreEntity> gradeScoreEntityResult = oucFly.run(gradeScore);
            if (gradeScoreEntityResult.isSuccess()) {
                logger.info("get the student grade of: {} success", userCode);
                resQueue.add(gradeScoreEntityResult.getContent());
            } else {
                logger.info("get the  student grade of: {} fail", userCode);
            }
        }
    }
}
