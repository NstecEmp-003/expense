<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>承認状況一覧</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
    
    <div class="container">
        <h1>承認状況一覧</h1>
        
        <%-- エラーメッセージ表示 --%>
        <c:if test="${not empty errorMessage}">
            <p class="error" id="errorMessage"><c:out value="${errorMessage}"/></p>
        </c:if>

        <div class="list-content">
            <c:choose>
                <c:when test="${empty applicationList}">
                    <div class="no-data" id="noDataMessage">
                        <p>申請データがありません。</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <table class="expense-table">
                        <thead>
                            <tr>
                                <th id="applicationIdHeader">申請ID</th>
                                <th id="applicationDateHeader">申請日</th>
                                <th id="applicantNameHeader">申請者</th>
                                <th id="accountNameHeader">勘定科目</th>
                                <th id="payeeHeader">支払先</th>
                                <th id="amountHeader">金額</th>
                                <th id="statusHeader">ステータス</th>
                                <th id="actionsHeader">操作</th>
                            </tr>
                        </thead>
                        <tbody id="applicationList">
                            <c:forEach items="${applicationList}" var="app">
                                <tr>
                                    <td>${app.applicationId}</td>
                                    <td>
                                        <fmt:formatDate value="${app.applicationDate}" pattern="yyyy/MM/dd"/>
                                    </td>
                                    <td>${app.applicantName}</td>
                                    <td>${app.accountName}</td>
                                    <td>${app.payee}</td>
                                    <td class="amount">
                                        <fmt:formatNumber value="${app.amount}" type="currency" currencySymbol="￥" maxFractionDigits="0"/>
                                    </td>
                                    <td class="status">
                                        <span class="status-badge status-${app.statusId}">
                                            ${app.statusName}
                                        </span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/approval/approve?applicationId=${app.applicationId}"
                                           class="btn btn-sm btn-outline detailLink">詳細</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    
    <jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
</body>
</html>