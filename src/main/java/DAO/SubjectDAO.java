package DAO;

import Entity.Subject;
import connect.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    /**
     * 获取所有学科
     */
    public List<Subject> getAllSubjects() {
        Connection connection = null;
        List<Subject> subjectList = new ArrayList<>();

        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM subjects ORDER BY subject_name";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Subject subject = new Subject();
                        subject.setSubjectId(resultSet.getInt("subject_id"));
                        subject.setSubjectName(resultSet.getString("subject_name"));
                        subjectList.add(subject);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return subjectList;
    }

    /**
     * 根据学科名称获取学科ID
     */
    public Integer getSubjectIdByName(String subjectName) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT subject_id FROM subjects WHERE subject_name = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, subjectName);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt("subject_id");
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
     * 根据学科ID获取学科名称
     */
    public String getSubjectNameById(int subjectId) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT subject_name FROM subjects WHERE subject_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, subjectId);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getString("subject_name");
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

    public boolean addSubject(String subjectName) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();

            // 先检查学科名称是否已存在（避免重复）
            if (getSubjectIdByName(subjectName) != null) {
                return false; // 名称已存在，新增失败
            }

            // 插入新学科（假设subject_id为自增主键）
            String insertSql = "INSERT INTO subjects (subject_name) VALUES (?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
                pstmt.setString(1, subjectName);
                return pstmt.executeUpdate() > 0; // 影响行数>0表示成功
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * 删除学科（需先删除该学科的所有成绩，避免外键约束错误）
     * @param subjectId 学科ID
     * @return 删除是否成功（true：成功；false：失败）
     */
    public boolean deleteSubject(int subjectId) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            // 关闭自动提交，确保成绩删除和学科删除在同一事务中
            connection.setAutoCommit(false);

            // 步骤1：删除该学科的所有成绩（依赖ScoreDAO的deleteAllScoresBySubject方法）
            ScoreDAO scoreDAO = new ScoreDAO();
            boolean scoresDeleted = scoreDAO.deleteAllScoresBySubject(subjectId);
            if (!scoresDeleted) {
                connection.rollback(); // 成绩删除失败，回滚事务
                return false;
            }

            // 步骤2：删除学科本身
            String deleteSql = "DELETE FROM subjects WHERE subject_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
                pstmt.setInt(1, subjectId);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    connection.commit(); // 全部成功，提交事务
                    return true;
                } else {
                    connection.rollback(); // 学科不存在，回滚
                    return false;
                }
            }
        } catch (SQLException e) {
            try {
                if (connection != null) connection.rollback(); // 异常时回滚
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true); // 恢复自动提交
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            DatabaseConnection.closeConnection(connection);
        }
    }

}