package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
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

    /* applyExpense(ExpenseApplication expense, User applicant)のテスト */

   

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