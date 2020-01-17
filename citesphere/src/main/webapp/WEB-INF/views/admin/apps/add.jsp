<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<h2>Add App</h2>

<c:url value="/admin/apps/add" var="postUrl" />
<form:form action="${postUrl}" method="POST" modelAttribute="appForm">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	
	<label>App Name: </label>
	<form:input path="name" class="form-control input-sm"></form:input>
	<p class="text-danger"><form:errors path="name"/></p>
	
	<label>Description: </label>
	<form:input path="description" class="form-control input-sm"></form:input>
	<p class="text-danger"><form:errors path="description"/></p>
	
	<button type="submit" class="btn btn-primary">Add</button>
	
</form:form>
