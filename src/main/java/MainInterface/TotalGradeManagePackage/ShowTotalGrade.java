
        package MainInterface.TotalGradeManagePackage;
import DAO.ScoreDAO;
import DAO.StudentDAO;
import DAO.SubjectDAO;
import Entity.Student;
import Entity.Score;
import Entity.Subject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 总成绩管理前端界面类 - 修改为支持多学科汇总
 */
public class ShowTotalGrade {

    private Composite parent;
    private Table gradeTable;
    private ScoreDAO scoreDAO;
    private StudentDAO studentDAO;
    private SubjectDAO subjectDAO;
    private Text searchText;

    // 分页相关变量
    private int currentPage = 1; // 当前页码
    private int pageSize = 10;   // 每页显示条数
    private int totalCount = 0;  // 总记录数
    private List<StudentGradeSummary> allStudentGrades = new ArrayList<>(); // 所有学生成绩汇总数据

    // 分页控件
    private Label pageInfoLabel;
    private Button prevPageButton;
    private Button nextPageButton;
    private Composite paginationComposite;

    /**
     * 学生成绩汇总类（内部类）
     */
    private static class StudentGradeSummary {
        private String studentId;
        private String studentName;
        private String className;
        private BigDecimal usualGrade;  // 平时成绩平均值
        private BigDecimal examGrade;   // 考试成绩平均值
        private BigDecimal totalGrade;  // 总成绩平均值
        private int subjectCount;       // 学科数量

        public StudentGradeSummary(String studentId, String studentName, String className) {
            this.studentId = studentId;
            this.studentName = studentName;
            this.className = className;
            this.usualGrade = BigDecimal.ZERO;
            this.examGrade = BigDecimal.ZERO;
            this.totalGrade = BigDecimal.ZERO;
            this.subjectCount = 0;
        }

        public void addSubjectGrade(BigDecimal usual, BigDecimal exam, BigDecimal total) {
            if (usual != null) {
                this.usualGrade = this.usualGrade.add(usual);
            }
            if (exam != null) {
                this.examGrade = this.examGrade.add(exam);
            }
            if (total != null) {
                this.totalGrade = this.totalGrade.add(total);
            }
            this.subjectCount++;
        }

        public void calculateAverages() {
            if (subjectCount > 0) {
                this.usualGrade = this.usualGrade.divide(new BigDecimal(subjectCount), 2, RoundingMode.HALF_UP);
                this.examGrade = this.examGrade.divide(new BigDecimal(subjectCount), 2, RoundingMode.HALF_UP);
                this.totalGrade = this.totalGrade.divide(new BigDecimal(subjectCount), 2, RoundingMode.HALF_UP);
            }
        }

        // Getters
        public String getStudentId() { return studentId; }
        public String getStudentName() { return studentName; }
        public String getClassName() { return className; }
        public BigDecimal getUsualGrade() { return subjectCount > 0 ? usualGrade : null; }
        public BigDecimal getExamGrade() { return subjectCount > 0 ? examGrade : null; }
        public BigDecimal getTotalGrade() { return subjectCount > 0 ? totalGrade : null; }
        public int getSubjectCount() { return subjectCount; }
    }

    public ShowTotalGrade(Composite parent) {
        this.parent = parent;
        this.scoreDAO = new ScoreDAO();
        this.studentDAO = new StudentDAO();
        this.subjectDAO = new SubjectDAO();
        createContent();
    }

    /**
     * 创建总成绩管理界面内容
     */
    private void createContent() {
        // 设置布局
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 10;
        layout.marginHeight = 10;
        layout.verticalSpacing = 10;
        parent.setLayout(layout);

        // 创建搜索区域
        createSearchArea();

        // 创建表格区域 - 修改为填充整个区域
        createTableArea();

        // 创建分页控件区域
        createPaginationArea();

        // 创建按钮区域 - 只保留刷新按钮
        createButtonArea();

        // 加载学生成绩汇总数据
        loadStudentGradeSummaryData();
    }

