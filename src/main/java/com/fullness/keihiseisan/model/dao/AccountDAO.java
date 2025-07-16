package com.fullness.keihiseisan.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fullness.keihiseisan.model.value.Account;
import com.fullness.keihiseisan.model.exception.SystemException;

/**
 * 勘定科目DAOクラス
 * 勘定科目情報の取得を行う
 */
public class AccountDAO {
    /** データベース接続 */
    private Connection connection;
    /** ロガー */
    private static final Logger logger = Logger.getLogger(AccountDAO.class.getName());
    /**
     * コンストラクタ
     * @param connection データベース接続
     */
    public AccountDAO(Connection connection) {
        this.connection = connection;
    }
    /**
     * 全ての勘定科目情報を取得する
     * @return List<Account>
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public List<Account> selectAll() throws SystemException {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT account_id, account_name FROM accounts ORDER BY account_id";
        try (
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                Account dto = new Account();
                dto.setAccountId(rs.getInt("account_id"));
                dto.setAccountName(rs.getString("account_name"));
                list.add(dto);
            }
            return list;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "勘定科目情報の取得中にエラーが発生しました", e);
            throw new SystemException("勘定科目情報の取得に失敗しました", e);
        }
    }
}