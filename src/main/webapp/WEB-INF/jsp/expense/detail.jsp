<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>経費申請 - 詳細</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp"/>
    <div class="container">
        <h1>経費申請詳細</h1>

        <%-- エラーメッセージ表示 --%>
        <c:if test="${not empty errorMessage}">
            <p class="error" id="errorMessage"><c:out value="${errorMessage}"/></p>
        </c:if>    
        
        <div>
            <table class="expense-table">
                <tr>
                    <th id="applicationIdLabel">申請ID:</th>
                    <td id="applicationId">${expense.applicationId}</td>
                </tr>

                <tr>
                    <th id="applicantStatusLabel">申請ステータス:</th>
                    <td id="applicantStatus">
                        <span class="status-badge status-${expense.statusId}" id="statusName">
                            ${expense.statusName}
                        </span>
                    </td>
                </tr>
                
                <tr>
                    <th id="applicantNameLabel">申請者:</th>
                    <td id="applicantName">${expense.applicantName}</td>
                </tr>
                
                <tr>
                    <th id="applicationDateLabel">申請日:</th>
                    <td id="applicationDate">
                        <fmt:formatDate value="${expense.applicationDate}" pattern="yyyy/MM/dd"/>
                    </td>
                </tr>
                
                <tr>
                    <th id="accountNameLabel">勘定科目:</th>
                    <td id="accountName">${expense.accountName}</td>
                </tr>
                
                <tr>
                    <th id="paymentDateLabel">支払日:</th>
                    <td id="paymentDate">
                        <fmt:formatDate value="${expense.paymentDate}" pattern="yyyy/MM/dd"/>
                    </td>
                </tr>
                
                <tr>
                    <th id="payeeLabel">支払先:</th>
                    <td id="payee">${expense.payee}</td>
                </tr>
                
                <tr>
                    <th id="amountLabel">金額:</th>
                    <td class="amount" id="amount">
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
                        <th id="receiptLinkLabel">領収書:</th>
                        <td id="receiptLinkArea">
                            <div class="receipt-thumbnail">
                                <img src="${pageContext.request.contextPath}/expense/download?filename=${expense.receiptPath}&view=thumbnail" 
                                     alt="領収書画像" 
                                     style="max-width: 200px; max-height: 200px; margin-bottom: 10px; border: 1px solid #ddd; padding: 3px;" />
                            </div><br>
                            <a id="receiptLink" href="${pageContext.request.contextPath}/expense/download?id=${expense.applicationId}"
                               class="btn btn-sm btn-outline" 
                               target="_blank">
                                <i class="fas fa-download"></i> ダウンロード
                            </a>
                        </td>
                    </tr>
                </c:if>
                
                <c:if test="${expense.statusId == 4 || expense.statusId == 5}">
                    <tr>
                        <th id="rejectionReasonLabel">却下理由:</th>
                        <td class="rejection-reason" id="rejectionReasonDisplay">
                            ${expense.rejectionReason}
                        </td>
                    </tr>
                </c:if>
                
                <tr>
                    <th id="approvalHistoryLabel">承認履歴:</th>
                    <td id="approvalHistory">
                        <c:if test="${not empty expense.approver1UserId}">
                            <div class="approval-history">
                                <span class="approver" id="approver1Name">課長: ${expense.approver1Name}</span>
                                <span class="approval-date" id="approval1Date">
                                    <fmt:formatDate value="${expense.approval1Date}" pattern="yyyy/MM/dd HH:mm"/>
                                </span>
                            </div>
                        </c:if>
                        <c:if test="${not empty expense.approver2UserId}">
                            <div class="approval-history">
                                <span class="approver" id="approver2Name">部長: ${expense.approver2Name}</span>
                                <span class="approval-date" id="approval2Date">
                                    <fmt:formatDate value="${expense.approval2Date}" pattern="yyyy/MM/dd HH:mm"/>
                                </span>
                            </div>
                        </c:if>
                    </td>
                </tr>
            </table>
        </div>
        
        <div class="button-group">
            <a id="backLink" href="${pageContext.request.contextPath}/expense/list"
               class="btn btn-secondary">一覧に戻る</a>
        </div>
    </div>
    
    <jsp:include page="/WEB-INF/jsp/common/footer.jsp"/>
    
    <%-- Font Awesome の読み込み --%>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</body>
</html>