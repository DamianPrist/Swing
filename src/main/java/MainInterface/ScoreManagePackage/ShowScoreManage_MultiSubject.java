
package MainInterface.ScoreManagePackage;

import Entity.Score;
import Entity.Subject;
import MainInterface.ScoreManagePackage.ScoreManage;
import MainInterface.SubjectManagePackage.ShowSubject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 多学科成绩管理前端界面类
 */
public class ShowScoreManage_MultiSubject {

    private Composite parent;
    private Table scoreTable;
    private ScoreManage scoreManage;
    private Text searchText;
    private List<TableEditor> tableEditors;
    private Combo subjectCombo; // 学科选择下拉框
    private String currentSubject = "数学"; // 默认学科
    private ShowSubject showSubject;
    // 分页相关变量
    private int currentPage = 1; // 当前页码
    private int pageSize = 10;   // 每页显示条数
    private int totalCount = 0;  // 总记录数
    private List<Score> allScores = new ArrayList<>(); // 所有成绩数据

    // 分页控件
    private Label pageInfoLabel;
    private Button prevPageButton;
    private Button nextPageButton;
    private Composite paginationComposite;
    // 新增：学科操作按钮（类级别成员变量，方便后续控制状态）
    private Button addSubjectButton;
    private Button deleteSubjectButton;

    public ShowScoreManage_MultiSubject(Composite parent) {
        this.parent = parent;
        this.scoreManage = new ScoreManage();
        this.tableEditors = new ArrayList<>();
        this.showSubject = new ShowSubject();
        createContent();
    }

    /**
     * 创建成绩管理界面内容
     */
    private void createContent() {
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 10;
        layout.marginHeight = 10;
        layout.verticalSpacing = 10;
        parent.setLayout(layout);

        // 创建搜索和学科选择区域
        createSearchAndSubjectArea();

        // 创建表格区域
        createTableArea();

        // 创建分页控件区域
        createPaginationArea();

        // 创建按钮区域
        createButtonArea();

        // 加载成绩数据
        loadScoreData();
    }

