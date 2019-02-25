<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<style>
.popover {
	min-width: 300px;
}
</style>
<script>
//@ sourceURL=submit.js
$(function() {
	$("#uriLoadingSpinner").hide();
	$("#uriLoadingFailure").hide();
	$("#uriLoadingFound").hide();
	
	$("#uriLoadingFound").popover();
	$("#uriLoadingFailure").popover();
	
	$("#submitForm").click(function() {
		$(".author-item").each(function(idx, author) {
			var authorIdField = $("<input>");
			authorIdField.attr("type", "hidden");
			authorIdField.attr("id", "authors" + idx + ".id");
			authorIdField.attr("name", "authors[" + idx + "].id");
			authorIdField.attr("value", $(author).data("author-id"));
			$("#editForm").append(authorIdField);
			
			var authorFirstNameField = $("<input>");
			authorFirstNameField.attr("type", "hidden");
			authorFirstNameField.attr("id", "authors" + idx + ".firstName");
			authorFirstNameField.attr("name", "authors[" + idx + "].firstName");
			authorFirstNameField.attr("value", $(author).data("author-firstname"));
			$("#editForm").append(authorFirstNameField);
			
			var authorLastNameField = $("<input>");
			authorLastNameField.attr("type", "hidden");
			authorLastNameField.attr("id", "authors" + idx + ".lastName");
			authorLastNameField.attr("name", "authors[" + idx + "].lastName");
			authorLastNameField.attr("value", $(author).data("author-lastname"));
			$("#editForm").append(authorLastNameField);
			
			var authorUriField = $("<input>");
			authorUriField.attr("type", "hidden");
			authorUriField.attr("id", "authors" + idx + ".uri");
			authorUriField.attr("name", "authors[" + idx + "].uri");
			authorUriField.attr("value", $(author).data("author-uri"));
			$("#editForm").append(authorUriField);
			
			var authorAuthorityField = $("<input>");
			authorAuthorityField.attr("type", "hidden");
			authorAuthorityField.attr("id", "authors" + idx + ".localAuthorityId");
			authorAuthorityField.attr("name", "authors[" + idx + "].localAuthorityId");
			authorAuthorityField.attr("value", $(author).data("author-authority-id"));
			$("#editForm").append(authorAuthorityField);
			
			$(author).children("span").each(function(idx2, affiliation) {
				var affiliationField = $("<input>");
				affiliationField.attr("type", "hidden");
				affiliationField.attr("id", "authors" + idx + ".affiliations" + idx2 + ".name");
				affiliationField.attr("name", "authors[" + idx + "].affiliations[" + idx2 + "].name");
				affiliationField.attr("value", $(affiliation).data("affiliation-name"));
				$("#editForm").append(affiliationField);
				
				var affiliationIdField = $("<input>");
				affiliationIdField.attr("type", "hidden");
				affiliationIdField.attr("id", "authors" + idx + ".affiliations" + idx2 + ".id");
				affiliationIdField.attr("name", "authors[" + idx + "].affiliations[" + idx2 + "].id");
				affiliationIdField.attr("value", $(affiliation).data("affiliation-id"));
				$("#editForm").append(affiliationIdField);
			});
		});
	});
	
	$("#addAuthorButton").click(function() {
		var firstname = $("#firstNameAuthor").val();
		var lastname = $("#lastNameAuthor").val();
		var uri = $("#uriAuthor").val();
		var localAuthority = $("#uriAuthorLocalId").val();
		
		var authorSpan = $("<span>");
		authorSpan.attr("class", "label label-primary author-item");
		authorSpan.attr("data-author-firstname", firstname);
		authorSpan.attr("data-author-lastname", lastname);
		authorSpan.attr("data-author-uri", uri);
		authorSpan.attr("data-author-authority-id", localAuthority);
		
		var affiliationsList = [];
		var affSpan = $("<span>");
		$("#affiliations").children().each(function(idx, elem) {
			var affSpan = $("<span>");
			var input = $(elem).find("input");
			affSpan.attr("data-affiliation-name", input.val());
			affiliationsList.push(input.val());
			authorSpan.append(affSpan);
		});
		
		var affiliationString = "";
		if (affiliationsList) {
			affiliationString = " (" + affiliationsList.join(", ") + ")";
		}
		
		authorSpan.append(lastname + ', ' + firstname + affiliationString + '&nbsp;&nbsp; ');
		var deleteIcon = $('<i class="fas fa-times remove-author"></i>');
		deleteIcon.click(removeAuthor);
		authorSpan.append(deleteIcon);
		$("#authorList").append(authorSpan);
		$("#authorList").append("&nbsp;&nbsp; ")
		
		$("#authorModal").modal('hide');
		resetAuthorCreationModal();
	});
	
	$("#addAuthorModalCancel").click(function() {
		$("#authorModal").modal('hide');
		resetAuthorCreationModal();
	});
	
	$(".remove-author").click(removeAuthor);
	$(".remove-author").css('cursor', 'pointer');
	
	$("#addAffiliation").click(function() {
		var affiliationCopy = $("#affiliationTemplate").clone();
		affiliationCopy.removeAttr("id");
		affiliationCopy.find("input").val("");
		$("#affiliations").append(affiliationCopy);
	});
	
	var timer = null;
	$("#uriAuthor").change(function() {
		resetAuthorAuthorityCreation();
		$("#uriLoadingSpinner").show();
		var uri = $("#uriAuthor").val();
		clearTimeout(timer); 
	    timer = setTimeout(function() {
	    	getAuthority(uri);
	    }, 1000);
	});
	
	$("#iconContainer").on('click', ".popover #createAuthority", function() {
		var uri = $("#uriLoadingFound").data('authority-uri');
		$.post("<c:url value="/auth/authority/create" />?${_csrf.parameterName}=${_csrf.token}&uri=" + uri, function(data) {
			$("#createAuthority").hide();
			$("#uriAuthorLocalId").val(data['id']);
			$("#authorAuthorityUsed").html("Created new authority entry <i>" + data['name'] + "</i>.");
			$("#authorityCreationFeedback").html('<div class="text-success" style="margin-top:10px;">Authority entry has been created!</div>');
			$("#uriLoadingFound").popover('hide');
		});
	});
	
	$("#iconContainer").on('click', ".popover .foundAuthorities li a", function(event) {
		var authId = $(this).data('authority-id');
		$("#uriAuthorLocalId").val(authId);
		$("#authorAuthorityUsed").html("Using stored authority entry <i>" + $(this).data('authority-name') + "</i>.");
		$("#uriLoadingFound").popover('hide');
		event.preventDefault();
	});
});

