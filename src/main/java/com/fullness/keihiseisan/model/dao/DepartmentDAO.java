package com.fullness.keihiseisan.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fullness.keihiseisan.model.value.Department;
import com.fullness.keihiseisan.model.exception.SystemException;

/**
 * 部門DAOクラス
 * 部門情報の取得を行う
 */
public class DepartmentDAO {
    private Connection connection;
    /** ロガー */
    private static final Logger logger = Logger.getLogger(DepartmentDAO.class.getName());

    /**
     * コンストラクタ
     * @param connection データベース接続
     */
    public DepartmentDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * 全ての部門情報を取得する
     * @return List<Department>
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public List<Department> selectAll() throws SystemException {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT dept_id, dept_name FROM departments ORDER BY dept_id";
        try (
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                Department dto = new Department();
                dto.setDeptId(rs.getInt("dept_id"));
                dto.setDeptName(rs.getString("dept_name")); // TODO: XSS確認用にあえてエスケープしない
                list.add(dto);
            }
            return list;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "部門情報の取得中にエラーが発生しました", e);
            throw new SystemException("部門情報の取得に失敗しました", e);
        }
    }

    /**
     * 部門IDをキーに部門情報を取得する
     * @param deptId 部門ID
     * @return Department (見つからない場合は null)
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public Department findById(int deptId) throws SystemException {
        String sql = "SELECT dept_id, dept_name FROM departments WHERE dept_id = ?";
        try (
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setInt(1, deptId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Department dto = new Department();
                    dto.setDeptId(rs.getInt("dept_id"));
                    dto.setDeptName(rs.getString("dept_name"));
                    return dto;
                }
                return null;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "部門情報の取得中にエラーが発生しました。部門ID: " + deptId, e);
            throw new SystemException("部門情報の取得に失敗しました。部門ID: " + deptId, e);
        }
    }
}