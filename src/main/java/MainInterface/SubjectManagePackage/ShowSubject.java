package MainInterface.SubjectManagePackage;

import Entity.Subject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.List;


/**
 * 学科交互页面类：封装新增/删除学科的SWT对话框，提供界面交互入口
 */
public class ShowSubject {
    // 依赖学科业务层，处理数据操作
    private SubjectService subjectService;

    // 构造方法：实例化业务层依赖
    public ShowSubject() {
        this.subjectService = new SubjectService();
    }

    // ====================== 核心方法1：显示新增学科对话框 ======================
    /**
     * 显示新增学科对话框
     * @param parentShell 父窗口（来自ShowScoreManage_MultiSubject，保证对话框居中）
     * @param subjectCombo 学科下拉框（操作后刷新，同步界面）
     */
    public void showAddSubjectDialog(Shell parentShell, Combo subjectCombo) {
        // 1. 校验父窗口有效性，避免控件已释放导致异常
        if (parentShell == null || parentShell.isDisposed()) {
            return;
        }
        Display display = parentShell.getDisplay();

        // 2. 创建模态对话框（与现有「添加成绩」对话框风格一致）
        Shell dialog = new Shell(parentShell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setText("新增学科");
        dialog.setSize(350, 220); // 适配输入内容，布局紧凑
        dialog.setLayout(new GridLayout(2, false)); // 2列布局，与现有对话框统一

        // 3. 对话框居中显示（复用现有界面的居中逻辑）
        dialog.setLocation(
                parentShell.getLocation().x + (parentShell.getSize().x - dialog.getSize().x) / 2,
                parentShell.getLocation().y + (parentShell.getSize().y - dialog.getSize().y) / 2
        );

        // 4. 对话框内容：学科名称输入项
        // 4.1 学科名称标签
        Label subjectNameLabel = new Label(dialog, SWT.NONE);
        subjectNameLabel.setText("学科名称：");
        subjectNameLabel.setFont(new Font(display, "微软雅黑", 11, SWT.NORMAL));
        GridData labelData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        subjectNameLabel.setLayoutData(labelData);

        // 4.2 学科名称输入框（带提示文字，与搜索框风格一致）
        Text subjectNameText = new Text(dialog, SWT.BORDER);
        subjectNameText.setFont(new Font(display, "微软雅黑", 11, SWT.NORMAL));
        subjectNameText.setMessage("输入学科名称（如：语文、英语）");
        GridData textData = new GridData(SWT.FILL, SWT.CENTER, true, false);
        textData.widthHint = 200;
        subjectNameText.setLayoutData(textData);

        // 5. 按钮区域（跨2列，与现有对话框按钮布局一致）
        Composite buttonComposite = new Composite(dialog, SWT.NONE);
        GridData buttonCompositeData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        buttonCompositeData.horizontalSpan = 2;
        buttonComposite.setLayoutData(buttonCompositeData);
        buttonComposite.setLayout(new GridLayout(2, true));
        //buttonComposite.setSpacing(20); // 按钮间距与现有界面一致

        // 5.1 确定按钮（样式：绿色系，与「添加成绩」按钮统一）
        Button okButton = new Button(buttonComposite, SWT.PUSH);
        okButton.setText("确定");
        okButton.setFont(new Font(display, "微软雅黑", 11, SWT.BOLD));
        okButton.setBackground(new Color(display, 102, 187, 106));
        okButton.setForeground(new Color(display, 255, 255, 255));
        GridData okBtnData = new GridData(80, 35);
        okButton.setLayoutData(okBtnData);

        // 5.2 取消按钮（默认样式，与现有对话框一致）
        Button cancelButton = new Button(buttonComposite, SWT.PUSH);
        cancelButton.setText("取消");
        cancelButton.setFont(new Font(display, "微软雅黑", 11, SWT.NORMAL));
        GridData cancelBtnData = new GridData(80, 35);
        cancelButton.setLayoutData(cancelBtnData);

        // 6. 确定按钮点击事件（输入验证→业务调用→界面刷新）
        okButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String subjectName = subjectNameText.getText().trim();

                // 6.1 输入验证（非空+去重+无特殊字符）
                if (subjectName.isEmpty()) {
                    showWarningDialog(dialog, "输入不完整", "请填写有效的学科名称！");
                    return;
                }
                if (subjectName.contains("/") || subjectName.contains("\\") || subjectName.contains("*") || subjectName.contains("?")) {
                    showWarningDialog(dialog, "输入非法", "学科名称不可包含/、\\、*、?等特殊字符！");
                    return;
                }
                if (subjectService.isSubjectExists(subjectName)) {
                    showWarningDialog(dialog, "学科已存在", "该学科名称已创建，请勿重复添加！");
                    return;
                }

                // 6.2 调用业务层，新增学科
                boolean success = subjectService.addSubject(subjectName);

                // 6.3 结果反馈+界面刷新
                if (success) {
                    showInfoDialog(dialog, "新增成功", "学科「" + subjectName + "」创建成功！");
                    dialog.close();
                    // 刷新下拉框，同步ShowScoreManage_MultiSubject的界面
                    refreshSubjectCombo(subjectCombo, display);
                } else {
                    showErrorDialog(dialog, "新增失败", "学科创建失败，请稍后重试！");
                }
            }
        });

        // 7. 取消按钮点击事件（关闭对话框，无业务操作）
        cancelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dialog.close();
            }
        });

        // 8. 回车触发确定按钮（优化交互，与现有搜索功能一致）
        subjectNameText.addListener(SWT.DefaultSelection, e -> okButton.notifyListeners(SWT.Selection, new Event()));

        // 9. 打开对话框，阻塞父窗口（模态对话框特性）
        dialog.open();
    }

    // ====================== 核心方法2：显示删除学科确认对话框 ======================
    /**
     * 显示删除学科确认对话框（含级联删除警告，不可恢复）
     * @param parentShell 父窗口
     * @param subjectCombo 学科下拉框（操作后刷新）
     * @param currentSubject 当前选中的学科（来自ShowScoreManage_MultiSubject）
     */
    public void showDeleteSubjectConfirmDialog(Shell parentShell, Combo subjectCombo, String currentSubject) {
        // 1. 校验有效性
        if (parentShell == null || parentShell.isDisposed() || currentSubject == null || currentSubject.isEmpty()) {
            return;
        }
        Display display = parentShell.getDisplay();

        // 2. 弹出确认对话框，明确告知级联删除风险
        MessageBox confirmDialog = new MessageBox(parentShell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
        confirmDialog.setText("确认删除（不可恢复）");
        confirmDialog.setMessage("警告：删除学科「" + currentSubject + "」后，该学科的所有成绩记录也将被永久删除！\n\n是否确定继续删除？");
        int result = confirmDialog.open();

        // 3. 用户选择「否」，直接返回
        if (result != SWT.YES) {
            return;
        }

        // 4. 用户选择「是」，执行级联删除
        try {
            boolean success = subjectService.deleteSubject(currentSubject);

            // 5. 结果反馈+界面刷新
            if (success) {
                showInfoDialog(parentShell, "删除成功", "学科「" + currentSubject + "」及其所有成绩记录已删除！");
                // 刷新下拉框，同步界面
                refreshSubjectCombo(subjectCombo, display);
            } else {
                showErrorDialog(parentShell, "删除失败", "学科删除失败，可能是数据关联异常，请稍后重试！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorDialog(parentShell, "删除异常", "删除过程中出现错误：" + e.getMessage());
        }
    }

    // ====================== 辅助方法：提示框+下拉框刷新（复用性强） ======================
    /**
     * 辅助：显示警告提示框
     */
    private void showWarningDialog(Shell parent, String title, String message) {
        MessageBox warning = new MessageBox(parent, SWT.ICON_WARNING);
        warning.setText(title);
        warning.setMessage(message);
        warning.open();
    }

    /**
     * 辅助：显示信息提示框
     */
    private void showInfoDialog(Shell parent, String title, String message) {
        MessageBox info = new MessageBox(parent, SWT.ICON_INFORMATION);
        info.setText(title);
        info.setMessage(message);
        info.open();
    }

    /**
     * 辅助：显示错误提示框
     */
    private void showErrorDialog(Shell parent, String title, String message) {
        MessageBox error = new MessageBox(parent, SWT.ICON_ERROR);
        error.setText(title);
        error.setMessage(message);
        error.open();
    }

    /**
     * 核心辅助：刷新学科下拉框（同步ShowScoreManage_MultiSubject的界面）
     * @param subjectCombo 待刷新的下拉框
     * @param display 显示设备，用于查询最新学科列表
     */
    private void refreshSubjectCombo(Combo subjectCombo, Display display) {
        if (subjectCombo == null || subjectCombo.isDisposed()) {
            return;
        }

        // 1. 清空原有选项
        subjectCombo.removeAll();

        // 2. 获取最新学科列表
        List<Subject> allSubjects = subjectService.getAllSubjects();
        if (allSubjects == null || allSubjects.isEmpty()) {
            return;
        }

        // 3. 重新添加选项
        for (Subject subject : allSubjects) {
            subjectCombo.add(subject.getSubjectName());
        }

        // 4. 默认选中第一个学科（与现有界面初始化逻辑一致）
        subjectCombo.select(0);
        // 5. 刷新下拉框控件，保证界面更新
        subjectCombo.layout();
    }
}
