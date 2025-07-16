<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ユーザー編集 - 経費申請システム</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp" />
    <div class="container">
        <h1>ユーザー編集</h1>

        <c:if test="${not empty errors}">
            <div class="error-messages">
                <ul>
                    <c:forEach var="error" items="${errors}">
                        <li><c:out value="${error}"/></li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        <c:if test="${not empty sessionScope.errorMessage}">
            <p class="error"><c:out value="${sessionScope.errorMessage}"/></p>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>


        <form action="${pageContext.request.contextPath}/user/edit/input" method="post">
            <input type="hidden" name="userId" value="<c:out value='${userToEdit.userId}'/>">
            <table class="apply-table">
                <tr>
                    <th>ユーザーID</th>
                    <td><c:out value="${userToEdit.userId}"/></td>
                </tr>
                <tr>
                    <th><label for="userName">氏名 <span class="required">*</span></label></th>
                    <td><input type="text" id="userName" name="userName" value="<c:out value='${userToEdit.userName}'/>" maxlength="50"></td>
                </tr>
                <tr>
                    <th><label for="password">パスワード</label></th>
                    <td>
                        <input type="password" id="password" name="password" value="" autocomplete="new-password">
                        <small class="form-text">変更する場合のみ入力してください。半角英数字4～20文字。</small>
                    </td>
                </tr>
                <tr>
                    <th><label for="confirmPassword">パスワード（確認）</label></th>
                    <td><input type="password" id="confirmPassword" name="confirmPassword" value=""></td>
                </tr>
                <tr>
                    <th><label for="departmentId">部署 <span class="required">*</span></label></th>
                    <td>
                        <select id="departmentId" name="departmentId">
                            <option value="">選択してください</option>
                            <c:forEach var="dept" items="${departments}">
                                <option value="${dept.deptId}" ${userToEdit.departmentId == dept.deptId ? 'selected' : ''}>
                                    <c:out value="${dept.deptName}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th><label for="roleId">役職 <span class="required">*</span></label></th>
                    <td>
                        <select id="roleId" name="roleId">
                            <option value="">選択してください</option>
                            <c:forEach var="roleItem" items="${roles}">
                                <option value="${roleItem.roleId}" ${userToEdit.roleId == roleItem.roleId ? 'selected' : ''}>
                                    <c:out value="${roleItem.roleName}"/>
                                </option>
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