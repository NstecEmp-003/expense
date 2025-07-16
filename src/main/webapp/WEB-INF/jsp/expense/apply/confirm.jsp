<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>経費申請（確認）</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
    
    <div class="container">
        <h1>経費申請（確認）</h1>
        
        <div>
            <%-- 申請内容の表示 --%>
            <table class="expense-table">
                <tr>
                    <th id="applicationDateLabel">申請日:</th>
                    <td id="applicationDate">
                        <fmt:formatDate value="${expense.applicationDate}" pattern="yyyy/MM/dd"/>
                    </td>
                </tr>
                <tr>
                    <th id="accountNameLabel">勘定科目:</th>
                    <td id="accountName">
                        ${expense.accountName}
                    </td>
                </tr>
                <tr>
                    <th id="paymentDateLabel">支払日:</th>
                    <td id="paymentDate">
                        <fmt:formatDate value="${expense.paymentDate}" pattern="yyyy/MM/dd"/>
                    </td>
                </tr>
                <tr>
                    <th id="payeeLabel">支払先:</th>
                    <td id="payee">
                        ${expense.payee}
                    </td>
                </tr>
                <tr>
                    <th id="amountLabel">金額:</th>
                    <td id="amount">
                        <fmt:formatNumber value="${expense.amount}" type="currency" currencySymbol="￥" maxFractionDigits="0"/>
                    </td>
                </tr>
                <tr>
                    <th id="descriptionLabel">内容（詳細）:</th>
                    <td id="description">
                        <pre class="description-text">${expense.description}</pre>
                    </td>
                </tr>
                <c:if test="${not empty expense.receiptPath}">
                    <tr>
                        <th id="receiptFileNameLabel">領収書:</th>
                        <td id="receiptFile">
                            <img src="${pageContext.request.contextPath}/expense/download?filename=${expense.receiptPath}&view=thumbnail" alt="領収書画像" style="max-width: 200px; max-height: 200px; border: 1px solid #ccc;">
                        </td>
                    </tr>
                </c:if>
            </table>
            
            <%-- ボタン類 --%>
            <div class="button-group">
                <form action="${pageContext.request.contextPath}/expense/apply/confirm" method="post" style="display: inline;">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <%-- hiddenによるデータ送信は不要。セッションからデータを取得する --%>
                    <button id="registerButton" type="submit" class="btn btn-primary">登録</button>
                </form>
                
                <a id="reinputButton" href="${pageContext.request.contextPath}/expense/apply/input" class="btn btn-secondary">修正</a>
                <a id="backLink" href="${pageContext.request.contextPath}/menu?clear=expense" class="btn btn-link">メニューへ戻る</a>
            </div>
        </div>
    </div>
    
    <jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
</body>
</html>