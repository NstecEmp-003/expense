package com.fullness.keihiseisan.controller;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import com.fullness.keihiseisan.model.exception.ApplicationException;
import com.fullness.keihiseisan.model.service.ExpenseApplicationService;
import com.fullness.keihiseisan.model.util.ValidationUtil;
import com.fullness.keihiseisan.model.value.Account;
import com.fullness.keihiseisan.model.value.ExpenseApplication;
import com.fullness.keihiseisan.model.value.User;
import com.fullness.keihiseisan.model.value.role;
import com.fullness.keihiseisan.model.util.FileUploadUtil;

/**
 * 申請入力画面のコントローラークラス
 * 申請入力画面の表示と登録処理を行う
 */
@WebServlet("/expense/apply/input")
@MultipartConfig(
    maxFileSize = 10 * 1024 * 1024,        // 10MB
    maxRequestSize = 10 * 1024 * 1024,    // 10MB
    fileSizeThreshold = 0
)
public class ApplyInputServlet extends BaseServlet {
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
            // ログインチェック
            loginCheck(request, response);
            // 権限チェック
            roleCheck(request, response, role.EMPLOYEE);
            // CSRFトークンの生成と設定
            String csrfToken = UUID.randomUUID().toString();
            HttpSession session = request.getSession(false);
            session.setAttribute("csrfToken", csrfToken);
            request.setAttribute("csrfToken", csrfToken);
            // 申請日の初期値として本日の日付を設定
            request.setAttribute("today", LocalDate.now().toString());
            // エラーメッセージの取得
            List<String> errorMessages = (List<String>) session.getAttribute("errorMessages");
            if (errorMessages != null) {
                request.setAttribute("errorMessages", errorMessages);
                session.removeAttribute("errorMessages");
            }
            // 勘定科目リストの取得
            ExpenseApplicationService service = new ExpenseApplicationService();
            List<Account> accounts = service.readAllAccounts();
            request.setAttribute("accounts", accounts);
            request.getRequestDispatcher("/WEB-INF/jsp/expense/apply/input.jsp").forward(request, response);
        } catch (ApplicationException e) {
            response.setContentType("text/plain; charset=UTF-8");
            e.printStackTrace(response.getWriter());
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
            // ログインチェック
            loginCheck(request, response);
            // 権限チェック
            roleCheck(request, response, role.EMPLOYEE);
            // CSRFトークンの検証
            HttpSession session = request.getSession(false);
            String sessionToken = (String) session.getAttribute("csrfToken");
            String requestToken = request.getParameter("csrfToken");
            if (sessionToken == null || !sessionToken.equals(requestToken)) {
                throw new ServletException("不正なリクエストです。");
            }
            System.out.println("CSRFトークンの検証: " + sessionToken + " " + requestToken);
            // セッションからログインユーザー情報を取得
            User loginUser = (User) session.getAttribute("loginUser");
            System.out.println("ログインユーザー情報の取得: " + loginUser.getUserId());
            // リクエストパラメータを取得してDTOに設定
            ExpenseApplication expense = new ExpenseApplication();
            expense.setApplicantUserId(loginUser.getUserId());
            System.out.println("DTOの設定: " + expense.getApplicantUserId());
            // 入力値の取得とサニタイズ
            String applicationDate = request.getParameter("applicationDate");
            String accountId = request.getParameter("accountId");
            String paymentDate = request.getParameter("paymentDate");
            String payee = request.getParameter("payee");
            String amount = request.getParameter("amount");
            String description = request.getParameter("description");
            System.out.println("申請日の取得: " + applicationDate);
            System.out.println("勘定科目の取得: " + accountId);
            System.out.println("支払日の取得: " + paymentDate);
            System.out.println("支払先の取得: " + payee);
            System.out.println("金額の取得: " + amount);
            System.out.println("内容（詳細）の取得: " + description);

            // DTOに値を設定
            expense.setApplicationDate(ValidationUtil.isEmpty(applicationDate) ? null : Date.valueOf(applicationDate));
            expense.setAccountId(ValidationUtil.isEmpty(accountId) ? 0 : Integer.parseInt(accountId));
            expense.setPaymentDate(ValidationUtil.isEmpty(paymentDate) ? null : Date.valueOf(paymentDate));
            expense.setPayee(payee);
            expense.setAmount(ValidationUtil.isEmpty(amount) ? 0 : Integer.parseInt(amount));
            expense.setDescription(description);
            // ファイルを取得
            Part filePart = request.getPart("receiptFile");
            // サービスのインスタンス化
            ExpenseApplicationService service = new ExpenseApplicationService();
            // バリデーション実行（ファイルの検証を含む）
            List<String> errorMessages = new ArrayList<>();
            errorMessages = service.validateApplication(expense, filePart);
            if (!errorMessages.isEmpty()) {
                // エラーがある場合は入力画面に戻る
                request.setAttribute("errorMessages", errorMessages);
                request.setAttribute("expense", expense);
                List<Account> accounts = service.readAllAccounts();
                request.setAttribute("accounts", accounts);
                // CSRFトークンを再生成
                String newCsrfToken = UUID.randomUUID().toString();
                session.setAttribute("csrfToken", newCsrfToken);
                request.setAttribute("csrfToken", newCsrfToken);
                request.getRequestDispatcher("/WEB-INF/jsp/expense/apply/input.jsp").forward(request, response);
                return;
            }
            // ファイルの保存
            if (filePart != null && filePart.getSize() > 0) {
                // ソースコード内のuploadディレクトリのパスを構築
                // ServletContextを使用して適切なアップロードパスを取得
                String uploadDir = FileUploadUtil.getUploadDirectoryPath(getServletContext());
                // ログでパスを確認
                System.out.println("アップロードパス: " + uploadDir);
                // ファイルを保存
                String receiptPath = service.saveReceiptFile(filePart, uploadDir);
                System.out.println("保存されたレシートパス: " + receiptPath);
                expense.setReceiptPath(receiptPath);
            }
            // セッションに保存して確認画面へ
            session.setAttribute("expenseInput", expense);
            response.sendRedirect(request.getContextPath() + "/expense/apply/confirm");
        } catch (ApplicationException e) {
            handleError(request, response, e);
            return;
        } catch (Exception e) {
            handleSystemError(request, response, e);
            return;
        }
    }
}