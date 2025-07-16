package com.fullness.keihiseisan.model.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.Part;
import com.fullness.keihiseisan.model.dao.AccountDAO;
import com.fullness.keihiseisan.model.dao.ExpenseApplicationDAO;
import com.fullness.keihiseisan.model.dao.StatusDAO;
import com.fullness.keihiseisan.model.dao.UserDAO;
import com.fullness.keihiseisan.model.util.ConnectionManager;
import com.fullness.keihiseisan.model.util.ValidationUtil;
import com.fullness.keihiseisan.model.value.Account;
import com.fullness.keihiseisan.model.value.ExpenseApplication;
import com.fullness.keihiseisan.model.value.Status;
import com.fullness.keihiseisan.model.value.User;
import com.fullness.keihiseisan.model.util.FileUploadUtil;
import com.fullness.keihiseisan.model.exception.SystemException;
import java.util.logging.Logger;

/**
 * 経費申請サービスクラス
 * 経費申請のバリデーション、ファイルアップロード、経費申請処理を行う
 */
public class ExpenseApplicationService {
    /** ロガー */
    private static final Logger logger = Logger.getLogger(ExpenseApplicationService.class.getName());
    /**
     * 経費申請のバリデーションを行う
     * @param expense
     * @param filePart
     * @return
     */
    public List<String> validateApplication(ExpenseApplication expense, Part filePart) {
        List<String> error_list = validateApplication(expense);
        // ファイルが添付されている場合のみ検証
        if (filePart != null && filePart.getSize() > 0) {
            if (!ValidationUtil.isFileSizeValid(filePart.getSize())) {
                error_list.add("領収書のファイルサイズは 5MB 以下にしてください。");
            }
        }
        return error_list;
    }
    /**
     * 経費申請のバリデーションを行う
     * @param expense
     * @return
     */
    private List<String> validateApplication(ExpenseApplication expense) {
        List<String> errors = new ArrayList<>();
        // 申請日の検証
        Date applicationDate = expense.getApplicationDate();
        if (applicationDate == null) { // まずnullチェック
            errors.add("申請日を入力してください。");
        } else {
            // nullでなければ形式と論理チェック
            String dateStr = applicationDate.toString();
            System.out.println("検証する申請日: " + dateStr);
            if (!ValidationUtil.isValidDate(dateStr)) {
                errors.add("申請日の形式が正しくありません。");
            } else {
                LocalDate appDate = applicationDate.toLocalDate();
                if (!appDate.isEqual(LocalDate.now())) {
                    errors.add("申請日は本日の日付を入力してください。");
                }
            }
        }
        // 勘定科目IDの検証
        if (expense.getAccountId() <= 0) { // 0以下ならエラー (ApplyServletで空なら0が入る)
            errors.add("勘定科目を選択してください。");
        }
        // 支払日の検証
        Date paymentDate = expense.getPaymentDate();
        if (paymentDate == null) { // まずnullチェック
            errors.add("支払日を入力してください。");
        } else {
            // nullでなければ形式と論理チェック
            String dateStr = paymentDate.toString();
            System.out.println("検証する支払日: " + dateStr);
            if (!ValidationUtil.isValidDate(dateStr)) {
                errors.add("支払日の形式が正しくありません。");
            } else {
                LocalDate payDate = paymentDate.toLocalDate();
                if (payDate.isAfter(LocalDate.now())) {
                    errors.add("支払日は未来の日付は入力できません。");
                }
            }
        }
        // 支払先の検証
        String payee = expense.getPayee();
        if (ValidationUtil.isEmpty(payee)) {
            errors.add("支払先を入力してください。");
        } else if (!ValidationUtil.isLengthValid(payee, 100)) {
            errors.add("支払先は100文字以内で入力してください。");
        }
        // 金額の検証
        // 内容の検証
        String description = expense.getDescription();
        if (ValidationUtil.isEmpty(description)) {
            errors.add("内容（詳細）を入力してください。");
        } else if (!ValidationUtil.isLengthValid(description, 500)) {
            errors.add("内容（詳細）は500文字以内で入力してください。");
        }
        return errors;
    }
    /**
     * ファイルアップロード処理を実行します。
     * @param filePart アップロードされたファイルパート
     * @param baseDirectory アプリケーションのベースディレクトリ
     * @return 保存されたファイルの相対パス、またはファイルが添付されていない場合はnull
     */
    public String saveReceiptFile(Part filePart, String baseDirectory) throws Exception {
        if (filePart == null || filePart.getSize() == 0) {
            return null; // ファイル未添付は正常
        }
        // FileUploadUtilを使ってファイルを保存し、相対パスを返す
        return FileUploadUtil.saveReceipt(filePart, baseDirectory);
    }
    /**
     * すべての勘定科目を取得する
     * @return 勘定科目リスト
     * @throws SystemException データベースエラー発生時
     */
    public List<Account> readAllAccounts() throws SystemException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            AccountDAO dao = new AccountDAO(connectionManager.getConnection());
            return dao.selectAll();
        } catch (SQLException e) {
            throw new SystemException(
                "勘定科目一覧の取得に失敗しました",
                "Failed to get account list",
                e
            );
        }
    }
    /**
     * 経費申請を行う
     * @param expense 申請情報
     * @return 生成された申請ID
     * @throws SystemException データベースエラー発生時
     */
    public int applyExpense(ExpenseApplication expense, User applicant) throws SystemException {
        logger.info("経費申請処理開始: " + expense.getPayee() + ", 金額: " + expense.getAmount());
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            Connection connection = connectionManager.getConnection();
            boolean originalAutoCommit = connection.getAutoCommit();
            try {
                connection.setAutoCommit(false);
                logger.info("経費申請処理開始: " + expense.getPayee() + ", 金額: " + expense.getAmount());
                // 申請者IDが未設定の場合は設定
                if (expense.getApplicantUserId() == null || expense.getApplicantUserId().isEmpty()) {
                    expense.setApplicantUserId(applicant.getUserId());
                }
                // 申請日が未設定の場合は今日の日付を設定
                if (expense.getApplicationDate() == null) {
                    expense.setApplicationDate(Date.valueOf(LocalDate.now()));
                }
                // 金額に応じて承認者を設定
                // 5万円以上なら部長承認も必要、それ以下なら課長のみ
                if (expense.getAmount() >= 50000) {
                    expense.setStatusId(StatusDAO.STATUS_APPLIED); // 課長承認待ち
                } else {
                    expense.setStatusId(StatusDAO.STATUS_APPLIED); // 課長承認待ち
                }
                // データベースに登録
                ExpenseApplicationDAO dao = new ExpenseApplicationDAO(connection);
                int newApplicationId = dao.insert(expense);
                // トランザクションをコミット
                connection.commit();
                logger.info("トランザクションをコミットしました");
                return newApplicationId;
            } catch (SQLException e) {
                try {
                    connection.rollback();
                    logger.info("トランザクションをロールバックしました");
                } catch (SQLException rollbackEx) {
                    throw new SystemException(
                        "ロールバック中にエラーが発生しました",
                        "Database error while applying expense",
                        rollbackEx
                    );
                }
                throw new SystemException(
                    "申請処理中にエラーが発生しました",
                    "Database error while applying expense",
                    e
                );
            } finally {
                try {
                    connection.setAutoCommit(originalAutoCommit);
                    logger.info("AutoCommit設定を復元しました");
                } catch (SQLException e) {
                    throw new SystemException(
                        "AutoCommit設定の復元中にエラーが発生しました",
                        "Database error while applying expense",
                        e
                    );
                }
            }
        } catch (SQLException e) {
            throw new SystemException(
                "申請処理中にエラーが発生しました",
                "Database error while applying expense",
                e
            );
        }
    }
    /**
     * 自分の経費申請一覧を取得する
     * @param userId ユーザーID
     * @return 申請一覧
     * @throws SystemException データベースエラー発生時
     */
    public List<ExpenseApplication> getMyApplications(String userId) throws SystemException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            Connection conn = connectionManager.getConnection();
            ExpenseApplicationDAO expenseDao = new ExpenseApplicationDAO(conn);
            AccountDAO accountDao = new AccountDAO(conn);
            StatusDAO statusDao = new StatusDAO(conn);
            List<ExpenseApplication> list = expenseDao.findByApplicantId(userId);
            list.forEach(app -> logger.info("申請ID: " + app.getApplicationId()));
            List<Account> allAccounts = accountDao.selectAll();
            List<Status> allStatuses = statusDao.selectAll();
            // 勘定科目名とステータス名を設定
            for(ExpenseApplication app : list) {
                allAccounts.stream()
                    .filter(a -> a.getAccountId() == app.getAccountId())
                    .findFirst()
                    .ifPresent(a -> app.setAccountName(a.getAccountName()));
                allStatuses.stream()
                    .filter(s -> s.getStatusId() == app.getStatusId())
                    .findFirst()
                    .ifPresent(s -> app.setStatusName(s.getStatusName()));
            }
            return list;
        } catch (SQLException e) {
            throw new SystemException(
                "申請一覧の取得に失敗しました",
                "Failed to get expense application list for user: " + userId,
                e
            );
        }
    }
    /**
     * 絞り込み条件を指定して経費申請一覧を取得する
     * @param userId 申請者ID
     * @param startDateStr 申請開始日（文字列）
     * @param endDateStr 申請終了日（文字列）
     * @param accountId 勘定科目ID
     * @param statusId ステータスID
     * @param minAmountStr 最小金額（文字列）
     * @param maxAmountStr 最大金額（文字列）
     * @param payee 支払先（部分一致）
     * @return 検索結果リスト
     * @throws SystemException データベースエラー発生時
     */
    public List<ExpenseApplication> getFilteredApplications(
            String userId,
            String startDateStr,
            String endDateStr,
            String accountIdStr,
            String statusIdStr,
            String minAmountStr,
            String maxAmountStr,
            String payee) throws SystemException {
        
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            Connection conn = connectionManager.getConnection();
            ExpenseApplicationDAO expenseDao = new ExpenseApplicationDAO(conn);
            // 文字列からDate型に変換
            Date startDate = null;
            if (startDateStr != null && !startDateStr.isEmpty()) {
                try {
                    startDate = Date.valueOf(startDateStr);
                } catch (IllegalArgumentException e) {
                    // 無効な形式の場合はnullのまま
                }
            }
            Date endDate = null;
            if (endDateStr != null && !endDateStr.isEmpty()) {
                try {
                    endDate = Date.valueOf(endDateStr);
                } catch (IllegalArgumentException e) {
                    // 無効な形式の場合はnullのまま
                }
            }
            // 文字列からInteger型に変換
            Integer accountId = null;
            if (accountIdStr != null && !accountIdStr.isEmpty()) {
                try {
                    accountId = Integer.parseInt(accountIdStr);
                } catch (NumberFormatException e) {
                    // 無効な形式の場合はnullのまま
                }
            }
            // ステータスIDの検証
            Integer statusId = null;
            if (statusIdStr != null && !statusIdStr.isEmpty()) {
                try {
                    statusId = Integer.parseInt(statusIdStr);
                } catch (NumberFormatException e) {
                    // 無効な形式の場合はnullのまま
                }
            }
            // 最小金額の検証
            Integer minAmount = null;
            if (minAmountStr != null && !minAmountStr.isEmpty()) {
                try {
                    minAmount = Integer.parseInt(minAmountStr);
                } catch (NumberFormatException e) {
                    // 無効な形式の場合はnullのまま
                }
            }
            // 最大金額の検証
            Integer maxAmount = null;
            if (maxAmountStr != null && !maxAmountStr.isEmpty()) {
                try {
                    maxAmount = Integer.parseInt(maxAmountStr);
                } catch (NumberFormatException e) {
                    // 無効な形式の場合はnullのまま
                }
            }
            // DAOを呼び出して絞り込み検索実行
            return expenseDao.findByFilter(
                    userId, 
                    startDate, 
                    endDate, 
                    accountId, 
                    statusId, 
                    minAmount, 
                    maxAmount, 
                    payee);
        } catch (SQLException e) {
            throw new SystemException(
                "申請一覧の取得に失敗しました",
                "Failed to get filtered expense application list",
                e
            );
        }
    }
    /**
     * 経費申請詳細を取得する
     * @param applicationId 申請ID
     * @return 申請詳細
     * @throws SystemException データベースエラー発生時
     */
    public ExpenseApplication getApplicationDetail(int applicationId) throws SystemException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            Connection conn = connectionManager.getConnection();
            ExpenseApplicationDAO expenseDao = new ExpenseApplicationDAO(conn);
            UserDAO userDao = new UserDAO(conn);
            AccountDAO accountDao = new AccountDAO(conn);
            StatusDAO statusDao = new StatusDAO(conn);
            final ExpenseApplication dto = expenseDao.findById(applicationId);
            if (dto != null) {
                User applicant = userDao.findByUserId(dto.getApplicantUserId());
                if (applicant != null) {
                    dto.setApplicantName(applicant.getUserName());
                }

                List<Account> accounts = accountDao.selectAll();
                accounts.stream()
                    .filter(a -> a.getAccountId() == dto.getAccountId())
                    .findFirst()
                    .ifPresent(account -> dto.setAccountName(account.getAccountName()));

                List<Status> statuses = statusDao.selectAll();
                statuses.stream()
                    .filter(s -> s.getStatusId() == dto.getStatusId())
                    .findFirst()
                    .ifPresent(status -> dto.setStatusName(status.getStatusName()));
            }

            return dto;
        } catch (SQLException e) {
            throw new SystemException(
                "申請詳細の取得に失敗しました",
                "Failed to get expense application detail with ID: " + applicationId,
                e
            );
        }
    }

    /**
     * 全てのステータス情報を取得する
     * @return ステータスリスト
     * @throws SystemException データベースエラー発生時
     */
    public List<Status> getAllStatuses() throws SystemException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            StatusDAO dao = new StatusDAO(connectionManager.getConnection());
            return dao.selectAll();
        } catch (SQLException e) {
            throw new SystemException(
                "ステータス一覧の取得に失敗しました",
                "Failed to get status list",
                e
            );
        }
    }
}