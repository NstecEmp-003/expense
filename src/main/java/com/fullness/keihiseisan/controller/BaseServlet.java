package com.fullness.keihiseisan.controller;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fullness.keihiseisan.model.exception.ApplicationException;
import com.fullness.keihiseisan.model.exception.BusinessException;
import com.fullness.keihiseisan.model.exception.SystemException;
import com.fullness.keihiseisan.model.value.User;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/*
 * エラーハンドリングを共通化するための抽象クラス
 */
public abstract class BaseServlet extends HttpServlet {
    /** ロガー */
    private static final Logger logger = Logger.getLogger(BaseServlet.class.getName());
    /**
     * ログインチェック
     * @param request リクエスト
     * @param response レスポンス
     * @throws BusinessException ログインしていない場合の例外
     */
    protected void loginCheck(HttpServletRequest request, HttpServletResponse response) throws BusinessException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            logger.log(Level.WARNING, "Login check failed: No login user found");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
    }
    /**
     * 権限チェック
     * @param request リクエスト
     * @param response レスポンス
     * @param roleId 権限ID
     * @throws BusinessException 権限がない場合の例外
     */
    protected void roleCheck(HttpServletRequest request, HttpServletResponse response, int roleId) throws BusinessException {
        HttpSession session = request.getSession(false);
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser.getRoleId() != roleId) {
            throw new BusinessException("権限がありません。");
        }
    }
    /*
     * エラーハンドリング
     */
    protected void handleError(HttpServletRequest request, HttpServletResponse response, ApplicationException e) throws IOException {
        e.printStackTrace();
    	// ビジネスエラーはWARNINGレベルでログ出力
        logger.log(Level.WARNING, "Business error occurred: " + e.getLogMessage(), e);
        // エラー情報をセッションに保存
        request.getSession().setAttribute("error", e);
        // エラー画面へリダイレクト
        response.sendRedirect(request.getContextPath() + "/error");
    }
    /*
     * システムエラーハンドリング
     */
    protected void handleSystemError(HttpServletRequest request, HttpServletResponse response, Exception e) throws IOException {
    	// システムエラーはSEVEREレベルでログ出力
        logger.log(Level.SEVERE, "System error occurred: " + e.getMessage(), e);
        handleError(request, response, new SystemException(e.getMessage(), e));
    }
}