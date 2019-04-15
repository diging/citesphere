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
		$("#delete-authority-button").attr("data-authority-id", id);
		$("#confirm-authority-entry-name").text($(this).data('authority-name'));
		$("#confirm-authority-entry-uri").text($(this).data('authority-uri'));
		$('#authority-delete-confirmation').modal("show");	
		event.preventDefault();
	});
	/* $(".edit-link").click(function(event) {
		var id=$(this).data("authority-id");
		$.ajax({
			'url': '<c:url value="/auth/authority/${id}/edit" />' + "?${_csrf.parameterName}=${_csrf.token}",
			'type':"GET",
			'success:' function(data) {
				
			},
			
		});
	}); */
	$("#delete-authority-button").click(function() {
		var id = $(this).data("authority-id");
		$.ajax({
			'url': '<c:url value="/auth/authority/" />' + id + "?${_csrf.parameterName}=${_csrf.token}",
			'type': "DELETE",
			'success': function(data) {
				$("#tr-" + id).remove();
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
		$('#authority-delete-confirmation').modal("hide");
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
<tr id="tr-${authority.id}">
<td>${authority.uri}</td>
<td>${authority.name}</td>
<td><span class="date">${authority.createdOn}</span></td>
<td><a class="delete-link" href="" data-authority-id="${authority.id}" data-authority-name="${authority.name}" data-authority-uri="${authority.uri}"><i class="fas fa-trash-alt"></i></a></td>

<td><a href="<c:url value="/auth/authority/${authority.id}/edit" />"><i class="far fa-edit" title="Edit"></i></a></td>
</tr>
</c:forEach>
</table>

<div class="modal fade" id="authority-delete-confirmation" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Delete Authority</h4>
      </div>
      <div class="modal-body">
        <p>Are you sure you want to delete the authority entry "<i><span id="confirm-authority-entry-name"></span></i>" with URI <i><span id="confirm-authority-entry-uri"></span></i>?</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">No, cancel!</button>
        <button id="delete-authority-button" data-authority-id="" type="button" class="btn btn-primary">Yes, delete!</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

