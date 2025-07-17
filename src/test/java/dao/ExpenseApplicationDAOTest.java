package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fullness.keihiseisan.model.dao.ExpenseApplicationDAO;
import com.fullness.keihiseisan.model.util.ConnectionManager;
import com.fullness.keihiseisan.model.value.ExpenseApplication;

public class ExpenseApplicationDAOTest {

    private ExpenseApplicationDAO expenseApplicationDAO;
    private ConnectionManager connectionManager = new ConnectionManager();
    /**
     * テストの前処理
     * @throws Exception
     */
    @BeforeEach
    public void setUp() throws Exception{
        ConnectionManager connectionManager = new ConnectionManager();
        expenseApplicationDAO = new ExpenseApplicationDAO(connectionManager.getConnection());
    }

    @Test 
    public void testInsert() throws Exception{
      // 新規経費申請データを作成
        ExpenseApplication expense = new ExpenseApplication();
        expense.setApplicantUserId("emp001");
        expense.setApplicationDate(Date.valueOf("2025-07-17"));
        expense.setAccountId(1);
        expense.setPaymentDate(Date.valueOf("2025-07-17")); 
        expense.setPayee("JR東日本");
        expense.setAmount(2000);
        expense.setDescription("出張");
        expense.setReceiptPath(null);

 // 登録されたデータを取得して検証);
        int newId = expenseApplicationDAO.insert(expense);
        
        var result = expenseApplicationDAO.findById(newId);
        
        System.out.println("登録データ:" + result);
        
        assertNotNull(result);
        assertEquals("emp001", result.getApplicantUserId());
        
    }
}
