<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>経費申請システム - メニュー</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
    <div class="container">
        <h1>メニュー</h1>
        <c:if test="${not empty errorMessage}">
            <p class="error" id="errorMessage"><c:out value="${errorMessage}"/></p>
        </c:if>
        <div id="loginUserName" class="welcome-message">
            ようこそ、<c:out value="${loginUser.userName}"/>さん
        </div>
        <div class="menu-buttons">
            <c:if test="${sessionScope.loginUser.roleId == 1}">
                <a id="applyButton" href="${pageContext.request.contextPath}/expense/apply/input" class="btn btn-primary">経費申請</a><br>
                <a id="listButton" href="${pageContext.request.contextPath}/expense/list" class="btn btn-primary">申請一覧</a><br>
            </c:if>
            <c:if test="${loginUser.roleId >= 2 && loginUser.roleId != 9}">
                <a id="approvalListButton" href="${pageContext.request.contextPath}/approval/list" class="btn btn-primary">承認状況一覧</a><br>
            </c:if>
            <c:if test="${loginUser.roleId == 9}">
                <a id="userManageButton" href="${pageContext.request.contextPath}/user/list" class="btn btn-primary">ユーザー管理</a><br>
                <%-- <a id="approvalListButtonForAdmin" href="approvalList" class="btn btn-primary">承認状況一覧</a><br> --%>
            </c:if>
        </div>
    </div>
    <jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
</body>
</html>