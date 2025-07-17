package dao;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fullness.keihiseisan.model.dao.AccountDAO;
import com.fullness.keihiseisan.model.util.ConnectionManager;

public class AccountDAOTest {
    private AccountDAO accountDAO = null;
    /**
     * テストの前処理
     * @throws Exception
     */
    @BeforeEach
    public void setUp() throws Exception{
        ConnectionManager connectionManager = new ConnectionManager();
        accountDAO = new AccountDAO(connectionManager.getConnection());
    }
    @Test
    public void testSelectAll_全ての勘定科目情報を取得する() throws Exception{
        var result = accountDAO.selectAll();
        // nullでないことを検証
        assertNotNull(result);
        // 件数を検証
        assertEquals(3, result.size());
        result.forEach((r) ->{
            System.out.println(r);
        });
    }
}

