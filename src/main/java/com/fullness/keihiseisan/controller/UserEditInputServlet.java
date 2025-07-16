package com.fullness.keihiseisan.controller;

import com.fullness.keihiseisan.model.service.UserService;
import com.fullness.keihiseisan.model.value.Department;
import com.fullness.keihiseisan.model.value.role;
import com.fullness.keihiseisan.model.value.User;
import com.fullness.keihiseisan.model.service.DepartmentService;
import com.fullness.keihiseisan.model.service.RoleService;
import com.fullness.keihiseisan.model.exception.ApplicationException;
import com.fullness.keihiseisan.model.exception.BusinessException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * ユーザー編集入力画面表示
 */
@WebServlet("/user/edit/input")
public class UserEditInputServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    /*
     * ユーザー編集入力画面表示
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // ログインチェック
            loginCheck(request, response);
            // 権限チェック
            roleCheck(request, response, role.SYSTEM_ADMIN); 
            // 編集対象のユーザー情報
            User userToEdit = null; 
            // エラー情報
            List<String> errors = new ArrayList<>(); 
            // セッション
            HttpSession session = request.getSession(false); 
            // バリデーションエラーで戻ってきた場合のユーザー情報
            User sessionInputUser = (User) session.getAttribute("userToEditInput");
            // 確認画面から戻ってきた場合のユーザー情報
            User sessionConfirmUser = (User) session.getAttribute("userToEditConfirm");
            // バリデーションエラーで戻ってきた場合
            if (sessionInputUser != null) {
                userToEdit = sessionInputUser;
                session.removeAttribute("userToEditInput"); // 一度使ったら消す
                List<String> sessionErrors = (List<String>) session.getAttribute("errors");
                if (sessionErrors != null) {
                    errors.addAll(sessionErrors);
                    session.removeAttribute("errors");
                }
            // 確認画面から戻ってきた場合
            } else if (sessionConfirmUser != null && request.getParameter("userId") != null && request.getParameter("userId").equals(sessionConfirmUser.getUserId())) {
                userToEdit = sessionConfirmUser;
            // 一覧画面から編集対象のユーザーIDを指定して編集画面に遷移した場合
            } else {
                // パラメーターを取得
                String userId = request.getParameter("userId");
                if (userId == null || userId.isEmpty()) {
                    throw new BusinessException("編集対象のユーザーIDが指定されていません。");
                }
                // ユーザー情報を取得
                UserService userService = new UserService();
                userToEdit = userService.getUserById(userId);
                if (userToEdit == null) {
                    throw new BusinessException("指定されたユーザーが見つかりません。");
                }
               
            }
            // パスワードは表示しない
            if (userToEdit != null) {
                userToEdit.setPassword(""); 
            }
            // DBから部署と役職の情報を取得する
            List<Department> departmentList = new ArrayList<>();
            List<role> roleList = new ArrayList<>();
            DepartmentService departmentService = new DepartmentService();
            RoleService roleService = new RoleService();
            departmentList = departmentService.getAllDepartments();
            roleList = roleService.getAllRoles();
            request.setAttribute("userToEdit", userToEdit);
            request.setAttribute("departments", departmentList);
            request.setAttribute("roles", roleList);
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/jsp/user/edit/input.jsp").forward(request, response);
        } catch (ApplicationException e) {
            handleError(request, response, e);
            return;
        } catch (Exception e) {
            handleSystemError(request, response, e);
            return;
        }
    }

    /*
     * ユーザー編集入力画面表示
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // ログインチェック
            loginCheck(request, response);
            // 権限チェック
            roleCheck(request, response, role.SYSTEM_ADMIN); 
            // パラメーターを取得
            String userId = request.getParameter("userId");
            String userName = request.getParameter("userName");
            String password = request.getParameter("password"); // 変更する場合のみ入力
            String confirmPassword = request.getParameter("confirmPassword");
            int departmentId = Integer.parseInt(request.getParameter("departmentId"));
            int roleId = Integer.parseInt(request.getParameter("roleId"));
            // ユーザー情報を取得
            User userToEdit = new User();
            userToEdit.setUserId(userId);
            userToEdit.setUserName(userName);
            userToEdit.setPassword(password);
            userToEdit.setDepartmentId(departmentId);
            userToEdit.setRoleId(roleId);
            // 部署名と役職名を取得
            DepartmentService departmentService = new DepartmentService();
            userToEdit.setDepartmentName(departmentService.getDepartmentById(departmentId).getDeptName());
            RoleService roleService = new RoleService();
            userToEdit.setRoleName(roleService.getRoleById(roleId).getRoleName());
            // バリデーション
            UserService userService = new UserService();
            List<String> errors = userService.validateUser(userToEdit);
            // 確認用パスワードと入力パスワードが一致しない場合はエラー
            if (!password.equals(confirmPassword)) {
                errors.add("確認用パスワードと入力パスワードが一致しません。");
            }
            if (!errors.isEmpty()) {
                request.setAttribute("errors", errors);
                request.getRequestDispatcher("/WEB-INF/jsp/user/edit/input.jsp").forward(request, response);
                return;
            }
            // ユーザー情報をセッションに保存
            request.getSession().setAttribute("userToEditConfirm", userToEdit);
            // 確認画面に遷移
            response.sendRedirect(request.getContextPath() + "/user/edit/confirm");
        } catch (ApplicationException e) {
            handleError(request, response, e);
            return;
        } catch (Exception e) {
            handleSystemError(request, response, e);
            return;
        }
    }
} 