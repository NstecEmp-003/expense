package com.fullness.keihiseisan.model.service;

import com.fullness.keihiseisan.model.dao.UserDAO;
import com.fullness.keihiseisan.model.util.ConnectionManager;
import com.fullness.keihiseisan.model.exception.BusinessException;
import com.fullness.keihiseisan.model.exception.SystemException;
import com.fullness.keihiseisan.model.dao.ExpenseApplicationDAO;
import com.fullness.keihiseisan.model.util.PasswordUtil;
import com.fullness.keihiseisan.model.value.ExpenseApplication;
import com.fullness.keihiseisan.model.value.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * ユーザーサービスクラス
 * ユーザー情報の取得、更新、削除を行う
 */
public class UserService {
    /** ロガー */
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    /**
     * 全てのユーザー情報を取得します。
     * システム管理者のみ実行可能です。
     * @return ユーザーリスト
     * @throws SystemException データベースアクセスエラー
     */
    public List<User> getAllUsers() throws SystemException {
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            UserDAO userDao = new UserDAO(connectionManager.getConnection());
            return userDao.findAll();
        } catch (SQLException e) {
            throw new SystemException(
                "ユーザー一覧の取得に失敗しました",
                "Failed to get user list",
                e
            );
        }
    }

    /**
     * 指定されたIDのユーザー情報を取得します。
     * システム管理者のみ実行可能です。
     * @param userId 取得対象のユーザーID
     * @return ユーザー情報 (見つからない場合はnull)
     * @throws SystemException データベースアクセスエラー
     * @throws BusinessException ユーザーが見つからない場合
     */
    public User getUserByUserId(String userId) throws SystemException, BusinessException {
        logger.info("ユーザー情報の取得開始: ユーザーID=" + userId);
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            UserDAO userDao = new UserDAO(connectionManager.getConnection());
            User user = userDao.findByUserId(userId);
            if (user == null) {
                throw new BusinessException(
                    "指定されたユーザーが見つかりません。",
                    "User not found with ID: " + userId
                );
            }
            return user;
        } catch (SQLException e) {
            throw new SystemException(
                "ユーザー情報の取得に失敗しました",
                "Database error while retrieving user with ID: " + userId,
                e
            );
        }
    }

    /**
     * 指定されたIDのユーザー情報を取得します。
     * システム管理者のみ実行可能です。
     * @param userId 取得対象のユーザーID
     * @return ユーザー情報
     * @throws BusinessException ユーザーが見つからない場合
     * @throws SystemException データベースアクセスエラー
     */
    public User getUserById(String userId) throws BusinessException, SystemException {
        logger.info("ユーザー情報の取得開始: ユーザーID=" + userId);
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            UserDAO userDAO = new UserDAO(connectionManager.getConnection());
            User user = userDAO.findByUserId(userId);
            if (user == null) {
                throw new BusinessException(
                    "指定されたユーザーが見つかりません。",
                    "User not found with ID: " + userId
                );
            }
            return user;
        } catch (SQLException e) {
            throw new SystemException(
                "ユーザー情報の取得に失敗しました",
                "Database error while retrieving user with ID: " + userId,
                e
            );
        }
    }

    /**
     * 新規ユーザーを登録します。
     * システム管理者のみ実行可能です。
     * @param newUser 登録するユーザー情報
     * @throws BusinessException ユーザーID重複など業務エラー
     * @throws SystemException データベースアクセスエラーまたはパスワード暗号化エラー
     */
    public void registerUser(User newUser) throws BusinessException, SystemException {
        logger.info("ユーザー登録開始: ユーザーID=" + newUser.getUserId());
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            UserDAO userDao = new UserDAO(connectionManager.getConnection());
            // ユーザーID重複チェック
            if (userDao.findByUserId(newUser.getUserId()) != null) {
                throw new BusinessException(
                    "指定されたユーザーIDは既に使用されています。",
                    "User ID already exists: " + newUser.getUserId()
                );
            }
            // ソルトの生成
            String salt = PasswordUtil.generateSalt();
            newUser.setSalt(salt);
            // ユーザー登録
            userDao.insert(newUser);
            connectionManager.commit();
            logger.info("ユーザー登録完了: " + newUser.getUserId());
        } catch (SQLException e) {
            throw new SystemException(
                "ユーザー登録に失敗しました",
                "Database error while registering user with ID: " + newUser.getUserId(),
                e
            );
        }
    }

    /**
     * ユーザー情報を更新します。
     * システム管理者のみ実行可能です。
     * @param userToUpdate 更新するユーザー情報
     * @throws BusinessException 更新対象ユーザーが存在しない場合
     * @throws SystemException データベースアクセスエラーまたはパスワード暗号化エラー
     */
    public void updateUser(User userToUpdate) throws BusinessException, SystemException {
        logger.info("ユーザー更新開始: ユーザーID=" + userToUpdate.getUserId());
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            UserDAO userDAO = new UserDAO(connectionManager.getConnection());
            // 更新対象ユーザーの存在チェック
            User existingUser = userDAO.findByUserId(userToUpdate.getUserId());
            if (existingUser == null) {
                throw new BusinessException(
                    "更新対象のユーザーが見つかりません。(ID: " + userToUpdate.getUserId() + ")",
                    "User to update not found with ID: " + userToUpdate.getUserId()
                );
            }
            // パスワードが入力されている場合のみ更新
            if (userToUpdate.getPassword() != null && !userToUpdate.getPassword().isEmpty()) {
                // 既存のソルトを使用してパスワードをハッシュ化
                String hashedPassword = PasswordUtil.hashPassword(userToUpdate.getPassword(), existingUser.getSalt());
                existingUser.setPassword(hashedPassword);
            }
            // それ以外の項目を更新
            existingUser.setUserName(userToUpdate.getUserName());
            existingUser.setDepartmentId(userToUpdate.getDepartmentId());
            existingUser.setRoleId(userToUpdate.getRoleId());
            // ユーザー更新
            int affectedRows = userDAO.update(existingUser);
            if (affectedRows == 0) {
                throw new BusinessException(
                    "ユーザー情報の更新に失敗しました。更新対象が見つかりません",
                    "Failed to update user with ID: " + userToUpdate.getUserId()
                );
            }
            // ユーザー更新
            connectionManager.commit();
            logger.info("ユーザー更新完了: " + userToUpdate.getUserId());
        } catch (SQLException e) {
            throw new SystemException(
                "ユーザー情報の更新に失敗しました",
                "Database error while updating user with ID: " + userToUpdate.getUserId(),
                e
            );
        }
    }

    /**
     * ユーザーを削除します。
     * システム管理者のみ実行可能です。
     * @param userId 削除対象のユーザーID
     * @throws BusinessException ユーザーが見つからない場合、または関連する申請データが存在する場合
     * @throws SystemException データベースアクセスエラー
     */
    public void deleteUser(String userId) throws BusinessException, SystemException {
        logger.info("ユーザー削除開始: ユーザーID=" + userId);
        try (ConnectionManager connectionManager = new ConnectionManager()) {
            UserDAO userDao = new UserDAO(connectionManager.getConnection());
            ExpenseApplicationDAO expenseDao = new ExpenseApplicationDAO(connectionManager.getConnection());
            User userToDelete = userDao.findByUserId(userId);
            if (userToDelete == null) {
                throw new BusinessException(
                    "削除対象のユーザーが見つかりません。",
                    "User to delete not found with ID: " + userId
                );
            }
            List<ExpenseApplication> relatedApplications = expenseDao.findByApplicantId(userId);
            if (relatedApplications != null && !relatedApplications.isEmpty()) {
                throw new BusinessException(
                    "このユーザーには関連する申請データが存在するため削除できません。",
                    "Cannot delete user with ID: " + userId + " due to existing expense applications"
                );
            }
            userDao.delete(userId);
            connectionManager.commit();
            logger.info("ユーザー削除完了: " + userId);
        } catch (SQLException e) {
            throw new SystemException(
                "ユーザーの削除に失敗しました",
                "Database error while deleting user with ID: " + userId,
                e
            );
        }
    }

    /**
     * ユーザー登録用のバリデーション
     * @param user バリデーション対象のユーザー情報
     * @return エラーメッセージのリスト（エラーがなければ空リスト）
     */
    public List<String> validateUser(User user) {
        List<String> errors = new ArrayList<>();
        if (user.getUserId() == null || user.getUserId().trim().isEmpty()) {
            errors.add("ユーザーIDを入力してください。");
        } else if (!user.getUserId().matches("^[a-zA-Z0-9]+$")) {
            errors.add("ユーザーIDは半角英数字で入力してください。");
        } else if (user.getUserId().length() < 4 || user.getUserId().length() > 50) {
            errors.add("ユーザーIDは4文字以上50文字以内で入力してください。");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            errors.add("パスワードを入力してください。");
        } else if (!user.getPassword().matches("^[a-zA-Z0-9]+$")) {
            errors.add("パスワードは半角英数字で入力してください。");
        } else if (user.getPassword().length() < 4 || user.getPassword().length() > 100) {
            errors.add("パスワードは4文字以上100文字以内で入力してください。");
        }
        if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
            errors.add("氏名を入力してください。");
        } else if (user.getUserName().length() > 100) {
            errors.add("氏名は100文字以内で入力してください。");
        }
        if (user.getDepartmentId() == 0) {
            errors.add("所属部門を選択してください。");
        }
        if (user.getRoleId() == 0) {
            errors.add("役職を選択してください。");
        }
        return errors;
    }
    /**
     * ユーザー編集用バリデーション
     * @param user バリデーション対象のユーザー情報
     * @return エラーメッセージのリスト（エラーがなければ空リスト）
     */
    public List<String> validateUserForEdit(User user) {
        List<String> errors = new ArrayList<>();
        // 氏名
        if (user.getUserName() == null || user.getUserName().trim().isEmpty()) {
        errors.add("氏名は必須です。");
        } else if (user.getUserName().length() > 50) {
        errors.add("氏名は50文字以内で入力してください。");
        }
        user.setUserName(user.getUserName());
        // パスワード（入力された場合のみチェック）
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        if (!user.getPassword().matches("^[a-zA-Z0-9]+$")) {
            errors.add("パスワードは半角英数字で入力してください。");
        } else if (user.getPassword().length() < 4 || user.getPassword().length() > 100) {
            errors.add("パスワードは4文字以上100文字以内で入力してください。");
        }
        user.setPassword(user.getPassword()); // バリデーションOKならセット
        } else {
        user.setPassword(null); // パスワード変更なし
        }
        return errors;
    }
} 