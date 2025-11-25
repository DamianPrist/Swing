package Entity;

import java.math.BigDecimal;

public class Student  {
    //todo Student实体类

    private Long studentId;
    private String studentName;
    private String gender;
    private BigDecimal usualGrade;
    private BigDecimal examGrade;

    public Student() {
    }

    // 全参构造器
    public Student(Long studentId, String studentName, String gender, BigDecimal usualGrade, BigDecimal examGrade) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.gender = gender;
        this.usualGrade = usualGrade;
        this.examGrade = examGrade;
    }

    // Getters and Setters
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public BigDecimal getUsualGrade() {
        return usualGrade;
    }

    public void setUsualGrade(BigDecimal usualGrade) {
        this.usualGrade = usualGrade;
    }

    public BigDecimal getExamGrade() {
        return examGrade;
    }

    public void setExamGrade(BigDecimal examGrade) {
        this.examGrade = examGrade;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", studentName='" + studentName + '\'' +
                ", gender='" + gender + '\'' +
                ", usualGrade=" + usualGrade +
                ", examGrade=" + examGrade +
                '}';
    }
}
