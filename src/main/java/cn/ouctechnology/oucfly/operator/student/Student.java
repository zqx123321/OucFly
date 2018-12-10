package cn.ouctechnology.oucfly.operator.student;

import cn.ouctechnology.oucfly.exception.OucException;
import cn.ouctechnology.oucfly.operator.Operator;
import cn.ouctechnology.oucfly.operator.dept.Dept;
import cn.ouctechnology.oucfly.operator.dept.DeptEntity;
import cn.ouctechnology.oucfly.operator.dept.DeptFilter;
import cn.ouctechnology.oucfly.result.Result;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 14:59
 * @description: 根据院系或者选课号获取学生信息
 **/
public class Student extends Operator<List<StudentEntity>> {

    /**
     * 过滤器
     *
     * @see StudentFilter
     */
    private StudentFilter filter;

    private String userCode;

    private String userName;

    //是否查找成功
    private boolean success = false;

    public Student() {
    }

    public Student(StudentFilter filter) {
        this.filter = filter;
    }

    public Student setFilter(StudentFilter filter) {
        this.filter = filter;
        return this;
    }

    public Student setUserCode(String userCode) {
        this.userCode = userCode;
        return this;
    }

    public Student setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    @Override
    public Result<List<StudentEntity>> run(String host) {
        if (userName == null && userCode == null) throw new OucException("must set the userName or userCode");
        //有过滤器，执行小范围匹配
        if (filter != null) {
            Result<List<StudentEntity>> listResult = runWithFilter(filter);
            if (listResult.isSuccess()) {
                List<StudentEntity> content = listResult.getContent();
                if (content == null || content.size() == 0)
                    return Result.fail("can not get the student by userCode: " + userCode
                            + "or by userName: " + userName);
            }
            return listResult;
        }
        //无过滤器，执行全局扫描
        int thread = oucFly.getThread();
        List<StudentEntity> studentEntityList;
        //单线程
        if (thread <= 1) {
            studentEntityList = runFullScanSingle();
        }
        //多线程
        else {
            studentEntityList = runFullScanThread(thread);
        }
        if (studentEntityList == null || studentEntityList.size() == 0)
            return Result.fail("can not get the student by userCode: " + userCode
                    + "or by userName: " + userName);
        return Result.success(studentEntityList);
    }


    /**
     * 有过滤器，可以进行有范围式搜索
     */
    private Result<List<StudentEntity>> runWithFilter(StudentFilter filter) {
        StudentList studentList = new StudentList(filter);
        Result<List<StudentEntity>> result = oucFly.run(studentList);
        if (!result.isSuccess()) return result;
        List<StudentEntity> studentEntityList = result.getContent();
        //默认开启并行流
        List<StudentEntity> filterList = studentEntityList
                .parallelStream()
                .filter(s -> {
                    //优先匹配学号
                    if (userCode != null) {
                        return s.getCode().equals(userCode);
                    }
                    //匹配姓名
                    return s.getName().equals(userName);
                })
                .collect(Collectors.toList());
        return Result.success(filterList);
    }


    /**
     * 执行扫描操作，单线程为同步遍历，多线程为加入线程池
     */
    interface ScanHandler {
        void handle(int grade, String dept, String major);
    }


    /**
     * 执行全局扫描操作
     *
     * @param handler 扫描到一个专业后应该执行的操作
     */
    private void runFullScan(ScanHandler handler) {
        //获取大一的年级号
        int year = Calendar.getInstance().get(Calendar.YEAR);
        //大四到大一
        for (int i = year - 3; i <= year; i++) {
            logger.info("scan the grade of {}", i);
            DeptFilter filter = new DeptFilter(i);
            //获得学院
            Dept dept = new Dept(filter);
            Result<List<DeptEntity>> deptResult = oucFly.run(dept);
            if (deptResult.isSuccess()) {
                List<DeptEntity> deptList = deptResult.getContent();
                for (DeptEntity deptEntity : deptList) {
                    String deptCode = deptEntity.getCode();
                    filter.setDept(deptCode);
                    //获得专业
                    Result<List<DeptEntity>> majorResult = oucFly.run(dept);
                    if (majorResult.isSuccess()) {
                        List<DeptEntity> majorList = majorResult.getContent();
                        for (DeptEntity majorEntity : majorList) {
                            //处理这个专业
                            logger.info("handle the dept: {}, major: {}", deptEntity.getName(), majorEntity.getName());
                            handler.handle(i, deptCode, majorEntity.getCode());
                        }
                    }
                }
            }
        }
    }

    /**
     * 单线程扫描
     */
    private List<StudentEntity> runFullScanSingle() {
        List<StudentEntity> res = new ArrayList<>();
        //单线程扫描操作
        ScanHandler handler = (grade, dept, major) -> {
            //成功获取到学号，学号唯一，直接返回
            if (success) return;
            StudentDeptFilter filter = new StudentDeptFilter(grade).setDept(dept).setMajor(major);
            Result<List<StudentEntity>> listResult = runWithFilter(filter);
            if (listResult.isSuccess()) {
                logger.info("get the student list of grade: {}, dept: {}, major: {}", grade, dept, major);
                res.addAll(listResult.getContent());
            }
        };
        runFullScan(handler);
        return res;
    }


    /**
     * 没有过滤器，只能全局搜索，多线程扫描
     */
    private List<StudentEntity> runFullScanThread(int thread) {
        //线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                thread,
                thread,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(500));
        Queue<StudentEntity> queue = new ConcurrentLinkedQueue<>();
        //多线程扫描操作
        ScanHandler handler = (grade, dept, major) -> {
            ScanTask scanTask = new ScanTask(grade, dept, major, queue);
            executor.submit(scanTask);
        };
        runFullScan(handler);
        //等待线程池中的任务全部结束
        executor.shutdown();
        while (!executor.isTerminated()) {
            LockSupport.parkNanos(10000);
        }
        return new ArrayList<>(queue);
    }

    /**
     * 扫描任务
     */
    class ScanTask implements Runnable {

        private Queue<StudentEntity> queue;

        private int grade;

        private String dept;

        private String major;

        public ScanTask(int grade, String dept, String major, Queue<StudentEntity> queue) {
            this.queue = queue;
            this.grade = grade;
            this.dept = dept;
            this.major = major;
        }

        @Override
        public void run() {
            //学号唯一，直接返回
            if (success) return;
            StudentDeptFilter filter = new StudentDeptFilter(grade).setDept(dept).setMajor(major);
            Result<List<StudentEntity>> listResult = runWithFilter(filter);
            if (listResult.isSuccess()) {
                logger.info("get the student list of grade: {}, dept: {}, major: {}", grade, dept, major);
                queue.addAll(listResult.getContent());
            }
        }
    }
}
