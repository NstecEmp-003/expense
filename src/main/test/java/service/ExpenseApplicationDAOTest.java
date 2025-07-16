import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fullness.keihiseisan.model.service.ExpenseApplicationService;
import com.fullness.keihiseisan.model.value.Status;

public class ExpenseApplicationDAOTest {

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

    
}