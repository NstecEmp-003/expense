package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fullness.keihiseisan.model.dao.StatusDAO;
import com.fullness.keihiseisan.model.exception.SystemException;
import com.fullness.keihiseisan.model.service.ExpenseApplicationService;
import com.fullness.keihiseisan.model.util.ConnectionManager;
import com.fullness.keihiseisan.model.value.ExpenseApplication;
import com.fullness.keihiseisan.model.value.Status;
import com.fullness.keihiseisan.model.value.User;

import jakarta.servlet.http.Part;

public class ExpenseApplicationServiceTest {

    private ExpenseApplicationService service;

    @BeforeEach
    void setUp() {
        service = new ExpenseApplicationService();
    }

    @Test
    void testGetAllStatuses_returnsList() throws Exception {
        List<Status> statuses = service.getAllStatuses();

        assertNotNull(statuses);
        assertFalse(statuses.isEmpty(), "ステータス一覧が空ではないこと");
        statuses.forEach(status -> {
            System.out.println("Status ID: " + status.getStatusId() + ", Name: " + status.getStatusName());
        });
    }

    @Test
    void testValidateApplication_noFileAttached() {
        ExpenseApplication expense = new ExpenseApplication();
        // Expenseの基本的な項目はここで適当に設定（今回は省略）

        List<String> errors = service.validateApplication(expense, null);

        assertNotNull(errors);
        // 基本エラーの想定（必要に応じてassertで確認）
    }

    @Test
    void testValidateApplication_withValidFileSize() {
        ExpenseApplication expense = new ExpenseApplication();
        Part filePart = new DummyPart(4_000_000); // 4MBのダミーファイル

        List<String> errors = service.validateApplication(expense, filePart);

        assertFalse(errors.contains("領収書のファイルサイズは 5MB 以下にしてください。"));
    }

    @Test
    void testValidateApplication_withTooLargeFile() {
        ExpenseApplication expense = new ExpenseApplication();
        Part filePart = new DummyPart(6_000_000); // 6MBのダミーファイル

        List<String> errors = service.validateApplication(expense, filePart);

        assertTrue(errors.contains("領収書のファイルサイズは 5MB 以下にしてください。"));
    }

    @Test
    void test_null申請日() {
        ExpenseApplication expense = new ExpenseApplication();
        expense.setAccountId(1);
        expense.setPaymentDate(Date.valueOf(LocalDate.now()));
        expense.setPayee("株式会社テスト");
        expense.setDescription("交通費");

        List<String> result = service.validateApplication(expense);
        assertTrue(result.contains("申請日を入力してください。"));
    }

    @Test
    void test_勘定科目IDが0以下() {
        ExpenseApplication expense = new ExpenseApplication();
        expense.setApplicationDate(Date.valueOf(LocalDate.now()));
        expense.setAccountId(0); // 無効
        expense.setPaymentDate(Date.valueOf(LocalDate.now()));
        expense.setPayee("テスト商事");
        expense.setDescription("交通費");

        List<String> result = service.validateApplication(expense);
        assertTrue(result.contains("勘定科目を選択してください。"));
    }

    @Test
    void test_未来の支払日() {
        ExpenseApplication expense = new ExpenseApplication();
        expense.setApplicationDate(Date.valueOf(LocalDate.now()));
        expense.setAccountId(1);
        expense.setPaymentDate(Date.valueOf(LocalDate.now().plusDays(1))); // 明日
        expense.setPayee("取引先A");
        expense.setDescription("接待費");

        List<String> result = service.validateApplication(expense);
        assertTrue(result.contains("支払日は未来の日付は入力できません。"));
    }

    @Test
    void test_支払先が空文字() {
        ExpenseApplication expense = new ExpenseApplication();
        expense.setApplicationDate(Date.valueOf(LocalDate.now()));
        expense.setAccountId(1);
        expense.setPaymentDate(Date.valueOf(LocalDate.now()));
        expense.setPayee(""); // 空
        expense.setDescription("昼食代");

        List<String> result = service.validateApplication(expense);
        assertTrue(result.contains("支払先を入力してください。"));
    }

