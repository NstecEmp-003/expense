package com.fullness.keihiseisan.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fullness.keihiseisan.model.exception.SystemException;
import com.fullness.keihiseisan.model.value.User;

/**
 * ユーザーDAOクラス
 * ユーザー情報の取得を行う
 */
public class UserDAO {
    /** データベース接続 */
    private Connection connection;
    /** ロガー */
    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    /**
     * コンストラクタ
     * @param connection データベース接続
     */
    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * ユーザーIDをキーにユーザー情報を取得する (パスワードも取得してしまっている - 要修正)
     * @param userId ユーザーID
     * @return User (見つからない場合は null)
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public User findByUserId(String userId) throws SystemException {
        String sql = "SELECT u.user_id, u.password, u.salt, u.user_name, u.department_id, d.dept_name, u.role_id, r.role_name " +
                     "FROM users u " +
                     "JOIN departments d ON u.department_id = d.dept_id " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "WHERE u.user_id = ?";
        try (
            PreparedStatement stmt = connection.prepareStatement(sql)
        ) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getString("user_id"));
                    user.setPassword(rs.getString("password")); // セキュリティリスク！
                    user.setSalt(rs.getString("salt")); // saltを取得
                    user.setUserName(rs.getString("user_name"));
                    user.setDepartmentId(rs.getInt("department_id"));
                    user.setDepartmentName(rs.getString("dept_name"));
                    user.setRoleId(rs.getInt("role_id"));
                    user.setRoleName(rs.getString("role_name"));
                    return user;
                }
                return null;
            }
        } catch (SQLException e) {
        	e.printStackTrace();
            throw new SystemException("ユーザー情報の取得に失敗しました。ユーザーID: " + userId, e);
        }
    }

    /**
     * 全ユーザーの情報を取得する
     * @return List<User> 全ユーザーのリスト
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public List<User> findAll() throws SystemException {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT u.user_id, u.user_name, u.department_id, d.dept_name, u.role_id, r.role_name " +
                     "FROM users u " +
                     "JOIN departments d ON u.department_id = d.dept_id " +
                     "JOIN roles r ON u.role_id = r.role_id " +
                     "ORDER BY u.user_id";
        try (
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getString("user_id"));
                user.setUserName(rs.getString("user_name"));
                user.setDepartmentId(rs.getInt("department_id"));
                user.setDepartmentName(rs.getString("dept_name"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                // パスワードは一覧には含めない
                userList.add(user);
            }
            return userList;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "ユーザー一覧の取得中にエラーが発生しました", e);
            throw new SystemException("ユーザー一覧の取得に失敗しました", e);
        }
    }

    /**
     * 部長権限を持つユーザーが存在するかチェックする
     * @return boolean 部長権限を持つユーザーが存在する場合はtrue
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public boolean existsDirector() throws SystemException {
        String sql = "SELECT COUNT(*) FROM users WHERE role_id = 3"; // 3は部長の役職ID
        try (
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "部長権限ユーザーの存在確認中にエラーが発生しました", e);
            throw new SystemException("部長権限ユーザーの存在確認に失敗しました", e);
        }
    }

    /**
     * 部長権限を持つユーザーの一覧を取得する
     * @return List<User> 部長権限を持つユーザーの一覧
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public List<User> findDirectors() throws SystemException {
        List<User> directors = new ArrayList<>();
        String sql = "SELECT user_id, user_name, department_id, role_id FROM users WHERE role_id = 3";
        try (
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                User director = new User();
                director.setUserId(rs.getString("user_id"));
                director.setUserName(rs.getString("user_name"));
                director.setDepartmentId(rs.getInt("department_id"));
                director.setRoleId(rs.getInt("role_id"));
                directors.add(director);
            }
            return directors;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "部長権限ユーザー一覧の取得中にエラーが発生しました", e);
            throw new SystemException("部長権限ユーザー一覧の取得に失敗しました", e);
        }
    }

    /**
     * 新規ユーザーを登録する
     * @param user 登録するユーザー情報
     * @return 登録件数
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public int insert(User user) throws SystemException {
        String sql = "INSERT INTO users (user_id, password, salt, user_name, department_id, role_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getUserId());
            stmt.setString(2, user.getPassword()); // 注意: パスワードはハッシュ化して保存することが望ましい
            stmt.setString(3, user.getSalt()); // saltを保存
            stmt.setString(4, user.getUserName());
            stmt.setInt(5, user.getDepartmentId());
            stmt.setInt(6, user.getRoleId());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "ユーザー登録中にエラーが発生しました。ユーザーID: " + user.getUserId(), e);
            throw new SystemException("ユーザー登録に失敗しました。ユーザーID: " + user.getUserId(), e);
        }
    }

    /**
     * ユーザー情報を更新する
     * @param user 更新するユーザー情報
     * @return 更新件数
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public int update(User user) throws SystemException {
        // パスワードが指定されている場合のみパスワードを更新する
        String sql;
        boolean passwordUpdate = user.getPassword() != null && !user.getPassword().isEmpty();
        if (passwordUpdate) {
            // パスワードも更新する場合 (salt は更新しない)
            sql = "UPDATE users SET password = ?, user_name = ?, department_id = ?, role_id = ? WHERE user_id = ?";
        } else {
            sql = "UPDATE users SET user_name = ?, department_id = ?, role_id = ? WHERE user_id = ?";
        }
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int parameterIndex = 1;
            if (passwordUpdate) {
                stmt.setString(parameterIndex++, user.getPassword()); // 注意: パスワードはハッシュ化して保存することが望ましい
            }
            stmt.setString(parameterIndex++, user.getUserName());
            stmt.setInt(parameterIndex++, user.getDepartmentId());
            stmt.setInt(parameterIndex++, user.getRoleId());
            stmt.setString(parameterIndex, user.getUserId());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "ユーザー情報の更新中にエラーが発生しました。ユーザーID: " + user.getUserId(), e);
            throw new SystemException("ユーザー情報の更新に失敗しました。ユーザーID: " + user.getUserId(), e);
        }
    }

    /**
     * ユーザーを削除する
     * @param userId 削除するユーザーID
     * @return 削除件数
     * @throws SystemException データベース処理中にエラーが発生した場合
     */
    public int delete(String userId) throws SystemException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, userId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "ユーザーの削除中にエラーが発生しました。ユーザーID: " + userId, e);
            throw new SystemException("ユーザーの削除に失敗しました。ユーザーID: " + userId, e);
        }
    }
}