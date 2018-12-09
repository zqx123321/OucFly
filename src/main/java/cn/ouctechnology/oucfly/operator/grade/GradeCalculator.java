package cn.ouctechnology.oucfly.operator.grade;

import cn.ouctechnology.oucfly.exception.OucException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: oucfly
 * @author: ZQX
 * @create: 2018-12-07 12:44
 * @description: 成绩计算器
 **/
public class GradeCalculator {
    //等级制转为分数制Map
    public static Map<String, Float> levelGradeMap = null;

    @AllArgsConstructor
    @Getter
    @ToString
    public static class GCEntry {
        float score;
        float grade;
    }

    /**
     * 开始计算
     *
     * @param gradeScoreEntryList
     * @return
     */
    public static GCEntry calculator(List<GCEntry> gradeScoreEntryList) {
        float totalScore = 0f;
        float totalGrade = 0f;
        for (GCEntry gradeScoreEntry : gradeScoreEntryList) {
            totalGrade += gradeScoreEntry.grade * gradeScoreEntry.score;
            totalScore += gradeScoreEntry.score;
        }
        float res = 0f;
        if (totalScore > 0)
            res = totalGrade / totalScore;
        return new GCEntry(totalScore, res);
    }

    /**
     * 初始化Map
     */
    public static synchronized void initMap() {
        if (levelGradeMap != null) return;
        levelGradeMap = new HashMap<>();
        levelGradeMap.put("优秀", 90f);
        levelGradeMap.put("良好", 80f);
        levelGradeMap.put("中等", 70f);
        levelGradeMap.put("合格", 60f);
        levelGradeMap.put("通过", 60f);
        levelGradeMap.put("不合格", 0f);
        levelGradeMap.put("不通过", 0f);
    }

    public static float transferGrade(String level) {
        if (levelGradeMap == null) initMap();
        Float grade = levelGradeMap.get(level);
        if (grade == null) throw new OucException("the level: " + level + " has no match score");
        return grade;
    }
}
