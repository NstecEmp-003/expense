import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fullness.keihiseisan.model.service.ExpenseApplicationService;
import com.fullness.keihiseisan.model.value.Status;

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

    // ダミーPart実装（サイズだけ返す簡易スタブ）
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