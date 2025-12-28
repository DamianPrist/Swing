package MainInterface.TeacherManagePackage;
import DAO.UserDAO; // 导入数据访问层的UserDAO
import Entity.User; // 按需导入（若无需使用可忽略）

public class TeacherManage {

    private UserDAO userDAO = new UserDAO();
    public boolean updateUserPassword(String username, String newPassword) {
        // 1. 入参合法性校验（可选，补充基础校验，避免无效的数据库调用）
        if (username == null || username.trim().isEmpty()
                || newPassword == null || newPassword.trim().isEmpty()) {
            return false;
        }

        try {
            // 2. 调用UserDAO的密码更新方法，完成数据库持久化
            // 直接复用你之前在UserDAO中新增的updateUserPassword方法
            return userDAO.updateUserPassword(username.trim(), newPassword.trim());
        } catch (RuntimeException e) {
            // 3. 捕获DAO层抛出的异常，统一处理（避免异常向上扩散到界面层）
            e.printStackTrace(); // 打印异常日志，便于排查问题
            return false; // 异常时返回更新失败
        }
    }
}
