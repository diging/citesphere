<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script src="<c:url value="/resources/notify/bootstrap-notify.min.js" />"></script>
<link href="<c:url value="/resources/notify/animate.css" />" rel="stylesheet">
<script>
$(function() {
	$(".revoke-access").click(function(event) {
		var id = $(this).data('client-id');
		$("#revoke-access-button").attr("data-client-id", id);
		$("#confirm-client-entry-name").text($(this).data('client-name'));
		$('#revoke-access-confirmation').modal("show");	
		event.preventDefault();
	});
	$("#revoke-access-button").click(function() {
		var id = $(this).data("client-id");
		$.ajax({
			'url': '<c:url value="/tokens/" />' + id + "?${_csrf.parameterName}=${_csrf.token}",
			'type': "DELETE",
			'success': function(data) {
				$("#tr-" + id).remove();
				$.notify('<i class="fas fa-check-circle"></i> Access Revoked successfully!', {
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
				$.notify('<i class="fas fa-exclamation-circle"></i> Error Occurred! Access could not be revoked', {
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
		$('#revoke-access-confirmation').modal("hide");
		event.preventDefault();
		$(this).removeData();
	});
});

</script>

<c:if test="${not empty clientList}">
	<h2>Authorized OAuth Apps</h2>
	<br/>
	<table class="table table-striped" id="table">
	<tr>
		<th>App Name</th>
		<th>Client Id</th>
		<th/>
	</tr>
	<c:forEach items="${clientList}" var="client">
	<tr id="tr-${client.clientId}"> 
		<td data-id="${client.name}">${client.name}</td>
		<td data-id="${client.clientId}">${client.clientId}</td>
		<td><a href="#" data-client-id="${client.clientId}" data-client-name = "${client.name}" class="revoke-access"><i class="btn btn-primary">Revoke Access</i></a></td>
		
	</tr>
	</c:forEach>
	</table>

</c:if>
<c:if test="${empty clientList}">
	<h3>No authorized applications</h3>
	<p>You have no applications authorized to access your account.</p>
</c:if>
<div class="modal fade" id="revoke-access-confirmation" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Revoke Access</h4>
      </div>
      <div class="modal-body">
        <p>Are you sure you want to revoke access for <i><span id="confirm-client-entry-name"></span></i>?</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
        <button id="revoke-access-button" data-client-id="" type="button" class="btn btn-primary">Yes</button>
      </div>
    </div>
  </div>
</div>