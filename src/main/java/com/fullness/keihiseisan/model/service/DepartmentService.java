package com.fullness.keihiseisan.model.service;

import com.fullness.keihiseisan.model.dao.DepartmentDAO;
import com.fullness.keihiseisan.model.exception.BusinessException;
import com.fullness.keihiseisan.model.exception.SystemException;
import com.fullness.keihiseisan.model.util.ConnectionManager;
import com.fullness.keihiseisan.model.value.Department;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 部署サービスクラス
 * 部署情報の取得を行う
 */
public class DepartmentService {
    /** ロガー */
    private static final Logger logger = Logger.getLogger(DepartmentService.class.getName());

    /**
     * すべての部署情報を取得する
     * @return 部署情報のリスト
     * @throws SystemException 取得に失敗した場合
     */
    public List<Department> getAllDepartments() throws SystemException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            DepartmentDAO departmentDAO = new DepartmentDAO(connectionManager.getConnection());
            return departmentDAO.selectAll();
        } catch (SQLException e) {
            throw new SystemException(
                "部署情報の取得に失敗しました", 
                "Failed to get department list", 
                e
            );
        }
    }

    /**
     * 部署IDに基づいて部署情報を取得する
     * @param deptId 部署ID
     * @return 部署情報
     * @throws BusinessException 部署が見つからない場合
     * @throws SystemException 取得に失敗した場合
     */
    public Department getDepartmentById(int deptId) throws BusinessException, SystemException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            DepartmentDAO departmentDAO = new DepartmentDAO(connectionManager.getConnection());
            Department department = departmentDAO.findById(deptId);
            if (department == null) {
                throw new BusinessException(
                    "指定された部署が見つかりません", 
                    "Department not found with ID: " + deptId
                );
            }
            return department;
        } catch (SQLException e) {
            throw new SystemException(
                "部署情報の取得に失敗しました", 
                "Failed to get department with ID: " + deptId, 
                e
            );
        }
    }
} 