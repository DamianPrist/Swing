package MainInterface.StudentInterfacePackage;

import Entity.Student;
import Entity.Score;
import DAO.ScoreDAO;
import DAO.SubjectDAO;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 多学科学生成绩查看界面
 */
public class ShowStudentGrade_MultiSubject {

    private Composite parent;
    private Student currentStudent;
    private ScoreDAO scoreDAO;
    private SubjectDAO subjectDAO;
    private Table gradeTable;

    public ShowStudentGrade_MultiSubject(Composite parent, Student student) {
        this.parent = parent;
        this.currentStudent = student;
        this.scoreDAO = new ScoreDAO();
        this.subjectDAO = new SubjectDAO();
        createContent();
    }

    /**
     * 创建学生成绩界面内容
     */
    private void createContent() {
        // 设置布局
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 20;
        layout.marginHeight = 20;
        layout.verticalSpacing = 20;
        parent.setLayout(layout);

        // 创建学生信息卡片
        createStudentInfoCard();

        // 创建成绩表格
        createGradeTable();

        // 加载成绩数据
        loadGradeData();
    }

    /**
     * 创建学生信息卡片
     */
    private void createStudentInfoCard() {
        Composite infoComposite = new Composite(parent, SWT.NONE);
        infoComposite.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
        GridData infoData = new GridData(SWT.FILL, SWT.TOP, true, false);
        infoData.heightHint = 120;
        infoComposite.setLayoutData(infoData);

        GridLayout infoLayout = new GridLayout(4, false);
        infoLayout.marginWidth = 30;
        infoLayout.marginHeight = 20;
        infoLayout.horizontalSpacing = 10;
        infoLayout.verticalSpacing = 15;
        infoComposite.setLayout(infoLayout);

        // 学号信息
        createInfoItem(infoComposite, "学号：", currentStudent.getStudentId(), 150);

        // 姓名信息
        createInfoItem(infoComposite, "姓名：", currentStudent.getStudentName(), 120);

        // 班级信息
        createInfoItem(infoComposite, "班级：", currentStudent.getClassName(), 200);

        // 性别信息
        createInfoItem(infoComposite, "性别：", currentStudent.getGender(), 80);
    }

