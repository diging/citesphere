<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<h1>Create Concept</h1>

<c:url value="/auth/concepts/add" var="postUrl" />
<form:form action="${postUrl}" method="POST" modelAttribute="conceptForm">

<table class="table table-striped">
<tr>
<td width="20%"><label>Name</label></td>
<td><form:input type="text" path="name" class="form-control" /></td>
</tr>

<tr>
<td width="20%"><label>Description</label></td>
<td><form:input type="text" path="description" class="form-control" /></td>
</tr>

<tr>
<td width="20%"><label>URI</label></td>
<td><form:input type="text" path="uri" class="form-control" /></td>
</tr>

</table>

<p class="pull-right">
<button class="btn btn-primary" type="submit">Create</button>
</p>
</form:form>