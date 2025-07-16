package com.fullness.keihiseisan.controller.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.fullness.keihiseisan.model.value.User;

/**
 * 認証フィルタークラス
 * ユーザーの認証を行う
 */
@WebFilter("/*")
public class AuthorizationFilter implements Filter {
    /** 共通パスリスト */
    private static final Set<String> COMMON_PATHS = new HashSet<>(Arrays.asList(
        "",
        "/menu",
        "/login",
        "/logout",
        "/error"
    ));
    /** 一般社員パスリスト */
    private static final Set<String> APPLICANT_PATHS = new HashSet<>(Arrays.asList(
        "/expense/*"
    ));
    /** 課長・部長パスリスト */
    private static final Set<String> APPROVER_PATHS = new HashSet<>(Arrays.asList(
        "/approval/*",
        "/expense/download"
    ));
    /** システム管理者パスリスト */
    private static final Set<String> ADMIN_PATHS = new HashSet<>(Arrays.asList(
        "/user/*"
        )
    );
    /** 申請者ロール */
    private static final int ROLE_APPLICANT = 1;
    /** 課長ロール */
    private static final int ROLE_MANAGER = 2;
    /** 部長ロール */
    private static final int ROLE_DEPARTMENT_MANAGER = 3;
    /** システム管理者ロール */
    private static final int ROLE_ADMIN = 9;
    /**
     * フィルター処理
     * @param request リクエスト
     * @param response レスポンス
     * @param chain フィルターチェーン
     * @throws IOException 入出力例外
     * @throws ServletException サーブレット例外
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // リクエストとレスポンスの型キャスト
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // セッションを取得
        HttpSession session = httpRequest.getSession(false);
        // パスとコンテキストパスを取得
        String path = httpRequest.getServletPath();
        String contextPath = httpRequest.getContextPath();
        // ログインページ、ログイン処理、静的リソースはチェック対象外
        if (
            path.equals("/login") ||
            path.startsWith("/css/") || 
            path.startsWith("/js/") || 
            path.startsWith("/images/") || 
            path.startsWith("/uploads/") ||
            path.startsWith("/_static/") || 
            path.equals("/favicon.ico")
        ) {
            chain.doFilter(request, response);
            return;
        }
        // ログインチェック
        User loginUser = null;
        if (session != null) {
            loginUser = (User) session.getAttribute("loginUser");
        }
        System.out.println("loginUser: " + loginUser);
        if (loginUser == null) {
            // 未ログインの場合、ログインページへリダイレクト
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }
        // アクセス権限チェック
        boolean authorized = false;
        int roleId = loginUser.getRoleId();
        System.out.println("[AuthorizationFilter] Checking authorization for path: " + path + " | UserRoleID: " + roleId);
        // 共通パスのチェック
        if (isPathAllowed(path, COMMON_PATHS)) {
            authorized = true;
            System.out.println("[AuthorizationFilter] Path matched COMMON_PATHS.");
        } else if (roleId == ROLE_APPLICANT) {
            if (isPathAllowed(path, APPLICANT_PATHS)) {
                authorized = true;
                System.out.println("[AuthorizationFilter] Path matched APPLICANT_PATHS for role 1.");
            }
        } else if (roleId == ROLE_MANAGER || roleId == ROLE_DEPARTMENT_MANAGER) {
            if (isPathAllowed(path, APPROVER_PATHS)) {
                authorized = true;
                System.out.println("[AuthorizationFilter] Path matched APPROVER_PATHS for role 2 or 3.");
            }
        } else if (roleId == ROLE_ADMIN) {
            System.out.println("[AuthorizationFilter] Checking ADMIN_PATHS for role 9. Path: " + path);
            if (isPathAllowed(path, ADMIN_PATHS)) {
                authorized = true;
                System.out.println("[AuthorizationFilter] Path matched ADMIN_PATHS for role 9.");
            }
        }
        System.out.println("[AuthorizationFilter] Authorization result: " + authorized);
        // 権限があれば次の処理へ
        if (authorized) {
            chain.doFilter(request, response);
        } else {
            request.setAttribute("errorMessage", "このページへのアクセス権限がありません。");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp").forward(request, response);
        }
    }
    /**
     * ワイルドカード対応のパス許可判定メソッド
     */
    private boolean isPathAllowed(String path, Set<String> allowedPaths) {
        for (String allowedPath : allowedPaths) {
            if (allowedPath.endsWith("/*")) {
                String prefix = allowedPath.substring(0, allowedPath.length() - 2);
                if (path.startsWith(prefix)) {
                    return true;
                }
            } else if (path.equals(allowedPath)) {
                return true;
            }
        }
        return false;
    }
    /**
     * フィルター初期化処理
     * @param filterConfig フィルター設定
     * @throws ServletException サーブレット例外
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // フィルター初期化処理
    }
    /**
     * フィルター終了処理
     */
    @Override
    public void destroy() {
        // フィルター終了処理
    }
}