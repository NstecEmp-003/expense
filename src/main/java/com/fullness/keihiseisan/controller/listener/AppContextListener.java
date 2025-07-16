package com.fullness.keihiseisan.controller.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import com.fullness.keihiseisan.model.util.LoggingConfig;

/**
 * アプリケーション起動時にログ設定を初期化するリスナークラス
 */
@WebListener
public class AppContextListener implements ServletContextListener {
    /**
     * アプリケーション起動時にログ設定を初期化する
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("アプリケーションの起動を開始します");
        // ロギング設定の初期化
        LoggingConfig.init();
        System.out.println("ロギング設定を初期化しました");
    }
    /**
     * アプリケーション終了時にログ設定を破棄する
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("アプリケーションを終了します");
    }
} 