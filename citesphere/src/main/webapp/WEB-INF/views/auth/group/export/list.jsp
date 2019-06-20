<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<script src="<c:url value="/resources/paginator/jquery.twbsPagination.min.js" />"></script>

<script>
//# sourceURL=page.js
$(function() {
    $('#pagination-top').twbsPagination({
        totalPages: ${total},
        startPage: ${page},
        prev: "«",
        next: "»",
        visiblePages: 10,
        initiateStartPageClick: false,
        onPageClick: function (event, page) {
            window.location.href = "<c:url value="/auth/exports" />?page=" + page;
        }
    });
});
</script>

    

<h1>Exports</h1>

<div class="alert alert-info" role="alert">Please reload the page to see updated status information.</div>

<ul id="pagination-top" class="pagination-sm"></ul>

<table class="table table-striped">
<tr>
<th>Task ID</th>
<th>Export Type</th>
<th>Group</th>
<th>Collection</th>
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
<td>${task.groupName}</td>
<td>${task.collectionName}</td>
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
<th>
<c:if test="${task.status == 'DONE'}">
<a href="<c:url value="/auth/exports/${task.id}/download" />"><i class="fas fa-download"></i> Download</a>
</c:if>
</tr>
</c:forEach>
</table>