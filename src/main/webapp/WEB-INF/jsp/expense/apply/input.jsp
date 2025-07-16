<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>経費申請（入力）</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
    
    <div class="container">
        <h1>経費申請（入力）</h1>
        
        <%-- エラーメッセージ表示 --%>
        <c:if test="${not empty errorMessages}">
            <div class="error-messages" id="errorMessages">
                <ul>
                    <c:forEach items="${errorMessages}" var="error">
                        <li>${error}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/expense/apply/input" method="post" enctype="multipart/form-data">
            <%-- CSRFトークン --%>
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <table class="apply-table">
                <tr>
                    <th>
                        <%-- 申請日 --%>
                        <label for="applicationDate" id="applicationDateLabel">申請日 <span class="required">*</span></label>
                    </th>
                    <td>
                        <c:choose>
                            <c:when test="${not empty expense.applicationDate}">
                                <input type="date" id="applicationDate" name="applicationDate" value="${expense.applicationDate}">
                            </c:when>
                            <c:otherwise>
                                <input type="date" id="applicationDate" name="applicationDate" value="${today}">
                            </c:otherwise>
                        </c:choose>
                        <%-- <small class="form-text">カレンダーから選択するか、YYYY-MM-DD形式で入力してください。</small> --%>
                    </td>
                </tr>
                <tr>
                    <th>
                        <%-- 勘定科目 --%>
                        <label for="accountId" id="accountIdLabel">勘定科目 <span class="required">*</span></label>
                    </th>
                    <td>
                        <select id="accountId" name="accountId">
                            <option value="">選択してください</option>
                            <c:forEach items="${accounts}" var="account">
                                <option value="${account.accountId}" ${expense.accountId == account.accountId ? 'selected' : ''}>
                                    ${account.accountName}
                                </option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>
                <tr>
                    <th>
                        <%-- 支払日 --%>
                        <label for="paymentDate" id="paymentDateLabel">支払日 <span class="required">*</span></label>
                    </th>
                    <td>
                        <input type="date" id="paymentDate" name="paymentDate" value="${expense.paymentDate}">
                        <%-- <small class="form-text">カレンダーから選択するか、YYYY-MM-DD形式で入力してください。</small> --%>
                    </td>
                </tr>
                <tr>
                    <th>
                        <%-- 支払先 --%>
                        <label for="payee" id="payeeLabel">支払先 <span class="required">*</span></label>
                    </th>
                    <td>
                        <input type="text" id="payee" name="payee" value="${expense.payee}">
                        <small class="form-text">100文字以内で入力してください。</small>
                    </td>
                </tr>
                <tr>
                    <th>
                        <%-- 金額 --%>
                        <label for="amount" id="amountLabel">金額 <span class="required">*</span></label>
                    </th>
                    <td>
                        <input type="number" id="amount" name="amount" value="${expense.amount}">
                        <small class="form-text">1円から9,999,999円の範囲で入力してください。</small>
                    </td>
                </tr>
                <tr>
                    <th>
                        <%-- 内容（詳細） --%>
                        <label for="description" id="descriptionLabel">内容（詳細） <span class="required">*</span></label>
                    </th>
                    <td>
                        <textarea rows="5" cols="40" id="description" name="description" maxlength="500">${expense.description}</textarea>
                        <small class="form-text">500文字以内で入力してください。</small>
                    </td>
                </tr>
                <tr>
                    <th>
                        <%-- 領収書 --%>
                        <label for="receipt" id="receiptLabel">領収書</label>
                    </th>
                    <td>
                        <input type="file" id="receiptFile" name="receiptFile">
                        <small class="form-text">JPGまたはPNG形式、5MB以下のファイルを選択してください。</small>
                    </td>
                </tr>
            </table>
            <%-- ボタン類 --%>
            <div class="button-group">
                <button type="submit" id="confirmButton" class="btn btn-primary">確認</button>
                <a href="${pageContext.request.contextPath}/menu" id="cancelLink" class="btn btn-secondary">キャンセル</a>
            </div>
        </form>
    </div>
    
    <jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
</body>
</html>