<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script src="<c:url value="/resources/paginator/jquery.twbsPagination.min.js" />"></script>
<script>
$(function() {
    $('#pagination-top').twbsPagination({
        totalPages: ${totalPages},
        startPage: ${currentPage},
        prev: "«",
        next: "»",
        visiblePages: 10,
        initiateStartPageClick: false,
        onPageClick: function (event, page) {
            window.location.href = "<c:url value="/admin/apps" />?page=" + page;
        }
    });
});
</script>
<h2>Apps</h2>

<ul id="pagination-top" class="pagination-sm"></ul>
<table class="table table-striped">
<tr>
<th>Client Id</th>
<th>Name</th>
<th>Description</th>
</tr>
<c:forEach items="${clientList}" var="client">
<tr> 
<td>${client.clientId}</td>
<td>${client.name}</td>
<td>${client.description}</td>
</tr>
</c:forEach>
</table>