package com.fullness.keihiseisan.controller;

import java.io.IOException;

import com.fullness.keihiseisan.model.exception.ApplicationException;
import com.fullness.keihiseisan.model.value.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * メニュー画面のコントローラークラス
 * メニュー画面の表示を行う
 */
@WebServlet("/menu")
public class MenuServlet extends BaseServlet {
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
            // セッション
            HttpSession session = request.getSession(false);
            // 確認画面から「戻る」ボタンでメニューに戻った場合は入力データを破棄
            if (request.getParameter("clear") != null && "expense".equals(request.getParameter("clear"))) {
                session.removeAttribute("expenseInput");
            }
            User loginUser = (User) session.getAttribute("loginUser");
            request.setAttribute("loginUserName", loginUser.getUserName()); // JSP表示用
            // 不正操作メッセージがあれば表示
            String errorMessage = (String) session.getAttribute("illegalOperationMsg");
            if (errorMessage != null) {
                request.setAttribute("errorMessage", errorMessage);
                session.removeAttribute("illegalOperationMsg"); // 表示したら消す
            }
            // 承認一覧からのエラーメッセージを削除
            if (session.getAttribute("errorMessage") != null) {
                session.removeAttribute("errorMessage");
            }
            request.getRequestDispatcher("/WEB-INF/jsp/menu.jsp").forward(request, response);
        } catch (ApplicationException e) {
            handleError(request, response, e);
        } catch (Exception e) {
            handleSystemError(request, response, e);
        }
    }
}