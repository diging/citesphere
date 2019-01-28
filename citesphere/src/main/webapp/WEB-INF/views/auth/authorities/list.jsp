<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script src="<c:url value="/resources/notify/bootstrap-notify.min.js" />"></script>
<link href="<c:url value="/resources/notify/animate.css" />" rel="stylesheet">
	
	
<script>
//# sourceURL=delete.js
$(function() {
	$(".delete-link").click(function(event) {
		var id = $(this).data('authority-id');
		var link = $(this);
		$.ajax({
			'url': '<c:url value="/auth/authority/" />' + id + "?${_csrf.parameterName}=${_csrf.token}",
			'type': "DELETE",
			'success': function(data) {
				link.closest("tr").hide();
				$.notify('<i class="fas fa-check-circle"></i> Authority successfully deleted!', {
					type: 'success',
					offset: {
						x: 50,
						y: 90
					},
					animate: {
						enter: 'animated fadeInRight',
						exit: 'animated fadeOutRight'
					}
				});
			},
			'error': function(data) {
				$.notify('<i class="fas fa-exclamation-circle"></i> Authority could not be deleted!', {
					type: 'danger',
					offset: {
						x: 50,
						y: 90
					},
					animate: {
						enter: 'animated fadeInRight',
						exit: 'animated fadeOutRight'
					}
				});
			}
		});
		event.preventDefault();
	});
});
</script>

<h2>Managed Authority Entries</h2>

<table class="table table-striped table-bordered">
<tr>
<th width="20%">URI</th>
<th>Name</th>
<th>Created on</th>
<th></th>
</tr>
<c:forEach items="${authorities}" var="authority">
<tr>
<td>${authority.uri}</td>
<td>${authority.name}</td>
<td><span class="date">${authority.createdOn}</span></td>
<td><a class="delete-link" href="" data-authority-id="${authority.id}"><i class="fas fa-trash-alt"></i></a></td>
</tr>
</c:forEach>
</table>