package com.fullness.keihiseisan.model.service;

import com.fullness.keihiseisan.model.dao.RoleDAO;
import com.fullness.keihiseisan.model.exception.BusinessException;
import com.fullness.keihiseisan.model.exception.SystemException;
import com.fullness.keihiseisan.model.util.ConnectionManager;
import com.fullness.keihiseisan.model.value.role;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 役職サービスクラス
 * 役職情報の取得を行う
 */
public class RoleService {
    /** ロガー */
    private static final Logger logger = Logger.getLogger(RoleService.class.getName());
    /**
     * すべての役職情報を取得する
     * @return 役職情報のリスト
     * @throws SystemException 取得に失敗した場合
     */
    public List<role> getAllRoles() throws SystemException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            RoleDAO roleDAO = new RoleDAO(connectionManager.getConnection());
            return roleDAO.selectAll();
        } catch (SQLException e) {
            throw new SystemException(
                "役職情報の取得に失敗しました", 
                "Failed to get role list", 
                e
            );
        }
    }
    /**
     * 役職IDに基づいて役職情報を取得する
     * @param roleId 役職ID
     * @return 役職情報
     * @throws BusinessException 役職が見つからない場合
     * @throws SystemException 取得に失敗した場合
     */
    public role getRoleById(int roleId) throws BusinessException, SystemException {
        logger.info("役職情報の取得開始: 役職ID=" + roleId);
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            RoleDAO roleDAO = new RoleDAO(connectionManager.getConnection());
            role role = roleDAO.findById(roleId);
            if (role == null) {
                throw new BusinessException(
                    "指定された役職が見つかりません", 
                    "Role not found with ID: " + roleId
                );
            }
            return role;
        } catch (SQLException e) {
            throw new SystemException(
                "役職情報の取得に失敗しました", 
                "Failed to get role with ID: " + roleId, 
                e
            );
        }
    }
} 