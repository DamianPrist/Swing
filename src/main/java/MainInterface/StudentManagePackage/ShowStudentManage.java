package MainInterface.StudentManagePackage;

import Entity.Student;
import Entity.User;
import MainInterface.TeacherManagePackage.TeacherManage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生管理前端界面类
 */
public class ShowStudentManage {

    private Composite parent;
    private Table studentTable;
    private StudentManage studentManage;
    private Text searchText;
    private List<TableEditor> tableEditors; // 保存所有的表格编辑器以便清理

    private User currentUser; // 当前登录用户（用于密码校验和持久化）
    private TeacherManage passwordService = new TeacherManage(); // 密码服务类（解耦DAO）

    // 分页相关变量
    private int currentPage = 1; // 当前页码
    private int pageSize = 10;   // 每页显示条数
    private int totalCount = 0;  // 总记录数
    private List<Student> allStudents = new ArrayList<>(); // 所有学生数据

    // 分页控件
    private Label pageInfoLabel;
    private Button prevPageButton;
    private Button nextPageButton;
    private Composite paginationComposite;


    public ShowStudentManage(Composite parent) {
        this.parent = parent;
        this.studentManage = new StudentManage();
        this.tableEditors = new ArrayList<>();
        createContent();
    }

    /**
     * 创建学生管理界面内容
     */
    private void createContent() {
        // 设置布局 - 修改为填充整个容器
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 10;
        layout.marginHeight = 10;
        layout.verticalSpacing = 10;
        parent.setLayout(layout);

        // 创建搜索区域
        createSearchArea();

        // 创建表格区域
        createTableArea();

        // 创建分页控件区域
        createPaginationArea();

        // 创建按钮区域
        createButtonArea();

        // 加载学生数据
        loadStudentData();
    }

