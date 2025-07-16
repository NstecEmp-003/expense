<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>500 - サーバーエラー</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
    <div class="container">
        <h1>500 - サーバーエラー</h1>
        <p>申し訳ありませんが、サーバーでエラーが発生しました。</p>
        <a href="/">トップページに戻る</a>
    </div>
    <jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
</body>
</html>