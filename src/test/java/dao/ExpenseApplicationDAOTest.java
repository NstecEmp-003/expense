package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.sql.Date;

import org.dbunit.Assertion;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.csv.CsvDataSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fullness.keihiseisan.model.dao.ExpenseApplicationDAO;
import com.fullness.keihiseisan.model.util.ConnectionManager;
import com.fullness.keihiseisan.model.value.ExpenseApplication;

/**
 * ExpenseApplicationDAOクラス単体テストドライバ
 */
public class ExpenseApplicationDAOTest {
    private ExpenseApplicationDAO expenseApplicationDAO;
    private ConnectionManager connectionManager = new ConnectionManager();

    /**
     * テストの前処理
     * 
     * @throws Exception
     */
    @BeforeEach
    public void setUp() throws Exception {
        ConnectionManager connectionManager = new ConnectionManager();
        expenseApplicationDAO = new ExpenseApplicationDAO(connectionManager.getConnection());
    }

    @Test
    public void 経費申請情報の登録() throws Exception {
        // DAOを使った追加処理
        var result = expenseApplicationDAO.insert();
        ExpenseApplication expense = new ExpenseApplication();
        expense.setApplicantUserId("user123");
        expense.setApplicationDate(null);
        expense.setAccountId(0);
        expense.setPaymentDate(null);
        expense.setPayee(null);
        expense.setAmount(0);
        expense.setDescription(null);
        expense.setReceiptPath(null);

        expenseApplicationDAO.insert(expense);
        // 結果と期待値比較
        Assertion.assertEquals(user123);
    }
}
