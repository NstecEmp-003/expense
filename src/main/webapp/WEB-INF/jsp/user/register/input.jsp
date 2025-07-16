<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ユーザー登録（入力） - 経費申請システム</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp" />

    <div class="container">
        <h1>ユーザー登録（入力）</h1>

        <c:if test="${not empty errors}">
            <div class="error-messages">
                <ul>
                    <c:forEach var="error" items="${errors}">
                        <li><c:out value="${error}"/></li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/user/register/input" method="post">
            <table class="apply-table">
                <tr>
                    <th><label for="userIdInput">ユーザーID <span class="required">*</span></label></th>
                    <td><input type="text" id="userIdInput" name="userIdInput" value="<c:out value='${inputUser.userId}'/>" maxlength="50"></td>
                </tr>
                <tr>
                    <th><label for="passwordInput">パスワード <span class="required">*</span></label></th>
                    <td><input type="password" id="passwordInput" name="passwordInput" value="" maxlength="100"></td>
                </tr>
                <tr>
                    <th><label for="userNameInput">氏名 <span class="required">*</span></label></th>
                    <td><input type="text" id="userNameInput" name="userNameInput" value="<c:out value='${inputUser.userName}'/>" maxlength="100"></td>
                </tr>
                <tr>
                    <th><label for="departmentInput">所属部門 <span class="required">*</span></label></th>
                    <td>
                        <select id="departmentInput" name="departmentInput">
                            <option value="">選択してください</option>
                            <c:forEach var="dept" items="${departmentList}">
                                <option value="${dept.deptId}" ${inputUser.departmentId == dept.deptId ? 'selected' : ''}><c:out value="${dept.deptName}"/></option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th><label for="roleInput">役職 <span class="required">*</span></label></th>
                    <td>
                        <select id="roleInput" name="roleInput">
                            <option value="">選択してください</option>
                            <c:forEach var="role" items="${roleList}">
                                <option value="${role.roleId}" ${inputUser.roleId == role.roleId ? 'selected' : ''}><c:out value="${role.roleName}"/></option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
            </table>
            <div class="button-group">
                <button type="submit" id="confirmButton" class="btn btn-primary">確認</button>
                <a href="${pageContext.request.contextPath}/user/list" id="cancelButton" class="btn btn-secondary">キャンセル</a>
            </div>
        </form>
    </div>

    <jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
</body>
</html> 