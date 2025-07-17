package dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fullness.keihiseisan.model.dao.StatusDAO;
import com.fullness.keihiseisan.model.util.ConnectionManager;

public class StatusDAOTest {
    private StatusDAO statusDAO = null;
    /**
     * テストの前処理
     * @throws Exception
     */
    @BeforeEach
    public void setUp() throws Exception{
        ConnectionManager connectionManager = new ConnectionManager();
        statusDAO = new StatusDAO(connectionManager.getConnection());
    }
    @Test
    public void testSelectAll_全てのステータス情報を取得する() throws Exception{
        var result = statusDAO.selectAll();
        // nullでないことを検証
        assertNotNull(result);
        // 件数を検証
        assertEquals(5, result.size());
        result.forEach((r) ->{
            System.out.println(r);
        });
    }
}

