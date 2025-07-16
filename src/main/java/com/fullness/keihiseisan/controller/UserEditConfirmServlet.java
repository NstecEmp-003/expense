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
 * ユーザー編集確認画面のコントローラークラス
 * ユーザー編集確認画面の表示を行う
 */
@WebServlet("/user/edit/confirm")
public class UserEditConfirmServlet extends BaseServlet {
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
            // ユーザー編集確認画面表示
            User userToEditConfirm = (User) session.getAttribute("userToEditConfirm");
            if (userToEditConfirm == null) {
                session.setAttribute("errorMessage", "編集情報が見つかりません。もう一度入力してください。");
                // 本来は編集対象のuserIdを付けて入力画面に戻すべきだが、セッションが切れている場合はIDも不明瞭
                response.sendRedirect(request.getContextPath() + "/user/list"); 
                return;
            }
            // ユーザー編集確認画面表示
            request.setAttribute("userToEditConfirm", userToEditConfirm);
            request.getRequestDispatcher("/WEB-INF/jsp/user/edit/confirm.jsp").forward(request, response);
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
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // ログインチェック
            loginCheck(request, response);
            // 権限チェック
            roleCheck(request, response, role.SYSTEM_ADMIN);
            // セッション
            HttpSession session = request.getSession(false);
            // ユーザー編集情報取得
            User userToUpdate = (User) session.getAttribute("userToEditConfirm");
            if (userToUpdate == null) {
                session.setAttribute("errorMessage", "編集情報が見つかりません。もう一度入力してください。");
                response.sendRedirect(request.getContextPath() + "/user/list"); 
                return;
            }
            // ユーザー編集処理
            UserService userService = new UserService();
            userService.updateUser(userToUpdate);
            session.removeAttribute("userToEditConfirm"); 
            session.setAttribute("message", "ユーザー「" + userToUpdate.getUserName() + " (ID: " + userToUpdate.getUserId() + ")」の情報を更新しました。");
            session.setAttribute("editedUser", userToUpdate);
            response.sendRedirect(request.getContextPath() + "/user/edit/complete");
        } catch (ApplicationException e) {
            handleError(request, response, e);
        } catch (Exception e) {
            handleSystemError(request, response, e);
        }
    }
} 