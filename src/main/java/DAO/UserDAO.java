package DAO;

import Entity.User;
import connect.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * 验证用户登录
     */
    public User validateUser(String username, String password) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM user WHERE username = ? AND password = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);  // 改为setString
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));  // 添加获取id
                    user.setUsername(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));  // 改为getString
                    return user;
                }
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库查询失败: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * 检查用户名是否存在
     */
    public boolean isUsernameExist(String username) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String query = "SELECT COUNT(*) FROM user WHERE username = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库查询失败: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    /**
     * 注册新用户
     */
    public boolean registerUser(User user) {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();
            String insertQuery = "INSERT INTO user (username, password) VALUES (?, ?)";

            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, user.getUsername());
                insertStatement.setString(2, user.getPassword());  // 改为setString
                int rowsAffected = insertStatement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("用户注册失败: " + e.getMessage(), e);
        } finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    public boolean updateUserPassword(String username, String newPassword) {
        // 与现有方法保持一致：先声明连接对象，后续在finally中关闭
        Connection connection = null;
        try {
            // 复用现有数据库连接工具类，获取连接
            connection = DatabaseConnection.getConnection();
            // SQL更新语句：根据用户名更新密码（用户名是唯一标识，与现有validateUser逻辑一致）
            String updateQuery = "UPDATE user SET password = ? WHERE username = ?";

            // 复用try-with-resources语法，自动关闭PreparedStatement，避免资源泄漏
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                // 设置SQL占位符参数：第一个?=新密码，第二个?=用户名（对应SQL语句顺序）
                preparedStatement.setString(1, newPassword);
                preparedStatement.setString(2, username);

                // 执行更新操作，返回受影响的行数
                int rowsAffected = preparedStatement.executeUpdate();

                // 受影响行数>0表示更新成功（找到对应用户名并修改密码）
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            // 与现有方法保持一致的异常处理风格
            e.printStackTrace();
            throw new RuntimeException("用户密码更新失败: " + e.getMessage(), e);
        } finally {
            // 复用现有工具类，关闭数据库连接，释放资源
            DatabaseConnection.closeConnection(connection);
        }
    }

}