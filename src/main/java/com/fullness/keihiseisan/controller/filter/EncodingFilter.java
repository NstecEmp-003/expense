package com.fullness.keihiseisan.controller.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
/**
 * エンコーディングフィルター
 * リクエストとレスポンスのエンコーディングをUTF-8に設定する
 */
@WebFilter(urlPatterns = { "/expense/*" }, initParams = { @WebInitParam(name = "encoding", value = "UTF-8") })
public class EncodingFilter implements Filter {
    /**
     * エンコーディング
     */
    private String encoding;

    /**
     * フィルターの初期化
     * @param filterConfig フィルターの設定
     * @throws ServletException エラーが発生した場合
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        encoding = filterConfig.getInitParameter("encoding");
        if (encoding == null) {
            encoding = "UTF-8"; // デフォルト
        }
    }
    /**
     * フィルターの実行
     * @param request リクエスト
     * @param response レスポンス
     * @param chain フィルターチェーン
     * @throws IOException 入出力エラーが発生した場合
     * @throws ServletException エラーが発生した場合
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        request.setCharacterEncoding(encoding);
        // response.setCharacterEncoding(encoding); // レスポンスにも設定する場合
        chain.doFilter(request, response);
    }
    /**
     * フィルターの破棄
     */
    @Override
    public void destroy() {}
}