    /**
     * 创建信息项辅助方法
     */
    private void createInfoItem(Composite parent, String labelText, String value, int widthHint) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelText);
        label.setFont(new Font(parent.getDisplay(), "微软雅黑", 13, SWT.BOLD));
        label.setBackground(new Color(parent.getDisplay(), 255, 255, 255));

        Label valueLabel = new Label(parent, SWT.NONE);
        valueLabel.setText(value != null ? value : "未设置");
        valueLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 13, SWT.NORMAL));
        valueLabel.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
        GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        data.widthHint = widthHint;
        valueLabel.setLayoutData(data);
    }

    /**
     * 创建成绩表格 - 修改为填充整个宽度
     */
    private void createGradeTable() {
        Composite tableComposite = new Composite(parent, SWT.NONE);
        tableComposite.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
        GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableData.minimumHeight = 300; // 设置最小高度
        tableComposite.setLayoutData(tableData);

        GridLayout tableLayout = new GridLayout(1, false);
        tableLayout.marginWidth = 10;
        tableLayout.marginHeight = 10;
        tableComposite.setLayout(tableLayout);

        // 表格标题
        Label tableTitle = new Label(tableComposite, SWT.CENTER);
        tableTitle.setText("各科成绩详情");
        tableTitle.setFont(new Font(parent.getDisplay(), "微软雅黑", 18, SWT.BOLD));
        tableTitle.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
        GridData titleData = new GridData(SWT.CENTER, SWT.CENTER, true, false);
        titleData.heightHint = 40;
        tableTitle.setLayoutData(titleData);

        // 创建表格容器
        Composite tableContainer = new Composite(tableComposite, SWT.NONE);
        tableContainer.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
        GridData containerData = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableContainer.setLayoutData(containerData);

        GridLayout containerLayout = new GridLayout(1, false);
        containerLayout.marginWidth = 0;
        containerLayout.marginHeight = 0;
        tableContainer.setLayout(containerLayout);

        // 创建表格 - 添加滚动条和填充数据
        gradeTable = new Table(tableContainer, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData gradeTableData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gradeTableData.minimumHeight = 250;
        gradeTable.setLayoutData(gradeTableData);
        gradeTable.setHeaderVisible(true);
        gradeTable.setLinesVisible(true);
        gradeTable.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));

        // 创建列
        String[] columns = {"学科", "平时成绩", "考试成绩", "总成绩", "评价"};
        // 设置初始宽度，最后一列自动填充
        int[] columnWidths = {150, 120, 120, 120, SWT.DEFAULT};

        for (int i = 0; i < columns.length; i++) {
            TableColumn column = new TableColumn(gradeTable, SWT.CENTER);
            column.setText(columns[i]);
            if (i == columns.length - 1) {
                // 最后一列设置为可调整大小，自动填充剩余空间
                column.setResizable(true);
                column.setWidth(150); // 设置初始宽度
            } else {
                column.setWidth(columnWidths[i]);
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
     * 调整表格列宽，确保填满整个表格宽度
     */
    private void adjustTableColumns() {
        if (gradeTable.isDisposed()) {
            return;
        }

        // 获取表格客户区宽度
        int tableWidth = gradeTable.getClientArea().width;

        // 计算前4列的固定宽度
        int fixedWidth = 150 + 120 + 120 + 120; // 学科150 + 平时成绩120 + 考试成绩120 + 总成绩120

        // 计算最后一列的宽度（剩余空间）
        int lastColumnWidth = tableWidth - fixedWidth;

        // 确保最后一列有最小宽度
        if (lastColumnWidth < 150) {
            lastColumnWidth = 150;
        }

        // 设置最后一列宽度
        TableColumn lastColumn = gradeTable.getColumn(4);
        if (lastColumn != null && !lastColumn.isDisposed()) {
            lastColumn.setWidth(lastColumnWidth);
        }

        // 如果表格太窄，需要重新分配宽度
        if (tableWidth < 600) {
            // 重新计算各列宽度，按比例分配
            int[] newWidths = new int[5];
            int availableWidth = tableWidth - 20; // 减去一些边距

            // 为每列分配宽度
            newWidths[0] = (int)(availableWidth * 0.25); // 学科25%
            newWidths[1] = (int)(availableWidth * 0.20); // 平时成绩20%
            newWidths[2] = (int)(availableWidth * 0.20); // 考试成绩20%
            newWidths[3] = (int)(availableWidth * 0.20); // 总成绩20%
            newWidths[4] = (int)(availableWidth * 0.15); // 评价15%

            // 设置列宽
            for (int i = 0; i < 5; i++) {
                TableColumn column = gradeTable.getColumn(i);
                if (column != null && !column.isDisposed()) {
                    column.setWidth(newWidths[i]);
                }
            }
        }
    }

    /**
     * 加载成绩数据
     */
    private void loadGradeData() {
        // 获取学生所有学科成绩
        List<Score> scores = scoreDAO.getScoresByStudent(currentStudent.getStudentId());

        // 清除现有数据
        gradeTable.removeAll();

        if (scores.isEmpty()) {
            TableItem item = new TableItem(gradeTable, SWT.NONE);
            item.setText(0, "暂无成绩数据");
            for (int i = 1; i < 5; i++) {
                item.setText(i, "");
            }
            return;
        }

        for (Score score : scores) {
            TableItem item = new TableItem(gradeTable, SWT.NONE);
            item.setText(0, score.getSubjectName() != null ? score.getSubjectName() : "未知学科");
            item.setText(1, score.getUsualGrade() != null ? String.format("%.2f", score.getUsualGrade()) : "-");
            item.setText(2, score.getExamGrade() != null ? String.format("%.2f", score.getExamGrade()) : "-");
            item.setText(3, score.getTotalGrade() != null ? String.format("%.2f", score.getTotalGrade()) : "-");

            // 评价
            String evaluation = getEvaluation(score.getTotalGrade());
            item.setText(4, evaluation);

            // 根据总成绩设置颜色
            if (score.getTotalGrade() != null) {
                Color color = getGradeColor(score.getTotalGrade());
                for (int i = 0; i < 5; i++) {
                    item.setForeground(i, color);
                }
            }
        }

        // 加载数据后调整列宽
        gradeTable.layout();
        adjustTableColumns();
    }

    /**
     * 获取成绩评价
     */
    private String getEvaluation(BigDecimal totalGrade) {
        if (totalGrade == null) return "暂无成绩";
        if (totalGrade.compareTo(new BigDecimal("90")) >= 0) return "优秀";
        if (totalGrade.compareTo(new BigDecimal("80")) >= 0) return "良好";
        if (totalGrade.compareTo(new BigDecimal("60")) >= 0) return "及格";
        return "不及格";
    }

    /**
     * 根据成绩获取颜色
     */
    private Color getGradeColor(BigDecimal totalGrade) {
        if (totalGrade == null) return new Color(parent.getDisplay(), 128, 128, 128);
        if (totalGrade.compareTo(new BigDecimal("90")) >= 0) return new Color(parent.getDisplay(), 76, 175, 80);
        if (totalGrade.compareTo(new BigDecimal("80")) >= 0) return new Color(parent.getDisplay(), 255, 193, 7);
        if (totalGrade.compareTo(new BigDecimal("60")) >= 0) return new Color(parent.getDisplay(), 255, 152, 0);
        return new Color(parent.getDisplay(), 244, 67, 54);
    }
}