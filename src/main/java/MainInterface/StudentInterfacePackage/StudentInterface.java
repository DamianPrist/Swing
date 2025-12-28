package MainInterface.StudentInterfacePackage;

import Entity.Student;
import login.Login;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import MainInterface.StudentInterfacePackage.StudentService;
import org.eclipse.swt.widgets.MessageBox;

/**
 * 学生主界面
 */
public class StudentInterface {

    protected Shell shell;
    private Student currentStudent;

    // 颜色资源
    private Color primaryColor;
    private Color secondaryColor;
    private Color accentColor;
    private Color dangerColor;
    private Color bgColor;
    private Color sidebarColor;
    private Color contentBgColor;

    // 界面组件
    private Composite contentArea; // 右侧内容区域
    private Label contentTitle;    // 内容区域标题

    // 新增：密码修改服务类（解耦DAO层，与管理员端保持一致）
    private StudentService studentService = new StudentService();

    /**
     * 打开学生主界面
     */
    public void open() {
        Display display = Display.getDefault();
        createContents();

        // 窗口居中
        Rectangle screenSize = display.getPrimaryMonitor().getBounds();
        Rectangle shellSize = shell.getBounds();
        shell.setLocation(
                (screenSize.width - shellSize.width) / 2,
                (screenSize.height - shellSize.height) / 2
        );

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
        shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN);
        shell.setSize(1200, 800);
        shell.setText("学生成绩管理系统 - 学生端");

        // 初始化颜色方案
        Display display = Display.getCurrent();
        primaryColor = new Color(display, 74, 144, 226);
        secondaryColor = new Color(display, 250, 250, 252);
        accentColor = new Color(display, 102, 187, 106);
        dangerColor = new Color(display, 239, 83, 80);
        bgColor = new Color(display, 248, 250, 252);
        sidebarColor = new Color(display, 240, 242, 245);
        contentBgColor = new Color(display, 255, 255, 255);

        shell.setBackground(bgColor);

        // 设置主窗口布局
        GridLayout shellLayout = new GridLayout(1, false);
        shellLayout.marginWidth = 0;
        shellLayout.marginHeight = 0;
        shell.setLayout(shellLayout);

        // 资源释放监听器
        shell.addDisposeListener(e -> disposeResources());

        // 创建头部区域
        createHeaderArea();

        // 创建主体区域（侧边栏+内容区域）
        createMainArea();

