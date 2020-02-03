<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="pull-right" style="margin-top: 20px;">
	<a href="<c:url value="/admin/apps/add" />" class="btn btn-primary"><i
		class="fas fa-plus-circle"></i> Add App</a> &nbsp;&nbsp; 
	<a href="<c:url value="/admin/apps" />" class="btn btn-primary">Show All Apps</a> &nbsp;&nbsp; 
	<form method="POST" action="<c:url value="/admin/apps/updateSecret/${clientId}" />"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /><button class="btn btn-primary" type="submit">Regenerate Secret</button></form>
</div>
<br/>
<h2>${clientName}</h2>
<br/>
<table class="table table-striped">
<tr>
	<td>Client Id</td>
	<td>${clientId}</td>
</tr>
<tr>
	<td>Client Secret</td>
	<c:if test="${not empty secret}">
		<td>${secret}</td>
	</c:if>
	<c:if test="${empty secret}">
		<td><I>*Secret Key is hidden*</I></td>
	</c:if>
	
</tr>
<tr>
	<td>Name</td>
	<td>${clientName}</td>
</tr>
<tr>
	<td>Description</td>
	<td>${description}</td>
</tr>
<tr>
	<td>Redirect URL</td>
	<td>${redirectUrl}</td>
</tr>
<tr>
	<td>Application Type</td>
	<td>${applicationType}</td>
</tr>
</table>
