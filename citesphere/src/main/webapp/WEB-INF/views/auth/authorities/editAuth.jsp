<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<style>
.popover {
	min-width: 300px;
}
</style>
<script>

</script>

<ol class="breadcrumb">
  <li><a href="<c:url value="/" />">Home</a></li>
  <li><a href="<c:url value="/auth/authority/list" />">Managed Authority Entries</a></li>
  <li class="active">${entry.name}</li>
</ol>

<h2>
	<strong><c:if test="${not empty entry.name}">${entry.name}</c:if></strong>	  
</h2>

<c:url value="/auth/authority/${authorityId}/edit/save" var="processingUrl" />
<form:form action="${processingUrl}" modelAttribute="form" method="POST" id="editForm">

<tr <c:if test="${not fn:contains(entry, 'name') }">style="display:none;"</c:if>>
<td>Name</td>
<td>
<form:input path="Name" type="text" class="form-control" placeholder="Name" value="${not empty entry.name ? form.name : entry.name}" /></td>
</tr>

<tr <c:if test="${not fn:contains(entry, 'description') }">style="display:none;"</c:if>>
<td>Description</td>
<td>
<form:input path="Description" type="text" class="form-control" placeholder="Description" value="${not empty entry.description ? form.description : entry.description}" /></td>
</tr>


<button id="submitForm" class="btn btn-primary" type="submit"><i class="far fa-save"></i> &nbsp;Save</button>
<a href="<c:url value="/auth/authority/list" />" class="btn btn-default">
		<i class="fa fa-times"></i>&nbsp;Cancel 
</a>
</form:form>
