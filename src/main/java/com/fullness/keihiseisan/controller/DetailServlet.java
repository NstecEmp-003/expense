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
     * @param request リクエスト
     * @param response レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // セッションからユーザー情報を取得
            User loginUser = (User) request.getSession().getAttribute("loginUser");
            if (loginUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            // URLパラメータから申請IDを取得
            String idStr = request.getParameter("id");
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
            request.setAttribute("expense", expense);
            // 詳細画面を表示
            request.getRequestDispatcher("/WEB-INF/jsp/expense/detail.jsp")
               .forward(request, response);
        } catch (ApplicationException e) {
            handleError(request, response, e);
            return;
        } catch (Exception e) {
            handleSystemError(request, response, e);
            return;
        }
    }
}