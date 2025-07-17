package dao;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fullness.keihiseisan.model.dao.RoleDAO;
import com.fullness.keihiseisan.model.util.ConnectionManager;
/**
 * RoleDAOクラス単体テストドライバ
 */
public class RoleDAOTest {
    private RoleDAO roleDAO = null;
    /**
     * テストの前処理
     * @throws Exception
     */
    @BeforeEach
    public void setUp() throws Exception{
        ConnectionManager connectionManager = new ConnectionManager();
        roleDAO = new RoleDAO(connectionManager.getConnection());
    }
    @Test
    public void testSelectAll_すべてのロールを取得できる() throws Exception{
        var result = roleDAO.selectAll();
        // nullでないことを検証
        assertNotNull(result);
        // 件数を検証
        assertEquals(4, result.size());
        result.forEach((r) ->{
            System.out.println(r);
        });
    }
    @Test
    public void testfindById_存在するロールIdでロールを取得できる() throws Exception{
        var result = roleDAO.findById(9);
        assertNotNull(result);
        assertEquals(9 , result.getRoleId());
        assertEquals("システム管理者" , result.getRoleName());
    }
    @Test
    public void testfindById_存在しないロールIdでnullを返す() throws Exception{
        var result = roleDAO.findById(10);
        assertNull(result);
    }
}