    /**
     * 创建搜索区域 - 修改为居中显示
     */
    private void createSearchArea() {
        Composite searchComposite = new Composite(parent, SWT.NONE);
        GridData searchData = new GridData(SWT.CENTER, SWT.TOP, true, false);
        searchData.widthHint = 500; // 设置搜索区域宽度
        searchComposite.setLayoutData(searchData);

        GridLayout searchLayout = new GridLayout(3, false);
        searchLayout.marginWidth = 0;
        searchLayout.marginHeight = 0;
        searchLayout.horizontalSpacing = 10;
        searchComposite.setLayout(searchLayout);

        // 搜索标签
        Label searchLabel = new Label(searchComposite, SWT.NONE);
        searchLabel.setText("搜索：");
        searchLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 12, SWT.NORMAL));

        // 搜索输入框
        searchText = new Text(searchComposite, SWT.BORDER | SWT.SEARCH);
        GridData textData = new GridData(250, SWT.DEFAULT);
        searchText.setLayoutData(textData);
        searchText.setFont(new Font(parent.getDisplay(), "微软雅黑", 12, SWT.NORMAL));
        searchText.setMessage("请输入学号或姓名");

        // 搜索按钮 - 修复文字显示不完整
        Button searchButton = new Button(searchComposite, SWT.PUSH);
        searchButton.setText("搜索");
        searchButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 12, SWT.BOLD));
        searchButton.setBackground(new Color(parent.getDisplay(), 74, 144, 226));
        searchButton.setForeground(new Color(parent.getDisplay(), 255, 255, 255));

        // 修复：增加按钮高度，确保文字完整显示
        GridData buttonData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        buttonData.widthHint = 80;
        buttonData.heightHint = 35; // 增加高度确保文字完整显示
        searchButton.setLayoutData(buttonData);

        // 搜索按钮事件
        searchButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                performSearch();
            }
        });

        // 回车搜索
        searchText.addListener(SWT.DefaultSelection, e -> performSearch());
    }

    /**
     * 执行搜索
     */
    private void performSearch() {
        String keyword = searchText.getText().trim();

        if (keyword.isEmpty()) {
            allStudents = studentManage.getAllStudents();
        } else {
            allStudents = studentManage.searchStudents(keyword);
        }

        currentPage = 1; // 搜索后重置到第一页
        refreshTableWithPagination();
    }

    /**
     * 创建表格区域 - 修改为撑满整个可用空间
     */
    private void createTableArea() {
        // 创建表格容器 - 设置填充数据
        Composite tableComposite = new Composite(parent, SWT.NONE);
        GridData tableCompositeData = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableCompositeData.minimumHeight = 300; // 设置最小高度
        tableComposite.setLayoutData(tableCompositeData);

        GridLayout tableLayout = new GridLayout(1, false);
        tableLayout.marginWidth = 0;
        tableLayout.marginHeight = 0;
        tableComposite.setLayout(tableLayout);

        // 创建表格 - 设置填充数据
        studentTable = new Table(tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableData.minimumHeight = 250; // 设置表格最小高度
        studentTable.setLayoutData(tableData);
        studentTable.setHeaderVisible(true);
        studentTable.setLinesVisible(true);
        studentTable.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        // 创建列 - 修改列宽设置，让表格撑满整个宽度
        String[] columns = {"学号", "姓名", "性别", "班级", "联系方式", "操作"};
        // 计算总宽度，让最后一列自动填充剩余空间
        int[] columnWidths = {120, 100, 80, 200, 150, SWT.DEFAULT}; // 最后一列设置为自动填充

        for (int i = 0; i < columns.length; i++) {
            TableColumn column = new TableColumn(studentTable, SWT.CENTER);
            column.setText(columns[i]);
            if (i == columns.length - 1) {
                // 最后一列设置为可调整大小，自动填充剩余空间
                column.setResizable(true);
            } else {
                column.setWidth(columnWidths[i]);
            }
        }

        // 添加表格大小监听器，动态调整列宽
        studentTable.addListener(SWT.Resize, new Listener() {
            public void handleEvent(Event event) {
                adjustTableColumns();
            }
        });
    }

    /**
     * 创建分页控件区域
     */
    private void createPaginationArea() {
        paginationComposite = new Composite(parent, SWT.NONE);
        GridData paginationData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        paginationComposite.setLayoutData(paginationData);

        GridLayout paginationLayout = new GridLayout(5, false);
        paginationLayout.marginWidth = 0;
        paginationLayout.marginHeight = 5;
        paginationLayout.horizontalSpacing = 10;
        paginationComposite.setLayout(paginationLayout);

        // 上一页按钮
        prevPageButton = new Button(paginationComposite, SWT.PUSH);
        prevPageButton.setText("上一页");
        prevPageButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 10, SWT.NORMAL));
        prevPageButton.setEnabled(false);

        GridData prevData = new GridData(60, 25);
        prevPageButton.setLayoutData(prevData);

        // 页码信息
        pageInfoLabel = new Label(paginationComposite, SWT.CENTER);
        pageInfoLabel.setText("第 0 页 / 共 0 页 (共 0 条)");
        pageInfoLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 10, SWT.NORMAL));

        GridData labelData = new GridData(180, SWT.DEFAULT); // 增加宽度以显示更多信息
        pageInfoLabel.setLayoutData(labelData);

        // 下一页按钮
        nextPageButton = new Button(paginationComposite, SWT.PUSH);
        nextPageButton.setText("下一页");
        nextPageButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 10, SWT.NORMAL));
        nextPageButton.setEnabled(false);

        GridData nextData = new GridData(60, 25);
        nextPageButton.setLayoutData(nextData);

        // 每页显示条数选择
        Label pageSizeLabel = new Label(paginationComposite, SWT.NONE);
        pageSizeLabel.setText("每页显示：");
        pageSizeLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 10, SWT.NORMAL));

        Combo pageSizeCombo = new Combo(paginationComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        pageSizeCombo.setItems(new String[]{"10", "5", "7", "20"});
        pageSizeCombo.select(0); // 默认选择10条
        pageSizeCombo.setFont(new Font(parent.getDisplay(), "微软雅黑", 10, SWT.NORMAL));

        GridData comboData = new GridData(60, SWT.DEFAULT);
        pageSizeCombo.setLayoutData(comboData);

        // 按钮事件
        prevPageButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (currentPage > 1) {
                    currentPage--;
                    refreshTableWithPagination();
                }
            }
        });

        nextPageButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int totalPages = (int) Math.ceil((double) totalCount / pageSize);
                if (currentPage < totalPages) {
                    currentPage++;
                    refreshTableWithPagination();
                }
            }
        });

        pageSizeCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                pageSize = Integer.parseInt(pageSizeCombo.getText());
                currentPage = 1; // 切换每页条数后回到第一页
                refreshTableWithPagination();
            }
        });
    }

    /**
     * 调整表格列宽，确保填满整个表格宽度
     */
    private void adjustTableColumns() {
        if (studentTable.isDisposed()) {
            return;
        }

        // 获取表格客户区宽度
        int tableWidth = studentTable.getClientArea().width;

        // 计算前5列的固定宽度
        int fixedWidth = 120 + 100 + 80 + 200 + 150;

        // 计算最后一列的宽度（剩余空间）
        int lastColumnWidth = tableWidth - fixedWidth;

        // 确保最后一列有最小宽度
        if (lastColumnWidth < 150) {
            lastColumnWidth = 150;
        }

        // 设置最后一列宽度
        TableColumn lastColumn = studentTable.getColumn(5);
        if (lastColumn != null && !lastColumn.isDisposed()) {
            lastColumn.setWidth(lastColumnWidth);
        }
    }

    /**
     * 创建按钮区域
     */
    private void createButtonArea() {
        Composite buttonComposite = new Composite(parent, SWT.NONE);
        GridData buttonData = new GridData(SWT.CENTER, SWT.BOTTOM, false, false);
        buttonData.verticalIndent = 10;
        buttonComposite.setLayoutData(buttonData);

        GridLayout buttonLayout = new GridLayout(1, false);
        buttonLayout.marginWidth = 0;
        buttonLayout.marginHeight = 0;
        buttonComposite.setLayout(buttonLayout);

        // 添加学生按钮
        Button addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setText("添加学生");
        addButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 12, SWT.BOLD));
        addButton.setBackground(new Color(parent.getDisplay(), 102, 187, 106));
        addButton.setForeground(new Color(parent.getDisplay(), 255, 255, 255));

        GridData addButtonData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        addButtonData.widthHint = 120;
        addButtonData.heightHint = 40;
        addButton.setLayoutData(addButtonData);

        // 添加按钮事件
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showAddStudentDialog();
            }
        });
    }

    /**
     * 加载学生数据到表格
     */
    private void loadStudentData() {
        allStudents = studentManage.getAllStudents();
        refreshTableWithPagination();
    }

    /**
     * 带分页的刷新表格数据
     */
    private void refreshTableWithPagination() {
        if (allStudents == null) {
            allStudents = new ArrayList<>();
        }

        totalCount = allStudents.size();
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);

        // 确保当前页在有效范围内
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        } else if (totalPages == 0) {
            currentPage = 0;
        }

        // 更新分页信息
        updatePaginationInfo(totalPages);

        // 获取当前页的数据
        List<Student> currentPageStudents = new ArrayList<>();
        if (currentPage > 0) {
            int startIndex = (currentPage - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalCount);

            if (startIndex < totalCount) {
                currentPageStudents = allStudents.subList(startIndex, endIndex);
            }
        }

        // 刷新表格显示当前页数据
        refreshTable(currentPageStudents);
    }

    /**
     * 更新分页控件状态
     */
    private void updatePaginationInfo(int totalPages) {
        if (pageInfoLabel != null && !pageInfoLabel.isDisposed()) {
            if (totalCount == 0) {
                pageInfoLabel.setText("暂无数据");
            } else {
                pageInfoLabel.setText("第 " + currentPage + " 页 / 共 " + totalPages + " 页 (共 " + totalCount + " 条)");
            }
        }

        if (prevPageButton != null && !prevPageButton.isDisposed()) {
            prevPageButton.setEnabled(currentPage > 1);
        }

        if (nextPageButton != null && !nextPageButton.isDisposed()) {
            nextPageButton.setEnabled(currentPage < totalPages);
        }

        if (paginationComposite != null && !paginationComposite.isDisposed()) {
            paginationComposite.layout();
        }
    }

    /**
     * 刷新表格数据 - 修复版本
     */
    private void refreshTable(List<Student> students) {
        // 检查控件是否有效
        if (studentTable.isDisposed()) {
            return;
        }

        // 清理之前的所有TableEditor
        cleanupTableEditors();

        // 清空表格
        studentTable.removeAll();

        // 添加数据行
        for (Student student : students) {
            TableItem item = new TableItem(studentTable, SWT.NONE);
            item.setText(0, student.getStudentId());
            item.setText(1, student.getStudentName());
            item.setText(2, student.getGender());
            item.setText(3, student.getClassName());
            item.setText(4, student.getPhone());

            // 创建操作按钮容器
            Composite actionComposite = new Composite(studentTable, SWT.NONE);
            GridLayout actionLayout = new GridLayout(2, true);
            actionLayout.marginWidth = 2;
            actionLayout.marginHeight = 2;
            actionLayout.horizontalSpacing = 5;
            actionComposite.setLayout(actionLayout);

            // 编辑按钮
            Button editButton = new Button(actionComposite, SWT.PUSH);
            editButton.setText("编辑");
            editButton.setData("studentId", student.getStudentId());
            editButton.setBackground(new Color(parent.getDisplay(), 255, 193, 7));
            editButton.setForeground(new Color(parent.getDisplay(), 255, 255, 255));
            editButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 9, SWT.NORMAL));

            GridData editData = new GridData(SWT.FILL, SWT.CENTER, true, false);
            editData.widthHint = 60;
            editButton.setLayoutData(editData);

            // 删除按钮
            Button deleteButton = new Button(actionComposite, SWT.PUSH);
            deleteButton.setText("删除");
            deleteButton.setData("studentId", student.getStudentId());
            deleteButton.setBackground(new Color(parent.getDisplay(), 239, 83, 80));
            deleteButton.setForeground(new Color(parent.getDisplay(), 255, 255, 255));
            deleteButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 9, SWT.NORMAL));

            GridData deleteData = new GridData(SWT.FILL, SWT.CENTER, true, false);
            deleteData.widthHint = 60;
            deleteButton.setLayoutData(deleteData);

            // 使用 TableEditor 将按钮容器嵌入表格
            TableEditor editor = new TableEditor(studentTable);
            editor.grabHorizontal = true;
            editor.grabVertical = true;
            editor.setEditor(actionComposite, item, 5);

            // 保存编辑器引用以便后续清理
            tableEditors.add(editor);

            // 设置按钮事件
            editButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (!editButton.isDisposed()) {
                        String studentId = (String) editButton.getData("studentId");
                        showEditStudentDialog(studentId);
                    }
                }
            });

            deleteButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (!deleteButton.isDisposed()) {
                        String studentId = (String) deleteButton.getData("studentId");
                        showDeleteConfirmDialog(studentId);
                    }
                }
            });
        }

        // 刷新表格并调整列宽
        studentTable.layout();
        adjustTableColumns();
    }

    /**
     * 清理表格编辑器 - 修复isDisposed问题
     */
    private void cleanupTableEditors() {
        List<TableEditor> editorsToRemove = new ArrayList<>();

        for (TableEditor editor : tableEditors) {
            if (editor != null) {
                try {
                    // 先释放编辑器中的控件
                    Control control = editor.getEditor();
                    if (control != null && !control.isDisposed()) {
                        control.dispose();
                    }
                    // 然后释放编辑器本身
                    editor.dispose();
                } catch (Exception e) {
                    // 如果编辑器已经被释放，忽略异常
                }
            }
            editorsToRemove.add(editor);
        }

        tableEditors.removeAll(editorsToRemove);
    }

    /**
     * 显示添加学生对话框
     */
    private void showAddStudentDialog() {
        // 检查父控件是否有效
        if (parent.isDisposed() || parent.getShell() == null || parent.getShell().isDisposed()) {
            return;
        }

        Shell shell = parent.getShell();
        Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setText("添加学生");
        dialog.setSize(400, 350);
        dialog.setLayout(new GridLayout(2, false));

        // 居中显示
        dialog.setLocation(
                shell.getLocation().x + (shell.getSize().x - dialog.getSize().x) / 2,
                shell.getLocation().y + (shell.getSize().y - dialog.getSize().y) / 2
        );

        // 学号
        Label idLabel = new Label(dialog, SWT.NONE);
        idLabel.setText("学号：");
        idLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Text idText = new Text(dialog, SWT.BORDER);
        idText.setLayoutData(new GridData(200, SWT.DEFAULT));
        idText.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        // 姓名
        Label nameLabel = new Label(dialog, SWT.NONE);
        nameLabel.setText("姓名：");
        nameLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Text nameText = new Text(dialog, SWT.BORDER);
        nameText.setLayoutData(new GridData(200, SWT.DEFAULT));
        nameText.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        // 性别
        Label genderLabel = new Label(dialog, SWT.NONE);
        genderLabel.setText("性别：");
        genderLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Combo genderCombo = new Combo(dialog, SWT.DROP_DOWN | SWT.READ_ONLY);
        genderCombo.setItems(new String[]{"男", "女"});
        genderCombo.select(0);
        genderCombo.setLayoutData(new GridData(200, SWT.DEFAULT));
        genderCombo.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        // 班级
        Label classLabel = new Label(dialog, SWT.NONE);
        classLabel.setText("班级：");
        classLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Text classText = new Text(dialog, SWT.BORDER);
        classText.setLayoutData(new GridData(200, SWT.DEFAULT));
        classText.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        // 联系方式
        Label phoneLabel = new Label(dialog, SWT.NONE);
        phoneLabel.setText("联系方式：");
        phoneLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Text phoneText = new Text(dialog, SWT.BORDER);
        phoneText.setLayoutData(new GridData(200, SWT.DEFAULT));
        phoneText.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        // 按钮区域
        Composite buttonComposite = new Composite(dialog, SWT.NONE);
        GridData buttonData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        buttonData.horizontalSpan = 2;
        buttonComposite.setLayoutData(buttonData);
        buttonComposite.setLayout(new GridLayout(2, true));

        Button okButton = new Button(buttonComposite, SWT.PUSH);
        okButton.setText("确定");
        okButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.BOLD));
        okButton.setBackground(new Color(parent.getDisplay(), 102, 187, 106));
        okButton.setForeground(new Color(parent.getDisplay(), 255, 255, 255));
        okButton.setLayoutData(new GridData(80, 35));

        Button cancelButton = new Button(buttonComposite, SWT.PUSH);
        cancelButton.setText("取消");
        cancelButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));
        cancelButton.setLayoutData(new GridData(80, 35));

        // 按钮事件
        okButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String studentId = idText.getText().trim();
                String studentName = nameText.getText().trim();
                String gender = genderCombo.getText();
                String className = classText.getText().trim();
                String phone = phoneText.getText().trim();

                // 验证输入
                if (studentId.isEmpty() || studentName.isEmpty() || className.isEmpty() || phone.isEmpty()) {
                    MessageBox warning = new MessageBox(dialog, SWT.ICON_WARNING);
                    warning.setText("输入不完整");
                    warning.setMessage("请填写所有必填字段！");
                    warning.open();
                    return;
                }

                // 创建学生对象
                Student student = new Student(studentId, studentName, gender, className, phone);

                // 调用添加方法
                boolean success = studentManage.addStudent(student);

                if (success) {
                    MessageBox info = new MessageBox(dialog, SWT.ICON_INFORMATION);
                    info.setText("添加成功");
                    info.setMessage("学生信息添加成功！");
                    info.open();
                    dialog.close();
                    loadStudentData(); // 刷新表格
                } else {
                    MessageBox error = new MessageBox(dialog, SWT.ICON_ERROR);
                    error.setText("添加失败");
                    error.setMessage("添加学生失败，可能学号已存在！");
                    error.open();
                }
            }
        });

        cancelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dialog.close();
            }
        });

        dialog.open();
    }

    /**
     * 显示编辑学生对话框
     */
    private void showEditStudentDialog(String studentId) {
        // 检查父控件是否有效
        if (parent.isDisposed() || parent.getShell() == null || parent.getShell().isDisposed()) {
            return;
        }

        Student student = studentManage.getStudentById(studentId);
        if (student == null) {
            MessageBox error = new MessageBox(parent.getShell(), SWT.ICON_ERROR);
            error.setText("错误");
            error.setMessage("未找到该学生信息！");
            error.open();
            return;
        }

        Shell shell = parent.getShell();
        Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setText("编辑学生信息");
        dialog.setSize(400, 350);
        dialog.setLayout(new GridLayout(2, false));

        // 居中显示
        dialog.setLocation(
                shell.getLocation().x + (shell.getSize().x - dialog.getSize().x) / 2,
                shell.getLocation().y + (shell.getSize().y - dialog.getSize().y) / 2
        );

        // 学号（不可编辑）
        Label idLabel = new Label(dialog, SWT.NONE);
        idLabel.setText("学号：");
        idLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Label idDisplay = new Label(dialog, SWT.NONE);
        idDisplay.setText(student.getStudentId());
        idDisplay.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.BOLD));
        idDisplay.setLayoutData(new GridData(200, SWT.DEFAULT));

        // 姓名
        Label nameLabel = new Label(dialog, SWT.NONE);
        nameLabel.setText("姓名：");
        nameLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Text nameText = new Text(dialog, SWT.BORDER);
        nameText.setText(student.getStudentName());
        nameText.setLayoutData(new GridData(200, SWT.DEFAULT));
        nameText.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        // 性别
        Label genderLabel = new Label(dialog, SWT.NONE);
        genderLabel.setText("性别：");
        genderLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Combo genderCombo = new Combo(dialog, SWT.DROP_DOWN | SWT.READ_ONLY);
        genderCombo.setItems(new String[]{"男", "女"});
        genderCombo.setText(student.getGender());
        genderCombo.setLayoutData(new GridData(200, SWT.DEFAULT));
        genderCombo.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        // 班级
        Label classLabel = new Label(dialog, SWT.NONE);
        classLabel.setText("班级：");
        classLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Text classText = new Text(dialog, SWT.BORDER);
        classText.setText(student.getClassName());
        classText.setLayoutData(new GridData(200, SWT.DEFAULT));
        classText.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        // 联系方式
        Label phoneLabel = new Label(dialog, SWT.NONE);
        phoneLabel.setText("联系方式：");
        phoneLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Text phoneText = new Text(dialog, SWT.BORDER);
        phoneText.setText(student.getPhone());
        phoneText.setLayoutData(new GridData(200, SWT.DEFAULT));
        phoneText.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        // 按钮区域
        Composite buttonComposite = new Composite(dialog, SWT.NONE);
        GridData buttonData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        buttonData.horizontalSpan = 2;
        buttonComposite.setLayoutData(buttonData);
        buttonComposite.setLayout(new GridLayout(2, true));

        Button okButton = new Button(buttonComposite, SWT.PUSH);
        okButton.setText("确定");
        okButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.BOLD));
        okButton.setBackground(new Color(parent.getDisplay(), 255, 193, 7));
        okButton.setForeground(new Color(parent.getDisplay(), 255, 255, 255));
        okButton.setLayoutData(new GridData(80, 35));

        Button cancelButton = new Button(buttonComposite, SWT.PUSH);
        cancelButton.setText("取消");
        cancelButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));
        cancelButton.setLayoutData(new GridData(80, 35));

        // 按钮事件
        okButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String studentName = nameText.getText().trim();
                String gender = genderCombo.getText();
                String className = classText.getText().trim();
                String phone = phoneText.getText().trim();

                // 验证输入
                if (studentName.isEmpty() || className.isEmpty() || phone.isEmpty()) {
                    MessageBox warning = new MessageBox(dialog, SWT.ICON_WARNING);
                    warning.setText("输入不完整");
                    warning.setMessage("请填写所有必填字段！");
                    warning.open();
                    return;
                }

                // 更新学生对象
                student.setStudentName(studentName);
                student.setGender(gender);
                student.setClassName(className);
                student.setPhone(phone);

                // 调用更新方法
                boolean success = studentManage.updateStudent(student);

                if (success) {
                    MessageBox info = new MessageBox(dialog, SWT.ICON_INFORMATION);
                    info.setText("更新成功");
                    info.setMessage("学生信息更新成功！");
                    info.open();
                    dialog.close();
                    loadStudentData(); // 刷新表格
                } else {
                    MessageBox error = new MessageBox(dialog, SWT.ICON_ERROR);
                    error.setText("更新失败");
                    error.setMessage("更新学生信息失败！");
                    error.open();
                }
            }
        });

        cancelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dialog.close();
            }
        });

        dialog.open();
    }

    /**
     * 显示删除确认对话框
     */
    private void showDeleteConfirmDialog(String studentId) {
        // 检查父控件是否有效
        if (parent.isDisposed() || parent.getShell() == null || parent.getShell().isDisposed()) {
            return;
        }

        Student student = studentManage.getStudentById(studentId);
        if (student != null) {
            MessageBox confirmDialog = new MessageBox(parent.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
            confirmDialog.setText("确认删除");
            confirmDialog.setMessage("确定要删除学生 " + student.getStudentName() + " (" + studentId + ") 吗？");

            int result = confirmDialog.open();
            if (result == SWT.YES) {
                boolean success = studentManage.deleteStudent(studentId);
                if (success) {
                    MessageBox successDialog = new MessageBox(parent.getShell(), SWT.ICON_INFORMATION);
                    successDialog.setText("删除成功");
                    successDialog.setMessage("学生信息删除成功！");
                    successDialog.open();
                    loadStudentData(); // 刷新表格
                }
            }
        }
    }

    /**
     * 释放资源
     */
    public void dispose() {
        cleanupTableEditors();
    }
}