package DAO;

import Entity.Student;
import connect.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    /**
     * 新增学生
     */
    public boolean addStudent(Student student) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "INSERT INTO student (student_id, student_name, gender, class_name, phone, password) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, student.getStudentId());
                preparedStatement.setString(2, student.getStudentName());
                preparedStatement.setString(3, student.getGender());
                preparedStatement.setString(4, student.getClassName());
                preparedStatement.setString(5, student.getPhone());
                preparedStatement.setString(6, "111111"); // 添加密码

                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("添加学生失败: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * 根据ID删除学生
     */
    public boolean deleteStudent(String studentId) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "DELETE FROM student WHERE student_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, studentId);
                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("删除学生失败: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * 更新学生信息
     */
    public boolean updateStudent(Student student) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "UPDATE student SET student_name = ?, gender = ?, class_name = ?, phone = ?, password = ? WHERE student_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, student.getStudentName());
                preparedStatement.setString(2, student.getGender());
                preparedStatement.setString(3, student.getClassName());
                preparedStatement.setString(4, student.getPhone());
                preparedStatement.setString(5, student.getPassword());
                preparedStatement.setString(6, student.getStudentId());

                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新学生信息失败: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * 根据学号获取学生信息
     */
    public Student getStudentById(String studentId) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM student WHERE student_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, studentId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapRowToStudent(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询学生信息失败: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return null;
    }

    /**
     * 查询所有学生列表
     */
    public List<Student> getAllStudents() {
        Connection connection = null;
        List<Student> studentList = new ArrayList<>();

        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM student ORDER BY student_id";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Student student = mapRowToStudent(resultSet);
                        studentList.add(student);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询学生列表失败: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return studentList;
    }

    /**
     * 根据关键字搜索学生（学号或姓名）
     */
    public List<Student> searchStudents(String keyword) {
        Connection connection = null;
        List<Student> studentList = new ArrayList<>();

        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM student WHERE student_id LIKE ? OR student_name LIKE ? ORDER BY student_id";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                String searchPattern = "%" + keyword + "%";
                preparedStatement.setString(1, searchPattern);
                preparedStatement.setString(2, searchPattern);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Student student = mapRowToStudent(resultSet);
                        studentList.add(student);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("搜索学生失败: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return studentList;
    }

    /**
     * 检查学号是否存在
     */
    public boolean isStudentIdExists(String studentId) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT COUNT(*) FROM student WHERE student_id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, studentId);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("检查学号是否存在失败: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return false;
    }

    /**
     * 验证学生登录
     */
    public Student validateStudentLogin(String studentId, String password) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM student WHERE student_id = ? AND password = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, studentId);
                preparedStatement.setString(2, password);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return mapRowToStudent(resultSet);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("验证学生登录失败: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
        return null;
    }

    /**
     * 辅助方法：将 ResultSet 的当前行封装成 Student 对象
     */
    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getString("student_id"));
        student.setStudentName(rs.getString("student_name"));
        student.setGender(rs.getString("gender"));
        student.setClassName(rs.getString("class_name"));
        student.setPhone(rs.getString("phone"));
        student.setPassword(rs.getString("password")); // 添加密码
        return student;
    }
}