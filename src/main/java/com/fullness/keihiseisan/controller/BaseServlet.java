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
     * @param req リクエスト
     * @param resp レスポンス
     * @throws BusinessException ログインしていない場合の例外
     */
    protected void loginCheck(HttpServletRequest req, HttpServletResponse resp) throws BusinessException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            logger.log(Level.WARNING, "Login check failed: No login user found");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
    }
    /**
     * 権限チェック
     * @param req リクエスト
     * @param resp レスポンス
     * @param roleId 権限ID
     * @throws BusinessException 権限がない場合の例外
     */
    protected void roleCheck(HttpServletRequest req, HttpServletResponse resp, int roleId) throws BusinessException {
        HttpSession session = req.getSession(false);
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser.getRoleId() != roleId) {
            throw new BusinessException("権限がありません。");
        }
    }
    /*
     * エラーハンドリング
     */
    protected void handleError(HttpServletRequest req, HttpServletResponse resp, ApplicationException e) throws IOException {
        e.printStackTrace();
    	// ビジネスエラーはWARNINGレベルでログ出力
        logger.log(Level.WARNING, "Business error occurred: " + e.getLogMessage(), e);
        // エラー情報をセッションに保存
        req.getSession().setAttribute("error", e);
        // エラー画面へリダイレクト
        resp.sendRedirect(req.getContextPath() + "/error");
    }
    /*
     * システムエラーハンドリング
     */
    protected void handleSystemError(HttpServletRequest req, HttpServletResponse resp, Exception e) throws IOException {
    	// システムエラーはSEVEREレベルでログ出力
        logger.log(Level.SEVERE, "System error occurred: " + e.getMessage(), e);
        handleError(req, resp, new SystemException(e.getMessage(), e));
    }
}