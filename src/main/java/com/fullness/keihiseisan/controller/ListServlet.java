package com.fullness.keihiseisan.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fullness.keihiseisan.model.exception.ApplicationException;
import com.fullness.keihiseisan.model.service.ExpenseApplicationService;
import com.fullness.keihiseisan.model.value.Account;
import com.fullness.keihiseisan.model.value.ExpenseApplication;
import com.fullness.keihiseisan.model.value.Status;
import com.fullness.keihiseisan.model.value.User;

/**
 * 申請一覧画面のコントローラークラス
 * 申請一覧画面の表示を行う
 */
@WebServlet("/expense/list")
public class ListServlet extends BaseServlet {
    /** 申請サービス */
    private final ExpenseApplicationService expenseService = new ExpenseApplicationService();
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
            // 勘定科目とステータスのリストを取得
            List<Account> accountList = expenseService.readAllAccounts();
            List<Status> statusList = expenseService.getAllStatuses();
            // リクエスト属性に設定
            req.setAttribute("accountList", accountList);
            req.setAttribute("statusList", statusList);
            // 絞り込みパラメータがある場合は絞り込み検索
            if (hasFilterParams(req)) {
                performFilterSearch(req, resp, loginUser);
            } else {
                // 通常の一覧取得
                List<ExpenseApplication> applications =
                    expenseService.getMyApplications(loginUser.getUserId());
                // リクエスト属性に設定
                req.setAttribute("applicationList", applications);
            }
            // 一覧画面表示
            req.getRequestDispatcher("/WEB-INF/jsp/expense/list.jsp")
               .forward(req, resp);
        } catch (ApplicationException e) {
            handleError(req, resp, e);
            return;
        } catch (Exception e) {
            handleSystemError(req, resp, e);
            return;
        }
    }
    /**
     * POSTリクエストを処理する
     * @param req リクエスト
     * @param resp レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // セッションからユーザー情報を取得
            User loginUser = (User) req.getSession().getAttribute("loginUser");
            if (loginUser == null) {
                resp.sendRedirect(req.getContextPath() + "/login");
                return;
            }
            // 絞り込み検索を実行
            performFilterSearch(req, resp, loginUser);
            // 勘定科目とステータスのリストを取得
            List<Account> accountList = expenseService.readAllAccounts();
            List<Status> statusList = expenseService.getAllStatuses();
            // リクエスト属性に設定
            req.setAttribute("accountList", accountList);
            req.setAttribute("statusList", statusList);
            // フォームの入力値を画面に戻す
            setFilterParamsToRequest(req);
            // 一覧画面表示
            req.getRequestDispatcher("/WEB-INF/jsp/expense/list.jsp")
               .forward(req, resp);
        } catch (ApplicationException e) {
            handleError(req, resp, e);
            return;
        } catch (Exception e) {
            handleSystemError(req, resp, e);
            return;
        }
    }
    /**
     * リクエストパラメータに絞り込み条件が含まれているか確認する
     * @param req リクエスト
     * @return 絞り込み条件が含まれているかどうか
     */
    private boolean hasFilterParams(HttpServletRequest req) {
        return req.getParameter("startDate") != null
            || req.getParameter("endDate") != null
            || req.getParameter("accountId") != null
            || req.getParameter("statusId") != null
            || req.getParameter("minAmount") != null
            || req.getParameter("maxAmount") != null
            || req.getParameter("payee") != null;
    }
    /**
     * 絞り込み検索を実行する
     * @param req リクエスト
     * @param resp レスポンス
     * @param loginUser ログインユーザー
     * @throws Exception 例外
     */
    private void performFilterSearch(HttpServletRequest req, HttpServletResponse resp, User loginUser) throws Exception {
        // 絞り込み条件を取得
        String startDate = req.getParameter("startDate");
        String endDate = req.getParameter("endDate");
        String accountId = req.getParameter("accountId");
        String statusId = req.getParameter("statusId");
        String minAmount = req.getParameter("minAmount");
        String maxAmount = req.getParameter("maxAmount");
        String payee = req.getParameter("payee");
        // 絞り込み検索を実行
        List<ExpenseApplication> filteredList = expenseService.getFilteredApplications(
                loginUser.getUserId(),
                startDate,
                endDate,
                accountId,
                statusId,
                minAmount,
                maxAmount,
                payee
        );
        // 結果が0件の場合はメッセージを設定
        if (filteredList.isEmpty()) {
            req.setAttribute("errorMessage", "該当する申請はありません");
        }
        // リクエスト属性に設定
        req.setAttribute("applicationList", filteredList);
    }
    /**
     * フォームの入力値をリクエスト属性に設定する
     * @param req リクエスト
     */
    private void setFilterParamsToRequest(HttpServletRequest req) {
        req.setAttribute("startDateValue", req.getParameter("startDate"));
        req.setAttribute("endDateValue", req.getParameter("endDate"));
        req.setAttribute("accountIdValue", req.getParameter("accountId"));
        req.setAttribute("statusIdValue", req.getParameter("statusId"));
        req.setAttribute("minAmountValue", req.getParameter("minAmount"));
        req.setAttribute("maxAmountValue", req.getParameter("maxAmount"));
        req.setAttribute("payeeValue", req.getParameter("payee"));
    }
}