    /**
     * 创建搜索和学科选择区域
     */
    private void createSearchAndSubjectArea() {
        Composite searchSubjectComposite = new Composite(parent, SWT.NONE);
        GridData searchSubjectData = new GridData(SWT.CENTER, SWT.TOP, true, false);
        searchSubjectData.widthHint = 650;
        searchSubjectComposite.setLayoutData(searchSubjectData);

        GridLayout searchSubjectLayout = new GridLayout(4, false);
        searchSubjectLayout.marginWidth = 0;
        searchSubjectLayout.marginHeight = 0;
        searchSubjectLayout.horizontalSpacing = 10;
        searchSubjectComposite.setLayout(searchSubjectLayout);

        // 学科选择标签
        Label subjectLabel = new Label(searchSubjectComposite, SWT.NONE);
        subjectLabel.setText("选择学科：");
        subjectLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 12, SWT.NORMAL));

        // 学科选择下拉框
        subjectCombo = new Combo(searchSubjectComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
        List<Subject> subjects = scoreManage.getAllSubjects();
        for (Subject subject : subjects) {
            subjectCombo.add(subject.getSubjectName());
        }
        if (subjects.size() > 0) {
            subjectCombo.select(0);
            currentSubject = subjects.get(0).getSubjectName();
        }
        updateDeleteSubjectButtonStatus();
        subjectCombo.setFont(new Font(parent.getDisplay(), "微软雅黑", 12, SWT.NORMAL));
        GridData subjectData = new GridData(150, SWT.DEFAULT);
        subjectCombo.setLayoutData(subjectData);

        // 学科选择事件
        subjectCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                currentSubject = subjectCombo.getText();
                loadScoreData();
                // 新增：切换学科后，刷新删除学科按钮状态
                updateDeleteSubjectButtonStatus();
            }
        });

        // 搜索输入框
        searchText = new Text(searchSubjectComposite, SWT.BORDER | SWT.SEARCH);
        searchText.setFont(new Font(parent.getDisplay(), "微软雅黑", 12, SWT.NORMAL));
        searchText.setMessage("输入学号或姓名搜索");
        GridData textData = new GridData(200, SWT.DEFAULT);
        searchText.setLayoutData(textData);

        // 搜索按钮
        Button searchButton = new Button(searchSubjectComposite, SWT.PUSH);
        searchButton.setText("搜索");
        searchButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 12, SWT.BOLD));
        searchButton.setBackground(new Color(parent.getDisplay(), 74, 144, 226));
        searchButton.setForeground(new Color(parent.getDisplay(), 255, 255, 255));
        GridData searchBtnData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        searchBtnData.widthHint = 80;
        searchBtnData.heightHint = 35;
        searchButton.setLayoutData(searchBtnData);

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
            // 加载所有成绩
            allScores = scoreManage.getScoresBySubject(currentSubject);
        } else {
            // 先获取所有成绩，然后过滤
            allScores = scoreManage.getScoresBySubject(currentSubject);
            if (allScores != null) {
                List<Score> filtered = new ArrayList<>();
                for (Score score : allScores) {
                    if (score.getStudentId().contains(keyword) ||
                            (score.getStudentName() != null &&
                                    score.getStudentName().toLowerCase().contains(keyword.toLowerCase()))) {
                        filtered.add(score);
                    }
                }
                allScores = filtered;
            }
        }

        currentPage = 1; // 搜索后重置到第一页
        refreshTableWithPagination();
    }

    /**
     * 创建表格区域
     */
    private void createTableArea() {
        Composite tableComposite = new Composite(parent, SWT.NONE);
        GridData tableCompositeData = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableCompositeData.minimumHeight = 300;
        tableComposite.setLayoutData(tableCompositeData);

        GridLayout tableLayout = new GridLayout(1, false);
        tableLayout.marginWidth = 0;
        tableLayout.marginHeight = 0;
        tableComposite.setLayout(tableLayout);

        // 创建表格
        scoreTable = new Table(tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableData.minimumHeight = 250;
        scoreTable.setLayoutData(tableData);
        scoreTable.setHeaderVisible(true);
        scoreTable.setLinesVisible(true);
        scoreTable.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        // 创建列
        String[] columns = {"学号", "姓名", "平时成绩", "考试成绩", "总成绩", "操作"};
        int[] columnWidths = {120, 100, 100, 100, 100, 150}; // 减小操作列宽度

        for (int i = 0; i < columns.length; i++) {
            TableColumn column = new TableColumn(scoreTable, SWT.CENTER);
            column.setText(columns[i]);
            if (i == columns.length - 1) {
                // 操作列设置为可调整大小
                column.setResizable(true);
                column.setWidth(columnWidths[i]);
            } else {
                column.setWidth(columnWidths[i]);
            }
        }

        // 添加表格大小监听器，动态调整列宽
        scoreTable.addListener(SWT.Resize, new Listener() {
            public void handleEvent(Event event) {
                adjustTableColumns();
            }
        });
    }

    /**
     * 调整表格列宽
     */
    private void adjustTableColumns() {
        if (scoreTable.isDisposed()) {
            return;
        }

        // 获取表格客户区宽度
        int tableWidth = scoreTable.getClientArea().width;

        // 计算前5列的固定宽度
        int fixedWidth = 120 + 100 + 100 + 100 + 100; // 学号120 + 姓名100 + 平时成绩100 + 考试成绩100 + 总成绩100

        // 计算最后一列的宽度（剩余空间）
        int lastColumnWidth = tableWidth - fixedWidth;

        // 确保最后一列有最小宽度
        if (lastColumnWidth < 150) {
            lastColumnWidth = 150;
        }

        // 设置最后一列宽度
        TableColumn lastColumn = scoreTable.getColumn(5);
        if (lastColumn != null && !lastColumn.isDisposed()) {
            lastColumn.setWidth(lastColumnWidth);
        }
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

        GridData labelData = new GridData(180, SWT.DEFAULT);
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
        pageSizeCombo.select(0);
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
                currentPage = 1;
                refreshTableWithPagination();
            }
        });
    }

    /**
     * 创建按钮区域
     */
    private void createButtonArea() {
        Composite buttonComposite = new Composite(parent, SWT.NONE);
        GridData buttonData = new GridData(SWT.CENTER, SWT.BOTTOM, false, false);
        buttonData.verticalIndent = 10;
        buttonComposite.setLayoutData(buttonData);

        // 改造：将GridLayout从2列改为4列，保持水平间距一致
        GridLayout buttonLayout = new GridLayout(4, false);
        buttonLayout.marginWidth = 0;
        buttonLayout.marginHeight = 0;
        buttonLayout.horizontalSpacing = 20; // 与原有间距保持一致，保证布局美观
        buttonComposite.setLayout(buttonLayout);

        // 1. 原有：添加成绩按钮
        Button addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setText("添加成绩");
        addButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 12, SWT.BOLD));
        addButton.setBackground(new Color(parent.getDisplay(), 102, 187, 106));
        addButton.setForeground(new Color(parent.getDisplay(), 255, 255, 255));
        GridData addButtonData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        addButtonData.widthHint = 120;
        addButtonData.heightHint = 40;
        addButton.setLayoutData(addButtonData);
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showAddScoreDialog();
            }
        });

        // 2. 原有：刷新数据按钮
        Button refreshButton = new Button(buttonComposite, SWT.PUSH);
        refreshButton.setText("刷新数据");
        refreshButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 12, SWT.BOLD));
        refreshButton.setBackground(new Color(parent.getDisplay(), 74, 144, 226));
        refreshButton.setForeground(new Color(parent.getDisplay(), 255, 255, 255));
        GridData refreshButtonData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        refreshButtonData.widthHint = 120;
        refreshButtonData.heightHint = 40;
        refreshButton.setLayoutData(refreshButtonData);
        refreshButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                loadScoreData();
            }
        });

        // 3. 新增：新增学科按钮（样式与添加成绩按钮协调，绿色系）
        addSubjectButton = new Button(buttonComposite, SWT.PUSH);
        addSubjectButton.setText("新增学科");
        addSubjectButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 12, SWT.BOLD));
        addSubjectButton.setBackground(new Color(parent.getDisplay(), 52, 152, 219)); // 浅蓝绿色，与现有配色协调
        addSubjectButton.setForeground(new Color(parent.getDisplay(), 255, 255, 255));
        GridData addSubjectButtonData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        addSubjectButtonData.widthHint = 120;
        addSubjectButtonData.heightHint = 40;
        addSubjectButton.setLayoutData(addSubjectButtonData);
        // 绑定点击事件（先占位，后续对接新增学科对话框）
        addSubjectButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showAddSubjectDialog();
            }
        });

        // 4. 新增：删除学科按钮（样式与删除成绩按钮协调，红色系）
        deleteSubjectButton = new Button(buttonComposite, SWT.PUSH);
        deleteSubjectButton.setText("删除学科");
        deleteSubjectButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 12, SWT.BOLD));
        deleteSubjectButton.setBackground(new Color(parent.getDisplay(), 231, 76, 60)); // 深红色，与现有删除按钮配色一致
        deleteSubjectButton.setForeground(new Color(parent.getDisplay(), 255, 255, 255));
        GridData deleteSubjectButtonData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        deleteSubjectButtonData.widthHint = 120;
        deleteSubjectButtonData.heightHint = 40;
        deleteSubjectButton.setLayoutData(deleteSubjectButtonData);
        // 初始禁用（无学科选中时不可点击）
        deleteSubjectButton.setEnabled(false);
        // 绑定点击事件（先占位，后续对接删除学科确认对话框）
        deleteSubjectButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                showDeleteSubjectConfirmDialog();
            }
        });
    }

    /**
     * 新增：刷新删除学科按钮的启用状态
     */
    private void updateDeleteSubjectButtonStatus() {
        if (deleteSubjectButton == null || deleteSubjectButton.isDisposed()) {
            return;
        }
        // 判断条件：1. 学科下拉框有选项 2. 下拉框有选中项
        boolean isEnable = subjectCombo.getItemCount() > 0 && subjectCombo.getSelectionIndex() != -1;
        deleteSubjectButton.setEnabled(isEnable);
    }


    /**
     * 加载成绩数据
     */
    private void loadScoreData() {
        allScores = scoreManage.getScoresBySubject(currentSubject);
        currentPage = 1; // 重置到第一页
        refreshTableWithPagination();
    }

    /**
     * 带分页的刷新表格数据
     */
    private void refreshTableWithPagination() {
        if (allScores == null) {
            allScores = new ArrayList<>();
        }

        totalCount = allScores.size();
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
        List<Score> currentPageScores = new ArrayList<>();
        if (currentPage > 0) {
            int startIndex = (currentPage - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalCount);

            if (startIndex < totalCount) {
                currentPageScores = allScores.subList(startIndex, endIndex);
            }
        }

        // 刷新表格显示当前页数据
        refreshTable(currentPageScores);
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
     * 刷新表格数据
     */
    private void refreshTable(List<Score> scores) {
        // 清理之前的所有TableEditor
        cleanupTableEditors();

        // 清空表格
        scoreTable.removeAll();

        if (scores == null || scores.isEmpty()) {
            // 只显示一行提示信息，不创建操作按钮
            TableItem item = new TableItem(scoreTable, SWT.NONE);
            item.setText(0, "暂无数据");
            for (int i = 1; i < 6; i++) {
                item.setText(i, "");
            }
            return;
        }

        for (Score score : scores) {
            TableItem item = new TableItem(scoreTable, SWT.NONE);
            item.setText(0, score.getStudentId());
            item.setText(1, score.getStudentName() != null ? score.getStudentName() : "未知");
            item.setText(2, score.getUsualGrade() != null ? String.format("%.2f", score.getUsualGrade()) : "-");
            item.setText(3, score.getExamGrade() != null ? String.format("%.2f", score.getExamGrade()) : "-");
            item.setText(4, score.getTotalGrade() != null ? String.format("%.2f", score.getTotalGrade()) : "-");

            // 创建操作按钮容器
            Composite actionComposite = new Composite(scoreTable, SWT.NONE);
            GridLayout actionLayout = new GridLayout(2, true);
            actionLayout.marginWidth = 2;
            actionLayout.marginHeight = 2;
            actionLayout.horizontalSpacing = 5;
            actionComposite.setLayout(actionLayout);

            // 编辑按钮 - 设置正确的大小和居中
            Button editButton = new Button(actionComposite, SWT.PUSH);
            editButton.setText("编辑");
            editButton.setData("studentId", score.getStudentId());
            editButton.setData("subjectName", currentSubject);
            editButton.setBackground(new Color(parent.getDisplay(), 255, 193, 7));
            editButton.setForeground(new Color(parent.getDisplay(), 255, 255, 255));
            editButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 9, SWT.NORMAL));

            GridData editData = new GridData(SWT.FILL, SWT.CENTER, true, false);
            editData.widthHint = 60;
            editButton.setLayoutData(editData);

            // 删除按钮 - 设置正确的大小和居中
            Button deleteButton = new Button(actionComposite, SWT.PUSH);
            deleteButton.setText("删除");
            deleteButton.setData("studentId", score.getStudentId());
            deleteButton.setData("subjectName", currentSubject);
            deleteButton.setBackground(new Color(parent.getDisplay(), 239, 83, 80));
            deleteButton.setForeground(new Color(parent.getDisplay(), 255, 255, 255));
            deleteButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 9, SWT.NORMAL));

            GridData deleteData = new GridData(SWT.FILL, SWT.CENTER, true, false);
            deleteData.widthHint = 60;
            deleteButton.setLayoutData(deleteData);

            // 使用TableEditor嵌入按钮容器
            TableEditor editor = new TableEditor(scoreTable);
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
                        showEditScoreDialog(studentId);
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
        scoreTable.layout();
        adjustTableColumns();
    }

    /**
     * 清理表格编辑器
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
     * 显示添加成绩对话框
     */
    private void showAddScoreDialog() {
        // 检查父控件是否有效
        if (parent.isDisposed() || parent.getShell() == null || parent.getShell().isDisposed()) {
            return;
        }

        Shell shell = parent.getShell();
        Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setText("添加成绩");
        dialog.setSize(400, 350);
        dialog.setLayout(new GridLayout(2, false));

        // 居中显示
        dialog.setLocation(
                shell.getLocation().x + (shell.getSize().x - dialog.getSize().x) / 2,
                shell.getLocation().y + (shell.getSize().y - dialog.getSize().y) / 2
        );

        // 学科名称（显示，不可编辑）
        Label subjectLabel = new Label(dialog, SWT.NONE);
        subjectLabel.setText("学科：");
        subjectLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Label subjectDisplay = new Label(dialog, SWT.NONE);
        subjectDisplay.setText(currentSubject);
        subjectDisplay.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.BOLD));
        subjectDisplay.setLayoutData(new GridData(200, SWT.DEFAULT));

        // 学号
        Label idLabel = new Label(dialog, SWT.NONE);
        idLabel.setText("学号：");
        idLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Text idText = new Text(dialog, SWT.BORDER);
        idText.setLayoutData(new GridData(200, SWT.DEFAULT));
        idText.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        // 平时成绩
        Label usualLabel = new Label(dialog, SWT.NONE);
        usualLabel.setText("平时成绩：");
        usualLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Text usualText = new Text(dialog, SWT.BORDER);
        usualText.setLayoutData(new GridData(200, SWT.DEFAULT));
        usualText.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));
        usualText.setText("0");

        // 考试成绩
        Label examLabel = new Label(dialog, SWT.NONE);
        examLabel.setText("考试成绩：");
        examLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Text examText = new Text(dialog, SWT.BORDER);
        examText.setLayoutData(new GridData(200, SWT.DEFAULT));
        examText.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));
        examText.setText("0");

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
                String usualStr = usualText.getText().trim();
                String examStr = examText.getText().trim();

                // 验证输入
                if (studentId.isEmpty() || usualStr.isEmpty() || examStr.isEmpty()) {
                    MessageBox warning = new MessageBox(dialog, SWT.ICON_WARNING);
                    warning.setText("输入不完整");
                    warning.setMessage("请填写所有字段！");
                    warning.open();
                    return;
                }

                try {
                    BigDecimal usualGrade = new BigDecimal(usualStr);
                    BigDecimal examGrade = new BigDecimal(examStr);

                    // 验证成绩范围（0-100）
                    if (usualGrade.compareTo(BigDecimal.ZERO) < 0 || usualGrade.compareTo(new BigDecimal(100)) > 0 ||
                            examGrade.compareTo(BigDecimal.ZERO) < 0 || examGrade.compareTo(new BigDecimal(100)) > 0) {
                        MessageBox error = new MessageBox(dialog, SWT.ICON_ERROR);
                        error.setText("成绩范围错误");
                        error.setMessage("成绩必须在0-100之间！");
                        error.open();
                        return;
                    }

                    // 添加或更新成绩
                    boolean success = scoreManage.addOrUpdateScore(studentId, currentSubject, usualGrade, examGrade);

                    if (success) {
                        MessageBox info = new MessageBox(dialog, SWT.ICON_INFORMATION);
                        info.setText("添加成功");
                        info.setMessage("成绩添加成功！");
                        info.open();
                        dialog.close();
                        loadScoreData(); // 刷新表格
                    } else {
                        MessageBox error = new MessageBox(dialog, SWT.ICON_ERROR);
                        error.setText("添加失败");
                        error.setMessage("添加成绩失败，请检查学号是否正确！");
                        error.open();
                    }
                } catch (NumberFormatException ex) {
                    MessageBox error = new MessageBox(dialog, SWT.ICON_ERROR);
                    error.setText("输入错误");
                    error.setMessage("请输入有效的数字成绩！");
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
     * 显示编辑成绩对话框
     */
    private void showEditScoreDialog(String studentId) {
        // 检查父控件是否有效
        if (parent.isDisposed() || parent.getShell() == null || parent.getShell().isDisposed()) {
            return;
        }

        // 获取当前成绩
        Score score = scoreManage.getScoreByStudentAndSubject(studentId, currentSubject);
        if (score == null) {
            MessageBox error = new MessageBox(parent.getShell(), SWT.ICON_ERROR);
            error.setText("错误");
            error.setMessage("未找到该学生的成绩信息！");
            error.open();
            return;
        }

        Shell shell = parent.getShell();
        Shell dialog = new Shell(shell, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        dialog.setText("编辑成绩");
        dialog.setSize(400, 350);
        dialog.setLayout(new GridLayout(2, false));

        // 居中显示
        dialog.setLocation(
                shell.getLocation().x + (shell.getSize().x - dialog.getSize().x) / 2,
                shell.getLocation().y + (shell.getSize().y - dialog.getSize().y) / 2
        );

        // 学科名称（显示，不可编辑）
        Label subjectLabel = new Label(dialog, SWT.NONE);
        subjectLabel.setText("学科：");
        subjectLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Label subjectDisplay = new Label(dialog, SWT.NONE);
        subjectDisplay.setText(currentSubject);
        subjectDisplay.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.BOLD));
        subjectDisplay.setLayoutData(new GridData(200, SWT.DEFAULT));

        // 学号（显示，不可编辑）
        Label idLabel = new Label(dialog, SWT.NONE);
        idLabel.setText("学号：");
        idLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Label idDisplay = new Label(dialog, SWT.NONE);
        idDisplay.setText(studentId);
        idDisplay.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.BOLD));
        idDisplay.setLayoutData(new GridData(200, SWT.DEFAULT));

        // 姓名（显示，不可编辑）
        Label nameLabel = new Label(dialog, SWT.NONE);
        nameLabel.setText("姓名：");
        nameLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Label nameDisplay = new Label(dialog, SWT.NONE);
