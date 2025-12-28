package MainInterface.StudentInterfacePackage;

import Entity.Student;
import Entity.Score;
import DAO.ScoreDAO;
import DAO.SubjectDAO;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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
    private ScrolledComposite scrolledComposite;
    private Font tableFont;
    private Font titleFont;

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
        layout.marginWidth = 10;
        layout.marginHeight = 10;
        layout.verticalSpacing = 10;  // 进一步减少间距
        parent.setLayout(layout);

        // 创建学生信息卡片
        createStudentInfoCard();

        // 创建成绩表格区域
        createGradeTableArea();

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
        infoData.heightHint = 90;  // 进一步减少高度
        infoComposite.setLayoutData(infoData);

        GridLayout infoLayout = new GridLayout(4, false);
        infoLayout.marginWidth = 15;  // 进一步减少边距
        infoLayout.marginHeight = 10;
        infoLayout.horizontalSpacing = 6;
        infoLayout.verticalSpacing = 8;
        infoComposite.setLayout(infoLayout);

        // 学号信息
        createInfoItem(infoComposite, "学号：", currentStudent.getStudentId(), 110);

        // 姓名信息
        createInfoItem(infoComposite, "姓名：", currentStudent.getStudentName(), 90);

        // 班级信息
        createInfoItem(infoComposite, "班级：", currentStudent.getClassName(), 140);

        // 性别信息
        createInfoItem(infoComposite, "性别：", currentStudent.getGender(), 50);
    }

    /**
     * 创建信息项辅助方法
     */
    private void createInfoItem(Composite parent, String labelText, String value, int widthHint) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelText);
        label.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.BOLD));  // 进一步减小字体
        label.setBackground(new Color(parent.getDisplay(), 255, 255, 255));

        Label valueLabel = new Label(parent, SWT.NONE);
        valueLabel.setText(value != null ? value : "未设置");
        valueLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 11, SWT.NORMAL));  // 进一步减小字体
        valueLabel.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
        GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        data.widthHint = widthHint;
        valueLabel.setLayoutData(data);
    }

    /**
     * 创建成绩表格区域 - 使用滚动条容器
     */
    private void createGradeTableArea() {
        Composite tableAreaComposite = new Composite(parent, SWT.NONE);
        tableAreaComposite.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
        GridData areaData = new GridData(SWT.FILL, SWT.FILL, true, true);
        tableAreaComposite.setLayoutData(areaData);

        GridLayout areaLayout = new GridLayout(1, false);
        areaLayout.marginWidth = 0;
        areaLayout.marginHeight = 0;
        areaLayout.verticalSpacing = 3;  // 进一步减少间距
        tableAreaComposite.setLayout(areaLayout);

        // 表格标题 - 关键修改：减小字体确保显示完整
        Label tableTitle = new Label(tableAreaComposite, SWT.CENTER);
        tableTitle.setText("各科成绩详情");
        titleFont = new Font(parent.getDisplay(), "微软雅黑", 14, SWT.BOLD);  // 减小字体大小
        tableTitle.setFont(titleFont);
        tableTitle.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
        GridData titleData = new GridData(SWT.CENTER, SWT.TOP, true, false);
        // 不设置固定高度，让字体自适应
        tableTitle.setLayoutData(titleData);

        // 添加字体资源释放监听器
        tableTitle.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                if (titleFont != null && !titleFont.isDisposed()) {
                    titleFont.dispose();
                }
            }
        });

        // 数据统计信息
        Label statsLabel = new Label(tableAreaComposite, SWT.CENTER);
        statsLabel.setText("共 0 门课程");
        statsLabel.setFont(new Font(parent.getDisplay(), "微软雅黑", 10, SWT.NORMAL));  // 减小字体
        statsLabel.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
        statsLabel.setData("stats");
        GridData statsData = new GridData(SWT.CENTER, SWT.TOP, true, false);
        // 不设置固定高度，让字体自适应
        statsLabel.setLayoutData(statsData);

        // 创建滚动容器
        scrolledComposite = new ScrolledComposite(tableAreaComposite,
                SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        scrolledComposite.setBackground(new Color(parent.getDisplay(), 255, 255, 255));

        GridData scrollData = new GridData(SWT.FILL, SWT.FILL, true, true);
        scrolledComposite.setLayoutData(scrollData);

        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);
        scrolledComposite.setAlwaysShowScrollBars(false);

        // 创建表格容器
        Composite tableContainer = new Composite(scrolledComposite, SWT.NONE);
        tableContainer.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
        scrolledComposite.setContent(tableContainer);

        GridLayout containerLayout = new GridLayout(1, false);
        containerLayout.marginWidth = 0;
        containerLayout.marginHeight = 0;
        tableContainer.setLayout(containerLayout);

        // 创建表格
        gradeTable = new Table(tableContainer, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
        GridData gradeTableData = new GridData(SWT.FILL, SWT.FILL, true, true);
        gradeTable.setLayoutData(gradeTableData);
        gradeTable.setHeaderVisible(true);
        gradeTable.setLinesVisible(true);

        // 使用稍大的字体来增加行高
        tableFont = new Font(parent.getDisplay(), "微软雅黑", 10, SWT.NORMAL);  // 进一步减小字体
        gradeTable.setFont(tableFont);

        // 添加字体资源释放监听器
        gradeTable.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent e) {
                if (tableFont != null && !tableFont.isDisposed()) {
                    tableFont.dispose();
                }
            }
        });

        // 创建列
        String[] columns = {"序号", "学科", "平时成绩", "考试成绩", "总成绩", "评价", "状态"};
        // 设置更窄的列宽度
        int[] columnWidths = {45, 140, 85, 85, 85, 90, 70};

        for (int i = 0; i < columns.length; i++) {
            TableColumn column = new TableColumn(gradeTable, SWT.CENTER);
            column.setText(columns[i]);
            if (i < columnWidths.length) {
                column.setWidth(columnWidths[i]);
            }
            column.setResizable(true);
        }

        // 设置表格容器的最小大小
        tableContainer.pack();

        // 设置滚动容器的最小大小
        scrolledComposite.setMinSize(tableContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        // 为滚动容器添加布局监听器
        scrolledComposite.addListener(SWT.Resize, new Listener() {
            @Override
            public void handleEvent(Event event) {
                updateScrollerMinSize();
            }
        });

        // 添加表格大小监听器，动态调整列宽
        gradeTable.addListener(SWT.Resize, new Listener() {
            public void handleEvent(Event event) {
                adjustTableColumns();
            }
        });
    }

    /**
     * 更新滚动容器的最小尺寸
     */
    private void updateScrollerMinSize() {
        if (gradeTable == null || gradeTable.isDisposed()) {
            return;
        }

        // 获取表格的实际大小
        int tableHeight = gradeTable.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
        int tableWidth = gradeTable.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;

        // 确保表格高度不超过父容器可用高度的85%
        int parentHeight = parent.getClientArea().height;
        int maxTableHeight = (int)(parentHeight * 0.85);

        int actualTableHeight = Math.min(tableHeight, maxTableHeight);

        // 设置滚动容器的最小大小
        scrolledComposite.setMinSize(tableWidth, actualTableHeight);

        // 重新布局
        scrolledComposite.getContent().pack();
        scrolledComposite.layout(true, true);
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
            item.setText(0, "1");
            item.setText(1, "暂无成绩数据");
            for (int i = 2; i < 7; i++) {
                item.setText(i, "");
            }
            item.setForeground(new Color(parent.getDisplay(), 128, 128, 128));
            updateStatsLabel(0);
            return;
        }

        int index = 1;
        for (Score score : scores) {
            TableItem item = new TableItem(gradeTable, SWT.NONE);

            // 序号
            item.setText(0, String.valueOf(index++));

            // 学科名称
            String subjectName = score.getSubjectName() != null ? score.getSubjectName() : "未知学科";
            item.setText(1, subjectName);

            // 平时成绩
            item.setText(2, score.getUsualGrade() != null ? String.format("%.2f", score.getUsualGrade()) : "-");

            // 考试成绩
            item.setText(3, score.getExamGrade() != null ? String.format("%.2f", score.getExamGrade()) : "-");

            // 总成绩
            BigDecimal totalGrade = score.getTotalGrade();
            String totalGradeStr = totalGrade != null ? String.format("%.2f", totalGrade) : "-";
            item.setText(4, totalGradeStr);

            // 评价
            String evaluation = getEvaluation(totalGrade);
            item.setText(5, evaluation);

            // 状态（根据成绩判断）
            String status = getStatus(totalGrade);
            item.setText(6, status);

            // 根据总成绩设置颜色
            Color color = getGradeColor(totalGrade);
            for (int i = 0; i < 7; i++) {
                item.setForeground(i, color);
            }
        }

        // 更新统计信息
        updateStatsLabel(scores.size());

        // 调整列宽
        adjustTableColumns();

        // 更新滚动容器大小
        updateScrollerMinSize();

        // 重新布局整个界面
        parent.layout(true, true);
        gradeTable.getParent().layout(true, true);
        scrolledComposite.layout(true, true);
    }

    /**
     * 调整表格列宽
     */
    private void adjustTableColumns() {
        if (gradeTable.isDisposed() || gradeTable.getItemCount() == 0) {
            return;
        }

        // 获取表格客户区宽度
        int tableWidth = gradeTable.getClientArea().width;

        // 如果表格内容为空，使用默认列宽
        if (gradeTable.getItemCount() == 1 && "暂无成绩数据".equals(gradeTable.getItem(0).getText(1))) {
            int[] defaultWidths = {45, 140, 85, 85, 85, 90, 70};
            for (int i = 0; i < Math.min(gradeTable.getColumnCount(), defaultWidths.length); i++) {
                TableColumn column = gradeTable.getColumn(i);
                if (!column.isDisposed()) {
                    column.setWidth(defaultWidths[i]);
                }
            }
            return;
        }

        // 计算所有列的当前总宽度
        int totalColumnWidth = 0;
        for (int i = 0; i < gradeTable.getColumnCount(); i++) {
            TableColumn column = gradeTable.getColumn(i);
            if (!column.isDisposed()) {
                totalColumnWidth += column.getWidth();
            }
        }

        // 如果总宽度小于表格宽度，调整最后一列以填充空间
        if (totalColumnWidth < tableWidth) {
            TableColumn lastColumn = gradeTable.getColumn(gradeTable.getColumnCount() - 1);
            if (lastColumn != null && !lastColumn.isDisposed()) {
                int currentWidth = lastColumn.getWidth();
                lastColumn.setWidth(currentWidth + (tableWidth - totalColumnWidth));
            }
        }
    }

    /**
     * 更新统计标签
     */
    private void updateStatsLabel(int count) {
        // 查找统计标签并更新
        for (Control child : parent.getChildren()) {
            if (child instanceof Composite) {
                for (Control subChild : ((Composite) child).getChildren()) {
                    if (subChild instanceof Label && "stats".equals(subChild.getData())) {
                        String text = String.format("共 %d 门课程", count);
                        if (count > 10) {
                            text += " (使用滚动条查看更多)";
                        }
                        ((Label) subChild).setText(text);
                        return;
                    }
                }
            }
        }
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
     * 获取成绩状态
     */
    private String getStatus(BigDecimal totalGrade) {
        if (totalGrade == null) return "未录入";
        if (totalGrade.compareTo(new BigDecimal("60")) >= 0) return "通过";
        return "未通过";
    }

    /**
     * 根据成绩获取颜色
     */
    private Color getGradeColor(BigDecimal totalGrade) {
        if (totalGrade == null) return new Color(parent.getDisplay(), 128, 128, 128);
        if (totalGrade.compareTo(new BigDecimal("90")) >= 0) return new Color(parent.getDisplay(), 76, 175, 80);   // 绿色
        if (totalGrade.compareTo(new BigDecimal("80")) >= 0) return new Color(parent.getDisplay(), 255, 193, 7);   // 黄色
        if (totalGrade.compareTo(new BigDecimal("60")) >= 0) return new Color(parent.getDisplay(), 255, 152, 0);   // 橙色
        return new Color(parent.getDisplay(), 244, 67, 54);  // 红色
    }

    /**
     * 清理资源
     */
    public void dispose() {
        // 清理表格字体
        if (tableFont != null && !tableFont.isDisposed()) {
            tableFont.dispose();
        }
        // 清理标题字体
        if (titleFont != null && !titleFont.isDisposed()) {
            titleFont.dispose();
        }
    }
}