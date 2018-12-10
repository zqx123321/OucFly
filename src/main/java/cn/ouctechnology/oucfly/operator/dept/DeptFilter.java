package cn.ouctechnology.oucfly.operator.dept;

import cn.ouctechnology.oucfly.exception.OucException;
import cn.ouctechnology.oucfly.http.OkHttpUtil;
import cn.ouctechnology.oucfly.result.Result;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-06 19:22
 * @description: 院系过滤器
 **/
@SuppressWarnings("all")
public class DeptFilter {
    private Logger logger = LoggerFactory.getLogger(DeptFilter.class);
    //年级，必须指定
    private int grade;
    //学院代号
    private String dept;
    //专业代号
    private String major;
    //学院模糊名字
    private String deptName;
    //专业模糊名字
    private String majorName;

    public DeptFilter(int grade) {
        this.grade = grade;
    }

    public DeptFilter setDept(String dept) {
        this.dept = dept;
        return this;
    }

    public DeptFilter setMajor(String major) {
        if (dept == null && deptName == null) {
            throw new OucException("must set dept firstly");
        }
        this.major = major;
        return this;
    }

    /**
     * 根据学院名字去模糊匹配
     *
     * @param deptName
     */
    public DeptFilter filterDept(String deptName) {
        this.deptName = deptName;
        return this;
    }

    /**
     * 根据专业名字去模糊匹配
     *
     * @param majorName
     */
    public DeptFilter filterMajor(String majorName) {
        if (dept == null && deptName == null) {
            throw new OucException("must set dept firstly");
        }
        this.majorName = majorName;
        return this;
    }

    private String getComboBoxName() {
        if (dept == null) return "MsYXB";
        if (major == null) return "MsYXB_Specialty";
        return "MsYXB_Specialty_Class";
    }

    private String getParamValue() {
        StringBuilder sb = new StringBuilder();
        sb.append("nj=").append(grade);
        if (dept != null) sb.append("&dwh=").append(dept);
        if (major != null) sb.append("&zydm=").append(major);
        return sb.toString();
    }

    public void doFilter(String host) {
        if (deptName != null && dept == null)
            this.dept = findValue(deptName, host);
        if (majorName != null && major == null)
            this.major = findValue(majorName, host);
    }


    /**
     * 获取数据
     *
     * @param host
     * @return
     */
    Result<List<DeptEntity>> getData(String host) {
        DeptParams params = DeptParams.builder()
                .comboBoxName(getComboBoxName())
                .paramValue(getParamValue())
                .build();

        String url = host + "frame/droplist/getDropLists.action";
        String refer = host + "core/popmsg/popmsg.sendOnlineMessage.jsp";
        try {
            String content = OkHttpUtil.post(url, refer, params);
            List<DeptEntity> deptEntities = JSON.parseArray(content, DeptEntity.class);
            return Result.success(deptEntities);
        } catch (OucException e) {
            return Result.fail("get dept list error" + e);
        } catch (JSONException e) {
            return Result.fail("parse response error" + e);
        }
    }

    /**
     * 执行模糊匹配算法
     */
    private String findValue(String name, String host) {
        //用于模糊的正则表达式
        String regex = ".*" + name + ".*";
        //获取全部数据
        Result<List<DeptEntity>> result = getData(host);
        if (!result.isSuccess()) {
            String msg = "get dept list fail: " + result.getErrorMsg();
            logger.info(msg);
            throw new OucException(msg);
        }
        //遍历
        List<DeptEntity> deptEntities = result.getContent();
        List<DeptEntity> findList = new ArrayList<>();
        for (DeptEntity deptEntity : deptEntities) {
            if (deptEntity.getName().matches(regex)) {
                findList.add(deptEntity);
            }
        }
        //没有匹配项
        if (findList.size() == 0) {
            String msg = "these is no dept match: " + regex;
            logger.info(msg);
            throw new OucException(msg);
        }
        //多个匹配项
        if (findList.size() > 1) {
            String msg = "find one more result,find choose again:";
            logger.info("find one more result,find choose again: {}", findList);
            System.out.println(msg);
            for (DeptEntity deptEntity : findList) {
                System.out.println(deptEntity);
            }
            throw new OucException(msg);
        }
        String code = findList.get(0).getCode();
        logger.info("get the code: {}", code);
        return code;
    }

    public int getGrade() {
        return grade;
    }

    public String getDept() {
        return dept;
    }

    public String getMajor() {
        return major;
    }
}
