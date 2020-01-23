<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script src="<c:url value="/resources/paginator/jquery.twbsPagination.min.js" />"></script>
<script>
$(function() {
    $('#pagination-top').twbsPagination({
        startPage: ${currentPage},
        totalPages: ${totalPages},
        prev: "«",
        next: "»",
        visiblePages: 10,
        initiateStartPageClick: false,
        onPageClick: function (event, page) {
            window.location.href = "<c:url value="/admin/apps" />?page=" + (page-1);
        }
    });
    
    $(".client-entry").click(function() {
		var id = $(this).data("id");
		window.location.href = "<c:url value="/admin/apps/" />" + id;
	});
});
</script>
<h2>Apps</h2>
<ul id="pagination-top" class="pagination-sm"></ul>
<div class="pull-right" style="margin-top: 20px;"><a href="<c:url value="/admin/apps/add" />" class="btn btn-primary"><i class="fas fa-plus-circle"></i> Add App</a></div>
<table class="table table-striped">
<tr>
	<th>Client Id</th>
	<th>Name</th>
	<th>Description</th>
</tr>
<c:forEach items="${clientList}" var="client">
<tr> 
	<td class="client-entry" data-id="${client.clientId}"> <a href=#>${client.clientId} </a></td>
	<td class="client-entry" data-id="${client.clientId}"> <a href=#>${client.name} </a></td>
	<td class="client-entry" data-id="${client.clientId}"> ${client.description} </td>
</tr>
</c:forEach>
</table>