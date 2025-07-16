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
     * @param req リクエスト
     * @param resp レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        if (session != null && session.getAttribute("loginUser") != null) {
            // ログイン済みの場合、メニュー画面へリダイレクト
            resp.sendRedirect(req.getContextPath() + "/menu");
            return;
        }
        // CSRFトークンの生成と設定
        String csrfToken = UUID.randomUUID().toString();
        session = req.getSession(true);
        session.setAttribute("csrfToken", csrfToken);
        req.setAttribute("csrfToken", csrfToken);
        // ログイン画面 (P001) を表示
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
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
            // CSRFトークンの検証
            HttpSession session = req.getSession(false);
            String sessionToken = (String) session.getAttribute("csrfToken");
            String requestToken = req.getParameter("csrfToken");
            if (sessionToken == null || !sessionToken.equals(requestToken)) {
                req.setAttribute("errorMessage", "セッションが切れました。再度ログインしてください。");
                req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
                return;
            }
            // ユーザーIDとパスワードの取得
            String userId = req.getParameter("userId");
            String password = req.getParameter("password");
            if (userId == null || userId.isEmpty() || password == null || password.isEmpty()) {
                 session.removeAttribute("csrfToken");
                 req.setAttribute("errorMessage", "ユーザーIDとパスワードを入力してください。");
                 req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
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
                resp.sendRedirect(req.getContextPath() + "/menu"); // メニュー画面へリダイレクト
            } else {
                // ログイン失敗
                session.removeAttribute("csrfToken");
                req.setAttribute("errorMessage", errorMessage);
                req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            handleSystemError(req, resp, e);
            return;
        }
    }
}