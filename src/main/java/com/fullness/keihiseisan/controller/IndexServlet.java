package com.fullness.keihiseisan.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * トップ画面のコントローラークラス
 * トップ画面の表示を行う
 */
@WebServlet("")
public class IndexServlet extends BaseServlet {
    /**
     * GETリクエストを処理する
     * @param request リクエスト
     * @param response レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        // ログイン状態をチェック
        if (session != null && session.getAttribute("loginUser") != null) {
            // ログイン済みの場合はメニュー画面にリダイレクト
            response.sendRedirect("menu");
        } else {
            // 未ログインの場合はログイン画面にリダイレクト
            response.sendRedirect("login");
        }
    }
} 