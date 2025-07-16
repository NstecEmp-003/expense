package com.fullness.keihiseisan.controller;

import java.io.IOException;
import java.util.List;

import com.fullness.keihiseisan.model.service.UserService;
import com.fullness.keihiseisan.model.value.role;
import com.fullness.keihiseisan.model.value.User;
import com.fullness.keihiseisan.model.exception.ApplicationException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ユーザー一覧画面のコントローラークラス
 * ユーザー一覧画面の表示を行う
 */
@WebServlet("/user/list")
public class UserListServlet extends BaseServlet {
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
            // ユーザー一覧取得
            UserService userService = new UserService();
            List<User> userList = userService.getAllUsers();
            request.setAttribute("userList", userList);
            // メッセージ表示
            // 他の処理からのメッセージをセッションから取得
            HttpSession session = request.getSession(false);
            String message = (String) session.getAttribute("message");
            if (message != null) {
                request.setAttribute("message", message);
                session.removeAttribute("message");
            }
            // このServlet内で発生したerrorMessageFromService、または他の処理からのerrorMessageをセッションから取得
            String errorMessage = (String) session.getAttribute("errorMessage");
            if (errorMessage != null) {
                request.setAttribute("errorMessage", errorMessage);
                session.removeAttribute("errorMessage");
            }
            // ユーザー一覧画面表示
            request.getRequestDispatcher("/WEB-INF/jsp/user/list.jsp").forward(request, response);
        } catch (ApplicationException e) {
            handleError(request, response, e);
        } catch (Exception e) {
            handleSystemError(request, response, e);
            return;
        }
    }
} 