package com.fullness.keihiseisan.controller;

import com.fullness.keihiseisan.model.service.UserService;
import com.fullness.keihiseisan.model.value.role;
import com.fullness.keihiseisan.model.value.User;
import com.fullness.keihiseisan.model.exception.ApplicationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * ユーザー削除確認画面のコントローラークラス
 * ユーザー削除確認画面の表示を行う
 */
@WebServlet("/user/delete/confirm")
public class UserDeleteConfirmServlet extends BaseServlet {
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
            // セッション
            HttpSession session = request.getSession(false);
            // 削除対象のユーザーIDを取得
            String userIdToDelete = request.getParameter("userId");
            if (userIdToDelete == null || userIdToDelete.isEmpty()) {
                session.setAttribute("errorMessage", "削除対象のユーザーIDが指定されていません。");
                response.sendRedirect(request.getContextPath() + "/user/list");
                return;
            }
            // ユーザー情報を取得
            UserService userService = new UserService();
            User user = userService.getUserById(userIdToDelete); // 権限チェックも兼ねる
            if (user == null) {
                session.setAttribute("errorMessage", "指定されたユーザーが見つかりません。(ID: " + userIdToDelete + ")");
                response.sendRedirect(request.getContextPath() + "/user/list");
                return;
            }
            request.setAttribute("userToDelete", user);
            request.getRequestDispatcher("/WEB-INF/jsp/user/delete/confirm.jsp").forward(request, response);
        } catch (ApplicationException e) {
            handleError(request, response, e);
        } catch (Exception e) {
            handleSystemError(request, response, e);
        }
    }
    /**
     * POSTリクエストを処理する
     * @param request リクエスト
     * @param response レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // ログインチェック
            loginCheck(request, response);
            // 権限チェック
            roleCheck(request, response, role.SYSTEM_ADMIN);
            // セッション
            HttpSession session = request.getSession(false);
            // 削除対象のユーザーIDを取得
            String userIdToDelete = request.getParameter("userId");
            if (userIdToDelete == null || userIdToDelete.isEmpty()) {
                session.setAttribute("errorMessage", "削除対象のユーザーIDが指定されていません。");
                response.sendRedirect(request.getContextPath() + "/user/list");
                return;
            }
            // ユーザー情報を取得
            UserService userService = new UserService();
            // 削除前にユーザー情報を取得して、完了メッセージで使用できるようにする
            User userBeforeDelete = userService.getUserById(userIdToDelete);
            String userNameForMessage = (userBeforeDelete != null) ? userBeforeDelete.getUserName() : userIdToDelete;
            // ユーザー削除
            userService.deleteUser(userIdToDelete);
            // 完了メッセージをセッションに保存
            session.setAttribute("message", "ユーザー「" + userNameForMessage + " (ID: " + userIdToDelete + ")」を削除しました。");
            response.sendRedirect(request.getContextPath() + "/user/delete/complete");
        } catch (ApplicationException e) {
            handleError(request, response, e);
        } catch (Exception e) {
            handleSystemError(request, response, e);
        }
    }
} 