package dao;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fullness.keihiseisan.model.dao.DepartmentDAO;
import com.fullness.keihiseisan.model.util.ConnectionManager;
/**
 * RoleDAOクラス単体テストドライバ
 */
public class DepartmentDAOTest {
    private DepartmentDAO departmentDAO = null;
    /**
     * テストの前処理
     * @throws Exception
     */
    @BeforeEach
    public void setUp() throws Exception{
        ConnectionManager connectionManager = new ConnectionManager();
        departmentDAO = new DepartmentDAO(connectionManager.getConnection());
    }
    @Test
    public void testSelectAll_すべての部門情報を取得できる() throws Exception{
        var result = departmentDAO.selectAll();
        // nullでないことを検証
        assertNotNull(result);
        // 件数を検証
        assertEquals(3, result.size());
        result.forEach((r) ->{
            System.out.println(r);
        });
    }
    @Test
    public void testfindById_存在する部門Idで部門情報を取得できる() throws Exception{
        var result = departmentDAO.findById(1);
        assertNotNull(result);
        assertEquals(1 , result.getDeptId());
        assertEquals("営業部" , result.getDeptName());
    }
    @Test
    public void testfindById_存在しない部門Idでnullを返す() throws Exception{
        var result = departmentDAO.findById(4);
        assertNull(result);
    }
}
