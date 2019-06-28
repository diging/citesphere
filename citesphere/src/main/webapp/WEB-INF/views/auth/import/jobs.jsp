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

<h1>Imports</h1>

<div class="alert alert-info" role="alert">Please reload the page to see updated status information.</div>

<ul id="pagination-top" class="pagination-sm"></ul>

<table class="table table-striped">
<tr>
<th>Job ID</th>
<th>Group</th>
<th>Uploaded file</th>
<th>File Size</th>
<th>Content Type</th>
<th>Status</th> 
<th>Created On</th>
</tr>

<c:forEach items="${jobs}" var="job">
<tr>
<td>${job.id}</td>
<td><span class="label label-primary">${job.citationGroup}</span></td>
<td>${job.filename}</td>
<td>${job.fileSize}</td>
<td>${job.contentType}</td>
<td>${job.status}</td>
<td>${job.createdOn}</td>
</tr>
</c:forEach>

</table>