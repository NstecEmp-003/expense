package com.fullness.keihiseisan.model.exception;

/**
 * システム例外 サブクラス
 * DBやシステム例外
 */
public class SystemException extends ApplicationException {
    /**
     * コンストラクタ
     * @param logMessage ログ用メッセージ
     * @param cause 原因
     */
    public SystemException(String logMessage, Throwable cause) {
        super("システムエラーが発生しました。", logMessage, true, cause);
    }
    /**
     * コンストラクタ
     * @param userMessage ユーザー向けメッセージ
     * @param logMessage ログ用メッセージ
     * @param cause 原因
     */
    public SystemException(String userMessage, String logMessage, Throwable cause) {
        super(userMessage, logMessage, true, cause);
    }
}