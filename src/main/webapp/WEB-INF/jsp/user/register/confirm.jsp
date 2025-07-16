<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ユーザー登録（確認） - 経費申請システム</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp" />

    <div class="container">
        <h1>ユーザー登録（確認）</h1>

        <table class="expense-table">
            <tr>
                <th>ユーザーID</th>
                <td><c:out value="${userToRegister.userId}"/></td>
            </tr>
            <tr>
                <th>パスワード</th>
                <td>******** (マスク表示)</td>
            </tr>
            <tr>
                <th>氏名</th>
                <td><c:out value="${userToRegister.userName}"/></td>
            </tr>
            <tr>
                <th>所属部門</th>
                <td><c:out value="${userToRegister.departmentName}"/></td>
            </tr>
            <tr>
                <th>役職</th>
                <td><c:out value="${userToRegister.roleName}"/></td>
            </tr>
        </table>

        <div class="button-group">
            <form action="${pageContext.request.contextPath}/user/register/confirm" method="post" style="display: inline;">
                <button type="submit" id="registerButton" class="btn btn-primary">登録</button>
            </form>
            
            <form action="${pageContext.request.contextPath}/user/register/input" method="get" style="display: inline;">
                <input type="hidden" name="action" value="correct"> <%-- 修正モードであることを示す --%>
                <button type="submit" id="editButton" class="btn btn-secondary">修正</button>
            </form>

            <a href="${pageContext.request.contextPath}/user/list" id="cancelButton" class="btn btn-link">キャンセル</a>
        </div>
    </div>

    <jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
</body>
</html> 