function resetAuthorCreationModal() {
	$("#firstNameAuthor").val("");
	$("#lastNameAuthor").val("");
	$("#affiliationTemplate").find("input").val("");
	$("#uriAuthor").val("");
	resetAuthorAuthorityCreation();
}

function resetAuthorAuthorityCreation() {
	$("#uriLoadingFound").hide();
	$("#uriLoadingFailure").hide();
	$("#uriLoadingSpinner").hide();
	$("#uriLoadingFound").popover('hide');
	$("#uriLoadingFailure").popover('hide');
}

function getAuthority(uri) {
	$.get('<c:url value="/auth/authority/get?uri=" />' + uri + '&zoteroGroupId=' + ${zoteroGroupId}, function(data) {
		$("#uriLoadingFound").attr("data-authority-uri", data['uri']);
		var content = "Authority <b>" + uri + "</b>";
		if (data['userAuthorityEntries'] != null && data['userAuthorityEntries'].length > 0) {
			content += "<br><br>This authority entry has already been imported by you:";
			content += '<ul class="foundAuthorities">';
			data['userAuthorityEntries'].forEach(function(elem) {
				content += '<li>' + elem['name'];
				content += ' [<a href="" data-authority-id="' + elem['id'] + '" data-authority-name="' + elem['name'] + '">Use this one</a>]';
				content += '</li>';
			});
			content += "</ul>";
		}
		if (data['datasetAuthorityEntries'] != null && data['datasetAuthorityEntries'].length > 0) {
			content += "<br><br>This authority entry has already been imported by someone else for this dataset:";
			content += '<ul class="foundAuthorities">';
			data['datasetAuthorityEntries'].forEach(function(elem) {
				content += '<li>' + elem['name'];
				content += ' [<a href="" data-authority-id="' + elem['id'] + '" data-authority-name="' + elem['name'] + '">Use this one</a>]';
				content += '</li>';
			});
			content += "</ul>";
		}
		content += "<br><br>Do you want to create a managed authority entry?<br>" +
					'<span id="authorityCreationFeedback"><button id="createAuthority" type="submit" class="btn btn-link pull-right"><b>Yes, create a new entry!</b></button></span>';
		$("#uriLoadingFound").attr("data-content", content);
		$("#uriLoadingFound").attr("data-authority-uri", uri);
		$("#uriLoadingFound").show();
		$("#uriLoadingFound").popover('show');
	})
	.fail(function() {
		$("#uriLoadingFailure").show();
	 })
    .always(function() {
    	$("#uriLoadingSpinner").hide();
	 });

}

