<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>経費申請一覧</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
    
    <div class="container">
        <h1>経費申請一覧</h1>
        
        <%-- 絞り込みフォーム --%>
        <div class="filter-form">
            <h2>絞り込み条件</h2>
            <form action="${pageContext.request.contextPath}/expense/list" method="post" id="filterForm">
                <div class="filter-row">
                    <div class="filter-item">
                        <label for="startDate">申請日（開始）</label>
                        <input type="date" id="startDate" name="startDate" value="${startDateValue}">
                    </div>
                    <div class="filter-item">
                        <label for="endDate">申請日（終了）</label>
                        <input type="date" id="endDate" name="endDate" value="${endDateValue}">
                    </div>
                </div>
                
                <div class="filter-row">
                    <div class="filter-item">
                        <label for="accountId">勘定科目</label>
                        <select id="accountId" name="accountId">
                            <option value="">選択してください</option>
                            <c:forEach items="${accountList}" var="account">
                                <option value="${account.accountId}" ${account.accountId == accountIdValue ? 'selected' : ''}>
                                    ${account.accountName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="filter-item">
                        <label for="statusId">ステータス</label>
                        <select id="statusId" name="statusId">
                            <option value="">選択してください</option>
                            <c:forEach items="${statusList}" var="status">
                                <option value="${status.statusId}" ${status.statusId == statusIdValue ? 'selected' : ''}>
                                    ${status.statusName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
                
                <div class="filter-row">
                    <div class="filter-item">
                        <label for="minAmount">金額（最小）</label>
                        <input type="number" id="minAmount" name="minAmount" value="${minAmountValue}" min="0">
                    </div>
                    <div class="filter-item">
                        <label for="maxAmount">金額（最大）</label>
                        <input type="number" id="maxAmount" name="maxAmount" value="${maxAmountValue}" min="0">
                    </div>
                </div>
                
                <div class="filter-row">
                    <div class="filter-item">
                        <label for="payee">支払先</label>
                        <input type="text" id="payee" name="payee" value="${payeeValue}" placeholder="部分一致で検索">
                    </div>
                </div>
                
                <div class="filter-buttons">
                    <button type="button" class="btn btn-outline" id="resetButton">リセット</button>
                    <button type="submit" class="btn btn-primary">絞り込み</button>
                </div>
            </form>
        </div>
        
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
                                        ${app.applicationDate}
                                    </td>
                                    <td>${app.accountName}</td>
                                    <td>${app.payee}</td>
                                    <td class="amount">
                                        ${app.amount}
                                    </td>
                                    <td class="status">
                                        <span class="status-badge status-${app.statusId}">
                                            ${app.statusName}
                                        </span>
                                    </td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/expense/detail?id=${app.applicationId}"
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
    
    <script>
        // リセットボタンのイベントハンドラ
        document.getElementById('resetButton').addEventListener('click', function() {
            // フォームの各入力欄をクリア
            document.getElementById('startDate').value = '';
            document.getElementById('endDate').value = '';
            document.getElementById('accountId').value = '';
            document.getElementById('statusId').value = '';
            document.getElementById('minAmount').value = '';
            document.getElementById('maxAmount').value = '';
            document.getElementById('payee').value = '';
            
            // フォームを送信
            document.getElementById('filterForm').submit();
        });
    </script>
    
    <jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
</body>
</html>