<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<h2>Add App</h2>

${clientId}: ${secret}

<c:url value="/admin/apps/add" var="postUrl" />
<form:form action="${postUrl}" method="POST" modelAttribute="appForm">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	
	<label>App Name: </label>
	<form:input path="name" class="form-control input-sm"></form:input>
	<p class="text-danger"><form:errors path="name"/></p>
	
	<label>Description: </label>
	<form:input path="description" class="form-control input-sm"></form:input>
	<p class="text-danger"><form:errors path="description"/></p>
	
	<label>Application Type: </label>
	<form:select path = "grantType" class="form-control input-sm">
       <form:option value="" label = "Select"/>
       <form:option value="authorization_code" label="Apps need user information (Authorization Code)" />
       <form:option value="client_credentials" label="Apps do not need user information (Client Credentials)" />
    </form:select> 
    <p class="text-danger"><form:errors path="grantType"/></p>
    
    <label>Redirect URL:</label>
    <form:input path="redirectUrl" class="form-control input-sm"></form:input>
    <p class="text-danger"><form:errors path="redirectUrl"/></p>
    
	
	<button type="submit" class="btn btn-primary">Add</button>
	
</form:form>
