package cn.ouctechnology.oucfly.operator.dept;

import cn.ouctechnology.oucfly.operator.Operator;
import cn.ouctechnology.oucfly.result.Result;

import java.util.List;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 14:21
 * @description: 获取院系列表
 **/
public class Dept extends Operator<List<DeptEntity>> {

    //院系过滤器
    private DeptFilter deptFilter;

    public Dept(DeptFilter deptFilter) {
        this.deptFilter = deptFilter;
    }

    @Override
    public Result<List<DeptEntity>> run(String host) {
        //执行过滤操作
        deptFilter.doFilter(host);
        //获取数据
        return deptFilter.getData(host);
    }
}
