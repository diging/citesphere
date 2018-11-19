<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<h2>Users</h2>
<ul class="list-group">
<c:forEach items="${users}" var="user">
	<li class="list-group-item">${user.username} (${user.firstName} ${user.lastName})
		<c:if test="${not user.enabled}"><span class="label label-default">Account Deactivated</span></c:if>
		<c:if test="${not user.enabled}"><div class="pull-right"><a href="<c:url value="/admin/user/${user.username}/approve" />"><i class="fa fa-handshake-o" aria-hidden="true"></i> Activate</a></div></c:if>
	</li>
</c:forEach>
</ul>