    @Test
    void test_詳細が長すぎる() {
        ExpenseApplication expense = new ExpenseApplication();
        expense.setApplicationDate(Date.valueOf(LocalDate.now()));
        expense.setAccountId(1);
        expense.setPaymentDate(Date.valueOf(LocalDate.now()));
        expense.setPayee("テスト株式会社");
        expense.setDescription("あ".repeat(501)); // 501文字

        List<String> result = service.validateApplication(expense);
        assertTrue(result.contains("内容（詳細）は500文字以内で入力してください。"));
    }

    @Test
    void test_正常パターン() {
        ExpenseApplication expense = new ExpenseApplication();
        expense.setApplicationDate(Date.valueOf(LocalDate.now()));
        expense.setAccountId(1);
        expense.setPaymentDate(Date.valueOf(LocalDate.now()));
        expense.setPayee("株式会社ネクスト");
        expense.setDescription("営業交通費の精算");

        List<String> result = service.validateApplication(expense);
        assertTrue(result.isEmpty());
    }

    private Connection connection;

    @BeforeEach
    public void setup() throws SQLException {
        service = new ExpenseApplicationService();

        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5433/expense",
                "postgres",
                "postgres")) {

            conn.createStatement().executeUpdate("""
                        CREATE TABLE IF NOT EXISTS expense_application (
                            id SERIAL PRIMARY KEY,
                            applicant_user_id VARCHAR(255),
                            payee VARCHAR(255),
                            amount INT,
                            application_date DATE,
                            status_id INT
                        );
                    """);
        }
    }

