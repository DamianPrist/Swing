package login;

import DAO.UserDAO;
import Entity.User;
import MainInterface.MainInterface;
import connect.DatabaseConnection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;
import register.Register;



/**
 * SWT login 界面
 * @author SUNRISE / Gemini / echo-escape
 */
public class Login {

    protected Shell shell;
    private Text textUser;
    private Text textPassword;
    private Font fontInput;// 统一输入字体格式

    // 用户数据访问对象
    private UserDAO userDAO = new UserDAO();
    private User currentUser;

    /**
     * 打开窗口
     */
    public void open() {
        Display display = Display.getDefault();
        createContents();

        // 窗口居中计算
        Rectangle screenSize = display.getPrimaryMonitor().getBounds();
        Rectangle shellSize = shell.getBounds();
        shell.setLocation(
                (screenSize.width - shellSize.width) / 2,
                (screenSize.height - shellSize.height) / 2
        );

        shell.open();
        shell.layout();

        // SWT 事件循环
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * 创建窗口内容
     */
    protected void createContents() {
        shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN);
        shell.setSize(550, 400);
        shell.setText("用户登录");
        // 设置背景色 (浅蓝)
        Color bgColor = new Color(Display.getCurrent(), 240, 248, 255);
        shell.setBackground(bgColor);

        // 这是一个监听器，用于在窗口关闭时销毁我们创建的颜色资源，防止内存泄漏
        shell.addDisposeListener(e -> bgColor.dispose());

        // 标题 Label
        Label labelTitle = new Label(shell, SWT.CENTER);
        labelTitle.setText("请登录");
        labelTitle.setBackground(bgColor); // 背景色需与父容器一致
        Font fontTitle = new Font(Display.getCurrent(), "微软雅黑", 24, SWT.BOLD);
        labelTitle.setFont(fontTitle);
        labelTitle.setBounds(175, 45, 200, 60); // 调整了一下X坐标以适应SWT的渲染
        // 销毁字体资源
        labelTitle.addDisposeListener(e -> fontTitle.dispose());

        // 账户 Label
        Label labelUser = new Label(shell, SWT.CENTER);
        labelUser.setText("账户");
        labelUser.setBackground(bgColor);
        Font fontNormal = new Font(Display.getCurrent(), "微软雅黑", 14, SWT.NORMAL);
        labelUser.setFont(fontNormal);
        labelUser.setBounds(130, 140, 60, 30);
        // 这里的fontNormal我们也需要在最后释放，或者让控件销毁时释放

        // 密码 Label
        Label labelPwd = new Label(shell, SWT.CENTER);
        labelPwd.setText("密码");
        labelPwd.setBackground(bgColor);
        labelPwd.setFont(fontNormal);
        labelPwd.setBounds(130, 190, 60, 30);

        // 用户名输入框
        textUser = new Text(shell, SWT.BORDER | SWT.CENTER);
        textUser.setFont(fontNormal);
        textUser.setBounds(220, 140, 200, 30);

        // 密码输入框
        // SWT.PASSWORD 用于隐藏输入内容
        textPassword = new Text(shell, SWT.BORDER | SWT.PASSWORD | SWT.CENTER);
        textPassword.setFont(fontNormal);
        textPassword.setBounds(220, 190, 200, 30);

        // 登录按钮
        Button btnLogin = new Button(shell, SWT.PUSH);
        btnLogin.setText("登录");
        btnLogin.setFont(new Font(Display.getCurrent(), "微软雅黑", 12, SWT.BOLD)); // 稍微减小字体以适应按钮
        btnLogin.setBounds(150, 250, 100, 35);

        // 登录逻辑
        btnLogin.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleLogin();
            }
        });

        // 注册按钮
        Button btnRegister = new Button(shell, SWT.PUSH);
        btnRegister.setText("注册");
        btnRegister.setFont(new Font(Display.getCurrent(), "微软雅黑", 12, SWT.BOLD));
        btnRegister.setBounds(270, 250, 100, 35);

        // 注册逻辑
        btnRegister.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleRegister();
            }
        });

        // 统一释放字体资源的监听器
        shell.addDisposeListener(e -> {
            fontNormal.dispose();
            if (fontInput != null) fontInput.dispose();
        });
    }

    /**
     * 处理登录事件
     */
    private void handleLogin() {
        String username = textUser.getText().trim();
        String password = textPassword.getText();

        // 输入验证
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("错误", "请输入用户名和密码！", SWT.ICON_ERROR);
            return;
        }

        try {
            currentUser = userDAO.validateUser(username, password);
            if (currentUser!= null) {
                showMessage("成功", "登录成功！", SWT.ICON_INFORMATION);
                // 关闭登录窗口
                shell.dispose();
                new MainInterface().open(); // 启动MainInterface
            } else {
                showMessage("失败", "登录失败，请检查用户名和密码！", SWT.ICON_ERROR);
            }
        } catch (Exception e) {
            showMessage("错误", "登录过程中出现错误：" + e.getMessage(), SWT.ICON_ERROR);
        }
    }

    /**
     * 获取当前登录用户
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * 处理注册事件
     */
    private void handleRegister() {
        // SWT 中通常打开另一个 Shell
        Register register = new Register();
        register.open();
    }

    /**
     * 辅助方法：显示消息弹窗 (替代 JOptionPane)
     */
    private void showMessage(String title, String message, int iconStyle) {
        MessageBox messageBox = new MessageBox(shell, iconStyle | SWT.OK);
        messageBox.setText(title);
        messageBox.setMessage(message);
        messageBox.open();
    }
}