let removeAuthor = function removeAuthor(e) {
	var deleteIcon = e.currentTarget;
	var author = $(deleteIcon).parent();
	author.remove();
}

</script>

<ol class="breadcrumb">
  <li><a href="<c:url value="/" />">Home</a></li>
  <li><a href="<c:url value="/auth/group/${zoteroGroupId}/items" />">Group</a></li>
  <li class="active">${citation.key}</li>
</ol>

<h2>
	 <c:forEach items="${citation.authors}" var="author" varStatus="status">
	 	  <strong>${author.lastName}<c:if test="${not empty author.firstName}">, ${author.firstName}</c:if></strong><c:if test="${!status.last}">; </c:if>
	 </c:forEach>
	  <em>${citation.title}</em>
	  <c:if test="${not empty citation.dateFreetext}">
 	  (${citation.dateFreetext})
 	  </c:if> 
</h2>

<div class="modal fade" id="messageModal" tabindex="-1" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div id="displayMessage" class="modal-body"></div>
		</div>
	</div>
</div>

<c:if test="${not empty citation.key}" >
<c:url value="/auth/group/${zoteroGroupId}/items/${citation.key}/edit" var="processingUrl" />
</c:if>
<c:if test="${empty citation.key}" >
<c:url value="/auth/group/${zoteroGroupId}/items/create" var="processingUrl" />
</c:if>

<form:form action="${processingUrl}" modelAttribute="form" method="POST" id="editForm">
<table class="table table-striped">

<tr <c:if test="${empty citation.key}" >style="display:none;"</c:if>>
<td width="20%">Item Key</td>
<td>${citation.key}</td>
<form:hidden path="key" value="${citation.key}"/>
</tr>

<tr>
<td width="20%">Citation Type</td>
<td>
<c:set var="enumValues" value="<%=edu.asu.diging.citesphere.core.model.bib.ItemType.values()%>"/>
<form:select id="items" path="itemType" data-show-icon="true" class="form-control selectpicker">
<c:forEach items="${enumValues}" var="enumValue">
    <spring:eval expression="@iconsResource.getProperty(enumValue + '_label')"  var="iconLabel" />	
    <c:if test="${empty iconLabel}">
	<c:set var="iconLabel" value="${enumValue}" />
	</c:if>
	<option <c:if test="${enumValue == citation.itemType}">selected</c:if> value="${enumValue}">
    ${iconLabel}
    </option>
</c:forEach>
</form:select>
</td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'title') }">style="display:none;"</c:if>>
<td>Title</td>
<td>
<form:input path="title" type="text" class="form-control" placeholder="Title" value="${not empty form.title ? form.title : citation.title}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'shortTitle') }">style="display:none;"</c:if>>
<td>Short Title</td>
<td><form:input path="shortTitle" type="text" class="form-control" placeholder="Short title" value="${citation.shortTitle}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'dateFreetext') }">style="display:none;"</c:if>>
<td>Date</td>
<td><form:input path="dateFreetext" type="text" class="form-control" placeholder="Date" value="${not empty form.dateFreetext ? form.dateFreetext : citation.dateFreetext}" /></td>
</tr>

