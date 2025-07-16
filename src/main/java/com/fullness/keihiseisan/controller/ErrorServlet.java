package com.fullness.keihiseisan.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fullness.keihiseisan.model.exception.ApplicationException;

/**
 * エラーハンドリング用コントローラークラス
 * エラー画面の表示を行う
 */
@WebServlet("/error")
public class ErrorServlet extends HttpServlet {
    /**
     * GETリクエストを処理する
     * @param req リクエスト
     * @param resp レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userMessage = "エラーが発生しました。";
        ApplicationException sessionError = (ApplicationException) req.getSession().getAttribute("error");
        // セッションからエラー情報を取得
        if (sessionError != null) {
            userMessage = sessionError.getUserMessage();
            req.getSession().removeAttribute("error");
        }
        req.setAttribute("errorMessage", userMessage);
        req.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(req, resp);
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
        doGet(req, resp); // GETリクエストと同じ処理を行う
    }
}