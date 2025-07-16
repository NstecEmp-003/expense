package com.fullness.keihiseisan.model.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.fullness.keihiseisan.model.exception.SystemException;

/**
 * データベース接続管理クラス
 * データベース接続の取得、コミット、ロールバック、クローズを行う
 */
public class ConnectionManager implements AutoCloseable {
    /** ドライバー名 */
    private static final String DRIVER = "org.postgresql.Driver";
    /** 接続 */
    private Connection connection;
    /** ロガー */
    private static final Logger logger = Logger.getLogger(ConnectionManager.class.getName());
    /**
     * 環境変数を取得するヘルパーメソッド（存在しない場合はデフォルト値を返す）
     * @param envName
     * @param defaultValue
     * @return
     */
    private static String getEnvOrDefault(String envName, String defaultValue) {
        String value = System.getenv(envName);
        return (value != null && !value.isEmpty()) ? value : defaultValue;
    }
    /**
     * ドライバーを読み込む
     */
    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQLドライバーの読み込みに失敗しました。", e);
        }
    }
    /**
     * データベース接続を取得する
     * @return
     * @throws SQLException
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
        	connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5433/expense", 
                "postgres", 
                "postgres");
            connection.setAutoCommit(false);
        }
        return connection;
    }
    /**
     * コミットを行う
     * @throws SQLException
     */
    public void commit() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.commit();
        }
    }
    /**
     * ロールバックを行う
     * @throws SQLException
     */
    public void rollback() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.rollback();
        }
    }
    /**
     * 接続をクローズする
     * @throws SystemException
     */
    @Override
    public void close() throws SystemException {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                throw new SystemException("データベース接続のクローズに失敗しました", e);
            }
        }
    }
}