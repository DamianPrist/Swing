package login;

import DAO.UserDAO;
import DAO.StudentDAO;
import Entity.User;
import Entity.Student;
import MainInterface.MainInterface;
import MainInterface.StudentInterfacePackage.StudentInterface;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * SWT login 界面
 */
public class Login {

    protected Shell shell;
    private Text textUser;
    private Text textPassword;
    private Combo userTypeCombo;
    private Label labelUser; // 新增：保存对用户标签的引用
    private Font fontInput;

    // 用户数据访问对象
    private UserDAO userDAO = new UserDAO();
    private StudentDAO studentDAO = new StudentDAO();
    private User currentUser;
    private Student currentStudent;

    // 美化用的颜色资源
    private Color primaryColor;
    private Color secondaryColor;
    private Color accentColor;
    private Color bgColor;
    private Color textFieldBgColor;

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
        shell.setSize(650, 550);
        shell.setText("用户登录");

        // 初始化颜色方案
        Display display = Display.getCurrent();
        primaryColor = new Color(display, 74, 144, 226);
        secondaryColor = new Color(display, 250, 250, 252);
        accentColor = new Color(display, 102, 187, 106);
        bgColor = new Color(display, 248, 250, 252);
        textFieldBgColor = new Color(display, 255, 255, 255);

        shell.setBackground(bgColor);

        // 使用GridLayout
        GridLayout shellLayout = new GridLayout(1, false);
        shellLayout.marginWidth = 0;
        shellLayout.marginHeight = 0;
        shell.setLayout(shellLayout);

        // 监听器用于释放资源
        shell.addDisposeListener(e -> {
            primaryColor.dispose();
            secondaryColor.dispose();
            accentColor.dispose();
            bgColor.dispose();
            textFieldBgColor.dispose();
        });

        // 创建渐变背景的Composite
        Composite headerComposite = new Composite(shell, SWT.NONE);
        headerComposite.setBackground(primaryColor);
        GridData headerData = new GridData(SWT.FILL, SWT.TOP, true, false);
        headerData.heightHint = 120;
        headerComposite.setLayoutData(headerData);

        GridLayout headerLayout = new GridLayout(1, true);
        headerLayout.marginHeight = 20;
        headerLayout.marginWidth = 0;
        headerLayout.verticalSpacing = 0;
        headerComposite.setLayout(headerLayout);

