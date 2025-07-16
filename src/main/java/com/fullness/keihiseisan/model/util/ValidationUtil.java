package com.fullness.keihiseisan.model.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * バリデーションユーティリティクラス
 * バリデーションを行う
 */
public class ValidationUtil {
    /** ロガー */
    private static final Logger logger = Logger.getLogger(ValidationUtil.class.getName());
    /**
     * HTML特殊文字をエスケープする
     * @param input 入力文字列
     * @return サニタイズされた文字列
     */
    public static String sanitize(String input) {
        if (input == null) {
            return "";
        }
        return input.trim()
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }
    /**
     * HTML文字列をサニタイズする
     * @param input 入力文字列
     * @return サニタイズされた文字列
     */
    public static String sanitizeHtml(String input) {
        return sanitize(input);
    }
    /**
     * 文字列が空かどうかチェックする
     * @param str 文字列
     * @return 文字列が空かどうか
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    /**
     * 日付形式（YYYY-MM-DD）が有効かチェックする
     * @param dateStr 日付文字列
     * @return 日付が有効かどうか
     */
    public static boolean isValidDate(String dateStr) {
        if (isEmpty(dateStr)) {
            logger.log(Level.FINE, "日付が空です");
            return false;
        }
        
        logger.log(Level.FINE, "日付のバリデーション: " + dateStr);
        
        try {
            // HTML5のdate型は"YYYY-MM-DD"形式で送信される
            LocalDate date = LocalDate.parse(dateStr);
            logger.log(Level.FINE, "日付のパース成功: " + date);
            return true;
        } catch (DateTimeParseException e) {
            // 標準形式でパースできない場合、他の形式も試してみる
            try {
                // "YYYY/MM/DD"形式を試す
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                LocalDate date = LocalDate.parse(dateStr, formatter);
                logger.log(Level.FINE, "代替日付形式でパース成功: " + date);
                return true;
            } catch (DateTimeParseException e2) {
                logger.log(Level.FINE, "日付のパースに失敗: " + e2.getMessage());
                return false;
            }
        }
    }
    /**
     * 金額が有効な範囲内かチェックする
     * @param amountStr 金額文字列
     * @return 金額が有効かどうか
     */
    public static boolean isValidAmount(String amountStr) {
        if (isEmpty(amountStr)) {
            return false;
        }
        try {
            int amount = Integer.parseInt(amountStr);
            return isAmountInRange(amount, 1, 9999999);
        } catch (NumberFormatException e) {
            return false;
        }
    }
    /**
     * 金額が指定範囲内かチェックする
     * @param amount 金額
     * @param min 最小値
     * @param max 最大値
     * @return 金額が指定範囲内かどうか
     */
    public static boolean isAmountInRange(int amount, int min, int max) {
        return amount >= min && amount <= max;
    }
    /**
     * 文字列の長さが有効かチェックする
     * @param str 文字列
     * @param maxLength 最大長
     * @return 文字列の長さが有効かどうか
     */
    public static boolean isValidLength(String str, int maxLength) {
        if (str == null) {
            return true; // nullは許容する（必須チェックは別で行う）
        }
        return str.length() <= maxLength;
    }
    /**
     * ファイルの拡張子が許可されているものかチェックする
     * @param fileName ファイル名
     * @return ファイルの拡張子が許可されているかどうか
     */
    public static boolean isAllowedFileExtension(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return false;
        }
        String lowerFileName = fileName.toLowerCase();
        return lowerFileName.endsWith(".jpg") || 
               lowerFileName.endsWith(".jpeg") || 
               lowerFileName.endsWith(".png");
    }
    /**
     * ファイルサイズが有効かチェックする
     * @param size ファイルサイズ
     * @return ファイルサイズが有効かどうか
     */
    public static boolean isFileSizeValid(long size) {
        return size > 0 && size <= 5 * 1024 * 1024; // 5MB
    }
    /**
     * 必須項目チェック
     * @param value チェック対象の文字列
     * @return 必須項目が入力されているかどうか
     */
    public static boolean isRequired(String value) {
        return !isEmpty(value);
    }
    /**
     * 日付が有効かチェックする
     * @param dateStr 日付文字列
     * @return 日付が有効かどうか
     */
    public static boolean isValidDateFormat(String dateStr) {
        return isValidDate(dateStr);
    }
    /**
     * 日付が有効かチェックする
     * @param dateStr 日付文字列
     * @param allowFuture 未来日を許可するかどうか
     * @return 日付が有効かどうか
     */
    public static boolean isDateLogical(String dateStr, boolean allowFuture) {
        if (!isValidDateFormat(dateStr)) {
            return false;
        }
        LocalDate date;
        try {
            // 日付文字列の形式によって適切なパース方法を選択
            if (dateStr.contains("/")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                date = LocalDate.parse(dateStr, formatter);
            } else {
                date = LocalDate.parse(dateStr);
            }
            
            LocalDate now = LocalDate.now();
            return allowFuture || !date.isAfter(now);
        } catch (DateTimeParseException e) {
            logger.log(Level.FINE, "isDateLogicalでパースエラー: " + e.getMessage());
            return false;
        }
    }
    /**
     * 文字列の長さが有効かチェックする
     * @param str 文字列
     * @param maxLength 最大長
     * @return 文字列の長さが有効かどうか
     */
    public static boolean isLengthValid(String str, int maxLength) {
        return isValidLength(str, maxLength);
    }
}