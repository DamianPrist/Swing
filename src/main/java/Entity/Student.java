package Entity;

public class Student {
    private String studentId;
    private String studentName;
    private String gender;
    private String className;
    private String phone;
    private String password;  // 添加密码字段

    public Student() {
    }

    public Student(String studentId, String studentName, String gender, String className, String phone) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.gender = gender;
        this.className = className;
        this.phone = phone;
    }

    // 添加密码的构造方法
    public Student(String studentId, String studentName, String gender, String className, String phone, String password) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.gender = gender;
        this.className = className;
        this.phone = phone;
        this.password = password;
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", gender='" + gender + '\'' +
                ", className='" + className + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + (password != null ? "***" : "null") + '\'' +
                '}';
    }
}