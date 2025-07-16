package com.fullness.keihiseisan.controller;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.fullness.keihiseisan.model.service.ApprovalService;
import com.fullness.keihiseisan.model.value.ExpenseApplication;
import com.fullness.keihiseisan.model.value.User;

/**
 * 承認状況一覧画面のコントローラークラス
 * 承認状況一覧画面の表示を行う
 */
@WebServlet("/approval/list")
public class ApprovalListServlet extends BaseServlet {
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
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("loginUser") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            User loginUser = (User) session.getAttribute("loginUser");
            // アクセス権チェック
            if (loginUser.getRoleId() < 2) { // 課長以上でない場合
                 session.setAttribute("errorMessage", "承認状況一覧を表示する権限がありません。");
                 response.sendRedirect(request.getContextPath() + "/menu");
                 return;
            }
            // 承認状況一覧データ取得
            ApprovalService service = new ApprovalService();
            List<ExpenseApplication> list = service.getDepartmentApplications(loginUser);
            request.setAttribute("applicationList", list);
            if(list.isEmpty()) {
                request.setAttribute("message", "担当部署の申請データはありません。");
            }
            // 承認状況一覧画面表示
            request.getRequestDispatcher("/WEB-INF/jsp/approval/list.jsp").forward(request, response);
        } catch (Exception e) {
            handleSystemError(request, response, e);
            return;
        }
    }
}