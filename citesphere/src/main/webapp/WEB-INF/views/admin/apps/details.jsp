<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<div class="pull-right" style="margin-top: 20px;">
	<a href="<c:url value="/admin/apps/add" />" class="btn btn-primary"><i
		class="fas fa-plus-circle"></i> Add App</a> &nbsp;&nbsp; 
	<a href="<c:url value="/admin/apps" />" class="btn btn-primary">Show All Apps</a>
</div>
<h2>App Details</h2>
<table class="table table-striped">
<tr>
<th>Client Id</th>
<th>Client Secret</th>
<th>Name</th>
<th>Description</th>
</tr>
<tr> 
<td>${clientId}</td>
<td>${secret}</td>
<td>${name}</td>
<td>${description}</td>
</tr>
</table>