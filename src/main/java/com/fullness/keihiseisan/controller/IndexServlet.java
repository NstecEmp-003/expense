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
     * @param req リクエスト
     * @param resp レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        // ログイン状態をチェック
        if (session != null && session.getAttribute("loginUser") != null) {
            // ログイン済みの場合はメニュー画面にリダイレクト
            resp.sendRedirect("menu");
        } else {
            // 未ログインの場合はログイン画面にリダイレクト
            resp.sendRedirect("login");
        }
    }
} 