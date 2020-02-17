<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script src="<c:url value="/resources/notify/bootstrap-notify.min.js" />"></script>
<script>

$(function() {
	$('#secret').click(function() {
		$('#client-update-confirmation').modal("show");	
		event.preventDefault();
	});
	
	$("#update-client-button").click(function() {
		var url = "/${clientId}/secret/update";
		$.ajax({
			'url': '<c:url value= "/admin/apps"/>'+url+"?${_csrf.parameterName}=${_csrf.token}",
			'type': "POST",
			'success': function(response){
				 $('#secretKey').text(response.secret);
				 $.notify('<i class="fas fa-check-circle"></i> New secret key successfully generated!', {
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
				$.notify('<i class="fas fa-exclamation-circle"></i> Error occurred. Unable to generate new secret key!', {
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
		$('#client-update-confirmation').modal("hide");
		event.preventDefault();
	});
});
</script>



<div class="pull-right" style="float:left;margin-top: 20px;">
	<form method="GET" action="<c:url value="/admin/apps" />"><button class="btn btn-primary" type="submit">Show All Apps</button></form>
</div>
<div class="pull-right" style="float:right;margin-top: 20px;">
	<button id ="secret" class="btn btn-primary" type="submit" href="">Regenerate Secret</button>&nbsp;&nbsp;
</div>

<br/>
<h2>${clientName}</h2>
<br/>
<table class="table table-striped">
<tr>
	<td>Client Id</td>
	<td>${clientId}</td>
</tr>
<tr>
	<td>Client Secret</td>
	<c:if test="${not empty secret}">
		<td id="secretKey">${secret}</td>
	</c:if>
	<c:if test="${empty secret}">
		<td id="secretKey"><I>*Secret Key is hidden*</I></td>
	</c:if>
	
</tr>
<tr>
	<td>Name</td>
	<td>${clientName}</td>
</tr>
<tr>
	<td>Description</td>
	<td>${description}</td>
</tr>
<tr>
	<td>Redirect URL</td>
	<td>${redirectUrl}</td>
</tr>
<tr>
	<td>Application Type</td>
	<td>${applicationType}</td>
</tr>
</table>
<div class="modal fade" id="client-update-confirmation" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Regenerate Client Secret</h4>
      </div>
      <div class="modal-body">
        <p>Once a new secret has been generated, applications using this client id and secret won't be able to connect anymore. Are you sure you want to regenerate secret?</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
        <button id="update-client-button" type="button" class="btn btn-primary">Yes</button>
      </div>
    </div>
  </div>
</div>
