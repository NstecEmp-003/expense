<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <title>404 - ページが見つかりません</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
    <div class="container">
        <h1>404 - ページが見つかりません</h1>
        <p>申し訳ありませんが、お探しのページは見つかりませんでした。</p>
        <a href="<c:url value='/' />">トップページに戻る</a>
    </div>
    <jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
</body>
</html>