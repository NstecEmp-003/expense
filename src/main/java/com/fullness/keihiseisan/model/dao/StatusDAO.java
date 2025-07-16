package com.fullness.keihiseisan.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fullness.keihiseisan.model.value.Status;
import com.fullness.keihiseisan.model.exception.SystemException;

/**
 * ステータスDAOクラス
 * ステータス情報の取得を行う
 */
public class StatusDAO {
    // ステータスID定数
    public static final int STATUS_APPLIED = 1;           // 申請中
    public static final int STATUS_MANAGER_APPROVED = 2;  // 課長承認済
    public static final int STATUS_APPROVED_COMPLETED = 3; // 承認完了
    public static final int STATUS_MANAGER_REJECTED = 8;  // 課長却下
    public static final int STATUS_DIRECTOR_REJECTED = 9; // 部長却下
    /** データベース接続 */
    private Connection connection;
    /** ロガー */
    private static final Logger logger = Logger.getLogger(StatusDAO.class.getName());

    /**
     * コンストラクタ
     * @param connection データベース接続
     */
    public StatusDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * 全てのステータス情報を取得する
     * @return List<Status>
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public List<Status> selectAll() throws SystemException {
        List<Status> statusList = new ArrayList<>();
        String sql = "SELECT status_id, status_name FROM statuses";
        try (
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                Status status = new Status();
                status.setStatusId(rs.getInt("status_id"));
                status.setStatusName(rs.getString("status_name"));
                statusList.add(status);
            }
            return statusList;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "ステータス情報の取得中にエラーが発生しました", e);
            throw new SystemException("ステータス情報の取得に失敗しました", e);
        }
    }
}
