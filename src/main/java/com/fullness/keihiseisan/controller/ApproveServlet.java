package com.fullness.keihiseisan.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.fullness.keihiseisan.model.exception.BusinessException;
import com.fullness.keihiseisan.model.exception.ApplicationException;
import com.fullness.keihiseisan.model.service.ApprovalService;
import com.fullness.keihiseisan.model.util.ValidationUtil;
import com.fullness.keihiseisan.model.value.ExpenseApplication;
import com.fullness.keihiseisan.model.value.User;

/**
 * 承認画面のコントローラークラス
 * 承認画面の表示と承認処理を行う
 */
@WebServlet("/approval/approve")
public class ApproveServlet extends BaseServlet {
    /** 承認サービス */
    private final ApprovalService approvalService;
    /**
     * コンストラクタ
     * @param approvalService 承認サービス
     */
    public ApproveServlet() {
        this.approvalService = new ApprovalService();
    }
    /**
     * GETリクエストを処理する
     * @param request リクエスト
     * @param response レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // セッションチェック
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("loginUser") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            // セッションからログインユーザー情報取得
            User loginUser = (User) session.getAttribute("loginUser");
            // 申請ID取得
            int applicationId = validateAndParseApplicationId(request);
            // 対象の申請情報と承認可能かどうかを取得
            ExpenseApplication expense = null;
            boolean canApprove = false;
            expense = approvalService.getApplicationForApproval(applicationId, loginUser);
            canApprove = approvalService.canApprove(expense, loginUser);
            // リクエスト属性に設定
            request.setAttribute("expense", expense);
            request.setAttribute("canApprove", canApprove);            
            // CSRFトークンを生成してセッションに保存
            if (session != null && canApprove) {
                String csrfToken = java.util.UUID.randomUUID().toString();
                session.setAttribute("csrfToken", csrfToken);
            }
            // 承認画面表示
            request.getRequestDispatcher("/WEB-INF/jsp/approval/detail.jsp")
               .forward(request, response);
        } catch (ApplicationException e) {
            handleError(request, response, e);
            return;
        } catch (Exception e) {
            handleSystemError(request, response, e);
            return;
        }
    }
    /**
     * POSTリクエストを処理する
     * @param request リクエスト
     * @param response レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // セッションチェック
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("loginUser") == null) {
                throw new BusinessException("セッションが切れました。再度ログインしてください。");
            }
            // CSRFトークンチェック
            String token = request.getParameter("csrfToken");
            String sessionToken = (String) session.getAttribute("csrfToken");
            if (token == null || !token.equals(sessionToken)) {
                throw new BusinessException("不正なリクエストです。");
            }
            User loginUser = (User) session.getAttribute("loginUser");
            // 入力値のバリデーション
            int applicationId = validateAndParseApplicationId(request);
            String action = validateAction(request);
            String reason = validateReason(request, action);
            // 承認処理の実行
            approvalService.processApproval(applicationId, action, reason, loginUser);
            response.sendRedirect(request.getContextPath() + "/approval/list");
        } catch (ApplicationException e) {
            handleError(request, response, e);
            return;
        } catch (Exception e) {
            handleSystemError(request, response, e);
            return;
        }
    }
    /**
     * 申請IDのバリデーションとパースを行う
     * @param request リクエスト
     * @return 申請ID
     * @throws BusinessException 申請IDが不正な場合
     */
    private int validateAndParseApplicationId(HttpServletRequest request) throws BusinessException {
        try {
            int applicationId = Integer.parseInt(request.getParameter("applicationId"));
            if (applicationId <= 0) {
                throw new BusinessException("不正な申請IDです。");
            }
            return applicationId;
        } catch (NumberFormatException e) {
            throw new BusinessException("申請IDが不正です。");
        }
    }
    /**
     * アクションのバリデーションを行う
     * @param request リクエスト
     * @return アクション
     * @throws BusinessException アクションが不正な場合
     */
    private String validateAction(HttpServletRequest request) throws BusinessException {
        String action = request.getParameter("action");
        if (action == null || (!action.equals("approve") && !action.equals("reject"))) {
            throw new BusinessException("不正なアクションです。");
        }
        return action;
    }
    /**
     * 却下理由のバリデーションを行う
     * @param request リクエスト
     * @param action アクション
     * @return 却下理由
     * @throws BusinessException 却下理由が不正な場合
     */
    private String validateReason(HttpServletRequest request, String action) throws BusinessException {
        String reason = request.getParameter("rejectionReason");
        if ("reject".equals(action)) {
            if (reason == null || reason.trim().isEmpty()) {
                throw new BusinessException("却下理由を入力してください。");
            }
            return ValidationUtil.sanitizeHtml(reason);
        }
        return reason;
    }
}