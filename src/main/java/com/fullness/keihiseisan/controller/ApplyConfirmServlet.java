package com.fullness.keihiseisan.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.fullness.keihiseisan.model.exception.BusinessException;
import com.fullness.keihiseisan.model.service.ExpenseApplicationService;
import com.fullness.keihiseisan.model.value.Account;
import com.fullness.keihiseisan.model.value.ExpenseApplication;
import com.fullness.keihiseisan.model.value.User;

/**
 * 申請確認画面のコントローラークラス
 * 申請確認画面の表示と登録処理を行う
 */
@WebServlet("/expense/apply/confirm")
public class ApplyConfirmServlet extends BaseServlet {
    /**
     * GETリクエストを処理する
     * @param req リクエスト
     * @param resp レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        ExpenseApplication expense = (ExpenseApplication) session.getAttribute("expenseInput");
        if (expense == null) {
            session.setAttribute("illegalOperationMsg", "申請情報が見つかりません。");
            resp.sendRedirect(req.getContextPath() + "/menu");
            return;
        }

        // 勘定科目名を取得してDTOにセット (確認画面表示用)
        try {
             List<Account> accounts = List.of( new Account(1, "交通費"), new Account(2, "備品費"), new Account(9, "その他"));
             for(Account acc : accounts) {
                 if(acc.getAccountId() == expense.getAccountId()){
                     expense.setAccountName(acc.getAccountName());
                     break;
                 }
             }
        } catch (Exception e) {
            handleSystemError(req, resp, e);
            return;
        }

        req.setAttribute("expense", expense);
        req.getRequestDispatcher("/WEB-INF/jsp/expense/apply/confirm.jsp").forward(req, resp);
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
        // セッションからログインユーザー情報を取得
        HttpSession session = request.getSession();
        User loginUser = (User) session.getAttribute("loginUser");
        // ログインユーザー情報がない場合はログイン画面へリダイレクト
        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        try {
            // CSRFトークンの検証
            String sessionToken = (String) session.getAttribute("csrfToken");
            String requestToken = request.getParameter("csrfToken");
            if (sessionToken == null || !sessionToken.equals(requestToken)) {
                throw new BusinessException("不正なリクエストです。");
            }
            // セッションから申請情報を取得
            ExpenseApplication expense = (ExpenseApplication) session.getAttribute("expenseInput");
            if (expense == null) {
                // セッションが切れているか、不正なアクセス
                throw new BusinessException("セッションが切れたか、不正なアクセスです。申請をやり直してください。");
            }
            // サービス呼び出し
            ExpenseApplicationService service = new ExpenseApplicationService();
            int newId = service.applyExpense(expense, loginUser);
            // 申請完了画面へリダイレクト
            System.out.println("申請完了 ID: " + newId);
            response.sendRedirect(request.getContextPath() + "/expense/apply/complete?id=" + newId);
        } catch (BusinessException e) {
            handleError(request, response, e);
            return;
        } catch (Exception e) {
            handleSystemError(request, response, e);
            return;
        }
    }
}