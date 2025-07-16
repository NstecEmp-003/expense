package com.fullness.keihiseisan.controller;

import java.io.IOException;

import com.fullness.keihiseisan.model.exception.ApplicationException;
import com.fullness.keihiseisan.model.value.role;
import com.fullness.keihiseisan.model.value.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ユーザー登録完了画面のコントローラークラス
 * ユーザー登録完了画面の表示を行う
 */
@WebServlet("/user/register/complete")
public class UserRegisterCompleteServlet extends BaseServlet {
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
            // セッションからメッセージと登録ユーザー情報を取得
            String message = (String) session.getAttribute("message");
            User registeredUser = (User) session.getAttribute("registeredUser");
            // 完了メッセージを取得
            if (message != null) {
                request.setAttribute("message", message);
                session.removeAttribute("message");
            }
            if (registeredUser != null) {
                request.setAttribute("registeredUser", registeredUser);
                session.removeAttribute("registeredUser");
            } else {
                // registeredUser がない場合は一覧にリダイレクト (直接アクセスなど)
                // またはエラーメッセージを表示して完了画面に遷移でもよい
                response.sendRedirect(request.getContextPath() + "/user/list");
                return;
            }
            // ユーザー登録完了画面表示
            request.getRequestDispatcher("/WEB-INF/jsp/user/register/complete.jsp").forward(request, response);
        } catch (ApplicationException e) {
            handleError(request, response, e);
        } catch (Exception e) {
            handleSystemError(request, response, e);
        }
    }
} 