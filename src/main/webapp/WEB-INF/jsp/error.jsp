<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>経費申請システム - エラー</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
    <div class="container">
        <h1 id="errorTitle">エラーが発生しました</h1>
        <div class="error-message" id="errorMessage">
            <c:choose>
                <c:when test="${not empty errorMessage}">
                    <c:out value="${errorMessage}"/>
                </c:when>
                <c:otherwise>
                    予期せぬエラーが発生しました。システム管理者にお問い合わせください。
                </c:otherwise>
            </c:choose>
        </div>
        <a id="backLink" href="${pageContext.request.contextPath}/menu" class="back-link">メニューへ戻る</a>
    </div>
    <jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
</body>
</html>