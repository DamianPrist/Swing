package MainInterface.StudentInterfacePackage;
import DAO.StudentDAO;
import Entity.Student;


public class StudentService {

    // 注入StudentDAO，复用原有DAO功能
    private StudentDAO studentDAO = new StudentDAO();

    /**
     * 专用：学生密码更新方法（仅封装密码更新逻辑，依赖StudentDAO现有方法）
     * @param studentId 学生学号（唯一标识）
     * @param newPassword 新密码
     * @return 密码更新是否成功
     */
    public boolean updateStudentPassword(String studentId, String newPassword) {
        // 1. 入参校验（避免无效调用）
        if (studentId == null || studentId.trim().isEmpty() || newPassword == null || newPassword.length() < 6 || newPassword.length() > 16) {
            return false;
        }

        try {
            // 2. 调用StudentDAO现有方法：获取学生完整信息（必须获取完整对象，因为DAO的updateStudent需要完整属性）
            Student student = studentDAO.getStudentById(studentId);
            if (student == null) {
                return false; // 学生不存在，更新失败
            }

            // 3. 仅更新密码属性，保留其他信息不变（避免覆盖学生其他信息）
            student.setPassword(newPassword);

            // 4. 调用StudentDAO现有方法：updateStudent（复用原有更新逻辑，无需修改DAO）
            return studentDAO.updateStudent(student);
        } catch (RuntimeException e) {
            e.printStackTrace(); // 复用DAO的异常打印逻辑
            return false;
        }
    }


}
