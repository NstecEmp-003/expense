package com.fullness.keihiseisan.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fullness.keihiseisan.model.service.DepartmentService;
import com.fullness.keihiseisan.model.service.RoleService;
import com.fullness.keihiseisan.model.exception.ApplicationException;
import com.fullness.keihiseisan.model.service.UserService;
import com.fullness.keihiseisan.model.value.Department;
import com.fullness.keihiseisan.model.value.role;
import com.fullness.keihiseisan.model.value.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * ユーザー登録入力画面のコントローラークラス
 * ユーザー登録入力画面の表示を行う
 */
@WebServlet("/user/register/input")
public class UserRegisterInputServlet extends BaseServlet {
    /**
     * GETリクエストを処理する
     * @param request リクエスト
     * @param response レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // ログインチェック
            loginCheck(request, response);
            // 権限チェック
            roleCheck(request, response, role.SYSTEM_ADMIN);
            // セッション
            HttpSession session = request.getSession(false);
            // DBから部署と役職の情報を取得する
            List<Department> departmentList = new ArrayList<>();
            List<role> roleList = new ArrayList<>();
            DepartmentService departmentService = new DepartmentService();
            RoleService roleService = new RoleService();
            departmentList = departmentService.getAllDepartments();
            roleList = roleService.getAllRoles();
            // 部署と役職の情報を設定
            request.setAttribute("departmentList", departmentList);
            request.setAttribute("roleList", roleList);
            // 確認画面から「修正」で戻ってきた場合、userToRegisterを優先的にinputUserとして使用
            User userFromConfirm = (User) session.getAttribute("userToRegister");
            User inputUser = null;
            List<String> errors = null;
            // 確認画面から「修正」で戻ってきた場合、userToRegisterを優先的にinputUserとして使用
            if (userFromConfirm != null) {
                inputUser = userFromConfirm;
            } else {
                // バリデーションエラーで戻ってきた場合
                inputUser = (User) session.getAttribute("inputUser");
                @SuppressWarnings("unchecked")
                List<String> tempErrors = (List<String>) session.getAttribute("errors");
                errors = tempErrors;
            }
            // ユーザー登録入力画面表示
            if (inputUser != null) {
                request.setAttribute("inputUser", inputUser);
                session.removeAttribute("inputUser");
            }
            if (errors != null) {
                request.setAttribute("errors", errors);
                session.removeAttribute("errors");
            }
            // ユーザー登録入力画面表示
            request.getRequestDispatcher("/WEB-INF/jsp/user/register/input.jsp").forward(request, response);
        } catch (ApplicationException e) {
            handleError(request, response, e);
        } catch (Exception e) {
            handleSystemError(request, response, e);
        }
    }
    /**
     * POSTリクエストを処理する
     * @param request リクエスト
     * @param response レスポンス
     * @throws ServletException サーブレット例外
     * @throws IOException 入出力例外
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // ログインチェック
            loginCheck(request, response);
            // 権限チェック
            roleCheck(request, response, role.SYSTEM_ADMIN);
            // セッション
            HttpSession session = request.getSession(false);
            // パラメーターを取得
            request.setCharacterEncoding("UTF-8");
            String userId = request.getParameter("userIdInput");
            String password = request.getParameter("passwordInput");
            String userName = request.getParameter("userNameInput");
            String departmentIdStr = request.getParameter("departmentInput");
            String roleIdStr = request.getParameter("roleInput");
            // ユーザー情報を設定
            User inputUser = new User();
            inputUser.setUserId(userId);
            inputUser.setPassword(password);
            inputUser.setUserName(userName);
            int tempDepartmentId = 0;
            int tempRoleId = 0;
            if (departmentIdStr != null && !departmentIdStr.isEmpty()) {
                tempDepartmentId = Integer.parseInt(departmentIdStr);
            }
            if (roleIdStr != null && !roleIdStr.isEmpty()) {
                tempRoleId = Integer.parseInt(roleIdStr);
            }
            inputUser.setDepartmentId(tempDepartmentId);
            inputUser.setRoleId(tempRoleId);
            // バリデーション
            List<String> errors = new ArrayList<>();
            UserService userService = new UserService();
            errors = userService.validateUser(inputUser);
            if (!errors.isEmpty()) {
                session.setAttribute("inputUser", inputUser);
                session.setAttribute("errors", errors);
                response.sendRedirect(request.getContextPath() + "/user/register/input");
            } else {
                // DBから部署と役職の情報を取得して、部署名と役職名を設定する
                DepartmentService departmentService = new DepartmentService();
                RoleService roleService = new RoleService();
                
                if (inputUser.getDepartmentId() > 0) {
                    Department dept = departmentService.getDepartmentById(inputUser.getDepartmentId());
                    inputUser.setDepartmentName(dept.getDeptName());
                } else {
                    inputUser.setDepartmentName("不明な部門");
                }
                
                if (inputUser.getRoleId() > 0) {
                    role role = roleService.getRoleById(inputUser.getRoleId());
                    inputUser.setRoleName(role.getRoleName());
                } else {
                    inputUser.setRoleName("不明な役職");
                }
                
                session.setAttribute("userToRegister", inputUser);
                response.sendRedirect(request.getContextPath() + "/user/register/confirm");
            }
        } catch (ApplicationException e) {
            handleError(request, response, e);
        } catch (Exception e) {
            handleSystemError(request, response, e);
        }
    }
} 