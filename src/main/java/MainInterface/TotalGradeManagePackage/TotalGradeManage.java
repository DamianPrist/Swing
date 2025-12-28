
        package MainInterface.TotalGradeManagePackage;

import DAO.ScoreDAO;
import DAO.StudentDAO;
import DAO.SubjectDAO;
import Entity.Score;
import Entity.Student;
import Entity.Subject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 总成绩管理后端逻辑类 - 修改为支持多学科汇总
 */
public class TotalGradeManage {

    private ScoreDAO scoreDAO;
    private StudentDAO studentDAO;
    private SubjectDAO subjectDAO;

    public TotalGradeManage() {
        this.scoreDAO = new ScoreDAO();
        this.studentDAO = new StudentDAO();
        this.subjectDAO = new SubjectDAO();
    }

    /**
     * 获取所有学科
     */
    public List<Subject> getAllSubjects() {
        return subjectDAO.getAllSubjects();
    }

    /**
     * 获取所有学生的各科成绩汇总（按平均总成绩排序）
     */
    public List<Map<String, Object>> getAllStudentsOrderByTotalGrade() {
        List<Student> allStudents = studentDAO.getAllStudents();
        List<Subject> allSubjects = subjectDAO.getAllSubjects();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Student student : allStudents) {
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("studentId", student.getStudentId());
            studentData.put("studentName", student.getStudentName());
            studentData.put("className", student.getClassName());

            // 获取该学生的所有学科成绩
            List<Score> studentScores = scoreDAO.getScoresByStudent(student.getStudentId());

            BigDecimal usualSum = BigDecimal.ZERO;
            BigDecimal examSum = BigDecimal.ZERO;
            BigDecimal totalSum = BigDecimal.ZERO;
            int subjectCount = 0;

            for (Score score : studentScores) {
                if (score.getUsualGrade() != null) {
                    usualSum = usualSum.add(score.getUsualGrade());
                }
                if (score.getExamGrade() != null) {
                    examSum = examSum.add(score.getExamGrade());
                }
                if (score.getTotalGrade() != null) {
                    totalSum = totalSum.add(score.getTotalGrade());
                }
                subjectCount++;
            }

            // 计算平均值
            if (subjectCount > 0) {
                BigDecimal usualAvg = usualSum.divide(new BigDecimal(subjectCount), 2, RoundingMode.HALF_UP);
                BigDecimal examAvg = examSum.divide(new BigDecimal(subjectCount), 2, RoundingMode.HALF_UP);
                BigDecimal totalAvg = totalSum.divide(new BigDecimal(subjectCount), 2, RoundingMode.HALF_UP);

                studentData.put("usualGrade", usualAvg);
                studentData.put("examGrade", examAvg);
                studentData.put("totalGrade", totalAvg);
            } else {
                studentData.put("usualGrade", null);
                studentData.put("examGrade", null);
                studentData.put("totalGrade", null);
            }

            studentData.put("subjectCount", subjectCount);
            result.add(studentData);
        }

        // 按平均总成绩排序（降序）
        result.sort(Comparator.comparing(
                (Map<String, Object> map) -> {
                    BigDecimal total = (BigDecimal) map.get("totalGrade");
                    return total != null ? total : BigDecimal.ZERO;
                },
                Comparator.reverseOrder()
        ));

        return result;
    }

    /**
     * 搜索学生并按平均总成绩排序
     */
    public List<Map<String, Object>> searchStudentsOrderByTotalGrade(String keyword) {
        List<Student> searchResults = studentDAO.searchStudents(keyword);
        List<Map<String, Object>> result = new ArrayList<>();

        for (Student student : searchResults) {
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("studentId", student.getStudentId());
            studentData.put("studentName", student.getStudentName());
            studentData.put("className", student.getClassName());

            // 获取该学生的所有学科成绩
            List<Score> studentScores = scoreDAO.getScoresByStudent(student.getStudentId());

            BigDecimal usualSum = BigDecimal.ZERO;
            BigDecimal examSum = BigDecimal.ZERO;
            BigDecimal totalSum = BigDecimal.ZERO;
            int subjectCount = 0;

            for (Score score : studentScores) {
                if (score.getUsualGrade() != null) {
                    usualSum = usualSum.add(score.getUsualGrade());
                }
                if (score.getExamGrade() != null) {
                    examSum = examSum.add(score.getExamGrade());
                }
                if (score.getTotalGrade() != null) {
                    totalSum = totalSum.add(score.getTotalGrade());
                }
                subjectCount++;
            }

            // 计算平均值
            if (subjectCount > 0) {
                BigDecimal usualAvg = usualSum.divide(new BigDecimal(subjectCount), 2, RoundingMode.HALF_UP);
                BigDecimal examAvg = examSum.divide(new BigDecimal(subjectCount), 2, RoundingMode.HALF_UP);
                BigDecimal totalAvg = totalSum.divide(new BigDecimal(subjectCount), 2, RoundingMode.HALF_UP);

                studentData.put("usualGrade", usualAvg);
                studentData.put("examGrade", examAvg);
                studentData.put("totalGrade", totalAvg);
            } else {
                studentData.put("usualGrade", null);
                studentData.put("examGrade", null);
                studentData.put("totalGrade", null);
            }

            studentData.put("subjectCount", subjectCount);
            result.add(studentData);
        }

        // 按平均总成绩排序（降序）
        result.sort(Comparator.comparing(
                (Map<String, Object> map) -> {
                    BigDecimal total = (BigDecimal) map.get("totalGrade");
                    return total != null ? total : BigDecimal.ZERO;
                },
                Comparator.reverseOrder()
        ));

        return result;
    }

    /**
     * 获取某学科所有学生成绩（按总成绩排序）
     */
    public List<Score> getScoresBySubjectOrderByTotal(String subjectName) {
        Integer subjectId = subjectDAO.getSubjectIdByName(subjectName);
        if (subjectId == null) return new ArrayList<>();
        return scoreDAO.getScoresBySubject(subjectId);
    }

    /**
     * 搜索某学科学生成绩
     */
    public List<Score> searchScoresBySubject(String subjectName, String keyword) {
        List<Score> allScores = getScoresBySubjectOrderByTotal(subjectName);
        if (keyword == null || keyword.trim().isEmpty()) {
            return allScores;
        }

        keyword = keyword.toLowerCase().trim();
        List<Score> filteredScores = new ArrayList<>();

        for (Score score : allScores) {
            if (score.getStudentName().toLowerCase().contains(keyword) ||
                    score.getStudentId().contains(keyword)) {
                filteredScores.add(score);
            }
        }

        return filteredScores;
    }
}
