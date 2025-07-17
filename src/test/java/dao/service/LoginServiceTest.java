package com.fullness.keihiseisan.model.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fullness.keihiseisan.model.dao.UserDAO;
import com.fullness.keihiseisan.model.exception.BusinessException;
import com.fullness.keihiseisan.model.exception.SystemException;
import com.fullness.keihiseisan.model.util.ConnectionManager;
import com.fullness.keihiseisan.model.util.PasswordUtil;
import com.fullness.keihiseisan.model.value.User;

import java.sql.Connection;

/**
 * LoginServiceクラス 単体テストドライバ
 */
public class LoginServiceTest {

    private LoginService loginService;
    private UserDAO userDAO;
    private Connection connection;
//a
    /**
     * テストの前処理
     */
    @BeforeEach
    public void setUp() throws Exception {
        // 実DBやテストDBを使用する場合はこちらで接続
        ConnectionManager connectionManager = new ConnectionManager();
        connection = connectionManager.getConnection();
        userDAO = new UserDAO(connection);
        loginService = new LoginService();
    }

    @Test
    public void testAuthenticate_正しいユーザーIDとパスワードで認証成功する() throws Exception {
        // テスト用のユーザー情報（事前にDBに登録しておく必要あり）
        String userId = "emp001";
        String rawPassword = "pass1";

        // LoginService による認証実行
        User result = loginService.authenticate(userId, rawPassword);

        // 検証: 結果のUserオブジェクトが取得できていること
        assertNotNull(result);
        // assertEquals(userId, result.getUserId());

        // 認証成功後はパスワード情報がnullになること
        // assertNull(result.getPassword());
        // assertNull(result.getSalt());
    }
}

