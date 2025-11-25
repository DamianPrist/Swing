package DAO;

import Entity.User;
import connect.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用户数据访问对象,封装数据库操作逻辑
 */
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
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    User user = new User();
                    user.setName(resultSet.getString("username"));
                    user.setPassword(resultSet.getString("password"));
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
            String query = "SELECT * FROM user WHERE username = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                return resultSet.next();
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
                insertStatement.setString(1, user.getName());
                insertStatement.setString(2, user.getPassword());
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


    /**
     * 根据用户名获取用户信息
     */

    /**
     *
     */
}
