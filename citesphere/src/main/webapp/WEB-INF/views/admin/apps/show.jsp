<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<script src="<c:url value="/resources/paginator/jquery.twbsPagination.min.js" />"></script>
<script src="<c:url value="/resources/notify/bootstrap-notify.min.js" />"></script>
<link href="<c:url value="/resources/notify/animate.css" />" rel="stylesheet">
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

$(function() {
	$(".delete-link").click(function(event) {
		var id = $(this).data('client-id');
		console.log(id);
		$("#delete-client-button").attr("data-client-id", id);
		$("#confirm-client-entry-id").text(id);
		$('#client-delete-confirmation').modal("show");	
		event.preventDefault();
	});
	$("#delete-client-button").click(function() {
		var id = $(this).data("client-id");
		console.log(id);
		$.ajax({
			'url': '<c:url value="/admin/apps/" />' + id + "?${_csrf.parameterName}=${_csrf.token}",
			'type': "DELETE",
			'success': function(data) {
				$("#tr-" + id).remove();
				$.notify('<i class="fas fa-check-circle"></i> App successfully deleted!', {
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
				$.notify('<i class="fas fa-exclamation-circle"></i> App could not be deleted!', {
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
		$('#client-delete-confirmation').modal("hide");
		event.preventDefault();
	});
});

</script>
<h2>Apps</h2>
<ul id="pagination-top" class="pagination-sm"></ul>
<div class="pull-right" style="margin-top: 20px;"><a href="<c:url value="/admin/apps/add" />" class="btn btn-primary"><i class="fas fa-plus-circle"></i> Add App</a></div>
<table class="table table-striped" id="table">
<tr>
<th>Client Id</th>
<th>Name</th>
<th>Description</th>
<th/>
</tr>
<c:forEach items="${clientList}" var="client">
<tr id="tr-${client.clientId}"> 
<td>${client.clientId}</td>
<td>${client.name}</td>
<td>${client.description}</td>
<td><a class="delete-link" href="" data-client-id="${client.clientId}"><i class="fas fa-trash-alt"></i></a></td>
</tr>
</c:forEach>
</table>
<div class="modal fade" id="client-delete-confirmation" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Delete App</h4>
      </div>
      <div class="modal-body">
        <p>Are you sure you want to delete app with client Id <i><span id="confirm-client-entry-id"></span></i>?</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">No, cancel!</button>
        <button id="delete-client-button" data-client-id="" type="button" class="btn btn-primary">Yes, delete!</button>
      </div>
    </div>
  </div>
</div>