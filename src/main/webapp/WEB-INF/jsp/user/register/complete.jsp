<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ユーザー登録完了 - 経費申請システム</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp" />
    <div class="container">
        <h1>ユーザー登録（完了）</h1>

        <c:if test="${not empty message}">
            <p class="message"><c:out value="${message}"/></p>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error"><c:out value="${errorMessage}"/></p>
        </c:if>

        <c:if test="${not empty registeredUser}">
            <p>以下のユーザー情報を登録しました。</p>
            <table class="expense-table">
                <tr>
                    <th>ユーザーID</th>
                    <td><c:out value="${registeredUser.userId}"/></td>
                </tr>
                <tr>
                    <th>氏名</th>
                    <td><c:out value="${registeredUser.userName}"/></td>
                </tr>
                <%-- 必要に応じて他の情報も表示 --%>
            </table>
        </c:if>

        <div class="button-group">
            <a href="${pageContext.request.contextPath}/user/list" id="backToListButton" class="btn btn-secondary">ユーザー一覧へ戻る</a>
            <a href="${pageContext.request.contextPath}/menu" id="backToMenuButton" class="btn btn-secondary">メニューへ戻る</a>
        </div>
    </div>
    <jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
</body>
</html> 