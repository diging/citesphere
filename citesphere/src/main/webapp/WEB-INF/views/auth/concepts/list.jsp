<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script src="<c:url value="/resources/notify/bootstrap-notify.min.js" />"></script>
<link href="<c:url value="/resources/notify/animate.css" />" rel="stylesheet">
	

<h2>Concepts</h2>

<p>
<a href="<c:url value="/auth/concepts/add" />" class="btn btn-primary btn-sm">Add</a>
</p>

<table class="table table-striped table-bordered">
<tr>
<th width="20%">URI</th>
<th>Name</th>
<th>Description</th>
<th>Created on</th>
<th></th>
</tr>
<c:forEach items="${concepts}" var="concept">
<tr id="tr-${concept.id}">
<td>${concept.uri}</td>
<td>${concept.name}</td>
<td>${concept.description}</td>
<td><span class="date">${concept.createdOn}</span></td>
<td><a href="<c:url value="/auth/concepts/edit" />"><i class="far fa-edit" title="Edit"></i></a></td>
</tr>
</c:forEach>
</table>


