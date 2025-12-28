package MainInterface.ScoreManagePackage;

import DAO.ScoreDAO;
import DAO.SubjectDAO;
import Entity.Score;
import Entity.Subject;

import java.math.BigDecimal;
import java.util.List;

public class ScoreManage {

    private ScoreDAO scoreDAO;
    private SubjectDAO subjectDAO;

    public ScoreManage() {
        this.scoreDAO = new ScoreDAO();
        this.subjectDAO = new SubjectDAO();
    }

    /**
     * 获取所有学科
     */
    public List<Subject> getAllSubjects() {
        return subjectDAO.getAllSubjects();
    }

    /**
     * 获取某学科所有学生成绩
     */
    public List<Score> getScoresBySubject(String subjectName) {
        Integer subjectId = subjectDAO.getSubjectIdByName(subjectName);
        if (subjectId == null) return null;
        return scoreDAO.getScoresBySubject(subjectId);
    }

    /**
     * 获取学生所有学科成绩
     */
    public List<Score> getScoresByStudent(String studentId) {
        return scoreDAO.getScoresByStudent(studentId);
    }

    /**
     * 添加或更新成绩
     */
    public boolean addOrUpdateScore(String studentId, String subjectName, BigDecimal usualGrade, BigDecimal examGrade) {
        Integer subjectId = subjectDAO.getSubjectIdByName(subjectName);
        if (subjectId == null) return false;
        return scoreDAO.addOrUpdateScore(studentId, subjectId, usualGrade, examGrade);
    }

    /**
     * 删除成绩
     */
    public boolean deleteScore(String studentId, String subjectName) {
        Integer subjectId = subjectDAO.getSubjectIdByName(subjectName);
        if (subjectId == null) return false;
        return scoreDAO.deleteScore(studentId, subjectId);
    }

    /**
     * 获取学生某学科成绩
     */
    public Score getScoreByStudentAndSubject(String studentId, String subjectName) {
        Integer subjectId = subjectDAO.getSubjectIdByName(subjectName);
        if (subjectId == null) return null;
        return scoreDAO.getScoreByStudentAndSubject(studentId, subjectId);
    }

    /**
     * 获取成绩值（适配原有接口）
     */
    public BigDecimal getScoreByStudentIdAndType(String studentId, String scoreType, String subjectName) {
        return scoreDAO.getScoreByStudentIdAndType(studentId, scoreType, subjectName);
    }

    /**
     * 新增：批量删除某学科的所有成绩（级联删除核心方法）
     * @param subjectName 学科名称
     * @return 批量删除是否成功
     */
    public boolean deleteAllScoresBySubject(String subjectName) {
        // 1. 沿用现有逻辑：先通过学科名称获取学科ID（保证与成绩表的关联正确性）
        Integer subjectId = subjectDAO.getSubjectIdByName(subjectName);

        // 2. 校验学科ID有效性（学科不存在则直接返回失败）
        if (subjectId == null) {
            return false;
        }

        // 3. 调用ScoreDAO的批量删除方法（后续需完善ScoreDAO对应方法）
        return scoreDAO.deleteAllScoresBySubject(subjectId);
    }

}