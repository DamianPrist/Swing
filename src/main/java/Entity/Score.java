package Entity;

import java.math.BigDecimal;

public class Score {
    private Integer id;
    private String studentId;
    private Integer subjectId;
    private BigDecimal usualGrade;
    private BigDecimal examGrade;
    private BigDecimal totalGrade;
    private String studentName;

    // 为了方便显示，可以添加关联字段（非数据库字段）
    private String subjectName;

    public Score() {
    }

    public Score(String studentId, Integer subjectId, BigDecimal usualGrade, BigDecimal examGrade) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.usualGrade = usualGrade;
        this.examGrade = examGrade;
        // 计算总成绩：平时成绩*0.4 + 考试成绩*0.6
        if (usualGrade != null && examGrade != null) {
            this.totalGrade = usualGrade.multiply(new BigDecimal("0.4"))
                    .add(examGrade.multiply(new BigDecimal("0.6")));
        }
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public BigDecimal getUsualGrade() {
        return usualGrade;
    }

    public void setUsualGrade(BigDecimal usualGrade) {
        this.usualGrade = usualGrade;
        // 更新总成绩
        calculateTotalGrade();
    }

    public BigDecimal getExamGrade() {
        return examGrade;
    }

    public void setExamGrade(BigDecimal examGrade) {
        this.examGrade = examGrade;
        // 更新总成绩
        calculateTotalGrade();
    }

    public BigDecimal getTotalGrade() {
        return totalGrade;
    }

    public void setTotalGrade(BigDecimal totalGrade) {
        this.totalGrade = totalGrade;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }


    // 计算总成绩的方法
    private void calculateTotalGrade() {
        if (usualGrade != null && examGrade != null) {
            this.totalGrade = usualGrade.multiply(new BigDecimal("0.4"))
                    .add(examGrade.multiply(new BigDecimal("0.6")));
        }
    }

    @Override
    public String toString() {
        return "Score{" +
                "id=" + id +
                ", studentId='" + studentId + '\'' +
                ", subjectId=" + subjectId +
                ", usualGrade=" + usualGrade +
                ", examGrade=" + examGrade +
                ", totalGrade=" + totalGrade +
                '}';
    }
}