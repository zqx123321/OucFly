package cn.ouctechnology.oucfly.operator.coin;

import cn.ouctechnology.oucfly.operator.Operator;
import cn.ouctechnology.oucfly.operator.XnXq;
import cn.ouctechnology.oucfly.operator.student.StudentClassFilter;
import cn.ouctechnology.oucfly.operator.student.StudentCode;
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
 * @create: 2018-12-08 10:20
 * @description: 根据选课号获取所有选这门课的人的选课币
 * 此过程需要发送大量请求，并且不保证全部成功，请通过返回结果中的all与success来判断成功数
 **/
public class CoinClass extends Operator<CoinClassEntity> {
    //8位选课号
    private String classCode;
    //学年学期
    private XnXq xnXq;

    public CoinClass(String classCode, XnXq xnXq) {
        this.classCode = classCode;
        this.xnXq = xnXq;
    }

    @Override
    public Result<CoinClassEntity> run(String host) {
        StudentClassFilter filter = new StudentClassFilter(xnXq, classCode);
        StudentCode studentCode = new StudentCode(filter);
        Result<List<String>> result = oucFly.run(studentCode);
        if (!result.isSuccess())
            return Result.fail("get the student list of class:" + classCode + " fail " + result.getErrorMsg());
        List<String> codeList = result.getContent();
        int thread = oucFly.getThread();
        CoinClassEntity coinClassEntity = new CoinClassEntity();
        coinClassEntity.setAll(codeList.size());
        List<CoinEntity> resList;
        //单线程
        if (thread <= 1) {
            Coin coin = new Coin(classCode, xnXq);
            resList = new ArrayList<>();
            for (String code : codeList) {
                coin.setUserCode(code);
                Result<CoinEntity> codeRes = oucFly.run(coin);
                if (codeRes.isSuccess()) {
                    logger.info("get the class coin of: {} success", code);
                    resList.add(codeRes.getContent());
                } else {
                    logger.info("get the class coin of: {} fail: {}", code, codeRes.getErrorMsg());
                }
            }
            coinClassEntity.setAll(codeList.size());
        } else {
            resList = runThread(codeList);
        }
        coinClassEntity.setSuccess(resList.size());
        coinClassEntity.setData(resList);
        return Result.success(coinClassEntity);
    }

    /**
     * 多线程获取数据
     */
    private List<CoinEntity> runThread(List<String> codeList) {
        int thread = oucFly.getThread();
        //线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                thread,
                thread,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(codeList.size()));
        Queue<CoinEntity> resQueue = new ConcurrentLinkedQueue<>();
        for (String code : codeList) {
            CoinTask coinTask = new CoinTask(code, resQueue);
            executor.execute(coinTask);
        }
        /**
         * 等待线程池中所有的任务执行完毕
         */
        executor.shutdown();
        while (!executor.isTerminated()) {
            LockSupport.parkNanos(10000);
        }
        return new ArrayList<>(resQueue);
    }

    private class CoinTask implements Runnable {

        private String userCode;
        private Coin coin;
        private Queue<CoinEntity> resQueue;

        private CoinTask(String userCode, Queue<CoinEntity> resQueue) {
            this.userCode = userCode;
            this.resQueue = resQueue;
            this.coin = new Coin(userCode, classCode, xnXq);
        }

        @Override
        public void run() {
            Result<CoinEntity> codeRes = oucFly.run(coin);
            if (codeRes.isSuccess()) {
                logger.info("get the class coin of: {} success", userCode);
                resQueue.add(codeRes.getContent());
            } else {
                logger.info("get the class coin of: {} fail: {}", userCode, codeRes.getErrorMsg());
            }
        }
    }
}
