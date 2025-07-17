package dao;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fullness.keihiseisan.model.dao.UserDAO;
import com.fullness.keihiseisan.model.util.ConnectionManager;
/**
 * UserDAOクラス単体テストドライバ
 */
public class UserDAOTest {
    private UserDAO userDAO = null;
    /**
     * テストの前処理
     * @throws Exception
     */
    @BeforeEach
    public void setUp() throws Exception{
        ConnectionManager connectionManager = new ConnectionManager();
        userDAO = new UserDAO(connectionManager.getConnection());
    }
    @Test
    public void findByUserId_存在するユーザーIDをキーにユーザー情報を取得する() throws Exception{
        var result = userDAO.findByUserId("admin001");
        // nullでないことを検証
        assertNotNull(result);
        // ユーザー情報を検証
        assertEquals("admin001" , result.getUserId());
        //assertEquals("006609f26371f0eaf2984bd066e40c0d5664bd6104c11cc2f2d07bd08241dc0f" , result.getPassword());
        assertEquals("zabcdefghijklmn" , result.getSalt());
        assertEquals("システム 管理者" , result.getUserName());
        assertEquals(2 , result.getDepartmentId());
        assertEquals("開発部" , result.getDepartmentName());
        assertEquals(9 , result.getRoleId());
        assertEquals("システム管理者" , result.getRoleName());
    }
    @Test
    public void findByUserId_存在しないユーザーIdでnullを返す() throws Exception{
        var result = userDAO.findByUserId("10");
        assertNull(result);
    }
    @Test
    public void findAll_全ユーザーの情報を取得する() throws Exception{
        var result = userDAO.findAll();
        // nullでないことを検証
        assertNotNull(result);
        // 件数を検証
        assertEquals(6, result.size());
        result.forEach((r) ->{
            System.out.println(r);
        });
    }
    /**
     * 部長権限を持つユーザーが存在するかチェックする*/
    @Test
    public void testExistsDirector_部長ユーザーが存在する場合はtrue() throws Exception {
        boolean exists = userDAO.existsDirector();
        assertTrue(exists, "部長権限を持つユーザーが存在する");
    }
    @Test
    public void testExistsDirector_部長ユーザーが存在しない場合はfalse() throws Exception {
        boolean exists = userDAO.existsDirector();
        assertFalse(exists, "部長権限を持つユーザーが存在しない");
    }
}
