package com.fullness.keihiseisan.model.service;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fullness.keihiseisan.model.dao.ExpenseApplicationDAO;
import com.fullness.keihiseisan.model.dao.StatusDAO;
import com.fullness.keihiseisan.model.exception.BusinessException;
import com.fullness.keihiseisan.model.exception.SystemException;
import com.fullness.keihiseisan.model.util.ConnectionManager;
import com.fullness.keihiseisan.model.value.ExpenseApplication;
import com.fullness.keihiseisan.model.value.User;
import com.fullness.keihiseisan.model.value.role;

/**
 * 承認サービスクラス
 * 承認/却下処理を行う
 */
public class ApprovalService {
    /* 承認閾値（金額50,000円以上の場合） */
    private static final int approvalThreshold = 50000;
    /* ロガー */
    private static final Logger logger = Logger.getLogger(ApprovalService.class.getName());
    /**
     * 承認/却下のために申請詳細を取得
     * @param applicationId 申請ID
     * @param loginUser ログインユーザー情報
     * @return ExpenseApplication
     * @throws BusinessException 申請が見つからない場合
     * @throws SystemException データベースアクセスエラー
     */
    public ExpenseApplication getApplicationForApproval(int applicationId, User loginUser) throws BusinessException, SystemException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            ExpenseApplicationDAO dao = new ExpenseApplicationDAO(connectionManager.getConnection());
            ExpenseApplication dto = dao.findById(applicationId);
            if (dto == null) {
                throw new BusinessException(
                    "指定された申請が見つかりません。",
                    "Application not found with ID: " + applicationId
                );
            }
            return dto;
        } catch (SQLException e) {
            throw new SystemException(
                "申請詳細の取得に失敗しました",
                "Database error while retrieving application with ID: " + applicationId + ", User: " + loginUser.getUserId(),
                e
            );
        }
    }

    /**
     * 承認・却下処理を行う
     * @param applicationId 申請ID
     * @param action "approve" or "reject"
     * @param reason 却下理由 (actionが"reject"の場合)
     * @param loginUser ログインユーザー情報
     * @throws BusinessException 申請が見つからない場合、または承認権限がない場合
     * @throws SystemException データベースアクセスエラー
     */
    public void processApproval(int applicationId, String action, String reason, User loginUser) throws BusinessException, SystemException {
        validateApprovalRequest(action, reason);
        logger.info(String.format("承認処理開始: 申請ID=%d, アクション=%s, 承認者=%s", applicationId, action, loginUser.getUserId()));

        try (ConnectionManager connectionManager = new ConnectionManager()) {
            // 申請情報を取得
            ExpenseApplicationDAO expenseDao = new ExpenseApplicationDAO(connectionManager.getConnection());
            ExpenseApplication targetApp = expenseDao.findById(applicationId);
            if (targetApp == null) {
                throw new BusinessException(
                    "対象の申請が存在しません。",
                    "Application not found with ID: " + applicationId
                );
            }

            // 承認可能かどうかをチェック
            if (!canApprove(targetApp, loginUser)) {
                throw new BusinessException(
                    "この申請を承認/却下する権限がありません。",
                    "No approval permission for application ID: " + applicationId + ", User ID: " + loginUser.getUserId()
                );
            }

            // 承認/却下処理を行う
            int nextStatus = determineNextStatus(targetApp, action, loginUser);
            int updatedRows = expenseDao.updateApplicationStatus(
                applicationId,
                nextStatus,
                loginUser.getUserId(),
                Timestamp.from(Instant.now()),
                ("reject".equals(action) ? reason : null),
                loginUser.getRoleId()
            );

            // 更新が0件の場合はエラー
            if (updatedRows == 0) {
                throw new BusinessException(
                    "申請情報の更新に失敗しました。対象が見つからないか、既に処理されている可能性があります。",
                    "Failed to update application status. Application ID: " + applicationId
                );
            }

            connectionManager.commit();
            logger.info(String.format("承認処理完了: 申請ID=%d, アクション=%s, 次のステータス=%d", applicationId, action, nextStatus));
        } catch (SQLException e) {
            throw new SystemException(
                "承認処理中にシステムエラーが発生しました",
                "Database error during approval process for application ID: " + applicationId + 
                ", Action: " + action + 
                ", User: " + loginUser.getUserId(),
                e
            );
        }
    }

    /**
     * 現在のステータスと承認者の権限から、承認可能かチェック
     * @param application 申請情報
     * @param approver 承認者
     * @return 承認可能かどうか
     */
    public boolean canApprove(ExpenseApplication application, User approver) {
        // 申請が存在しない場合は承認不可
        if (application == null) return false;
        // 現在のステータスが申請中または課長承認済の場合のみ承認可能
        if (application.getStatusId() != StatusDAO.STATUS_APPLIED &&
            application.getStatusId() != StatusDAO.STATUS_MANAGER_APPROVED) {
            return false;
        }
        // 課長の場合：申請中のみ承認可能
        if (approver.getRoleId() == role.MANAGER) { // 課長
            return application.getStatusId() == StatusDAO.STATUS_APPLIED;
        }
        // 部長の場合：課長承認済のみ承認可能（金額50,000円以上の場合）
        if (approver.getRoleId() == role.DIRECTOR) { // 部長
            return application.getStatusId() == StatusDAO.STATUS_MANAGER_APPROVED &&
                   application.getAmount() > approvalThreshold;
        }
        return false;
    }

    /**
     * 担当部署の申請一覧を取得する (ステータス問わず)
     * @param loginUser ログインユーザー情報
     * @return List<ExpenseApplication>
     * @throws SystemException データベースアクセスエラー
     */
    public List<ExpenseApplication> getDepartmentApplications(User loginUser) throws SystemException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            ExpenseApplicationDAO dao = new ExpenseApplicationDAO(connectionManager.getConnection());
            // 部署IDで申請リストを取得
            return dao.findApplicationsByDepartment(loginUser.getDepartmentId());
        } catch (SQLException e) {
            throw new SystemException(
                "申請一覧の取得に失敗しました",
                "Database error while retrieving applications for department ID: " + loginUser.getDepartmentId() + 
                ", User: " + loginUser.getUserId(),
                e
            );
        }
    }

    /**
     * 承認リクエストのバリデーション
     * @param action 承認/却下
     * @param reason 却下理由
     * @throws BusinessException 不正なアクションまたは却下理由が入力された場合
     */
    private void validateApprovalRequest(String action, String reason) throws BusinessException {
        if (!"approve".equals(action) && !"reject".equals(action)) {
            throw new BusinessException(
                "不正なアクションです。",
                "Invalid action: " + action
            );
        }
        if ("reject".equals(action) && (reason == null || reason.trim().isEmpty())) {
            throw new BusinessException(
                "却下理由を入力してください。",
                "Rejection reason is required for reject action"
            );
        }
    }

    /**
     * 承認/却下後の次のステータスを決定
     * @param application 申請情報
     * @param action 承認/却下
     * @param approver 承認者
     * @return 次のステータス番号
     */
    private int determineNextStatus(ExpenseApplication application, String action, User approver) {
        // 却下の場合
        if ("reject".equals(action)) {
            return approver.getRoleId() == role.MANAGER ?
                   StatusDAO.STATUS_MANAGER_REJECTED : StatusDAO.STATUS_DIRECTOR_REJECTED;
        }
        // 承認の場合
        if (approver.getRoleId() == role.MANAGER) {
            // 課長承認の場合、金額で分岐
            return application.getAmount() >= approvalThreshold ?
                   StatusDAO.STATUS_MANAGER_APPROVED : StatusDAO.STATUS_APPROVED_COMPLETED;
        } else {
            return StatusDAO.STATUS_APPROVED_COMPLETED;
        }
    }
}