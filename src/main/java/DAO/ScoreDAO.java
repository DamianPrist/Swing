package DAO;

import Entity.Score;
import Entity.Student;
import connect.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ScoreDAO {

    /**
     * 添加或更新成绩
     * 如果该学生该学科已有成绩，则更新；否则插入新记录
     */
    public boolean addOrUpdateScore(String studentId, int subjectId, BigDecimal usualGrade, BigDecimal examGrade) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();

            // 先检查是否已存在记录
            String checkQuery = "SELECT id FROM scores WHERE student_id = ? AND subject_id = ?";
            boolean exists = false;

            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, studentId);
                checkStmt.setInt(2, subjectId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    exists = rs.next();
                }
            }

            if (exists) {
                // 更新现有记录
                String updateQuery = "UPDATE scores SET usual_grade = ?, exam_grade = ?, total_grade = ? WHERE student_id = ? AND subject_id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(updateQuery)) {
                    pstmt.setBigDecimal(1, usualGrade);
                    pstmt.setBigDecimal(2, examGrade);

                    // 计算总成绩
                    BigDecimal totalGrade = calculateTotalGrade(usualGrade, examGrade);
                    pstmt.setBigDecimal(3, totalGrade);

                    pstmt.setString(4, studentId);
                    pstmt.setInt(5, subjectId);

                    return pstmt.executeUpdate() > 0;
                }
            } else {
                // 插入新记录
                String insertQuery = "INSERT INTO scores (student_id, subject_id, usual_grade, exam_grade, total_grade) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                    pstmt.setString(1, studentId);
                    pstmt.setInt(2, subjectId);
                    pstmt.setBigDecimal(3, usualGrade);
                    pstmt.setBigDecimal(4, examGrade);

                    // 计算总成绩
                    BigDecimal totalGrade = calculateTotalGrade(usualGrade, examGrade);
                    pstmt.setBigDecimal(5, totalGrade);

                    return pstmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * 删除学生某学科成绩
     */
    public boolean deleteScore(String studentId, int subjectId) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "DELETE FROM scores WHERE student_id = ? AND subject_id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, studentId);
                pstmt.setInt(2, subjectId);
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * 获取学生所有学科成绩
     */
    public List<Score> getScoresByStudent(String studentId) {
        Connection connection = null;
        List<Score> scores = new ArrayList<>();

        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT sc.*, s.subject_name FROM scores sc " +
                    "JOIN subjects s ON sc.subject_id = s.subject_id " +
                    "WHERE sc.student_id = ? ORDER BY s.subject_name";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, studentId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Score score = new Score();
                        score.setId(rs.getInt("id"));
                        score.setStudentId(rs.getString("student_id"));
                        score.setSubjectId(rs.getInt("subject_id"));
                        score.setUsualGrade(rs.getBigDecimal("usual_grade"));
                        score.setExamGrade(rs.getBigDecimal("exam_grade"));
                        score.setTotalGrade(rs.getBigDecimal("total_grade"));
                        score.setSubjectName(rs.getString("subject_name"));
                        scores.add(score);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return scores;
    }

    /**
     * 获取学生特定学科成绩
     */
    public Score getScoreByStudentAndSubject(String studentId, int subjectId) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            // 修改查询语句，添加学生姓名
            String query = "SELECT sc.*, s.subject_name, st.student_name " +
                    "FROM scores sc " +
                    "JOIN subjects s ON sc.subject_id = s.subject_id " +
                    "JOIN student st ON sc.student_id = st.student_id " +
                    "WHERE sc.student_id = ? AND sc.subject_id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setString(1, studentId);
                pstmt.setInt(2, subjectId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Score score = new Score();
                        score.setId(rs.getInt("id"));
                        score.setStudentId(rs.getString("student_id"));
                        score.setSubjectId(rs.getInt("subject_id"));
                        score.setUsualGrade(rs.getBigDecimal("usual_grade"));
                        score.setExamGrade(rs.getBigDecimal("exam_grade"));
                        score.setTotalGrade(rs.getBigDecimal("total_grade"));
                        score.setSubjectName(rs.getString("subject_name"));
                        score.setStudentName(rs.getString("student_name")); // 添加学生姓名
                        return score;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return null;
    }

    /**
     * 获取某学科所有学生成绩
     */
    public List<Score> getScoresBySubject(int subjectId) {
        Connection connection = null;
        List<Score> scores = new ArrayList<>();

        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT sc.*, s.subject_name, st.student_name FROM scores sc " +
                    "JOIN subjects s ON sc.subject_id = s.subject_id " +
                    "JOIN student st ON sc.student_id = st.student_id " +
                    "WHERE sc.subject_id = ? ORDER BY sc.total_grade DESC";

            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, subjectId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Score score = new Score();
                        score.setId(rs.getInt("id"));
                        score.setStudentId(rs.getString("student_id"));
                        score.setSubjectId(rs.getInt("subject_id"));
                        score.setUsualGrade(rs.getBigDecimal("usual_grade"));
                        score.setExamGrade(rs.getBigDecimal("exam_grade"));
                        score.setTotalGrade(rs.getBigDecimal("total_grade"));
                        score.setSubjectName(rs.getString("subject_name"));
                        score.setStudentName(rs.getString("student_name"));
                        scores.add(score);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return scores;
    }

    /**
     * 获取学生某学科的成绩（适配原有接口）
     */
    public BigDecimal getScoreByStudentIdAndType(String studentId, String scoreType, String subjectName) {
        try {
            // 获取学科ID
            SubjectDAO subjectDAO = new SubjectDAO();
            Integer subjectId = subjectDAO.getSubjectIdByName(subjectName);
            if (subjectId == null) return null;

            // 获取成绩
            Score score = getScoreByStudentAndSubject(studentId, subjectId);
            if (score == null) return null;

            if ("usual".equals(scoreType)) {
                return score.getUsualGrade();
            } else if ("exam".equals(scoreType)) {
                return score.getExamGrade();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算总成绩
     */
    private BigDecimal calculateTotalGrade(BigDecimal usualGrade, BigDecimal examGrade) {
        if (usualGrade == null || examGrade == null) return null;
        return usualGrade.multiply(new BigDecimal("0.4"))
                .add(examGrade.multiply(new BigDecimal("0.6")));
    }

    /**
     * 删除某学科的所有成绩记录
     * @param subjectId 学科ID
     * @return 操作是否成功（true表示成功，false表示失败）
     */
    public boolean deleteAllScoresBySubject(int subjectId) {
        Connection connection = null;
        try {
            // 获取数据库连接
            connection = DatabaseConnection.getConnection();
            // 编写删除SQL：删除scores表中指定学科ID的所有记录
            String query = "DELETE FROM scores WHERE subject_id = ?";

            // 使用try-with-resources自动关闭PreparedStatement
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                // 设置参数（学科ID）
                pstmt.setInt(1, subjectId);
                // 执行删除操作，返回受影响的行数
                int affectedRows = pstmt.executeUpdate();
                // 受影响行数>0表示删除成功
                return affectedRows >= 0;
            }
        } catch (SQLException e) {
            // 打印异常信息
            e.printStackTrace();
            return false;
        } finally {
            // 确保连接关闭
            DatabaseConnection.closeConnection(connection);
        }
    }

}