        // 创建底部区域
        createFooterArea();
    }

    /**
     * 创建头部区域
     */
    private void createHeaderArea() {
        Composite headerComposite = new Composite(shell, SWT.NONE);
        headerComposite.setBackground(primaryColor);
        GridData headerData = new GridData(SWT.FILL, SWT.TOP, true, false);
        headerData.heightHint = 140;
        headerComposite.setLayoutData(headerData);

        GridLayout headerLayout = new GridLayout(1, true);
        headerLayout.marginHeight = 20;
        headerLayout.marginWidth = 0;
        headerLayout.verticalSpacing = 0;
        headerComposite.setLayout(headerLayout);

        // 系统标题
        Label titleLabel = new Label(headerComposite, SWT.CENTER);
        titleLabel.setText("学生成绩管理系统 - 学生端");
        titleLabel.setForeground(new Color(Display.getCurrent(), 255, 255, 255));
        Font titleFont = new Font(Display.getCurrent(), "微软雅黑", 24, SWT.BOLD);
        titleLabel.setFont(titleFont);
        titleLabel.setBackground(primaryColor);

        GridData titleData = new GridData(SWT.CENTER, SWT.CENTER, true, true);
        titleLabel.setLayoutData(titleData);

        // 显示当前登录学生信息
        Label studentInfoLabel = new Label(headerComposite, SWT.CENTER);
        if (currentStudent != null) {
            studentInfoLabel.setText("欢迎您：" + currentStudent.getStudentName() +
                    " (" + currentStudent.getStudentId() + ")");
        } else {
            studentInfoLabel.setText("欢迎您，学生用户");
        }
        studentInfoLabel.setForeground(new Color(Display.getCurrent(), 255, 255, 255));
        Font infoFont = new Font(Display.getCurrent(), "微软雅黑", 14, SWT.NORMAL);
        studentInfoLabel.setFont(infoFont);
        studentInfoLabel.setBackground(primaryColor);

        GridData infoData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        infoData.verticalIndent = 10;
        studentInfoLabel.setLayoutData(infoData);

        titleLabel.addDisposeListener(e -> {
            titleFont.dispose();
            infoFont.dispose();
        });
    }

    /**
     * 创建主体区域
     */
    private void createMainArea() {
        Composite mainComposite = new Composite(shell, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainComposite.setBackground(bgColor);

        GridLayout mainLayout = new GridLayout(2, false);
        mainLayout.marginWidth = 0;
        mainLayout.marginHeight = 0;
        mainLayout.horizontalSpacing = 0;
        mainComposite.setLayout(mainLayout);

        // 创建侧边栏
        createSidebar(mainComposite);

        // 创建内容区域
        createContentArea(mainComposite);
    }

    /**
     * 创建侧边栏 - 学生端只有查看成绩功能
     */
    private void createSidebar(Composite parent) {
        Composite sidebarComposite = new Composite(parent, SWT.NONE);
        sidebarComposite.setBackground(sidebarColor);
        GridData sidebarData = new GridData(SWT.LEFT, SWT.FILL, false, true);
        sidebarData.widthHint = 250;
        sidebarComposite.setLayoutData(sidebarData);

        GridLayout sidebarLayout = new GridLayout(1, false);
        sidebarLayout.marginWidth = 20;
        sidebarLayout.marginHeight = 50;
        sidebarLayout.verticalSpacing = 25;
        sidebarComposite.setLayout(sidebarLayout);

        // 查看我的成绩按钮
        Button btnMyGrade = createSidebarButton(sidebarComposite, "查看我的成绩", primaryColor);
        btnMyGrade.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showMyGrade();
            }
        });

        // 新增：修改密码按钮（样式与原有按钮一致，背景色用accentColor，协调美观）
        Button btnChangePwd = createSidebarButton(sidebarComposite, "修改登录密码", accentColor);
        btnChangePwd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showChangePasswordDialog();
            }
        });

        // 添加空白区域
        Label spacer = new Label(sidebarComposite, SWT.NONE);
        spacer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        spacer.setBackground(sidebarColor);

        // 退出登录按钮
        Button btnLogout = createSidebarButton(sidebarComposite, "退出登录", dangerColor);
        btnLogout.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleLogout();
            }
        });
    }

    /**
     * 创建侧边栏按钮
     */
    private Button createSidebarButton(Composite parent, String text, Color bgColor) {
        Button button = new Button(parent, SWT.PUSH);
        button.setText(text);
        button.setFont(new Font(Display.getCurrent(), "微软雅黑", 14, SWT.BOLD));
        button.setBackground(bgColor);
        button.setForeground(new Color(Display.getCurrent(), 255, 255, 255));

        GridData buttonData = new GridData(SWT.FILL, SWT.TOP, true, false);
        buttonData.heightHint = 70;
        button.setLayoutData(buttonData);

        // 添加鼠标悬停效果
        button.addListener(SWT.MouseEnter, e -> {
            Color hoverColor = darkenColor(bgColor, 0.85f);
            button.setBackground(hoverColor);
        });
        button.addListener(SWT.MouseExit, e -> {
            button.setBackground(bgColor);
        });

        return button;
    }

    /**
     * 创建内容区域
     */
    private void createContentArea(Composite parent) {
        Composite contentComposite = new Composite(parent, SWT.NONE);
        contentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        contentComposite.setBackground(contentBgColor);

        GridLayout contentLayout = new GridLayout(1, false);
        contentLayout.marginWidth = 50;
        contentLayout.marginHeight = 50;
        contentComposite.setLayout(contentLayout);

        // 内容区域标题
        contentTitle = new Label(contentComposite, SWT.CENTER);
        contentTitle.setText("欢迎使用学生成绩管理系统");
        contentTitle.setForeground(primaryColor);
        Font contentTitleFont = new Font(Display.getCurrent(), "微软雅黑", 20, SWT.BOLD);
        contentTitle.setFont(contentTitleFont);
        contentTitle.setBackground(contentBgColor);
        GridData titleData = new GridData(SWT.FILL, SWT.TOP, true, false);
        titleData.heightHint = 65;
        contentTitle.setLayoutData(titleData);
        contentTitle.addDisposeListener(e -> contentTitleFont.dispose());

        // 内容显示区域
        contentArea = new Composite(contentComposite, SWT.NONE);
        contentArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        contentArea.setBackground(contentBgColor);

        // 内容区域使用FillLayout
        FillLayout contentAreaLayout = new FillLayout(SWT.VERTICAL);
        contentAreaLayout.marginHeight = 30;
        contentArea.setLayout(contentAreaLayout);

        // 显示欢迎内容
        showWelcomeContent();
    }

    /**
     * 创建底部区域
     */
    private void createFooterArea() {
        Label footerLabel = new Label(shell, SWT.CENTER);
        footerLabel.setText("© 2025 学生成绩管理系统 - 学生端");
        footerLabel.setForeground(new Color(Display.getCurrent(), 153, 153, 153));
        footerLabel.setFont(new Font(Display.getCurrent(), "微软雅黑", 13, SWT.NORMAL));
        footerLabel.setBackground(bgColor);
        GridData footerData = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
        footerData.heightHint = 50;
        footerLabel.setLayoutData(footerData);
    }

    /**
     * 颜色变深效果
     */
    private Color darkenColor(Color color, float factor) {
        Display display = (Display) color.getDevice();
        int r = Math.max(0, (int)(color.getRed() * factor));
        int g = Math.max(0, (int)(color.getGreen() * factor));
        int b = Math.max(0, (int)(color.getBlue() * factor));
        return new Color(display, r, g, b);
    }

    /**
     * 显示欢迎内容
     */
    private void showWelcomeContent() {
        // 清除内容区域
        for (Control control : contentArea.getChildren()) {
            control.dispose();
        }

        // 创建欢迎标签
        Label welcomeLabel = new Label(contentArea, SWT.WRAP | SWT.CENTER);
        welcomeLabel.setText("欢迎使用学生成绩管理系统！\n\n" +
                "您当前以学生身份登录。\n\n" +
                "• 查看我的成绩 - 查看您的各科成绩和总成绩\n" +
                "• 退出登录 - 返回登录界面");
        welcomeLabel.setFont(new Font(contentArea.getDisplay(), "微软雅黑", 18, SWT.NORMAL));
        welcomeLabel.setBackground(contentBgColor);

        contentArea.layout();
    }

    /**
     * 显示我的成绩界面
     */
    private void showMyGrade() {
        contentTitle.setText("我的成绩");

        // 清除内容区域
        for (Control control : contentArea.getChildren()) {
            control.dispose();
        }

        // 创建学生成绩界面 - 使用多学科版本
        new ShowStudentGrade_MultiSubject(contentArea, currentStudent);

        contentArea.layout();
    }

    /**
     * 处理退出登录
     */
    private void handleLogout() {
        MessageBox confirmBox = new MessageBox(shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
        confirmBox.setText("确认退出");
        confirmBox.setMessage("确定要退出系统吗？");

        int result = confirmBox.open();
        if (result == SWT.YES) {
            shell.dispose();
            // 返回登录界面
            new Login().open();
        }
    }

    /**
     * 设置当前学生
     */
    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
    }

    /**
     * 释放资源
     */
    private void disposeResources() {
        if (primaryColor != null) primaryColor.dispose();
        if (secondaryColor != null) secondaryColor.dispose();
        if (accentColor != null) accentColor.dispose();
        if (dangerColor != null) dangerColor.dispose();
        if (bgColor != null) bgColor.dispose();
        if (sidebarColor != null) sidebarColor.dispose();
        if (contentBgColor != null) contentBgColor.dispose();
    }

    /**
     * 新增：学生端 - 显示修改登录密码弹窗（完全适配学生端界面风格和颜色方案）
     */
    private void showChangePasswordDialog() {
        // 1. 容错校验：当前学生未登录/信息为空
        if (currentStudent == null) {
            MessageBox tipBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
            tipBox.setText("提示");
            tipBox.setMessage("当前未获取到学生信息，无法修改密码！");
            tipBox.open();
            return;
        }

        // 2. 创建弹窗（模态窗口，匹配学生端窗口风格）
        Shell pwdDialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CLOSE);
        pwdDialog.setText("修改学生登录密码");
        pwdDialog.setSize(450, 350);
        pwdDialog.setBackground(bgColor);

        // 弹窗居中（复用学生端主窗口的居中逻辑）
        Display display = Display.getCurrent();
        Rectangle screenSize = display.getPrimaryMonitor().getBounds();
        Rectangle dialogSize = pwdDialog.getBounds();
        pwdDialog.setLocation(
                (screenSize.width - dialogSize.width) / 2,
                (screenSize.height - dialogSize.height) / 2
        );

        // 3. 弹窗布局（与学生端主界面布局逻辑一致）
        GridLayout pwdLayout = new GridLayout(2, false);
        pwdLayout.marginWidth = 50;
        pwdLayout.marginHeight = 40;
        pwdLayout.verticalSpacing = 25;
        pwdLayout.horizontalSpacing = 20;
        pwdDialog.setLayout(pwdLayout);

        // 4. 创建弹窗输入项（样式匹配学生端，使用已有颜色资源）
        // 4.1 原密码
        Label lblOldPwd = new Label(pwdDialog, SWT.NONE);
        lblOldPwd.setText("原密码：");
        lblOldPwd.setFont(new Font(display, "微软雅黑", 14, SWT.NORMAL));
        lblOldPwd.setBackground(bgColor);
        lblOldPwd.setForeground(primaryColor);

        Text txtOldPwd = new Text(pwdDialog, SWT.BORDER | SWT.PASSWORD);
        txtOldPwd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtOldPwd.setFont(new Font(display, "微软雅黑", 14, SWT.NORMAL));
        txtOldPwd.setForeground(primaryColor);
        txtOldPwd.setBackground(contentBgColor);

        // 4.2 新密码
        Label lblNewPwd = new Label(pwdDialog, SWT.NONE);
        lblNewPwd.setText("新密码：");
        lblNewPwd.setFont(new Font(display, "微软雅黑", 14, SWT.NORMAL));
        lblNewPwd.setBackground(bgColor);
        lblNewPwd.setForeground(primaryColor);

        Text txtNewPwd = new Text(pwdDialog, SWT.BORDER | SWT.PASSWORD);
        txtNewPwd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtNewPwd.setFont(new Font(display, "微软雅黑", 14, SWT.NORMAL));
        txtNewPwd.setForeground(primaryColor);
        txtNewPwd.setBackground(contentBgColor);

        // 4.3 确认新密码
        Label lblConfirmPwd = new Label(pwdDialog, SWT.NONE);
        lblConfirmPwd.setText("确认新密码：");
        lblConfirmPwd.setFont(new Font(display, "微软雅黑", 14, SWT.NORMAL));
        lblConfirmPwd.setBackground(bgColor);
        lblConfirmPwd.setForeground(primaryColor);

        Text txtConfirmPwd = new Text(pwdDialog, SWT.BORDER | SWT.PASSWORD);
        txtConfirmPwd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtConfirmPwd.setFont(new Font(display, "微软雅黑", 14, SWT.NORMAL));
        txtConfirmPwd.setForeground(primaryColor);
        txtConfirmPwd.setBackground(contentBgColor);

        // 4.4 操作按钮（匹配学生端按钮风格，使用已有颜色资源）
        Composite btnComposite = new Composite(pwdDialog, SWT.NONE);
        btnComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
        btnComposite.setBackground(bgColor);
        GridLayout btnLayout = new GridLayout(2, false);
        btnLayout.horizontalSpacing = 30;
        btnComposite.setLayout(btnLayout);

        // 确认修改按钮（使用accentColor，与学生端新增按钮同色）
        Button btnConfirm = new Button(btnComposite, SWT.PUSH);
        btnConfirm.setText("确认修改");
        btnConfirm.setBackground(accentColor);
        btnConfirm.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
        btnConfirm.setFont(new Font(display, "微软雅黑", 14, SWT.BOLD));
        GridData btnConfirmData = new GridData(SWT.FILL, SWT.FILL, false, false);
        btnConfirmData.widthHint = 120;
        btnConfirmData.heightHint = 40;
        btnConfirm.setLayoutData(btnConfirmData);

        // 取消按钮（使用secondaryColor，匹配学生端次要颜色）
        Button btnCancel = new Button(btnComposite, SWT.PUSH);
        btnCancel.setText("取消");
        btnCancel.setBackground(secondaryColor);
        btnCancel.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        btnCancel.setFont(new Font(display, "微软雅黑", 14, SWT.BOLD));
        GridData btnCancelData = new GridData(SWT.FILL, SWT.FILL, false, false);
        btnCancelData.widthHint = 120;
        btnCancelData.heightHint = 40;
        btnCancel.setLayoutData(btnCancelData);

        // 5. 确认按钮点击事件（核心校验+密码修改，适配学生端数据）
        btnConfirm.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // 5.1 获取输入值并去空格
                String oldPwd = txtOldPwd.getText().trim();
                String newPwd = txtNewPwd.getText().trim();
                String confirmPwd = txtConfirmPwd.getText().trim();

                // 5.2 非空校验
                if (oldPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
                    MessageBox errorBox = new MessageBox(pwdDialog, SWT.ICON_ERROR | SWT.OK);
                    errorBox.setText("输入错误");
                    errorBox.setMessage("所有密码输入项均不能为空，请补充填写！");
                    errorBox.open();
                    return;
                }

                // 5.3 原密码校验（学生端：校验currentStudent的原有密码）
                if (!oldPwd.equals(currentStudent.getPassword())) {
                    MessageBox errorBox = new MessageBox(pwdDialog, SWT.ICON_ERROR | SWT.OK);
                    errorBox.setText("验证失败");
                    errorBox.setMessage("原密码输入错误，请重新输入！");
                    errorBox.open();
                    txtOldPwd.setText("");
                    txtOldPwd.setFocus();
                    return;
                }

                // 5.4 新密码长度校验（6-16位，与管理员端保持一致）
                if (newPwd.length() < 6 || newPwd.length() > 16) {
                    MessageBox errorBox = new MessageBox(pwdDialog, SWT.ICON_ERROR | SWT.OK);
                    errorBox.setText("格式错误");
                    errorBox.setMessage("新密码长度需在6-16位之间，请调整！");
                    errorBox.open();
                    txtNewPwd.setText("");
                    txtConfirmPwd.setText("");
                    txtNewPwd.setFocus();
                    return;
                }

                // 5.5 新密码与确认密码一致性校验
                if (!newPwd.equals(confirmPwd)) {
                    MessageBox errorBox = new MessageBox(pwdDialog, SWT.ICON_ERROR | SWT.OK);
                    errorBox.setText("验证失败");
                    errorBox.setMessage("新密码与确认密码不一致，请重新输入！");
                    errorBox.open();
                    txtNewPwd.setText("");
                    txtConfirmPwd.setText("");
                    txtNewPwd.setFocus();
                    return;
                }

                // 5.6 密码修改（更新内存中学生密码+持久化，调用服务类解耦）
                currentStudent.setPassword(newPwd); // 更新学生对象密码
                // 持久化：以学生学号作为唯一标识（适配UserPasswordService，确保后台兼容）
                boolean persistSuccess = false;
                try {
                    persistSuccess = studentService.updateStudentPassword(currentStudent.getStudentId(), newPwd);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    persistSuccess = false;
                }
                // 5.7 结果反馈（匹配学生端弹窗风格）
                MessageBox resultBox;
                if (persistSuccess) {
                    resultBox = new MessageBox(pwdDialog, SWT.ICON_INFORMATION | SWT.OK);
                    resultBox.setText("修改成功");
                    resultBox.setMessage("密码修改成功！下次登录请使用新密码。");
                } else {
                    resultBox = new MessageBox(pwdDialog, SWT.ICON_WARNING | SWT.OK);
                    resultBox.setText("修改警告");
                    resultBox.setMessage("内存中密码已更新，数据库持久化失败！");
                }
                resultBox.open();
                pwdDialog.dispose(); // 关闭弹窗
            }
        });

        // 6. 取消按钮点击事件（关闭弹窗，无数据修改）
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                pwdDialog.dispose();
            }
        });

        // 7. 弹窗资源释放（避免内存泄露，匹配学生端资源释放逻辑）
        pwdDialog.addDisposeListener(e -> {
            // 释放字体资源
            lblOldPwd.getFont().dispose();
            txtOldPwd.getFont().dispose();
            lblNewPwd.getFont().dispose();
            txtNewPwd.getFont().dispose();
            lblConfirmPwd.getFont().dispose();
            txtConfirmPwd.getFont().dispose();
            btnConfirm.getFont().dispose();
            btnCancel.getFont().dispose();
        });

        // 8. 打开弹窗并布局
        pwdDialog.open();
        pwdDialog.layout();
        while (!pwdDialog.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

}