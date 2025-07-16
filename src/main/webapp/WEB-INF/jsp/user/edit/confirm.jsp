<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>ユーザー編集（確認） - 経費申請システム</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/common/header.jsp" />
    <div class="container">
        <h1>ユーザー編集（確認）</h1>
        <p>以下の内容でユーザー情報を更新します。よろしいですか？</p>

        <c:if test="${not empty userToEditConfirm}">
            <table class="expense-table">
                <tr>
                    <th>ユーザーID</th>
                    <td><c:out value="${userToEditConfirm.userId}"/></td>
                </tr>
                <tr>
                    <th>氏名</th>
                    <td><c:out value="${userToEditConfirm.userName}"/></td>
                </tr>
                <tr>
                    <th>パスワード</th>
                    <td>
                        <c:choose>
                            <c:when test="${not empty userToEditConfirm.password}">
                                （変更あり）
                            </c:when>
                            <c:otherwise>
                                （変更なし）
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
                <tr>
                    <th>部署</th>
                    <td><c:out value="${userToEditConfirm.departmentName}"/></td>
                </tr>
                <tr>
                    <th>役職</th>
                    <td><c:out value="${userToEditConfirm.roleName}"/></td>
                </tr>
            </table>

            <div class="button-group">
                <form action="${pageContext.request.contextPath}/user/edit/confirm" method="post" style="display: inline;">
                    <button type="submit" id="updateButton" class="btn btn-primary">更新</button>
                </form>
                <form action="${pageContext.request.contextPath}/user/edit/input" method="get" style="display: inline;">
                    <input type="hidden" name="userId" value="<c:out value='${userToEditConfirm.userId}'/>">
                    <input type="hidden" name="action" value="correct">
                    <button type="submit" id="backButton" class="btn btn-secondary">修正</button>
                </form>
                <a href="${pageContext.request.contextPath}/user/list" id="cancelButton" class="btn btn-link">キャンセル</a>
            </div>
        </c:if>
        <c:if test="${empty userToEditConfirm}">
            <p class="error">編集情報が見つかりません。</p>
            <div class="button-group">
                <a href="${pageContext.request.contextPath}/user/list" class="btn btn-secondary">ユーザー一覧へ戻る</a>
            </div>
        </c:if>

    </div>
    <jsp:include page="/WEB-INF/jsp/common/footer.jsp" />
</body>
</html> 