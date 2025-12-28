package MainInterface.SubjectManagePackage;

import Entity.Subject;
import DAO.SubjectDAO;
import DAO.ScoreDAO;
import java.util.List;

/**
 * 学科业务层：处理学科的新增、查询、删除，以及级联删除成绩
 */
public class SubjectService {
    // 依赖DAO层：学科数据访问和成绩数据访问
    private final SubjectDAO subjectDAO;
    private final ScoreDAO scoreDAO;

    // 构造方法：初始化DAO依赖（符合现有代码的DAO使用方式）
    public SubjectService() {
        this.subjectDAO = new SubjectDAO();
        this.scoreDAO = new ScoreDAO();
    }

    /**
     * 1. 新增学科（业务层）
     * @param subjectName 学科名称
     * @return 新增是否成功（true：成功；false：失败/名称重复）
     */
    public boolean addSubject(String subjectName) {
        // 1. 基础校验：名称非空
        if (subjectName == null || subjectName.trim().isEmpty()) {
            return false;
        }
        String trimName = subjectName.trim();

        // 2. 校验是否已存在（直接调用DAO查询，比全量查询更高效）
        if (subjectDAO.getSubjectIdByName(trimName) != null) {
            return false; // 学科已存在
        }

        // 3. 调用DAO执行新增（依赖SubjectDAO中新增的addSubject方法）
        return subjectDAO.addSubject(trimName);
    }

    /**
     * 2. 查询所有学科（业务层）
     * @return 学科列表（无数据时返回空列表，而非null）
     */
    public List<Subject> getAllSubjects() {
        // 直接调用SubjectDAO的查询方法（原代码依赖ScoreManage不合理，改为直接查学科表）
        return subjectDAO.getAllSubjects();
    }

    /**
     * 3. 删除学科（核心：级联删除成绩+事务保证）
     * @param subjectName 学科名称
     * @return 删除是否成功
     */
    public boolean deleteSubject(String subjectName) {
        // 1. 基础校验
        if (subjectName == null || subjectName.trim().isEmpty()) {
            return false;
        }
        String trimName = subjectName.trim();

        // 2. 获取学科ID（不存在则直接返回失败）
        Integer subjectId = subjectDAO.getSubjectIdByName(trimName);
        if (subjectId == null) {
            return false; // 学科不存在
        }

        // 3. 调用DAO执行删除（SubjectDAO的deleteSubject已处理级联删除和事务）
        return subjectDAO.deleteSubject(subjectId);
    }

    /**
     * 4. 校验学科是否已存在（辅助方法）
     * @param subjectName 学科名称
     * @return 是否存在
     */
    public boolean isSubjectExists(String subjectName) {
        if (subjectName == null || subjectName.trim().isEmpty()) {
            return false;
        }
        // 直接通过DAO查询ID，比全量加载列表遍历更高效
        return subjectDAO.getSubjectIdByName(subjectName.trim()) != null;
    }
}
