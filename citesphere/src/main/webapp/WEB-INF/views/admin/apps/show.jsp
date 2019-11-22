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
});

function removeRow(url,count) {
    $.ajax({
        url: url,
        type: 'DELETE',
        success : function(){
        	var id = "row"+count;
        	var row = document.getElementById(id);
        	row.parentNode.removeChild(row); 
        	}
    });
}
</script>
<h2>Apps</h2>
<ul id="pagination-top" class="pagination-sm"></ul>
<div class="pull-right" style="margin-top: 20px;"><a href="<c:url value="/admin/apps/add" />" class="btn btn-primary"><i class="fas fa-plus-circle"></i> Add App</a></div>
<table class="table table-striped">
<tr>
<th>Client Id</th>
<th>Name</th>
<th>Description</th>
<th/>
</tr>
<c:set var="count" value="0" />
<c:forEach items="${clientList}" var="client">
<c:set var="count" value='${count+1}' />
<tr id="row${count}"> 
<td>${client.clientId}</td>
<td>${client.name}</td>
<td>${client.description}</td>
<td><input type="button" value="Delete" onclick="removeRow('<c:url value="/admin/apps/${client.clientId}?page=${currentPage - 1}&${_csrf.parameterName}=${_csrf.token}" />','${count}')" /></td>
</tr>
</c:forEach>
</table>