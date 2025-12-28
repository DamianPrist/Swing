package MainInterface;

import Entity.User;
import MainInterface.ScoreManagePackage.ShowScoreManage_MultiSubject;
import MainInterface.StudentManagePackage.ShowStudentManage;
import MainInterface.TotalGradeManagePackage.ShowTotalGrade;
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
import MainInterface.TeacherManagePackage.TeacherManage;

/**
 * 系统主界面
 * @author SUNRISE / Gemini / echo-escape
 */
public class MainInterface {

    protected Shell shell;
    private User currentUser;

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

    /**
     * 打开主界面
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
     * 创建界面内容 - 固定大小窗口
     */
    protected void createContents() {
        // 创建固定大小的窗口（移除SWT.RESIZE）
        shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN);
        // 设置更大的固定尺寸
        shell.setSize(1400, 900); // 进一步增大窗口尺寸
        shell.setText("学生成绩管理系统");

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

        // 设置主窗口布局 - 固定布局
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
     * 创建头部区域 - 修复标题显示问题
     */
    private void createHeaderArea() {
        Composite headerComposite = new Composite(shell, SWT.NONE);
        headerComposite.setBackground(primaryColor);
        GridData headerData = new GridData(SWT.FILL, SWT.TOP, true, false);
        headerData.heightHint = 140; // 增加头部高度，确保标题完整显示
        headerComposite.setLayoutData(headerData);

        // 使用GridLayout替代FillLayout，提供更好的控制
        GridLayout headerLayout = new GridLayout(1, true);
        headerLayout.marginHeight = 20;
        headerLayout.marginWidth = 0;
        headerLayout.verticalSpacing = 0;
        headerComposite.setLayout(headerLayout);

        // 系统标题 - 使用GridData确保居中和对齐
        Label titleLabel = new Label(headerComposite, SWT.CENTER);
        titleLabel.setText("学生成绩管理系统");
        titleLabel.setForeground(new Color(Display.getCurrent(), 255, 255, 255));
        Font titleFont = new Font(Display.getCurrent(), "微软雅黑", 26, SWT.BOLD); // 增大字体
        titleLabel.setFont(titleFont);
        titleLabel.setBackground(primaryColor);

        // 使用GridData确保标题正确居中并占用可用空间
        GridData titleData = new GridData(SWT.CENTER, SWT.CENTER, true, true);
        titleLabel.setLayoutData(titleData);

        titleLabel.addDisposeListener(e -> titleFont.dispose());
    }

