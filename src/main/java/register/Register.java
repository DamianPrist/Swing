package register;

import DAO.UserDAO;
import Entity.User;
//import connect.DatabaseConnection;
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

    // 颜色和字体资源
    private Color primaryColor;
    private Color secondaryColor;
    private Color accentColor;
    private Color bgColor;
    private Color textFieldBgColor;
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
        shell.setSize(650, 550); // 增大窗口尺寸
        shell.setText("用户注册");

        // 初始化颜色和字体资源
        Display display = Display.getCurrent();
        primaryColor = new Color(display, 74, 144, 226);   // 主色调蓝色
        secondaryColor = new Color(display, 250, 250, 252); // 次要背景色
        accentColor = new Color(display, 102, 187, 106);   // 强调色绿色
        bgColor = new Color(display, 248, 250, 252);       // 主背景色
        textFieldBgColor = new Color(display, 255, 255, 255); // 输入框背景

        titleFont = new Font(display, "微软雅黑", 24, SWT.BOLD); // 减小标题字体
        normalFont = new Font(display, "微软雅黑", 11, SWT.NORMAL);
        btnFont = new Font(display, "微软雅黑", 12, SWT.BOLD);

        shell.setBackground(bgColor);

        // 窗口居中逻辑
        Rectangle screenSize = display.getPrimaryMonitor().getBounds();
        Rectangle shellSize = shell.getBounds();
        shell.setLocation(
                (screenSize.width - shellSize.width) / 2,
                (screenSize.height - shellSize.height) / 2
        );

        // 头部区域
        Composite headerComposite = new Composite(shell, SWT.NONE);
        headerComposite.setBackground(primaryColor);
        headerComposite.setBounds(0, 0, 650, 100);

        //---- 标题 Label - 调整高度确保字体完整显示
        Label labelTitle = new Label(headerComposite, SWT.CENTER);
        labelTitle.setText("创建账号");
        labelTitle.setFont(titleFont);
        labelTitle.setForeground(new Color(display, 255, 255, 255));
        labelTitle.setBackground(primaryColor);
        labelTitle.setBounds(0, 15, 650, 70); // 调整位置和高度

        // 主内容区域
        Composite mainComposite = new Composite(shell, SWT.NONE);
        mainComposite.setBackground(bgColor);
        mainComposite.setBounds(75, 130, 500, 350);

        //---- 用户名 Label - 增加高度
        Label labelUser = new Label(mainComposite, SWT.NONE);
        labelUser.setText("用户名");
        labelUser.setFont(normalFont);
        labelUser.setBackground(bgColor);
        labelUser.setBounds(50, 40, 100, 30); // 增加高度到30

        //---- 用户名输入框 - 增加高度
        textUsername = new Text(mainComposite, SWT.BORDER | SWT.SINGLE);
        textUsername.setFont(normalFont);
        textUsername.setBackground(textFieldBgColor);
        textUsername.setBounds(150, 35, 300, 40); // 增加高度到40

        //---- 密码 Label - 增加高度
        Label labelPwd = new Label(mainComposite, SWT.NONE);
        labelPwd.setText("设置密码");
        labelPwd.setFont(normalFont);
        labelPwd.setBackground(bgColor);
        labelPwd.setBounds(50, 100, 100, 30); // 增加高度到30

        // 密码输入框 - 增加高度
        textPassword = new Text(mainComposite, SWT.BORDER | SWT.PASSWORD | SWT.SINGLE);
        textPassword.setFont(normalFont);
        textPassword.setBackground(textFieldBgColor);
        textPassword.setBounds(150, 95, 300, 40); // 增加高度到40

        // 确认密码 Label - 增加高度
        Label labelConfirm = new Label(mainComposite, SWT.NONE);
        labelConfirm.setText("确认密码");
        labelConfirm.setFont(normalFont);
        labelConfirm.setBackground(bgColor);
        labelConfirm.setBounds(50, 160, 100, 30); // 增加高度到30

        // 确认密码输入框 - 增加高度
        textConfirmPassword = new Text(mainComposite, SWT.BORDER | SWT.PASSWORD | SWT.SINGLE);
        textConfirmPassword.setFont(normalFont);
        textConfirmPassword.setBackground(textFieldBgColor);
        textConfirmPassword.setBounds(150, 155, 300, 40); // 增加高度到40

        // 密码要求提示 - 增加高度
        Label passwordHint = new Label(mainComposite, SWT.NONE);
        passwordHint.setText("• 密码长度至少6位");
        passwordHint.setForeground(new Color(display, 153, 153, 153));
        passwordHint.setFont(new Font(display, "微软雅黑", 10, SWT.NORMAL)); // 调整字体大小
        passwordHint.setBackground(bgColor);
        passwordHint.setBounds(150, 200, 200, 25); // 增加高度到25

        // 按钮容器
        Composite buttonComposite = new Composite(mainComposite, SWT.NONE);
        buttonComposite.setBackground(bgColor);
        buttonComposite.setBounds(50, 240, 400, 50);

        // 返回按钮 - 增加高度
        Button btnBack = new Button(buttonComposite, SWT.PUSH);
        btnBack.setText("返回登录");
        btnBack.setFont(btnFont);
        btnBack.setBackground(secondaryColor);
        btnBack.setForeground(primaryColor);
        btnBack.setBounds(0, 0, 180, 45); // 增加高度到45

        // 添加鼠标悬停效果
        btnBack.addListener(SWT.MouseEnter, e -> {
            btnBack.setBackground(new Color(display, 240, 242, 245));
        });
        btnBack.addListener(SWT.MouseExit, e -> {
            btnBack.setBackground(secondaryColor);
        });

        // 添加返回事件
        btnBack.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                backAction();
            }
        });

        // 注册按钮 - 增加高度
        Button btnRegister = new Button(buttonComposite, SWT.PUSH);
        btnRegister.setText("立即注册");
        btnRegister.setFont(btnFont);
        btnRegister.setBackground(accentColor);
        btnRegister.setForeground(new Color(display, 255, 255, 255));
        btnRegister.setBounds(220, 0, 180, 45); // 增加高度到45

        // 添加鼠标悬停效果
        btnRegister.addListener(SWT.MouseEnter, e -> {
            btnRegister.setBackground(new Color(display, 85, 170, 85));
        });
        btnRegister.addListener(SWT.MouseExit, e -> {
            btnRegister.setBackground(accentColor);
        });

        // 添加注册事件
        btnRegister.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                registerAction();
            }
        });

        // 底部信息 - 增加高度
        Label footerLabel = new Label(shell, SWT.CENTER);
        footerLabel.setText("已有账号？请返回登录界面");
        footerLabel.setForeground(new Color(display, 153, 153, 153));
        footerLabel.setFont(new Font(display, "微软雅黑", 10, SWT.NORMAL)); // 调整字体大小
        footerLabel.setBackground(bgColor);
        footerLabel.setBounds(0, 500, 650, 30); // 增加高度到30

        // 监听窗口关闭，释放资源
        shell.addDisposeListener(e -> disposeResources());
    }

    // 释放颜色和字体资源
    private void disposeResources() {
        if (primaryColor != null) primaryColor.dispose();
        if (secondaryColor != null) secondaryColor.dispose();
        if (accentColor != null) accentColor.dispose();
        if (bgColor != null) bgColor.dispose();
        if (textFieldBgColor != null) textFieldBgColor.dispose();
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