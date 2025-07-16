package com.fullness.keihiseisan.controller;

import java.io.IOException;
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
 * ユーザー登録確認画面のコントローラークラス
 * ユーザー登録確認画面の表示を行う
 */
@WebServlet("/user/register/confirm")
public class UserRegisterConfirmServlet extends BaseServlet {
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
            // ユーザー登録確認画面表示
            User userToRegister = (User) session.getAttribute("userToRegister");
            if (userToRegister == null) {
                // 確認情報がない場合は入力画面に戻す（エラーメッセージ付き）
                session.setAttribute("errorMessage", "登録情報が見つかりません。もう一度入力してください。");
                response.sendRedirect(request.getContextPath() + "/user/register/input");
                return;
            }
            // ユーザー登録確認画面表示
            request.setAttribute("userToRegister", userToRegister);
            request.getRequestDispatcher("/WEB-INF/jsp/user/register/confirm.jsp").forward(request, response);
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
            // ユーザー登録確認画面表示
            User userToRegister = (User) session.getAttribute("userToRegister");
            if (userToRegister == null) {
                session.setAttribute("errorMessage", "登録情報が見つかりません。もう一度入力してください。");
                response.sendRedirect(request.getContextPath() + "/user/register/input");
                return;
            }
            // ユーザー登録処理
            UserService userService = new UserService();
            userService.registerUser(userToRegister);
            // 登録成功
            session.removeAttribute("userToRegister");
            session.setAttribute("message", "ユーザー「" + userToRegister.getUserName() + " (ID: " + userToRegister.getUserId() + ")」を登録しました。");
            session.setAttribute("registeredUser", userToRegister);
            response.sendRedirect(request.getContextPath() + "/user/register/complete");
            return;
        } catch (ApplicationException e) {
            handleError(request, response, e);
        } catch (Exception e) {
            handleSystemError(request, response, e);
        }
    }
} 