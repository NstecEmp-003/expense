<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>経費申請システム - ログイン</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
    <div class="container">
        <h1>ログイン</h1>
        <c:if test="${not empty errorMessage}">
            <p class="error" id="errorMessage">${errorMessage}</p>
        </c:if>
        <div class="login-form">
            <form action="login" method="post">
                <input type="hidden" name="csrfToken" value="${csrfToken}">
                <div>
                    <label for="userId" id="userIdLabel">ユーザーID:</label>
                <input type="text" id="userId" name="userId" value="${param.userId}" required>
            </div>
            <div>
                <label for="password" id="passwordLabel">パスワード:</label>
                <input type="password" id="password" name="password" required>
                </div>
                <div class="button-group">
                    <button id="loginButton" type="submit" class="btn btn-primary">ログイン</button>
                </div>
            </form>
        </div>
    </div>
    <jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
</body>
</html>