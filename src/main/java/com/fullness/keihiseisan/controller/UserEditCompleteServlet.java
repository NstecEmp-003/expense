package com.fullness.keihiseisan.controller;

import java.io.IOException;
import com.fullness.keihiseisan.model.value.role;
import com.fullness.keihiseisan.model.value.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ユーザー編集完了画面のコントローラークラス
 * ユーザー編集完了画面の表示を行う
 */
@WebServlet("/user/edit/complete")
public class UserEditCompleteServlet extends BaseServlet {
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
            roleCheck(request, response, role.SYSTEM_ADMIN); 
            // セッション
            HttpSession session = request.getSession(false);
            // メッセージを取得
            String message = (String) session.getAttribute("message");
            User editedUser = (User) session.getAttribute("editedUser");
            // 完了メッセージを設定
            if (message != null) {
                request.setAttribute("message", message);
                session.removeAttribute("message");
            }
            if (editedUser != null) {
                request.setAttribute("editedUser", editedUser);
                session.removeAttribute("editedUser");
            } else {
                // editedUser がない場合は一覧にリダイレクト (直接アクセスなど)
                response.sendRedirect(request.getContextPath() + "/user/list");
                return;
            }
            // ユーザー編集完了画面表示
            request.getRequestDispatcher("/WEB-INF/jsp/user/edit/complete.jsp").forward(request, response);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
} 