package com.fullness.keihiseisan.controller;

import java.io.IOException;
import java.util.UUID;

import com.fullness.keihiseisan.model.service.LoginService;
import com.fullness.keihiseisan.model.value.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ログイン画面のコントローラークラス
 * ログイン画面の表示を行う
 */
@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    /**
     * GETリクエストを処理する
     * @param request リクエスト
     * @param response レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        if (session != null && session.getAttribute("loginUser") != null) {
            // ログイン済みの場合、メニュー画面へリダイレクト
            response.sendRedirect(request.getContextPath() + "/menu");
            return;
        }
        // CSRFトークンの生成と設定
        String csrfToken = UUID.randomUUID().toString();
        session = request.getSession(true);
        session.setAttribute("csrfToken", csrfToken);
        request.setAttribute("csrfToken", csrfToken);
        // ログイン画面 (P001) を表示
        request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
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
            // CSRFトークンの検証
            HttpSession session = request.getSession(false);
            String sessionToken = (String) session.getAttribute("csrfToken");
            String requestToken = request.getParameter("csrfToken");
            if (sessionToken == null || !sessionToken.equals(requestToken)) {
                request.setAttribute("errorMessage", "セッションが切れました。再度ログインしてください。");
                request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
                return;
            }
            // ユーザーIDとパスワードの取得
            String userId = request.getParameter("userId");
            String password = request.getParameter("password");
            
            if (userId == null || userId.isEmpty() || password == null || password.isEmpty()) {
                session.removeAttribute("csrfToken");
                request.setAttribute("errorMessage", "ユーザーIDとパスワードを入力してください。");
                request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
                return;
            }
            // ログイン処理
            LoginService loginService = new LoginService();
            User loginUser = null;
            String errorMessage = null;
            loginUser = loginService.authenticate(userId, password);
            if (loginUser == null) {
                errorMessage = "ユーザーIDまたはパスワードが違います。";
            }
            // ログイン成功または失敗の処理
            if (loginUser != null) {
                // ログイン成功
                session.removeAttribute("csrfToken");
                session.setAttribute("loginUser", loginUser); // ユーザー情報をセッションに保存
                response.sendRedirect(request.getContextPath() + "/menu"); // メニュー画面へリダイレクト
            } else {
                // ログイン失敗
                session.removeAttribute("csrfToken");
                request.setAttribute("errorMessage", errorMessage);
                request.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            handleSystemError(request, response, e);
            return;
        }
    }
}