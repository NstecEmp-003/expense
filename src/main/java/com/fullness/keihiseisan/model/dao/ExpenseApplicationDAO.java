package com.fullness.keihiseisan.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fullness.keihiseisan.model.value.ExpenseApplication;
import com.fullness.keihiseisan.model.exception.SystemException;

/**
 * 経費申請DAOクラス
 * 経費申請情報の取得を行う
 */
public class ExpenseApplicationDAO {
    private final Connection connection;
    /** ロガー */
    private static final Logger logger = Logger.getLogger(ExpenseApplicationDAO.class.getName());

    /**
     * 経費申請情報の登録用SQL
     */
    private static final String INSERT_SQL = 
        "INSERT INTO expense_applications ("
        + "applicant_user_id, application_date, account_id, "
        + "payment_date, payee, amount, description, "
        + "receipt_path, status_id"
        + ") VALUES ("
        + "?, ?, ?, ?, ?, ?, ?, ?, ?"
        + ")";

    /**
     * 経費申請情報の更新用SQL
     */
    private static final String UPDATE_STATUS_MANAGER_SQL =
        "UPDATE expense_applications "
        + "SET status_id = ?, "
        + "approver1_user_id = ?, "
        + "approval1_date = CURRENT_TIMESTAMP, "
        + "rejection_reason = ? "
        + "WHERE application_id = ?";

    /**
     * 経費申請情報の更新用SQL
     */
    private static final String UPDATE_STATUS_DIRECTOR_SQL =
        "UPDATE expense_applications "
        + "SET status_id = ?, "
        + "approver2_user_id = ?, "
        + "approval2_date = CURRENT_TIMESTAMP, "
        + "rejection_reason = ? "
        + "WHERE application_id = ?";

