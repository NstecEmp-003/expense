<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<header>
    <nav>
        <div class="nav-left">
            <a href="${pageContext.request.contextPath}/"><b>経費申請システム</b></a>
            <c:if test="${not empty loginUser}">
                <a href="${pageContext.request.contextPath}/menu">メニュー</a>
                <c:if test="${sessionScope.loginUser.roleId == 1}">
                    <a href="${pageContext.request.contextPath}/expense/list">申請一覧</a>
                    <a href="${pageContext.request.contextPath}/expense/apply/input">新規申請</a>
                </c:if>
                <c:if test="${sessionScope.loginUser.roleId >= 2 && sessionScope.loginUser.roleId != 9}">
                    <a href="${pageContext.request.contextPath}/approval/list">承認一覧</a>
                </c:if>
                <c:if test="${sessionScope.loginUser.roleId == 9}">
                    <a href="${pageContext.request.contextPath}/user/list">ユーザー管理</a>
                </c:if>
            </c:if>
        </div>
        <div class="nav-right">
            <c:if test="${not empty loginUser}">
                <span class="user-info">${loginUser.userName}（${loginUser.userId}）さん</span>
                <a href="${pageContext.request.contextPath}/logout">ログアウト</a>
            </c:if>
        </div>
    </nav>
</header>