// 确保这里使用从数据库获取的学生姓名
        nameDisplay.setText(score.getStudentName() != null ? score.getStudentName() : "未知");
        nameDisplay.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));
        nameDisplay.setLayoutData(new GridData(200, SWT.DEFAULT));

        // 平时成绩
        Label usualLabel = new Label(dialog, SWT.NONE);
        usualLabel.setText("平时成绩：");
        usualLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Text usualText = new Text(dialog, SWT.BORDER);
        usualText.setText(score.getUsualGrade() != null ? String.format("%.2f", score.getUsualGrade()) : "0");
        usualText.setLayoutData(new GridData(200, SWT.DEFAULT));
        usualText.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        // 考试成绩
        Label examLabel = new Label(dialog, SWT.NONE);
        examLabel.setText("考试成绩：");
        examLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        Text examText = new Text(dialog, SWT.BORDER);
        examText.setText(score.getExamGrade() != null ? String.format("%.2f", score.getExamGrade()) : "0");
        examText.setLayoutData(new GridData(200, SWT.DEFAULT));
        examText.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

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
                String usualStr = usualText.getText().trim();
                String examStr = examText.getText().trim();

                // 验证输入
                if (usualStr.isEmpty() || examStr.isEmpty()) {
                    MessageBox warning = new MessageBox(dialog, SWT.ICON_WARNING);
                    warning.setText("输入不完整");
                    warning.setMessage("请填写所有字段！");
                    warning.open();
                    return;
                }

                try {
                    BigDecimal usualGrade = new BigDecimal(usualStr);
                    BigDecimal examGrade = new BigDecimal(examStr);

                    // 验证成绩范围（0-100）
                    if (usualGrade.compareTo(BigDecimal.ZERO) < 0 || usualGrade.compareTo(new BigDecimal(100)) > 0 ||
                            examGrade.compareTo(BigDecimal.ZERO) < 0 || examGrade.compareTo(new BigDecimal(100)) > 0) {
                        MessageBox error = new MessageBox(dialog, SWT.ICON_ERROR);
                        error.setText("成绩范围错误");
                        error.setMessage("成绩必须在0-100之间！");
                        error.open();
                        return;
                    }

                    // 更新成绩
                    boolean success = scoreManage.addOrUpdateScore(studentId, currentSubject, usualGrade, examGrade);

                    if (success) {
                        MessageBox info = new MessageBox(dialog, SWT.ICON_INFORMATION);
                        info.setText("更新成功");
                        info.setMessage("成绩更新成功！");
                        info.open();
                        dialog.close();
                        loadScoreData(); // 刷新表格
                    } else {
                        MessageBox error = new MessageBox(dialog, SWT.ICON_ERROR);
                        error.setText("更新失败");
                        error.setMessage("更新成绩失败！");
                        error.open();
                    }
                } catch (NumberFormatException ex) {
                    MessageBox error = new MessageBox(dialog, SWT.ICON_ERROR);
                    error.setText("输入错误");
                    error.setMessage("请输入有效的数字成绩！");
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

        Score score = scoreManage.getScoreByStudentAndSubject(studentId, currentSubject);
        if (score != null) {
            MessageBox confirmDialog = new MessageBox(parent.getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
            confirmDialog.setText("确认删除");
            confirmDialog.setMessage("确定要删除学生 " +
                    (score.getStudentName() != null ? score.getStudentName() : studentId) +
                    " 的 " + currentSubject + " 成绩吗？");

            int result = confirmDialog.open();
            if (result == SWT.YES) {
                boolean success = scoreManage.deleteScore(studentId, currentSubject);
                if (success) {
                    MessageBox successDialog = new MessageBox(parent.getShell(), SWT.ICON_INFORMATION);
                    successDialog.setText("删除成功");
                    successDialog.setMessage("成绩删除成功！");
                    successDialog.open();
                    loadScoreData(); // 刷新表格
                }
            }
        }
    }


    /**
     * 替换占位：调用ShowSubject的新增学科对话框
     */
    private void showAddSubjectDialog() {
        // 获取父窗口（来自parent控件）
        Shell parentShell = parent.getShell();
        // 调用ShowSubject的对话框方法，传递父窗口和学科下拉框
        showSubject.showAddSubjectDialog(parentShell, subjectCombo);
    }

    /**
     * 新增：占位 - 显示删除学科确认对话框
     */
    /**
     * 替换占位：调用ShowSubject的删除学科确认对话框
     */
    private void showDeleteSubjectConfirmDialog() {
        Shell parentShell = parent.getShell();
        // 获取当前选中的学科
        String selectedSubject = subjectCombo.getText();
        // 调用ShowSubject的删除确认方法
        showSubject.showDeleteSubjectConfirmDialog(parentShell, subjectCombo, selectedSubject);
    }


    /**
     * 释放资源
     */
    public void dispose() {
        cleanupTableEditors();
    }
}