<tr>
<td>Authors</td>
<td>
<span id="authorList" style="font-size: 18px">
<c:forEach items="${citation.authors}" var="author" varStatus="status">
<span class="label label-primary author-item" data-author-id="${author.id}" data-author-firstname="${author.firstName}" data-author-lastname="${author.lastName}" data-author-uri="${author.uri}" data-author-authority-id="${author.localAuthorityId}">
<c:forEach items="${author.affiliations}" var="aff"> <span data-affiliation-name="${aff.name}" data-affiliation-id="${aff.id}"></span></c:forEach>
${author.lastName}<c:if test="${not empty author.firstName}">, ${author.firstName}</c:if><c:forEach items="${author.affiliations}" var="aff"> (${aff.name})</c:forEach>
&nbsp;&nbsp;
<i class="fas fa-times remove-author"></i>
</span>
&nbsp;&nbsp;
</c:forEach>
</span>
<div class="pull-right"><a data-toggle="modal" data-target="#authorModal"><i class="fas fa-plus-circle"></i> Add Author</a></div>
</td>
</tr>
<tr>
<td>Editors</td>
<td>
<c:forEach items="${citation.editors}" var="editor" varStatus="status">
 ${editor.lastName}<c:if test="${not empty editor.firstName}">, ${editor.firstName}</c:if><c:if test="${!status.last}">; </c:if>
</c:forEach>
</td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'publicationTitle') }">style="display:none;"</c:if>>
<td>Publication Title</td>
<td><form:input path="publicationTitle" type="text" class="form-control" placeholder="Publication Title" value="${not empty form.publicationTitle ? form.publicationTitle : citation.publicationTitle}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'journalAbbreviation') }">style="display:none;"</c:if>>
<td>Journal Abbreviation</td>
<td><form:input path="journalAbbreviation" type="text" class="form-control" placeholder="Journal Abbreviation" value="${not empty form.journalAbbreviation ? form.journalAbbreviation : citation.journalAbbreviation}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'volume') }">style="display:none;"</c:if>>
<td>Volume</td>
<td><form:input path="volume"  type="text" class="form-control" placeholder="Volume" value="${not empty form.volume ? form.volume : citation.volume}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'issue') }">style="display:none;"</c:if>>
<td>Issue</td>
<td><form:input path="issue" type="text" class="form-control" placeholder="Issue" value="${not empty form.issue ? form.issue : citation.issue}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'pages') }">style="display:none;"</c:if>>
<td>Pages</td>
<td><form:input path="pages" type="text" class="form-control" placeholder="Pages" value="${not empty form.pages ? form.pages : citation.pages}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'series') }">style="display:none;"</c:if>>
<td>Series</td>
<td><form:input path="series" type="text" class="form-control" placeholder="Series" value="${not empty form.series ? form.series : citation.series}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'seriesTitle') }">style="display:none;"</c:if>>
<td>Series Title</td>
<td><form:input path="seriesTitle" type="text" class="form-control" placeholder="Series Title" value="${not empty form.seriesTitle ? form.seriesTitle : citation.seriesTitle}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'seriesText') }">style="display:none;"</c:if>>
<td>Series Text</td>
<td><form:input path="seriesText" type="text" class="form-control" placeholder="Series Text" value="${not empty form.seriesText ? form.seriesText : citation.seriesText}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'url') }">style="display:none;"</c:if>>
<td>URL</td>
<td><form:input path="url" type="text" class="form-control" placeholder="Url" value="${not empty form.url ? form.url : citation.url}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'language') }">style="display:none;"</c:if>>
<td>Language</td>
<td><form:input path="language" type="text" class="form-control" placeholder="Language" value="${not empty form.language ? form.language : citation.language}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'doi') }">style="display:none;"</c:if>>
<td>DOI</td>
<td><form:input path="doi" type="text" class="form-control" placeholder="DOI" value="${not empty form.doi ? form.doi : citation.doi}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'issn') }">style="display:none;"</c:if>>
<td>ISSN</td>
<td><form:input path="issn" type="text" class="form-control" placeholder="ISSN" value="${not empty form.issn ? form.issn : citation.issn}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'archive') }">style="display:none;"</c:if>>
<td>Archive</td>
<td><form:input path="archive" type="text" class="form-control" placeholder="Archive" value="${not empty form.archive ? form.archive : citation.archive}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'archiveLocation') }">style="display:none;"</c:if>>
<td>Archive Location</td>
<td><form:input path="archiveLocation" type="text" class="form-control" placeholder="Archive Location" value="${not empty form.archiveLocation ? form.archiveLocation : citation.archiveLocation}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'libraryCatalog') }">style="display:none;"</c:if>>
<td>Library Catalog</td>
<td><form:input path="libraryCatalog" type="text" class="form-control" placeholder="Library Catalog" value="${not empty form.libraryCatalog ? form.libraryCatalog : citation.libraryCatalog}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'callNumber') }">style="display:none;"</c:if>>
<td>Call Number</td>
<td><form:input path="callNumber" type="text" class="form-control" placeholder="Call Number" value="${not empty form.callNumber ? form.callNumber : citation.callNumber}" /></td>
</tr>

