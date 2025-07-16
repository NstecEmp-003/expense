package com.fullness.keihiseisan.model.exception;

/**
 * アプリケーション例外 基底クラス
 */
public class ApplicationException extends Exception {
    /** ユーザー向けメッセージ */
    private final String userMessage;
    /** ログ用メッセージ */
    private final String logMessage;
    /** システムエラーかどうか */
    private final boolean isSystemError;
    /**
     * コンストラクタ
     * @param userMessage ユーザー向けメッセージ
     * @param logMessage ログ用メッセージ
     * @param isSystemError システムエラーかどうか
     * @param cause 原因
     */
    public ApplicationException(String userMessage, String logMessage, boolean isSystemError, Throwable cause) {
        super(userMessage, cause);
        this.userMessage = userMessage;
        this.logMessage = logMessage;
        this.isSystemError = isSystemError;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getLogMessage() {
        return logMessage;
    }

    public boolean isSystemError() {
        return isSystemError;
    }
}