    /**
     * 创建主体区域（侧边栏+内容区域）
     */
    private void createMainArea() {
        Composite mainComposite = new Composite(shell, SWT.NONE);
        mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        mainComposite.setBackground(bgColor);

        // 主体区域使用2列网格布局
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
     * 创建侧边栏
     */
    private void createSidebar(Composite parent) {
        Composite sidebarComposite = new Composite(parent, SWT.NONE);
        sidebarComposite.setBackground(sidebarColor);
        GridData sidebarData = new GridData(SWT.LEFT, SWT.FILL, false, true);
        sidebarData.widthHint = 280; // 增加侧边栏宽度
        sidebarComposite.setLayoutData(sidebarData);

        // 侧边栏使用网格布局
        GridLayout sidebarLayout = new GridLayout(1, false);
        sidebarLayout.marginWidth = 20;
        sidebarLayout.marginHeight = 50;
        sidebarLayout.verticalSpacing = 25;
        sidebarComposite.setLayout(sidebarLayout);

        // 创建侧边栏按钮
        createSidebarButtons(sidebarComposite);
    }

    /**
     * 创建侧边栏按钮
     */
    private void createSidebarButtons(Composite parent) {
        // 学生管理按钮
        Button btnStudentManage = createSidebarButton(parent, "学生管理", primaryColor);
        btnStudentManage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showStudentManagement();
            }
        });

        // 成绩管理按钮
        Button btnScoreManage = createSidebarButton(parent, "成绩管理", accentColor);
        btnScoreManage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showScoreManagement();
            }
        });

        // 学生总成绩按钮
        Button btnTotalScore = createSidebarButton(parent, "学生总成绩", new Color(Display.getCurrent(), 255, 149, 0));
        btnTotalScore.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showTotalScore();
            }
        });

        // ========== 新增：更改密码按钮 ==========
        Button btnChangePwd = createSidebarButton(parent, "更改密码", new Color(Display.getCurrent(), 156, 39, 176));
        btnChangePwd.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleChangePassword(); // 处理更改密码逻辑
            }
        });

        // 添加空白区域使退出按钮在底部
        Label spacer = new Label(parent, SWT.NONE);
        spacer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        spacer.setBackground(sidebarColor);

        // 退出登录按钮
        Button btnLogout = createSidebarButton(parent, "退出登录", dangerColor);
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
        button.setFont(new Font(Display.getCurrent(), "微软雅黑", 14, SWT.BOLD)); // 增大字体
        button.setBackground(bgColor);
        button.setForeground(new Color(Display.getCurrent(), 255, 255, 255));

        GridData buttonData = new GridData(SWT.FILL, SWT.TOP, true, false);
        buttonData.heightHint = 70; // 增加按钮高度
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

        // 内容区域使用网格布局
        GridLayout contentLayout = new GridLayout(1, false);
        contentLayout.marginWidth = 50;
        contentLayout.marginHeight = 50;
        contentComposite.setLayout(contentLayout);

        // 内容区域标题
        contentTitle = new Label(contentComposite, SWT.CENTER);
        contentTitle.setText("欢迎使用学生成绩管理系统");
        contentTitle.setForeground(primaryColor);
        Font contentTitleFont = new Font(Display.getCurrent(), "微软雅黑", 20, SWT.BOLD); // 增大字体
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
        footerLabel.setText("© 2025 学生成绩管理系统");
        footerLabel.setForeground(new Color(Display.getCurrent(), 153, 153, 153));
        footerLabel.setFont(new Font(Display.getCurrent(), "微软雅黑", 13, SWT.NORMAL)); // 增大字体
        footerLabel.setBackground(bgColor);
        GridData footerData = new GridData(SWT.FILL, SWT.BOTTOM, true, false);
        footerData.heightHint = 50; // 增加底部高度
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
        welcomeLabel.setText("欢迎使用学生成绩管理系统！\n\n请从左侧菜单选择您要操作的功能：\n\n• 学生管理 - 管理学生基本信息\n• 成绩管理 - 录入和修改学生成绩\n• 学生总成绩 - 查看学生成绩统计");
        welcomeLabel.setFont(new Font(contentArea.getDisplay(), "微软雅黑", 18, SWT.NORMAL)); // 增大字体
        welcomeLabel.setBackground(contentBgColor);

        contentArea.layout();
    }
    
    /**
     * 显示学生管理界面
     */
    private void showStudentManagement() {
        contentTitle.setText("学生管理");

        // 清除内容区域
        for (Control control : contentArea.getChildren()) {
            control.dispose();
        }

        // 创建学生管理界面
        new ShowStudentManage(contentArea);

        contentArea.layout();
    }

    /**
     * 显示成绩管理界面
     */
    private void showScoreManagement() {
        contentTitle.setText("成绩管理");

        // 清除内容区域
        for (Control control : contentArea.getChildren()) {
            control.dispose();
        }
        new ShowScoreManage_MultiSubject(contentArea);

        contentArea.layout();
    }

    /**
     * 显示学生总成绩界面
     */
    private void showTotalScore() {
        contentTitle.setText("学生总成绩");

        // 清除内容区域
        for (Control control : contentArea.getChildren()) {
            control.dispose();
        }
        new ShowTotalGrade(contentArea);
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
     * 设置当前用户
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
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
     * 处理更改密码逻辑（弹出自定义弹窗，完成密码修改）
     */
    private void handleChangePassword() {
        // 1. 校验当前用户是否存在（登录后应非空，做容错处理）
        if (currentUser == null) {
            MessageBox tipBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
            tipBox.setText("提示");
            tipBox.setMessage("当前未获取到用户信息，无法修改密码！");
            tipBox.open();
            return;
        }

        // 2. 创建更改密码弹窗（模态窗口，阻塞主界面操作）
        Shell pwdShell = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.CLOSE);
        pwdShell.setText("修改登录密码");
        pwdShell.setSize(450, 350);
        pwdShell.setBackground(bgColor);

        // 弹窗居中显示（与主界面居中逻辑一致）
        Display display = Display.getCurrent();
        Rectangle screenSize = display.getPrimaryMonitor().getBounds();
        Rectangle shellSize = pwdShell.getBounds();
        pwdShell.setLocation(
                (screenSize.width - shellSize.width) / 2,
                (screenSize.height - shellSize.height) / 2
        );

        // 3. 弹窗布局设置（GridLayout，4行2列）
        GridLayout pwdLayout = new GridLayout(2, false);
        pwdLayout.marginWidth = 50;
        pwdLayout.marginHeight = 40;
        pwdLayout.verticalSpacing = 25;
        pwdLayout.horizontalSpacing = 20;
        pwdShell.setLayout(pwdLayout);

        // 4. 创建弹窗输入项和标签
        // 4.1 原密码
        Label lblOldPwd = new Label(pwdShell, SWT.NONE);
        lblOldPwd.setText("原密码：");
        lblOldPwd.setFont(new Font(display, "微软雅黑", 14, SWT.NORMAL));
        lblOldPwd.setBackground(bgColor);

        Text txtOldPwd = new Text(pwdShell, SWT.BORDER | SWT.PASSWORD);
        txtOldPwd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtOldPwd.setFont(new Font(display, "微软雅黑", 14, SWT.NORMAL));
        txtOldPwd.setForeground(primaryColor);

        // 4.2 新密码
        Label lblNewPwd = new Label(pwdShell, SWT.NONE);
        lblNewPwd.setText("新密码：");
        lblNewPwd.setFont(new Font(display, "微软雅黑", 14, SWT.NORMAL));
        lblNewPwd.setBackground(bgColor);

        Text txtNewPwd = new Text(pwdShell, SWT.BORDER | SWT.PASSWORD);
        txtNewPwd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtNewPwd.setFont(new Font(display, "微软雅黑", 14, SWT.NORMAL));
        txtNewPwd.setForeground(primaryColor);

        // 4.3 确认新密码
        Label lblConfirmPwd = new Label(pwdShell, SWT.NONE);
        lblConfirmPwd.setText("确认新密码：");
        lblConfirmPwd.setFont(new Font(display, "微软雅黑", 14, SWT.NORMAL));
        lblConfirmPwd.setBackground(bgColor);

        Text txtConfirmPwd = new Text(pwdShell, SWT.BORDER | SWT.PASSWORD);
        txtConfirmPwd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        txtConfirmPwd.setFont(new Font(display, "微软雅黑", 14, SWT.NORMAL));
        txtConfirmPwd.setForeground(primaryColor);

        // 4.4 操作按钮（确认/取消）
        Composite btnComposite = new Composite(pwdShell, SWT.NONE);
        btnComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false, 2, 1));
        btnComposite.setBackground(bgColor);
        GridLayout btnLayout = new GridLayout(2, false);
        btnLayout.horizontalSpacing = 30;
        btnComposite.setLayout(btnLayout);

        Button btnConfirm = new Button(btnComposite, SWT.PUSH);
        btnConfirm.setText("确认修改");
        btnConfirm.setBackground(accentColor);
        btnConfirm.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
        btnConfirm.setFont(new Font(display, "微软雅黑", 14, SWT.BOLD));
        GridData btnConfirmData = new GridData(SWT.FILL, SWT.FILL, false, false);
        btnConfirmData.widthHint = 120;
        btnConfirmData.heightHint = 40;
        btnConfirm.setLayoutData(btnConfirmData);

        Button btnCancel = new Button(btnComposite, SWT.PUSH);
        btnCancel.setText("取消");
        btnCancel.setBackground(secondaryColor);
        btnCancel.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
        btnCancel.setFont(new Font(display, "微软雅黑", 14, SWT.BOLD));
        GridData btnCancelData = new GridData(SWT.FILL, SWT.FILL, false, false);
        btnCancelData.widthHint = 120;
        btnCancelData.heightHint = 40;
        btnCancel.setLayoutData(btnCancelData);

        // 5. 确认按钮点击事件（核心校验逻辑）
        btnConfirm.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // 5.1 获取输入值并去除首尾空格
                String oldPwd = txtOldPwd.getText().trim();
                String newPwd = txtNewPwd.getText().trim();
                String confirmPwd = txtConfirmPwd.getText().trim();

                // 5.2 输入非空校验
                if (oldPwd.isEmpty() || newPwd.isEmpty() || confirmPwd.isEmpty()) {
                    MessageBox errorBox = new MessageBox(pwdShell, SWT.ICON_ERROR | SWT.OK);
                    errorBox.setText("输入错误");
                    errorBox.setMessage("所有密码输入项均不能为空，请补充填写！");
                    errorBox.open();
                    return;
                }

                // 5.3 原密码正确性校验（需确保User类有getPassword()方法）
                if (!oldPwd.equals(currentUser.getPassword())) {
                    MessageBox errorBox = new MessageBox(pwdShell, SWT.ICON_ERROR | SWT.OK);
                    errorBox.setText("验证失败");
                    errorBox.setMessage("原密码输入错误，请重新输入！");
                    errorBox.open();
                    txtOldPwd.setText(""); // 清空原密码输入框
                    txtOldPwd.setFocus(); // 聚焦原密码输入框
                    return;
                }

                // 5.4 新密码长度校验（建议6-16位）
                if (newPwd.length() < 6 || newPwd.length() > 16) {
                    MessageBox errorBox = new MessageBox(pwdShell, SWT.ICON_ERROR | SWT.OK);
                    errorBox.setText("格式错误");
                    errorBox.setMessage("新密码长度需在6-16位之间，请调整！");
                    errorBox.open();
                    txtNewPwd.setText(""); // 清空新密码输入框
                    txtConfirmPwd.setText(""); // 清空确认密码输入框
                    txtNewPwd.setFocus(); // 聚焦新密码输入框
                    return; // 修复原方法遗漏的return，避免后续逻辑继续执行
                }

                // 5.5 新密码与确认密码一致性校验
                if (!newPwd.equals(confirmPwd)) {
                    MessageBox errorBox = new MessageBox(pwdShell, SWT.ICON_ERROR | SWT.OK);
                    errorBox.setText("验证失败");
                    errorBox.setMessage("新密码与确认密码不一致，请重新输入！");
                    errorBox.open();
                    txtNewPwd.setText(""); // 清空新密码输入框
                    txtConfirmPwd.setText(""); // 清空确认密码输入框
                    txtNewPwd.setFocus(); // 聚焦新密码输入框
                    return;
                }

                // 5.6 密码修改：先更新内存，再通过服务类完成数据库持久化（解耦DAO层）
                currentUser.setPassword(newPwd); // 需确保User类有setPassword()方法

                // 实例化密码服务类，调用持久化方法（需导入Service.UserPasswordService）
                TeacherManage passwordService = new TeacherManage();
                boolean persistSuccess = passwordService.updateUserPassword(currentUser.getUsername(), newPwd);

                // 5.7 根据持久化结果给出友好反馈
                MessageBox resultBox;
                if (persistSuccess) {
                    resultBox = new MessageBox(pwdShell, SWT.ICON_INFORMATION | SWT.OK);
                    resultBox.setText("修改成功");
                    resultBox.setMessage("密码修改成功！请使用新密码下次登录。");
                } else {
                    resultBox = new MessageBox(pwdShell, SWT.ICON_WARNING | SWT.OK);
                    resultBox.setText("修改警告");
                    resultBox.setMessage("内存中密码已更新，但数据库持久化失败！请稍后重试。");
                }
                resultBox.open();
                pwdShell.dispose(); // 关闭密码修改弹窗
            }
        });

        // 6. 取消按钮点击事件（关闭弹窗）
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                pwdShell.dispose(); // 关闭密码修改弹窗
            }
        });

        // 7. 弹窗资源释放（字体、颜色）- 修复原方法无效的Color释放
        pwdShell.addDisposeListener(e -> {
            lblOldPwd.getFont().dispose();
            txtOldPwd.getFont().dispose();
            lblNewPwd.getFont().dispose();
            txtNewPwd.getFont().dispose();
            lblConfirmPwd.getFont().dispose();
            txtConfirmPwd.getFont().dispose();
            btnConfirm.getFont().dispose();
            btnCancel.getFont().dispose();
            // 移除原无效代码：new Color(display, 156, 39, 176).dispose();
            // 该颜色是侧边栏按钮的，已在全局资源释放中处理，弹窗中无需重复创建和释放
        });

        // 8. 打开弹窗并布局
        pwdShell.open();
        pwdShell.layout();
        while (!pwdShell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }


}






