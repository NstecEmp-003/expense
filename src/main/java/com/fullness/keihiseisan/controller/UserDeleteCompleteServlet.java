package com.fullness.keihiseisan.controller;

import java.io.IOException;
import com.fullness.keihiseisan.model.value.role;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ユーザー削除コントローラークラス
 * ユーザー削除完了画面の表示を行う
 */
@WebServlet("/user/delete/complete")
public class UserDeleteCompleteServlet extends BaseServlet {
    /**
     * GETリクエストを処理する
     * @param request リクエスト
     * @param response レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // ログインチェック
            loginCheck(request, response);
            // 権限チェック
            roleCheck(request, response, role.SYSTEM_ADMIN); 
            // メッセージを取得
            HttpSession session = request.getSession(false);
            String message = (String) session.getAttribute("message");
            if (message != null) {
                request.setAttribute("message", message);
                session.removeAttribute("message"); // 一度表示したら削除
            } else {
                // 直接アクセスされた場合などはメッセージがないので一覧にリダイレクト
                response.sendRedirect(request.getContextPath() + "/user/list");
                return;
            }
            // ユーザー削除完了画面表示
            request.getRequestDispatcher("/WEB-INF/jsp/user/delete/complete.jsp").forward(request, response);
        } catch (Exception e) {
            handleSystemError(request, response, e);
        }
    }
} 