        // 标题 Label
        Label labelTitle = new Label(headerComposite, SWT.CENTER);
        labelTitle.setText("欢迎登录学生成绩管理系统");
        labelTitle.setForeground(new Color(display, 255, 255, 255));
        Font fontTitle = new Font(display, "微软雅黑", 22, SWT.BOLD);
        labelTitle.setFont(fontTitle);
        labelTitle.setBackground(primaryColor);
        labelTitle.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));

        // 主内容区域
        Composite mainComposite = new Composite(shell, SWT.NONE);
        mainComposite.setBackground(bgColor);
        GridData mainData = new GridData(SWT.CENTER, SWT.CENTER, true, true);
        mainData.widthHint = 550;
        mainData.heightHint = 350;
        mainComposite.setLayoutData(mainData);

        GridLayout mainLayout = new GridLayout(2, false);
        mainLayout.marginWidth = 20;
        mainLayout.marginHeight = 20;
        mainLayout.verticalSpacing = 20;
        mainLayout.horizontalSpacing = 15;
        mainComposite.setLayout(mainLayout);

        // 用户类型选择
        Label labelType = new Label(mainComposite, SWT.NONE);
        labelType.setText("登录身份：");
        labelType.setBackground(bgColor);
        Font fontLabel = new Font(display, "微软雅黑", 13, SWT.NORMAL);
        labelType.setFont(fontLabel);
        labelType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        // 用户类型选择框
        userTypeCombo = new Combo(mainComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        userTypeCombo.setItems(new String[]{"学生", "教师"});
        userTypeCombo.select(0);
        userTypeCombo.setFont(new Font(display, "微软雅黑", 12, SWT.NORMAL));
        userTypeCombo.setBackground(textFieldBgColor);
        GridData comboData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboData.widthHint = 300;
        comboData.heightHint = 40;
        userTypeCombo.setLayoutData(comboData);

        // 账户标签 - 动态变化 (保存引用)
        labelUser = new Label(mainComposite, SWT.NONE);
        labelUser.setText("学 号：");
        labelUser.setBackground(bgColor);
        labelUser.setFont(fontLabel);
        labelUser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        // 用户名/学号输入框
        textUser = new Text(mainComposite, SWT.BORDER | SWT.SINGLE);
        textUser.setFont(new Font(display, "微软雅黑", 12, SWT.NORMAL));
        textUser.setBackground(textFieldBgColor);
        textUser.setMessage("请输入学号");
        GridData userData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        userData.widthHint = 300;
        userData.heightHint = 40;
        textUser.setLayoutData(userData);

        // 密码标签
        Label labelPwd = new Label(mainComposite, SWT.NONE);
        labelPwd.setText("密码：");
        labelPwd.setBackground(bgColor);
        labelPwd.setFont(fontLabel);
        labelPwd.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        // 密码输入框
        textPassword = new Text(mainComposite, SWT.BORDER | SWT.PASSWORD | SWT.SINGLE);
        textPassword.setFont(new Font(display, "微软雅黑", 12, SWT.NORMAL));
        textPassword.setBackground(textFieldBgColor);
        GridData pwdData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        pwdData.widthHint = 300;
        pwdData.heightHint = 40;
        textPassword.setLayoutData(pwdData);

        // 按钮容器 - 现在只包含登录按钮
        Composite buttonComposite = new Composite(mainComposite, SWT.NONE);
        GridData buttonCompositeData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        buttonCompositeData.horizontalSpan = 2;
        buttonComposite.setLayoutData(buttonCompositeData);

        GridLayout buttonLayout = new GridLayout(1, true);
        buttonLayout.marginWidth = 0;
        buttonLayout.marginHeight = 0;
        buttonLayout.horizontalSpacing = 0;
        buttonComposite.setLayout(buttonLayout);

        // 登录按钮
        Button btnLogin = new Button(buttonComposite, SWT.PUSH);
        btnLogin.setText("登录");
        btnLogin.setFont(new Font(display, "微软雅黑", 13, SWT.BOLD));
        btnLogin.setBackground(primaryColor);
        btnLogin.setForeground(new Color(display, 255, 255, 255));
        GridData loginData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        loginData.widthHint = 140;
        loginData.heightHint = 45;
        btnLogin.setLayoutData(loginData);

        // 添加鼠标悬停效果
        btnLogin.addListener(SWT.MouseEnter, e -> {
            btnLogin.setBackground(new Color(display, 56, 124, 206));
        });
        btnLogin.addListener(SWT.MouseExit, e -> {
            btnLogin.setBackground(primaryColor);
        });

        // 登录逻辑
        btnLogin.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleLogin();
            }
        });

        // 底部信息
        Composite footerComposite = new Composite(shell, SWT.NONE);
        footerComposite.setBackground(bgColor);
        GridData footerData = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
        footerData.heightHint = 50;
        footerComposite.setLayoutData(footerData);

        GridLayout footerLayout = new GridLayout(1, false);
        footerLayout.marginWidth = 0;
        footerLayout.marginHeight = 0;
        footerComposite.setLayout(footerLayout);

        Label footerLabel = new Label(footerComposite, SWT.CENTER);
        footerLabel.setText("© 2025 学生成绩管理系统");
        footerLabel.setForeground(new Color(display, 153, 153, 153));
        footerLabel.setFont(new Font(display, "微软雅黑", 11, SWT.NORMAL));
        footerLabel.setBackground(bgColor);
        footerLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));

        // 添加用户类型变更事件
        userTypeCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateLoginFields();
            }
        });

        // 统一释放字体资源
        shell.addDisposeListener(e -> {
            fontLabel.dispose();
            fontTitle.dispose();
            if (fontInput != null) fontInput.dispose();
        });
    }

    /**
     * 更新登录字段的提示信息 - 修复版
     */
    private void updateLoginFields() {
        String userType = userTypeCombo.getText();

        // 直接使用保存的labelUser引用
        if (labelUser != null && !labelUser.isDisposed()) {
            if ("学生".equals(userType)) {
                labelUser.setText("学 号：");
                textUser.setMessage("请输入学号");
            } else {
                labelUser.setText("用户名：");
                textUser.setMessage("请输入用户名");
            }
        }

        textUser.setText("");
        textPassword.setText("");
    }

    /**
     * 处理登录事件
     */
    private void handleLogin() {
        String userType = userTypeCombo.getText();
        String username = textUser.getText().trim();
        String passwordStr = textPassword.getText();

        // 输入验证
        if (username.isEmpty() || passwordStr.isEmpty()) {
            showMessage("错误", "请输入用户名/学号和密码！", SWT.ICON_ERROR);
            return;
        }

        try {
            if ("学生".equals(userType)) {
                // 学生登录：使用学号和密码验证
                handleStudentLogin(username, passwordStr);
            } else {
                // 教师登录：使用用户名和密码验证
                handleTeacherLogin(username, passwordStr);
            }
        } catch (Exception e) {
            showMessage("错误", "登录过程中出现错误：" + e.getMessage(), SWT.ICON_ERROR);
        }
    }

    /**
     * 处理学生登录
     */
    private void handleStudentLogin(String studentId, String password) {
        // 使用StudentDAO实例验证学生登录
        Student student = studentDAO.validateStudentLogin(studentId, password);
        if (student != null) {
            currentStudent = student;
            showMessage("成功", "学生登录成功！", SWT.ICON_INFORMATION);

            // 关闭登录窗口
            shell.dispose();

            // 启动学生主界面
            StudentInterface studentInterface = new StudentInterface();
            studentInterface.setCurrentStudent(currentStudent);
            studentInterface.open();
        } else {
            showMessage("失败", "登录失败，学号或密码错误！", SWT.ICON_ERROR);
        }
    }

    /**
     * 处理教师登录 - 修改为String类型密码
     */
    private void handleTeacherLogin(String username, String password) {
        try {
            currentUser = userDAO.validateUser(username, password);
            if (currentUser != null) {
                showMessage("成功", "教师登录成功！", SWT.ICON_INFORMATION);

                // 关闭登录窗口
                shell.dispose();

                // 启动教师主界面
                MainInterface mainInterface = new MainInterface();
                mainInterface.setCurrentUser(currentUser);
                mainInterface.open();
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
     * 获取当前登录学生
     */
    public Student getCurrentStudent() {
        return currentStudent;
    }

    /**
     * 辅助方法：显示消息弹窗
     */
    private void showMessage(String title, String message, int iconStyle) {
        MessageBox messageBox = new MessageBox(shell, iconStyle | SWT.OK);
        messageBox.setText(title);
        messageBox.setMessage(message);
        messageBox.open();
    }
}