    /**
     * コンストラクタ
     * @param connection データベース接続
     */
    public ExpenseApplicationDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * 経費申請情報を登録する
     * @param expense 登録する経費情報 (applicationId以外がセットされている想定)
     * @return 生成された申請ID
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public int insert(ExpenseApplication expense) throws SystemException {
        logger.info("ExpenseApplicationDAO.insert開始: " + expense.getPayee() + ", 金額: " + expense.getAmount());
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            int i = 1;
            stmt.setString(i++, expense.getApplicantUserId());
            stmt.setDate(i++, expense.getApplicationDate());
            stmt.setInt(i++, expense.getAccountId());
            stmt.setDate(i++, expense.getPaymentDate());
            stmt.setString(i++, expense.getPayee());
            stmt.setInt(i++, expense.getAmount());
            stmt.setString(i++, expense.getDescription());
            stmt.setString(i++, expense.getReceiptPath());
            stmt.setInt(i++, StatusDAO.STATUS_APPLIED); // 申請中ステータス
            
            logger.info("SQL実行: " + INSERT_SQL);
            int rows = stmt.executeUpdate();
            logger.info("更新された行数: " + rows);
            
            // 自動コミット設定を確認
            boolean autoCommit = connection.getAutoCommit();
            logger.info("自動コミット設定: " + autoCommit);
            
            // 明示的にコミット
            if (!autoCommit) {
                connection.commit();
                logger.info("明示的にコミットしました");
            }
            
            // 生成された申請IDを取得
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    logger.info("生成された申請ID: " + newId);
                    return newId;
                }
                throw new SystemException("申請IDの取得に失敗しました。", null);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "経費申請情報の登録中にエラーが発生しました", e);
            throw new SystemException("経費申請情報の登録に失敗しました", e);
        }
    }

    /**
     * 申請IDをキーに経費申請情報を取得する
     * @param applicationId 申請ID
     * @return 経費申請情報
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public ExpenseApplication findById(int applicationId) throws SystemException {
        String sql = "SELECT e.*, u.user_name as applicant_name, "
                + "a.account_name, s.status_name, "
                + "u1.user_name as approver1_name, "
                + "u2.user_name as approver2_name "
                + "FROM expense_applications e "
                + "JOIN users u ON e.applicant_user_id = u.user_id "
                + "JOIN accounts a ON e.account_id = a.account_id "
                + "JOIN statuses s ON e.status_id = s.status_id "
                + "LEFT JOIN users u1 ON e.approver1_user_id = u1.user_id "
                + "LEFT JOIN users u2 ON e.approver2_user_id = u2.user_id "
                + "WHERE e.application_id = ?";

        try (
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, applicationId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetTo(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "経費申請情報の取得中にエラーが発生しました。申請ID: " + applicationId, e);
            throw new SystemException("経費申請情報の取得に失敗しました。申請ID: " + applicationId, e);
        }
    }

    /**
     * 特定のユーザーの申請一覧を取得する
     * @param userId ユーザーID
     * @return 経費申請情報
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public List<ExpenseApplication> findByApplicantId(String userId) throws SystemException {
        List<ExpenseApplication> list = new ArrayList<>();
        String sql = "SELECT e.*, u.user_name as applicant_name, "
                + "a.account_name, s.status_name, "
                + "u1.user_name as approver1_name, "
                + "u2.user_name as approver2_name "
                + "FROM expense_applications e "
                + "JOIN users u ON e.applicant_user_id = u.user_id "
                + "JOIN accounts a ON e.account_id = a.account_id "
                + "JOIN statuses s ON e.status_id = s.status_id "
                + "LEFT JOIN users u1 ON e.approver1_user_id = u1.user_id "
                + "LEFT JOIN users u2 ON e.approver2_user_id = u2.user_id "
                + "WHERE e.applicant_user_id = ? "
                + "ORDER BY e.application_id DESC";

        try (
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetTo(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "経費申請一覧の取得中にエラーが発生しました。ユーザーID: " + userId, e);
            throw new SystemException("経費申請一覧の取得に失敗しました。ユーザーID: " + userId, e);
        }
    }

    /**
     * 申請のステータスを更新する
     * @param applicationId 申請ID
     * @param statusId ステータスID
     * @param approverUserId 承認者ID
     * @param approvalDate 承認日
     * @param rejectionReason 却下理由
     * @param approvalLevel 承認レベル
     * @return 更新された行数
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public int updateApplicationStatus(
            int applicationId, 
            int statusId, 
            String approverUserId,
            Timestamp approvalDate,
            String rejectionReason,
            int approvalLevel) throws SystemException {

        String sql;
        if (approvalLevel == 2) {
            sql = UPDATE_STATUS_MANAGER_SQL;
        } else if (approvalLevel == 3) {
            sql = UPDATE_STATUS_DIRECTOR_SQL;
        } else {
            throw new SystemException("不正な承認レベルです。", null);
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, statusId);
            stmt.setString(2, approverUserId);
            if (rejectionReason != null) {
                stmt.setString(3, rejectionReason);
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }
            stmt.setInt(4, applicationId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "申請ステータスの更新中にエラーが発生しました。申請ID: " + applicationId, e);
            throw new SystemException("申請ステータスの更新に失敗しました。申請ID: " + applicationId, e);
        }
    }

    /**
     * 部門IDと承認ステータスで経費申請を検索する
     * @param departmentId 部門ID
     * @param statusId ステータスID
     * @return 経費申請のリスト
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public List<ExpenseApplication> findPendingByDepartmentAndStatus(int departmentId, int statusId) throws SystemException {
        List<ExpenseApplication> list = new ArrayList<>();
        String sql = "SELECT e.*, u.user_name as applicant_name, "
                + "a.account_name, s.status_name, "
                + "u1.user_name as approver1_name, "
                + "u2.user_name as approver2_name "
                + "FROM expense_applications e "
                + "JOIN users u ON e.applicant_user_id = u.user_id "
                + "JOIN accounts a ON e.account_id = a.account_id "
                + "JOIN statuses s ON e.status_id = s.status_id "
                + "LEFT JOIN users u1 ON e.approver1_user_id = u1.user_id "
                + "LEFT JOIN users u2 ON e.approver2_user_id = u2.user_id "
                + "WHERE u.department_id = ? AND e.status_id = ? "
                + "ORDER BY e.application_id DESC";

        try (
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, departmentId);
            stmt.setInt(2, statusId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetTo(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "部門別承認待ち申請の取得中にエラーが発生しました。部門ID: " + departmentId + ", ステータスID: " + statusId, e);
            throw new SystemException("部門別承認待ち申請の取得に失敗しました。部門ID: " + departmentId + ", ステータスID: " + statusId, e);
        }
    }

    /**
     * 部門IDで経費申請を検索する
     * @param departmentId 部門ID
     * @return 経費申請のリスト
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public List<ExpenseApplication> findApplicationsByDepartment(int departmentId) throws SystemException {
        List<ExpenseApplication> list = new ArrayList<>();
        String sql = "SELECT e.*, u.user_name as applicant_name, "
                + "a.account_name, s.status_name, "
                + "u1.user_name as approver1_name, "
                + "u2.user_name as approver2_name "
                + "FROM expense_applications e "
                + "JOIN users u ON e.applicant_user_id = u.user_id "
                + "JOIN accounts a ON e.account_id = a.account_id "
                + "JOIN statuses s ON e.status_id = s.status_id "
                + "LEFT JOIN users u1 ON e.approver1_user_id = u1.user_id "
                + "LEFT JOIN users u2 ON e.approver2_user_id = u2.user_id "
                + "WHERE u.department_id = ? "
                + "ORDER BY e.application_id DESC";

        try (
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, departmentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetTo(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "部門別申請一覧の取得中にエラーが発生しました。部門ID: " + departmentId, e);
            throw new SystemException("部門別申請一覧の取得に失敗しました。部門ID: " + departmentId, e);
        }
    }

    /**
     * 検索条件に基づいて経費申請を検索する
     * @param userId ユーザーID
     * @param startDate 開始日
     * @param endDate 終了日
     * @param accountId 勘定科目ID
     * @param statusId ステータスID
     * @param minAmount 最小金額
     * @param maxAmount 最大金額
     * @param payee 支払先
     * @return 経費申請のリスト
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public List<ExpenseApplication> findByFilter(
            String userId,
            Date startDate,
            Date endDate,
            Integer accountId,
            Integer statusId,
            Integer minAmount,
            Integer maxAmount,
            String payee) throws SystemException {
        
        List<ExpenseApplication> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT e.*, u.user_name as applicant_name, ")
           .append("a.account_name, s.status_name, ")
           .append("u1.user_name as approver1_name, ")
           .append("u2.user_name as approver2_name ")
           .append("FROM expense_applications e ")
           .append("JOIN users u ON e.applicant_user_id = u.user_id ")
           .append("JOIN accounts a ON e.account_id = a.account_id ")
           .append("JOIN statuses s ON e.status_id = s.status_id ")
           .append("LEFT JOIN users u1 ON e.approver1_user_id = u1.user_id ")
           .append("LEFT JOIN users u2 ON e.approver2_user_id = u2.user_id ")
           .append("WHERE e.applicant_user_id = '" + userId + "' ");
        // 申請日範囲
        if (startDate != null) {
            sql.append("AND e.application_date >= '" + startDate + "' ");
        }
        if (endDate != null) {
            sql.append("AND e.application_date <= '" + endDate + "' ");
        }
        // 勘定科目
        if (accountId != null) {
            sql.append("AND e.account_id = " + accountId + " ");
        }
        // ステータス
        if (statusId != null) {
            sql.append(" AND e.payee LIKE '%" + payee + "%' ");
        }
        // 支払先
        if (payee != null && !payee.trim().isEmpty()) {
            sql.append("AND e.payee LIKE '%" + payee + "%' ");
        }
        sql.append("ORDER BY e.application_id DESC");
        try (Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(sql.toString())) {
                while (rs.next()) {
                    list.add(mapResultSetTo(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new SystemException("経費申請の検索に失敗しました。ユーザーID: " + userId, e);
        }
    }

    /**
     * ResultSetから経費申請DTOへのマッピングを行う
     * @param rs ResultSet
     * @return 経費申請DTO
     * @throws SQLException
     */
    private ExpenseApplication mapResultSetTo(ResultSet rs) throws SQLException {
        ExpenseApplication dto = new ExpenseApplication();
        dto.setApplicationId(rs.getInt("application_id"));
        dto.setApplicantUserId(rs.getString("applicant_user_id"));
        dto.setApplicantName(rs.getString("applicant_name"));
        dto.setApplicationDate(rs.getDate("application_date"));
        dto.setAccountId(rs.getInt("account_id"));
        dto.setAccountName(rs.getString("account_name"));
        dto.setPaymentDate(rs.getDate("payment_date"));
        dto.setPayee(rs.getString("payee"));
        dto.setAmount(rs.getInt("amount"));
        dto.setDescription(rs.getString("description"));
        dto.setReceiptPath(rs.getString("receipt_path"));
        dto.setStatusId(rs.getInt("status_id"));
        dto.setStatusName(rs.getString("status_name"));
        
        // 承認者1情報
        String approver1UserId = rs.getString("approver1_user_id");
        if (approver1UserId != null) {
            dto.setApprover1UserId(approver1UserId);
            dto.setApprover1Name(rs.getString("approver1_name"));
            dto.setApproval1Date(rs.getTimestamp("approval1_date"));
        }
        
        // 承認者2情報
        String approver2UserId = rs.getString("approver2_user_id");
        if (approver2UserId != null) {
            dto.setApprover2UserId(approver2UserId);
            dto.setApprover2Name(rs.getString("approver2_name"));
            dto.setApproval2Date(rs.getTimestamp("approval2_date"));
        }
        
        // 却下理由
        dto.setRejectionReason(rs.getString("rejection_reason"));
        
        return dto;
    }
}