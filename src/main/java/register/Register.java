package register;

import DAO.UserDAO;
import Entity.User;
import connect.DatabaseConnection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.*;


/**
 * SWT 版本注册界面
 * @author SUNRISE / Gemini / echo-escape
 */
public class Register {

    protected Shell shell;
    private Text textUsername;
    private Text textPassword;
    private Text textConfirmPassword;

    // 资源对象，用于后续释放
    private Color bgColor;
    private Color btnRegisterColor;
    private Color btnBackColor;
    private Font titleFont;
    private Font normalFont;
    private Font btnFont;

    // 数据库访问对象
    private UserDAO userDAO = new UserDAO();

    /**
     * 打开窗口的方法
     */
    public void open() {
        Display display = Display.getDefault();
        createContents();
        shell.open();
        shell.layout();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    /**
     * 创建界面内容
     */
    protected void createContents() {
        // 初始化 Shell
        shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN);
        shell.setSize(550, 400);
        shell.setText("注册");

        // 初始化颜色和字体资源
        Display display = Display.getDefault();
        bgColor = new Color(display, 240, 248, 255); // AliceBlue
        btnRegisterColor = new Color(display, 70, 130, 180); // SteelBlue
        btnBackColor = new Color(display, 72, 209, 204); // MediumTurquoise

        titleFont = new Font(display, "微软雅黑", 24, SWT.BOLD);
        normalFont = new Font(display, "微软雅黑", 14, SWT.NORMAL);
        btnFont = new Font(display, "微软雅黑", 14, SWT.BOLD);

        shell.setBackground(bgColor);

        // 窗口居中逻辑
        Rectangle screenSize = display.getPrimaryMonitor().getBounds();
        Rectangle shellSize = shell.getBounds();
        shell.setLocation(
                (screenSize.width - shellSize.width) / 2,
                (screenSize.height - shellSize.height) / 2
        );

        //---- 标题 Label ----
        Label labelTitle = new Label(shell, SWT.CENTER);
        labelTitle.setText("注册");
        labelTitle.setFont(titleFont);
        labelTitle.setBackground(bgColor);
        labelTitle.setBounds(200, 40, 150, 50);

        //---- 用户名 Label ----
        Label labelUser = new Label(shell, SWT.CENTER);
        labelUser.setText("用户名");
        labelUser.setFont(normalFont);
        labelUser.setBackground(bgColor);
        labelUser.setBounds(130, 110, 80, 30);

        //---- 用户名输入框 ----
        textUsername = new Text(shell, SWT.BORDER | SWT.CENTER);
        textUsername.setFont(normalFont);
        textUsername.setBounds(220, 110, 200, 35);

        //---- 密码 Label ----
        Label labelPwd = new Label(shell, SWT.CENTER);
        labelPwd.setText("密码");
        labelPwd.setFont(normalFont);
        labelPwd.setBackground(bgColor);
        labelPwd.setBounds(130, 160, 80, 30);

        // 密码输入框
        textPassword = new Text(shell, SWT.BORDER | SWT.PASSWORD | SWT.CENTER);
        textPassword.setFont(normalFont);
        textPassword.setBounds(220, 160, 200, 35);

        // 确认密码 Label
        Label labelConfirm = new Label(shell, SWT.CENTER);
        labelConfirm.setText("确认密码");
        labelConfirm.setFont(normalFont);
        labelConfirm.setBackground(bgColor);
        labelConfirm.setBounds(100, 210, 120, 30);

        // 确认密码输入框
        textConfirmPassword = new Text(shell, SWT.BORDER | SWT.PASSWORD | SWT.CENTER);
        textConfirmPassword.setFont(normalFont);
        textConfirmPassword.setBounds(220, 210, 200, 30);

        // 返回按钮
        Button btnBack = new Button(shell, SWT.PUSH);
        btnBack.setText("返回");
        btnBack.setFont(btnFont);
        btnBack.setBounds(140, 270, 100, 35);

        // 添加返回事件
        btnBack.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                backAction();
            }
        });

        // 注册按钮
        Button btnRegister = new Button(shell, SWT.PUSH);
        btnRegister.setText("注册");
        btnRegister.setFont(btnFont);
        btnRegister.setBounds(260, 270, 100, 35);

        // 添加注册事件
        btnRegister.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                registerAction();
            }
        });

        // 监听窗口关闭，释放资源
        shell.addDisposeListener(e -> disposeResources());
    }

    // 释放颜色和字体资源
    private void disposeResources() {
        if (bgColor != null) bgColor.dispose();
        if (btnRegisterColor != null) btnRegisterColor.dispose();
        if (btnBackColor != null) btnBackColor.dispose();
        if (titleFont != null) titleFont.dispose();
        if (normalFont != null) normalFont.dispose();
        if (btnFont != null) btnFont.dispose();
    }

    // 返回按钮逻辑
    private void backAction() {
        shell.dispose();
    }

    // 注册按钮逻辑
    private void registerAction() {
        String username = textUsername.getText().trim();
        String password = textPassword.getText();
        String password2 = textConfirmPassword.getText();

        if (!password.equals(password2)) {
            showMessageBox("错误", "两次输入的密码不一致！", SWT.ICON_ERROR);
            return;
        }
        if (username.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            showMessageBox("错误", "请填写完整！", SWT.ICON_ERROR);
            return;
        }
        if (password.length() < 6) {
            showMessageBox("错误", "密码长度不能小于6位！", SWT.ICON_ERROR);
            return;
        }

        try {
            // 检查用户名是否存在
            if (userDAO.isUsernameExist(username)) {
                showMessageBox("错误", "用户名已存在！", SWT.ICON_ERROR);
                return;
            }
            User newUser = new User(username, password);
            // 注册用户
            if (userDAO.registerUser(newUser)) {
                showMessageBox("提示", "注册成功！", SWT.ICON_INFORMATION);
                backAction();
            } else {
                showMessageBox("错误", "注册失败，请重试！", SWT.ICON_ERROR);
            }
        } catch (Exception e) {
            showMessageBox("错误", "注册过程中出现错误：" + e.getMessage(), SWT.ICON_ERROR);
        }
    }

    // 显示弹窗的辅助方法
    private void showMessageBox(String title, String message, int style) {
        MessageBox mb = new MessageBox(shell, style | SWT.OK);
        mb.setText(title);
        mb.setMessage(message);
        mb.open();
    }

}