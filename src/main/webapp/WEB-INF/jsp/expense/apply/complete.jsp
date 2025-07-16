<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>経費申請（完了）</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
    
    <div class="container">
        <div class="complete-content">
            <div class="complete-icon">
                <i class="fas fa-check-circle"></i>
            </div>
            
            <h1 id="successMessage">経費申請が完了しました</h1>
            
            <div class="complete-message">
                <p>申請IDは <strong id="applicationId">${applicationId}</strong> です。</p>
                <p>申請内容は「申請一覧」から確認できます。</p>
            </div>
            
            <div class="button-group">
                <a id="listButton" href="${pageContext.request.contextPath}/expense/list" class="btn btn-primary">申請一覧へ</a>
                <a id="backLink" href="${pageContext.request.contextPath}/menu" class="btn btn-secondary">メニューへ戻る</a>
            </div>
        </div>
    </div>
    
    <jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
    
    <%-- Font Awesome の読み込み --%>
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</body>
</html>