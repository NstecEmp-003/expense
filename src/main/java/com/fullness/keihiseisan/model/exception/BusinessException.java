package com.fullness.keihiseisan.model.exception;

/**
 * ビジネス例外 サブクラス
 * 業務ロジック例外
 */
public class BusinessException extends ApplicationException {
    /**
     * コンストラクタ
     * @param userMessage ユーザー向けメッセージ
     */
    public BusinessException(String userMessage) {
        super(userMessage, userMessage, false, null);
    }

    /**
     * コンストラクタ
     * @param userMessage ユーザー向けメッセージ
     * @param logMessage ログ用メッセージ
     */
    public BusinessException(String userMessage, String logMessage) {
        super(userMessage, logMessage, false, null);
    }

    /**
     * コンストラクタ
     * @param userMessage ユーザー向けメッセージ
     * @param logMessage ログ用メッセージ
     * @param cause 原因
     */
    public BusinessException(String userMessage, String logMessage, Throwable cause) {
        super(userMessage, logMessage, false, cause);
    }
}