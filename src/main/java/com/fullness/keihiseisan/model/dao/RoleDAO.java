package com.fullness.keihiseisan.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fullness.keihiseisan.model.value.role;
import com.fullness.keihiseisan.model.exception.SystemException;

/**
 * 役職DAOクラス
 * 役職情報の取得を行う
 */
public class RoleDAO {
    /** データベース接続 */
    private Connection connection;
    /** ロガー */
    private static final Logger logger = Logger.getLogger(RoleDAO.class.getName());

    /**
     * コンストラクタ
     * @param connection データベース接続
     */
    public RoleDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * 全ての役職情報を取得する
     * @return List<Role>
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public List<role> selectAll() throws SystemException {
        List<role> list = new ArrayList<>();
        String sql = "SELECT role_id, role_name FROM roles ORDER BY role_id";
        try (
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                role dto = new role();
                dto.setRoleId(rs.getInt("role_id"));
                dto.setRoleName(rs.getString("role_name"));
                list.add(dto);
            }
            return list;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "役職情報の取得中にエラーが発生しました", e);
            throw new SystemException("役職情報の取得に失敗しました", e);
        }
    }

    /**
     * 役職IDをキーに役職情報を取得する
     * @param roleId 役職ID
     * @return Role (見つからない場合は null)
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public role findById(int roleId) throws SystemException {
        String sql = "SELECT role_id, role_name FROM roles WHERE role_id = ?";
        try (
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, roleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    role dto = new role();
                    dto.setRoleId(rs.getInt("role_id"));
                    dto.setRoleName(rs.getString("role_name"));
                    return dto;
                }
                return null;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "役職情報の取得中にエラーが発生しました。役職ID: " + roleId, e);
            throw new SystemException("役職情報の取得に失敗しました。役職ID: " + roleId, e);
        }
    }
} 