    /**
     * 创建搜索区域
     */
    private void createSearchArea() {
        Composite searchComposite = new Composite(parent, SWT.NONE);
        GridData searchData = new GridData(SWT.CENTER, SWT.TOP, true, false);
        searchData.widthHint = 500;
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

        // 搜索按钮
        Button searchButton = new Button(searchComposite, SWT.PUSH);
        searchButton.setText("搜索");
        searchButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 12, SWT.BOLD));
        searchButton.setBackground(new Color(parent.getDisplay(), 74, 144, 226));
        searchButton.setForeground(new Color(parent.getDisplay(), 255, 255, 255));

        GridData buttonData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        buttonData.widthHint = 80;
        buttonData.heightHint = 35;
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
            loadStudentGradeSummaryData();
        } else {
            // 搜索学生
            List<Student> searchResults = studentDAO.searchStudents(keyword);
            loadStudentGradeSummaryData(searchResults);
        }

        currentPage = 1; // 搜索后重置到第一页
        refreshTableWithPagination();
    }

    /**
     * 创建表格区域
     */
    private void createTableArea() {
        // 创建表格容器
        Composite tableComposite = new Composite(parent, SWT.NONE);
        GridData tableCompositeData = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableCompositeData.minimumHeight = 350;
        tableCompositeData.heightHint = 350;
        tableComposite.setLayoutData(tableCompositeData);

        GridLayout tableLayout = new GridLayout(1, false);
        tableLayout.marginWidth = 0;
        tableLayout.marginHeight = 0;
        tableComposite.setLayout(tableLayout);

        // 创建表格
        gradeTable = new Table(tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableData.minimumHeight = 300;
        gradeTable.setLayoutData(tableData);
        gradeTable.setHeaderVisible(true);
        gradeTable.setLinesVisible(true);
        gradeTable.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        // 创建列：学号、姓名、班级、平均平时成绩、平均考试成绩、平均总成绩、学科数量
        String[] columns = {"学号", "姓名", "班级", "平均平时成绩", "平均考试成绩", "平均总成绩", "学科数量"};
        int[] columnWidths = {120, 120, 150, 120, 120, 120, 100};

        for (int i = 0; i < columns.length; i++) {
            TableColumn column = new TableColumn(gradeTable, SWT.CENTER);
            column.setText(columns[i]);
            column.setWidth(columnWidths[i]);
            if (i == columns.length - 1) {
                column.setResizable(true);
            }
        }

        // 添加表格大小监听器，动态调整列宽
        gradeTable.addListener(SWT.Resize, new Listener() {
            public void handleEvent(Event event) {
                adjustTableColumns();
            }
        });
    }

    /**
     * 调整表格列宽
     */
    private void adjustTableColumns() {
        if (gradeTable.isDisposed()) {
            return;
        }

        int tableWidth = gradeTable.getClientArea().width;
        int fixedWidth = 120 + 120 + 150 + 120 + 120 + 120;
        int lastColumnWidth = tableWidth - fixedWidth;

        if (lastColumnWidth < 100) {
            lastColumnWidth = 100;
        }

        TableColumn lastColumn = gradeTable.getColumn(6);
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

        GridLayout buttonLayout = new GridLayout(1, false);
        buttonLayout.marginWidth = 0;
        buttonLayout.marginHeight = 0;
        buttonComposite.setLayout(buttonLayout);

        // 刷新按钮
        Button refreshButton = new Button(buttonComposite, SWT.PUSH);
        refreshButton.setText("刷新数据");
        refreshButton.setFont(new Font(parent.getDisplay(), "微软雅黑", 12, SWT.BOLD));
        refreshButton.setBackground(new Color(parent.getDisplay(), 33, 150, 243));
        refreshButton.setForeground(new Color(parent.getDisplay(), 255, 255, 255));

        GridData refreshButtonData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
        refreshButtonData.widthHint = 120;
        refreshButtonData.heightHint = 40;
        refreshButton.setLayoutData(refreshButtonData);

        // 刷新按钮事件
        refreshButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                loadStudentGradeSummaryData();
            }
        });
    }

    /**
     * 加载所有学生的成绩汇总数据
     */
    private void loadStudentGradeSummaryData() {
        List<Student> allStudents = studentDAO.getAllStudents();
        loadStudentGradeSummaryData(allStudents);
    }

    /**
     * 加载指定学生列表的成绩汇总数据
     */
    private void loadStudentGradeSummaryData(List<Student> students) {
        allStudentGrades.clear();

        // 获取所有学科
        List<Subject> allSubjects = subjectDAO.getAllSubjects();

        for (Student student : students) {
            StudentGradeSummary summary = new StudentGradeSummary(
                    student.getStudentId(),
                    student.getStudentName(),
                    student.getClassName()
            );

            // 获取该学生的所有学科成绩
            List<Score> studentScores = scoreDAO.getScoresByStudent(student.getStudentId());

            // 累加各科成绩
            for (Score score : studentScores) {
                summary.addSubjectGrade(score.getUsualGrade(), score.getExamGrade(), score.getTotalGrade());
            }

            // 计算平均值
            summary.calculateAverages();
            allStudentGrades.add(summary);
        }

        // 按平均总成绩排序（降序）
        allStudentGrades.sort((s1, s2) -> {
            BigDecimal t1 = s1.getTotalGrade();
            BigDecimal t2 = s2.getTotalGrade();
            if (t1 == null && t2 == null) return 0;
            if (t1 == null) return 1;
            if (t2 == null) return -1;
            return t2.compareTo(t1);
        });

        refreshTableWithPagination();
    }

    /**
     * 带分页的刷新表格数据
     */
    private void refreshTableWithPagination() {
        if (allStudentGrades == null) {
            allStudentGrades = new ArrayList<>();
        }

        totalCount = allStudentGrades.size();
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
        List<StudentGradeSummary> currentPageGrades = new ArrayList<>();
        if (currentPage > 0) {
            int startIndex = (currentPage - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, totalCount);

            if (startIndex < totalCount) {
                currentPageGrades = allStudentGrades.subList(startIndex, endIndex);
            }
        }

        // 刷新表格显示当前页数据
        refreshTable(currentPageGrades);
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
    private void refreshTable(List<StudentGradeSummary> summaries) {
        if (gradeTable.isDisposed()) {
            return;
        }

        // 清空表格
        gradeTable.removeAll();

        // 添加数据行
        for (StudentGradeSummary summary : summaries) {
            TableItem item = new TableItem(gradeTable, SWT.NONE);
            item.setText(0, summary.getStudentId());
            item.setText(1, summary.getStudentName());
            item.setText(2, summary.getClassName());

            // 显示平均平时成绩
            if (summary.getUsualGrade() != null) {
                item.setText(3, String.format("%.2f", summary.getUsualGrade()));
            } else {
                item.setText(3, "-");
            }

            // 显示平均考试成绩
            if (summary.getExamGrade() != null) {
                item.setText(4, String.format("%.2f", summary.getExamGrade()));
            } else {
                item.setText(4, "-");
            }

            // 显示平均总成绩
            if (summary.getTotalGrade() != null) {
                item.setText(5, String.format("%.2f", summary.getTotalGrade()));
            } else {
                item.setText(5, "-");
            }

            // 显示学科数量
            item.setText(6, String.valueOf(summary.getSubjectCount()));
        }

        // 刷新表格并调整列宽
        gradeTable.layout();
        adjustTableColumns();
    }

    /**
     * 释放资源
     */
    public void dispose() {
        // 清理资源
    }
}