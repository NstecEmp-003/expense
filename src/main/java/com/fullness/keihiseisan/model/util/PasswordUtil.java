package com.fullness.keihiseisan.model.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fullness.keihiseisan.model.exception.SystemException;

/**
 * パスワードユーティリティクラス
 * パスワードのハッシュ化を行う
 */
public class PasswordUtil {
    /** ハッシュアルゴリズム */
    private static final String HASH_ALGORITHM = "SHA-256";
    /** ソルトの長さ */
    private static final int SALT_LENGTH_BYTES = 16; // 16 bytes = 128 bits
    /** ロガー */
    private static final Logger logger = Logger.getLogger(PasswordUtil.class.getName());
    /**
     * ランダムなソルトを生成する
     * @return 生成されたソルト (Base64エンコード文字列)
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH_BYTES];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    /**
     * パスワードとソルトからハッシュ値を生成する
     * @param password 平文のパスワード
     * @param salt     ソルト (Base64エンコード文字列)
     * @return ハッシュ化されたパスワード (Hex文字列)
     * @throws SystemException ハッシュ化処理中にエラーが発生した場合
     */
    public static String hashPassword(String password, String salt) throws SystemException {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            // ソルトをパスワードに追加
            // Base64エンコードされたソルトをデコードしてバイト配列として結合します。
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
            // ソルトをパスワードに追加
            byte[] combined = new byte[passwordBytes.length + saltBytes.length];
            System.arraycopy(passwordBytes, 0, combined, 0, passwordBytes.length);
            System.arraycopy(saltBytes, 0, combined, passwordBytes.length, saltBytes.length);
            // ハッシュ値を生成
            md.update(combined);
            byte[] digest = md.digest();
            // ハッシュ値を16進数文字列に変換
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new SystemException("パスワードのハッシュ化に失敗しました。アルゴリズム: " + HASH_ALGORITHM, e);
        }
    }
} 