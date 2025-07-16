package com.fullness.keihiseisan.controller;

import java.io.IOException;

import com.fullness.keihiseisan.model.exception.BusinessException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * 申請完了画面のコントローラークラス
 * 申請完了画面の表示を行う
 */
@WebServlet("/expense/apply/complete")
public class ApplyCompleteServlet extends BaseServlet {
    /**
     * GETリクエストを処理する
     * @param req リクエスト
     * @param resp レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // ログインチェック
        HttpSession session = req.getSession(false);
        if (session == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        // セッションから申請ID取得
        Integer applicationId = (Integer) session.getAttribute("completedApplicationId");
        if (applicationId == null) {
            // URL パラメータからも試す
            String idParam = req.getParameter("id");
            if (idParam != null && !idParam.isEmpty()) {
                try {
                    applicationId = Integer.parseInt(idParam);
                } catch (NumberFormatException e) {
                    handleError(req, resp, new BusinessException("申請IDの変換に失敗しました。"));
                }
            }
            // それでも申請IDがない場合はメニューへリダイレクト
            if (applicationId == null) {
                resp.sendRedirect(req.getContextPath() + "/menu");
                return;
            }
        }
        // 申請IDをリクエスト属性に設定
        req.setAttribute("applicationId", applicationId);
        // セッションから申請ID削除（再表示対策）
        session.removeAttribute("completedApplicationId");
        // 完了画面表示
        req.getRequestDispatcher("/WEB-INF/jsp/expense/apply/complete.jsp").forward(req, resp);
    }
}