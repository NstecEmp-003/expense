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
            // 勘定科目とステータスのリストを取得
            List<Account> accountList = expenseService.readAllAccounts();
            List<Status> statusList = expenseService.getAllStatuses();
            // リクエスト属性に設定
            request.setAttribute("accountList", accountList);
            request.setAttribute("statusList", statusList);
            // 絞り込みパラメータがある場合は絞り込み検索
            if (hasFilterParams(request)) {
                performFilterSearch(request, response, loginUser);
            } else {
                // 通常の一覧取得
                List<ExpenseApplication> applications =
                    expenseService.getMyApplications(loginUser.getUserId());
                // リクエスト属性に設定
                request.setAttribute("applicationList", applications);
            }
            // 一覧画面表示
            request.getRequestDispatcher("/WEB-INF/jsp/expense/list.jsp")
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
            // セッションからユーザー情報を取得
            User loginUser = (User) request.getSession().getAttribute("loginUser");
            if (loginUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            // 絞り込み検索を実行
            performFilterSearch(request, response, loginUser);
            // 勘定科目とステータスのリストを取得
            List<Account> accountList = expenseService.readAllAccounts();
            List<Status> statusList = expenseService.getAllStatuses();
            // リクエスト属性に設定
            request.setAttribute("accountList", accountList);
            request.setAttribute("statusList", statusList);
            // フォームの入力値を画面に戻す
            setFilterParamsToRequest(request);
            // 一覧画面表示
            request.getRequestDispatcher("/WEB-INF/jsp/expense/list.jsp")
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
     * リクエストパラメータに絞り込み条件が含まれているか確認する
     * @param request リクエスト
     * @return 絞り込み条件が含まれているかどうか
     */
    private boolean hasFilterParams(HttpServletRequest request) {
        return request.getParameter("startDate") != null
            || request.getParameter("endDate") != null
            || request.getParameter("accountId") != null
            || request.getParameter("statusId") != null
            || request.getParameter("minAmount") != null
            || request.getParameter("maxAmount") != null
            || request.getParameter("payee") != null;
    }
    /**
     * 絞り込み検索を実行する
     * @param request リクエスト
     * @param response レスポンス
     * @param loginUser ログインユーザー
     * @throws Exception 例外
     */
    private void performFilterSearch(HttpServletRequest request, HttpServletResponse response, User loginUser) throws Exception {
        // 絞り込み条件を取得
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String accountId = request.getParameter("accountId");
        String statusId = request.getParameter("statusId");
        String minAmount = request.getParameter("minAmount");
        String maxAmount = request.getParameter("maxAmount");
        String payee = request.getParameter("payee");
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
            request.setAttribute("errorMessage", "該当する申請はありません");
        }
        // リクエスト属性に設定
        request.setAttribute("applicationList", filteredList);
    }
    /**
     * フォームの入力値をリクエスト属性に設定する
     * @param request リクエスト
     */
    private void setFilterParamsToRequest(HttpServletRequest request) {
        request.setAttribute("startDateValue", request.getParameter("startDate"));
        request.setAttribute("endDateValue", request.getParameter("endDate"));
        request.setAttribute("accountIdValue", request.getParameter("accountId"));
        request.setAttribute("statusIdValue", request.getParameter("statusId"));
        request.setAttribute("minAmountValue", request.getParameter("minAmount"));
        request.setAttribute("maxAmountValue", request.getParameter("maxAmount"));
        request.setAttribute("payeeValue", request.getParameter("payee"));
    }
}