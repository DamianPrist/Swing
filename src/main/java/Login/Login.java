package Login;

import Connect.DatabaseConnection;
import Register.Register;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
/*
 * Created by JFormDesigner on Sun Nov 23 23:31:56 CST 2025
 */



/**
 * @author SUNRISE
 */
public class Login extends JFrame {
    public Login() {
        initComponents();
    }

    // 登录按钮点击事件
    private void login_button(ActionEvent e) {
        if(e.getSource()==loginbutton){
            String username = textField1.getText();
            String password = new String(passwordField1.getPassword());
            if (validateUser(username, password)) {
                JOptionPane.showMessageDialog(this, "登录成功！");
            } else {
                JOptionPane.showMessageDialog(this, "登录失败，请检查用户名和密码！");
            }
        }
    }

    // 验证用户名和密码
    private boolean validateUser(String username, String password) {
        Connection connection = null;
        try {
            connection= DatabaseConnection.getConnection();
            String query = "SELECT * FROM jformtest WHERE username = ? AND password = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);//设置第一个占位符为用户名
            preparedStatement.setString(2, password);//设置第二个占位符为密码

            ResultSet resultSet = preparedStatement.executeQuery();//执行查询
            return resultSet.next();//判断结果集是否有下一行，如果有则返回true，否则返回false
        }
        }catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据库连接或查询出现问题：" + e.getMessage());
            return false;
        }finally {
            DatabaseConnection.closeConnection(connection);
        }
    }

    private void register_button(ActionEvent e) {
        Register register = new Register();
        register.setVisible(true);
    }

    //前端界面
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        textField1 = new JTextField();
        passwordField1 = new JPasswordField();
        loginbutton = new JButton();
        button2 = new JButton();

        //======== this ========
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- label1 (标题) ----
        label1.setText("请登录");
        label1.setFont(new Font("微软雅黑", Font.BOLD, 24));
        label1.setHorizontalAlignment(SwingConstants.CENTER); // 文字居中
        contentPane.add(label1);
        label1.setBounds(200, 45, 200, 60);

        //---- label2 (账户标签) ----
        label2.setText("账户");
        label2.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        label2.setHorizontalAlignment(SwingConstants.CENTER); // 文字居中
        contentPane.add(label2);
        label2.setBounds(130, 140, 60, 30);

        //---- label3 (密码标签) ----
        label3.setText("密码");
        label3.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        label3.setHorizontalAlignment(SwingConstants.CENTER); // 文字居中
        contentPane.add(label3);
        label3.setBounds(130, 190, 60, 30);

        //---- 文本框和密码框 ----
        textField1.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        textField1.setHorizontalAlignment(JTextField.CENTER); // 输入文字居中
        contentPane.add(textField1);
        textField1.setBounds(200, 140, 200, 30);

        passwordField1.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        passwordField1.setHorizontalAlignment(JTextField.CENTER); // 输入文字居中
        contentPane.add(passwordField1);
        passwordField1.setBounds(200, 190, 200, 30);

        //---- 登录按钮 ----
        loginbutton.setText("登录");
        loginbutton.setFont(new Font("微软雅黑", Font.BOLD, 14));
        loginbutton.setBackground(new Color(70, 130, 180));
        loginbutton.setForeground(Color.WHITE);
        loginbutton.setFocusPainted(false);
        loginbutton.addActionListener(e -> login_button(e));
        contentPane.add(loginbutton);
        loginbutton.setBounds(150, 250, 100, 35);

        //---- 注册按钮 ----
        button2.setText("注册");
        button2.setFont(new Font("微软雅黑", Font.BOLD, 14));
        button2.setBackground(new Color(72, 209, 204));
        button2.setForeground(Color.WHITE);
        button2.setFocusPainted(false);
        button2.addActionListener(e -> register_button(e));
        contentPane.add(button2);
        button2.setBounds(270, 250, 100, 35);

        // 设置内容面板背景色
        contentPane.setBackground(new Color(240, 248, 255));

        {
            // compute preferred size
            Dimension preferredSize = new Dimension();
            for(int i = 0; i < contentPane.getComponentCount(); i++) {
                Rectangle bounds = contentPane.getComponent(i).getBounds();
                preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
            }
            Insets insets = contentPane.getInsets();
            preferredSize.width += insets.right;
            preferredSize.height += insets.bottom;
            contentPane.setMinimumSize(preferredSize);
            contentPane.setPreferredSize(preferredSize);
        }

        // 设置窗口大小
        setSize(550, 400);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton loginbutton;
    private JButton button2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
