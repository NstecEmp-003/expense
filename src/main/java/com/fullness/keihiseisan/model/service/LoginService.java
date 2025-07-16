package com.fullness.keihiseisan.model.service;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fullness.keihiseisan.model.dao.UserDAO;
import com.fullness.keihiseisan.model.exception.BusinessException;
import com.fullness.keihiseisan.model.exception.SystemException;
import com.fullness.keihiseisan.model.util.ConnectionManager;
import com.fullness.keihiseisan.model.util.PasswordUtil;
import com.fullness.keihiseisan.model.value.User;

/**
 * ログインサービスクラス
 * ユーザー認証を行う
 */
public class LoginService {
    /** ロガー */
    private static final Logger logger = Logger.getLogger(LoginService.class.getName());
    /**
     * ユーザー認証を行う
     * @param userId ユーザーID
     * @param password 入力されたパスワード
     * @return 認証成功時はUser、失敗時はnull
     * @throws BusinessException 認証に失敗した場合
     * @throws SystemException データベースアクセスエラーまたはパスワード暗号化エラー
     */
    public User authenticate(String userId, String password) throws BusinessException, SystemException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            UserDAO dao = new UserDAO(connectionManager.getConnection());
            User user = dao.findByUserId(userId);
            logger.info("認証処理開始: ユーザーID=" + userId);
            if (user != null) {
                // パスワードの検証
                String hashedPasswordFromDb = user.getPassword();
                String salt = user.getSalt();
                String hashedInputPassword = PasswordUtil.hashPassword(password, salt);
                if (hashedPasswordFromDb.equals(hashedInputPassword)) {
                    // 認証成功時はパスワード情報をクリア
                    user.setPassword(null);
                    user.setSalt(null);
                    logger.info("認証成功: ユーザーID=" + userId);
                    return user;
                } else {
                    throw new BusinessException(
                        "ユーザーIDまたはパスワードが正しくありません。",
                        "Password mismatch for user ID: " + userId
                    );
                }
            } else {
                throw new BusinessException(
                    "ユーザーIDまたはパスワードが正しくありません。",
                    "User not found with ID: " + userId
                );
            }
        } catch (SQLException e) {
            throw new SystemException(
                "認証処理中にシステムエラーが発生しました",
                "Database error during authentication for user ID: " + userId,
                e
            );
        }
    }
}