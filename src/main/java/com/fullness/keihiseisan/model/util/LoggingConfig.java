package com.fullness.keihiseisan.model.util;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.fullness.keihiseisan.model.exception.SystemException;

/**
 * ロギング設定クラス
 * ロギングの初期化を行う
 * 本番環境の場合はコンソールにログ出力
 * 開発環境の場合はファイルにログ出力
 */
public class LoggingConfig {
    /** ロガー */
    private static final Logger logger = Logger.getLogger(LoggingConfig.class.getName());
    /** 環境変数名 */
    private static final String ENV_VAR_NAME = "APP_ENV";
    /** 本番環境 */
    private static final String PROD_ENV = "production";
    /**
     * 静的初期化ブロック
     * ロギングの初期化を行う
     */
    static {
        try {
            // 環境変数から環境を取得
            String env = System.getenv(ENV_VAR_NAME);
            // 本番環境の場合はコンソールにログ出力
            if (PROD_ENV.equalsIgnoreCase(env)) {
                // 本番環境: コンソールにログ出力
                setupConsoleLogging();
            } else {
                // 開発環境の場合はファイルにログ出力
                // setupFileLogging();
            	setupConsoleLogging();
            }
        } catch (SystemException e) {
            System.err.println("ログ設定の初期化に失敗しました: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * コンソールにログ出力を行う
     * @throws SystemException ログ設定の初期化に失敗した場合
     */
    private static void setupConsoleLogging() throws SystemException {
        try {
            // ルートロガーのハンドラをクリア
            Logger rootLogger = LogManager.getLogManager().getLogger("");
            for (Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }
            // コンソールハンドラを設定
            ConsoleHandler consoleHandler = new ConsoleHandler();
            SimpleFormatter formatter = new SimpleFormatter();
            consoleHandler.setFormatter(formatter);
            consoleHandler.setLevel(Level.INFO);
            rootLogger.addHandler(consoleHandler);
            rootLogger.setLevel(Level.INFO);
            System.out.println("本番環境: コンソール出力モードでログを初期化しました");
        } catch (Exception e) {
            throw new SystemException("コンソールログ設定の初期化に失敗しました", e);
        }
    }
    /**
     * ファイルにログ出力を行う
     * @throws SystemException ログ設定の初期化に失敗した場合
     */
    private static void setupFileLogging() throws SystemException {
        // カレントディレクトリ確認
        String currentPath = Paths.get("").toAbsolutePath().toString();
        System.out.println("実行カレントディレクトリ: " + currentPath);

        // logsディレクトリ生成（なければ）
        String logDirPath = Paths.get("logs").toAbsolutePath().toString();
        java.io.File logDir = new java.io.File(logDirPath);
        if (!logDir.exists()) {
            boolean created = logDir.mkdirs();
            System.out.println("logsディレクトリ自動生成: " + (created ? "成功" : "失敗"));
        } else {
            System.out.println("logsディレクトリは既に存在します: " + logDirPath);
        }
        try {
            Logger rootLogger = LogManager.getLogManager().getLogger("");
            for (Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }
            FileHandler fileHandler = new FileHandler("logs/application_%g.log", 10485760, 10, true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            fileHandler.setLevel(Level.INFO);

            rootLogger.addHandler(fileHandler);
            rootLogger.setLevel(Level.INFO);
        } catch (IOException ex) {
            throw new SystemException("ファイルログ設定の初期化に失敗しました", ex);
        }
    }
    /**
     * ロギングの初期化を行う
     */
    public static void init() {
        // 明示的な初期化メソッド
        String env = System.getenv(ENV_VAR_NAME);
        logger.info("ロギングシステムが初期化されました (環境: " + (env != null ? env : "開発") + ")");
    }
}