<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> <%-- 日付フォーマット等で必要なら --%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ユーザー一覧 - 経費申請システム</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp" />

    <div class="container">
        <h1>ユーザー一覧</h1>

        <c:if test="${not empty message}">
            <p class="message"><c:out value="${message}"/></p>
            <c:remove var="message" scope="session"/>
        </c:if>
        <c:if test="${not empty errorMessage}">
            <p class="error"><c:out value="${errorMessage}"/></p>
            <c:remove var="errorMessage" scope="request"/>
        </c:if>

        <p>
            <a href="${pageContext.request.contextPath}/user/register/input" id="newUserButton" class="btn btn-primary">新規ユーザー登録</a>
        </p>

        <c:if test="${not empty userList}">
            <table class="expense-table">
                <thead>
                    <tr>
                        <th>ユーザーID</th>
                        <th>氏名</th>
                        <th>部署</th>
                        <th>役職</th>
                        <th>操作</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${userList}">
                        <tr>
                            <td><c:out value="${user.userId}"/></td>
                            <td><c:out value="${user.userName}"/></td>
                            <td><c:out value="${user.departmentName}"/></td>
                            <td><c:out value="${user.roleName}"/></td>
                            <td>
                                <a href="${pageContext.request.contextPath}/user/edit/input?userId=${user.userId}" id="editUserButton-${user.userId}" class="btn btn-sm btn-secondary">編集</a>
                                <a href="#" id="deleteUserButton-${user.userId}" class="btn btn-sm btn-danger" onclick="confirmDelete('${user.userId}', '${user.userName}'); return false;">削除</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
        <c:if test="${empty userList}">
            <p>登録されているユーザーはいません。</p>
        </c:if>
    </div>

    <script>
        function confirmDelete(userId, userName) {
            if (confirm("ユーザー「" + userName + " (ID: " + userId + ")」を削除してもよろしいですか？")) {
                window.location.href = "${pageContext.request.contextPath}/user/delete/confirm?userId=" + userId;
            }
        }
    </script>

    <jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
</body>
</html> 