<tr <c:if test="${not fn:contains(fields, 'rights') }">style="display:none;"</c:if>>
<td>Rights</td>
<td><form:input path="rights" type="text" class="form-control" placeholder="Rights" value="${not empty form.rights ? form.rights : citation.rights}" /></td>
</tr>
</table>

<button id="submitForm" class="btn btn-primary" type="submit"><i class="far fa-save"></i> &nbsp;Save</button>
<a href="<c:url value="/auth/group/${zoteroGroupId}/items/${itemId}" />" class="btn btn-default">
		<i class="fa fa-times"></i>&nbsp;Cancel
</a>
</form:form>

<!-- Author Modal -->
<div class="modal fade" id="authorModal" tabindex="-1" role="dialog" aria-labelledby="authorLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="authorLabel">Enter Author Information</h4>
      </div>
      <div class="modal-body">
          <div class="form-group">
		    <label for="firstNameAuthor">First Name:</label>
		    <input type="text" class="form-control" id="firstNameAuthor" placeholder="First Name">
		  </div>
		  <div class="form-group">
		    <label for="lastNameAuthor">Last Name:</label>
		    <input type="text" class="form-control" id="lastNameAuthor" placeholder="Last Name">
		  </div>
		  <div class="form-group">
		    <label for="uriAuthor">URI:</label>
		    <div class="input-group">
			    <input type="text" class="form-control" id="uriAuthor" placeholder="URI">
			    <div id="iconContainer" class="input-group-addon" style="min-width: 35px;">
			    	<i id="uriLoadingSpinner" class="fas fa-spinner fa-spin text-info"></i>
			    	<i id="uriLoadingFound" class="fas fa-info-circle text-success" data-toggle="popover" data-html="true" data-placement="right"></i>
			    	<i id="uriLoadingFailure" class="fas fa-exclamation-triangle text-danger" data-toggle="popover" data-html="true" data-placement="right" data-content="Could not find any data for this URI."></i>
			    </div>
			    <input type="hidden" id="uriAuthorLocalId" />
		    </div>
		    <div class="text-warning pull-right" id="authorAuthorityUsed"></div>
		  </div>
		  <div id="affiliations">
		  <div id="affiliationTemplate" class="form-group">
		    <label for="affiliationAuthor">Affiliation:</label>
		    <input type="text" class="form-control" placeholder="Affiliation">
		  </div>
		  </div>
		  <div>
		  <div class="text-right"><a id="addAffiliation"><i class="fas fa-plus-circle" title="Add another affiliation"></i> Add Affiliation</a></div>
      	  </div>
      </div>
      
      <div class="modal-footer">
        <button type="button" class="btn btn-default" id="addAuthorModalCancel">Close</button>
        <button id="addAuthorButton" type="button" class="btn btn-primary">Add Author</button>
      </div>
    </div>
  </div>
</div>
<script>
//# sourceURL=fields.js
$(document).ready(function() {
	$('#items').on("change", function(e){
		loadFields();
	});
	
	<c:if test="${empty citation}">
	$("#items").val("${defaultItemType}");
	loadFields();
	</c:if>
});

function loadFields() {
	var itemType = $('#items option:selected').val()
	$("#displayMessage").html("<i class='glyphicon glyphicon-refresh spinning'></i>" +
		" Loading form fields");
	$("#messageModal").modal('show');
	$.ajax({
		url : '<c:url value="/auth/items/'+itemType+'/fields" />',
		type : 'GET',
		success: function(changedFields){
			$('form input').each(function(idx, elem) {
				var tr = $(elem).parent().closest('tr');
				tr.hide()
			});
			for(i=0;i<changedFields.length;i++){
				$('form input#'+changedFields[i]).parent().closest('tr').show();
			}
			$('#messageModal').modal('hide');
			
		},
		error: function(){
			$("#displayMessage").html("<i class='glyphicon glyphicon-remove-sign'></i>" +
			"Error loading the form fields. Try again later.");
			$('#messageModal').modal('show');
			setTimeout(function() {
				$('#messageModal').modal('hide');
		  	}, 3000);
			
		}
	});
}
</script>
