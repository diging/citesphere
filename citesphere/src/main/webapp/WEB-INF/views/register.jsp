<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<h2>Create new Account</h2>

<c:url value="/register" var="postUrl" />
<form:form action="${postUrl}" method="POST" modelAttribute="user">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	
	<label>Username: </label>
	<form:input path="username" class="form-control input-sm"></form:input>
	<p class="text-danger"><form:errors path="username"/></p>

	<label>First name:</label>
	<form:input path="firstName" class="form-control input-sm"></form:input>
	<p class="text-danger"><form:errors path="firstName"/></p>
	
	<label>Last name:</label>
	<form:input path="lastName" class="form-control input-sm"></form:input>
	<p class="text-danger"><form:errors path="lastName"/></p>
	
	<label>Email:</label>
	<form:input path="email" class="form-control input-sm"></form:input>
	<p class="text-danger"><form:errors path="email"/></p>
	
	<label>Password:</label>
	<form:password path="password" class="form-control input-sm"></form:password>
	<p class="text-danger"><form:errors path="password"/></p>
	
	<label>Confirm Password:</label>
	<form:password path="confirmPassword" class="form-control input-sm"></form:password>
	<p class="text-danger"><form:errors path="confirmPassword"/></p>

	<div style="padding-top: 20px;">
	<button class="btn btn-sm btn-primary pull-right">Create Account</button>
	</div>
</form:form>