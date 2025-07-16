package com.fullness.keihiseisan.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fullness.keihiseisan.model.exception.BusinessException;
import com.fullness.keihiseisan.model.exception.ApplicationException;
import com.fullness.keihiseisan.model.service.ExpenseApplicationService;
import com.fullness.keihiseisan.model.value.ExpenseApplication;
import com.fullness.keihiseisan.model.value.User;

/**
 * 申請詳細画面のコントローラークラス
 * 申請詳細画面の表示を行う
 */
@WebServlet("/expense/detail")
public class DetailServlet extends BaseServlet {
    /** 申請サービス */
    private final ExpenseApplicationService expenseService;
    /**
     * コンストラクタ
     * @param expenseService 申請サービス
     */
    public DetailServlet() {
        this.expenseService = new ExpenseApplicationService();
    }
    /**
     * GETリクエストを処理する
     * @param req リクエスト
     * @param resp レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // セッションからユーザー情報を取得
            User loginUser = (User) req.getSession().getAttribute("loginUser");
            if (loginUser == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            // URLパラメータから申請IDを取得
            String idStr = req.getParameter("id");
            if (idStr == null || idStr.trim().isEmpty()) {
                throw new BusinessException("申請IDが指定されていません。");
            }
            int applicationId;
            try {
                applicationId = Integer.parseInt(idStr);
            } catch (NumberFormatException e) {
                throw new BusinessException("不正な申請IDです。");
            }
            ExpenseApplication expense = expenseService.getApplicationDetail(applicationId);
            if (expense == null) {
                throw new BusinessException("該当する申請が見つかりません。");
            }
            // 申請者本人のみアクセス可能
            if (!loginUser.getUserId().equals(expense.getApplicantUserId())) {
                throw new BusinessException("この申請を閲覧する権限がありません。");
            }
            // リクエスト属性を設定
            req.setAttribute("expense", expense);
            // 詳細画面を表示
            req.getRequestDispatcher("/WEB-INF/jsp/expense/detail.jsp")
               .forward(req, resp);
        } catch (ApplicationException e) {
            handleError(req, resp, e);
            return;
        } catch (Exception e) {
            handleSystemError(req, resp, e);
            return;
        }
    }
}