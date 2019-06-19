<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<h1>Exports</h1>

<table class="table table-striped">
<tr>
<th>Task ID</th>
<th>Export Type</th>
<th># of records</th>
<th># of processed records</th>
<th>Status</th> 
<th>Created On</th>
<th></th>
</tr>
<c:forEach items="${tasks}" var="task">
<tr>
<td>${task.id}</td>
<td><span class="label label-primary">${task.exportType}</span></td>
<td>${task.totalRecords}</td>
<td>${task.progress}</td>
<td>
<c:choose>
    <c:when test="${task.status == 'DONE'}">
        <span class="label label-success">${task.status}</span>
    </c:when>
    <c:when test="${task.status == 'FAILED'}">
        <span class="label label-danger">${task.status}</span>
    </c:when>
    <c:otherwise>
        <span class="label label-warning">${task.status}</span>
    </c:otherwise>
</c:choose>
</td>
<td>${task.createdOn}</td>
<th><a href="<c:url value="/auth/exports/${task.id}/download" />"><i class="fas fa-download"></i> Download</a>
</tr>
</c:forEach>
</table>