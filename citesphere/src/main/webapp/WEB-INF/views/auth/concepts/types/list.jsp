<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script src="<c:url value="/resources/notify/bootstrap-notify.min.js" />"></script>
<link href="<c:url value="/resources/notify/animate.css" />" rel="stylesheet">
	

<h2>Concept Types</h2>

<p>
<a href="<c:url value="/auth/concepts/types/add" />" class="btn btn-primary btn-sm">Add</a>
</p>

<table class="table table-striped table-bordered">
<tr>
<th width="20%">URI</th>
<th>Name</th>
<th>Description</th>
<th>Created on</th>
</tr>
<c:forEach items="${types}" var="type">
<tr id="tr-${type.id}">
<td>${type.uri}</td>
<td>${type.name}</td>
<td>${type.description}</td>
<td><span class="date">${type.createdOn}</span></td>
</tr>
</c:forEach>
</table>


