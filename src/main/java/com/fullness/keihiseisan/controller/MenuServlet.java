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
     * @param req リクエスト
     * @param resp レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // ログインチェック
            loginCheck(req, resp);
            // セッション
            HttpSession session = req.getSession(false);
            // 確認画面から「戻る」ボタンでメニューに戻った場合は入力データを破棄
            if (req.getParameter("clear") != null && "expense".equals(req.getParameter("clear"))) {
                session.removeAttribute("expenseInput");
            }
            User loginUser = (User) session.getAttribute("loginUser");
            req.setAttribute("loginUserName", loginUser.getUserName()); // JSP表示用
            // 不正操作メッセージがあれば表示
            String errorMessage = (String) session.getAttribute("illegalOperationMsg");
            if (errorMessage != null) {
                req.setAttribute("errorMessage", errorMessage);
                session.removeAttribute("illegalOperationMsg"); // 表示したら消す
            }
            // 承認一覧からのエラーメッセージを削除
            if (session.getAttribute("errorMessage") != null) {
                session.removeAttribute("errorMessage");
            }
            req.getRequestDispatcher("/WEB-INF/jsp/menu.jsp").forward(req, resp);
        } catch (ApplicationException e) {
            handleError(req, resp, e);
        } catch (Exception e) {
            handleSystemError(req, resp, e);
        }
    }
}