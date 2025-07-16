<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ユーザー削除（確認） - 経費申請システム</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp" />
    <div class="container">
        <h1>ユーザー削除（確認）</h1>

        <c:if test="${not empty sessionScope.errorMessage}">
            <p class="error"><c:out value="${sessionScope.errorMessage}"/></p>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>

        <c:if test="${not empty userToDelete}">
            <p>以下のユーザーを削除します。よろしいですか？</p>
            <p class="warning-message">この操作は取り消せません。関連する申請データがある場合は削除できません。</p>
            
            <table class="expense-table">
                <tr>
                    <th>ユーザーID</th>
                    <td><c:out value="${userToDelete.userId}"/></td>
                </tr>
                <tr>
                    <th>氏名</th>
                    <td><c:out value="${userToDelete.userName}"/></td>
                </tr>
                <tr>
                    <th>部署</th>
                    <td><c:out value="${userToDelete.departmentName}"/></td>
                </tr>
                <tr>
                    <th>役職</th>
                    <td><c:out value="${userToDelete.roleName}"/></td>
                </tr>
            </table>

            <div class="button-group">
                <form action="${pageContext.request.contextPath}/user/delete/confirm" method="post" style="display: inline;">
                    <input type="hidden" name="userId" value="<c:out value='${userToDelete.userId}'/>">
                    <button type="submit" id="deleteButton" class="btn btn-danger">削除</button>
                </form>
                <a href="${pageContext.request.contextPath}/user/list" id="cancelButton" class="btn btn-secondary">キャンセル</a>
            </div>
        </c:if>
        <c:if test="${empty userToDelete && empty sessionScope.errorMessage}"> <%-- 初期表示でエラーなし、対象もなしは通常ありえないが念のため --%>
            <p class="error">削除対象のユーザー情報が見つかりません。</p>
            <div class="button-group">
                <a href="${pageContext.request.contextPath}/user/list" class="btn btn-secondary">ユーザー一覧へ戻る</a>
            </div>
        </c:if>

    </div>
    <jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
</body>
</html> 