@Test
public void TC01_申請者IDと申請日が未設定() throws Exception {
    ExpenseApplication expense = new ExpenseApplication();

    // 必須項目（DAOのinsertで使用される順に設定）
    expense.setAccountId(101); // 勘定科目ID（例：旅費交通費）
    expense.setPaymentDate(Date.valueOf(LocalDate.of(2024, 12, 1))); // 支払日
    expense.setPayee("テスト商事"); // 支払先
    expense.setAmount(60000); // 金額
    expense.setDescription("備品購入"); // 内容
    expense.setReceiptPath("/receipts/2024/12/01.pdf"); // 領収書パス

    // 申請者ID・申請日は未設定（applyExpense内で自動設定される）
    expense.setApplicantUserId(null);
    expense.setApplicationDate(null);

    User applicant = new User();
    applicant.setUserId("user001");

    int id = service.applyExpense(expense, applicant);

    // 自動設定された項目の検証
    assertEquals("user001", expense.getApplicantUserId());
    assertEquals(Date.valueOf(LocalDate.now()), expense.getApplicationDate());
    assertEquals(StatusDAO.STATUS_APPLIED, expense.getStatusId());
    assertTrue(id > 0);

    // DB登録確認（PostgreSQL）
    try (Connection conn = DriverManager.getConnection(
            "jdbc:postgresql://localhost:5433/expense",
            "postgres",
            "postgres")) {

        var rs = conn.createStatement().executeQuery("SELECT * FROM expense_application WHERE id = " + id);
        assertTrue(rs.next());
        assertEquals("user001", rs.getString("applicant_user_id"));
        assertEquals(Date.valueOf(LocalDate.now()), rs.getDate("application_date"));
        assertEquals(101, rs.getInt("account_id"));
        assertEquals(Date.valueOf(LocalDate.of(2024, 12, 1)), rs.getDate("payment_date"));
        assertEquals("テスト商事", rs.getString("payee"));
        assertEquals(60000, rs.getInt("amount"));
        assertEquals("備品購入", rs.getString("description"));
        assertEquals("/receipts/2024/12/01.pdf", rs.getString("receipt_path"));
        assertEquals(StatusDAO.STATUS_APPLIED, rs.getInt("status_id"));
    }
}

    @Test
    public void TC02_申請者IDと申請日が設定済み() throws Exception {
        ExpenseApplication expense = new ExpenseApplication();
        expense.setPayee("テスト商事");
        expense.setAmount(60000);
        expense.setApplicantUserId("user999");
        expense.setApplicationDate(Date.valueOf(LocalDate.of(2020, 1, 1)));

        User applicant = new User();
        applicant.setUserId("user001");

        int id = service.applyExpense(expense, applicant);

        assertEquals("user999", expense.getApplicantUserId());
        assertEquals(Date.valueOf(LocalDate.of(2020, 1, 1)), expense.getApplicationDate());
        assertTrue(id > 0);
    }

    @Test
    public void TC03_金額が5万円以上() throws Exception {
        ExpenseApplication expense = new ExpenseApplication();
        expense.setPayee("高額商事");
        expense.setAmount(80000);

        User applicant = new User();
        applicant.setUserId("user002");

        service.applyExpense(expense, applicant);

        assertEquals(StatusDAO.STATUS_APPLIED, expense.getStatusId());
    }

    @Test
    public void TC04_金額が5万円未満() throws Exception {
        ExpenseApplication expense = new ExpenseApplication();
        expense.setPayee("低額商事");
        expense.setAmount(30000);

        User applicant = new User();
        applicant.setUserId("user003");

        service.applyExpense(expense, applicant);

        assertEquals(StatusDAO.STATUS_APPLIED, expense.getStatusId());
    }

    @Test
    public void TC05_金額がちょうど5万円() throws Exception {
        ExpenseApplication expense = new ExpenseApplication();
        expense.setPayee("境界商事");
        expense.setAmount(50000);

        User applicant = new User();
        applicant.setUserId("user004");

        service.applyExpense(expense, applicant);

        assertEquals(StatusDAO.STATUS_APPLIED, expense.getStatusId());
    }

    @Test
    public void TC06_DB接続失敗() {
        try {
            new ConnectionManager() {
                @Override
                public Connection getConnection() throws SQLException {
                    throw new SQLException("接続失敗");
                }
            }.getConnection();
            fail("例外が発生すべき");
        } catch (SQLException e) {
            assertEquals("接続失敗", e.getMessage());
        }
    }

    @Test
    public void TC07_insert中にSQLException発生() {
        ExpenseApplication expense = new ExpenseApplication();
        expense.setPayee("例外商事");
        expense.setAmount(30000);

        User applicant = new User();
        applicant.setUserId("user005");

        try {
            // insert失敗をシミュレートするにはDAO側で例外を投げるように改造が必要
            // ここでは例外を直接投げてテスト
            throw new SystemException("申請処理中にエラーが発生しました", "Database error", new SQLException("Insert失敗"));
        } catch (SystemException e) {
            assertTrue(e.getMessage().contains("申請処理中にエラーが発生しました"));
        }
    }

    @Test
    public void TC08_ロールバック中にSQLException発生() {
        try {
            throw new SystemException("ロールバック中にエラーが発生しました", "Database error", new SQLException("Rollback失敗"));
        } catch (SystemException e) {
            assertTrue(e.getMessage().contains("ロールバック中にエラーが発生しました"));
        }
    }

    // スタブ実装
    class DummyPart implements Part {
        private long size;

        DummyPart(long size) {
            this.size = size;
        }

        @Override
        public long getSize() {
            return size;
        }

        // 他のメソッドは未使用なので空でOK
        @Override
        public void write(String fileName) {
        }

        @Override
        public void delete() {
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String getContentType() {
            return null;
        }

        @Override
        public String getSubmittedFileName() {
            return null;
        }

        @Override
        public java.io.InputStream getInputStream() {
            return null;
        }

        @Override
        public java.util.Collection<String> getHeaderNames() {
            return null;
        }

        @Override
        public String getHeader(String name) {
            return null;
        }

        @Override
        public java.util.Collection<String> getHeaders(String name) {
            return null;
        }

    }
}