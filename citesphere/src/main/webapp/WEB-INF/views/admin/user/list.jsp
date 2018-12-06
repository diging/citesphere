<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<h2>Users</h2>
<ul class="list-group">
<c:forEach items="${users}" var="user">
	<li class="list-group-item clearfix">
		<c:if test="${not user.enabled}"><span class="label label-default">Account Deactivated</span></c:if>
		<c:if test="${user.admin}"><span class="label label-warning">Admin User</span></c:if>
		${user.username} (${user.firstName} ${user.lastName})
		<div class="pull-right text-right">
		<c:if test="${not user.enabled}">
			<c:url value="/admin/user/${user.username}/approve" var="approveUrl"/>
			<form:form action="${approveUrl}" method="POST">
			<button style="padding:0px" class="btn btn-link"><i class="fas fa-user-check"></i> Activate</button>
			</form:form>
		</c:if>
		
		<c:if test="${user.enabled}">
			<c:if test="${not user.admin}">
			<c:url value="/admin/user/${user.username}/admin" var="postUrl"/>
			<form:form action="${postUrl}" method="POST">
			<button style="padding:0px" class="btn btn-link"><i class="fas fa-user-tie"></i> Make Admin</button>
			</form:form>
			</c:if>
			<c:if test="${user.admin}">
			<c:url value="/admin/user/${user.username}/admin/remove" var="removeUrl"/>
			<form:form action="${removeUrl}" method="POST">
			<button style="padding:0px" class="btn btn-link"><i class="fas fa-user"></i> Remove Admin Role</button>
			</form:form>
			</c:if>
		
			<c:url value="/admin/user/${user.username}/disable" var="disableUrl"/>
			<form:form action="${disableUrl}" method="POST">
			<button style="padding:0px" class="btn btn-link"><i class="fas fa-user-slash"></i> Deactivate User</button>
			</form:form>
			</c:if>
		</div>
	</li>
</c:forEach>
</ul>