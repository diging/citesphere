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
	$("#uriLoadingSpinnerAuthor").hide();
	$("#uriLoadingFailureAuthor").hide();
	$("#uriLoadingFoundAuthor").hide();
	
	$("#uriLoadingFoundAuthor").popover();
	$("#uriLoadingFailureAuthor").popover();
	
	$("#uriLoadingSpinnerEditor").hide();
	$("#uriLoadingFailureEditor").hide();
	$("#uriLoadingFoundEditor").hide();
	
	$("#uriLoadingFoundEditor").popover();
	$("#uriLoadingFailureEditor").popover();
	
	$("#submitForm").click(function() {
		constructPersonArray("author");
		constructPersonArray("editor");
	});
	
	$("#addAuthorButton").click(function() {
		savePersonDetails('Author');
	});
	
	$("#addAuthorModalCancel").click(function() {
		$("#authorModal").modal('hide');
		resetPersonCreationModal();
	});
	
	$("#addEditorButton").click(function() {
		savePersonDetails('Editor');
	});

	$("#addEditorModalCancel").click(function() {
		$("#editorModal").modal('hide');
		resetPersonCreationModal();
	});
	
	$(".remove-author").click(removePerson);
	$(".remove-author").css('cursor', 'pointer');
	
	$("#addAuthorAffiliation").click(function() {
		var affiliationCopy = $("#authorAffiliationTemplate").clone();
		affiliationCopy.removeAttr("id");
		affiliationCopy.find("input").val("");
		$("#authorAffiliations").append(affiliationCopy);
	});
	
	$(".remove-editor").click(removePerson);
	$(".remove-editor").css('cursor', 'pointer');

	$("#addEditorAffiliation").click(function() {
		var affiliationCopy = $("#editorAffiliationTemplate").clone();
		affiliationCopy.removeAttr("id");
		affiliationCopy.find("input").val("");
		$("#editorAffiliations").append(affiliationCopy);
	});
	
	var timer = null;
	$("#uriAuthor").change(function() {
		resetPersonAuthorityCreation("Author");
		$("#uriLoadingSpinnerAuthor").show();
		var uri = $("#uriAuthor").val();
		clearTimeout(timer); 
	    timer = setTimeout(function() {
	    	getPersonAuthority(uri, "Author");
	    }, 1000);
	});
	
	$("#uriEditor").change(function() {
		resetPersonAuthorityCreation("Editor");
		$("#uriLoadingSpinnerEditor").show();
		var uri = $("#uriEditor").val();
		clearTimeout(timer); 
	    timer = setTimeout(function() {
	    	getPersonAuthority(uri, "Editor");
	    }, 1000);
	});
	
	$("#authorIconContainer").on('click', ".popover #authorCreateAuthority", function() {
		var uri = $("#uriLoadingFoundAuthor").attr('data-authority-uri');
		$.post("<c:url value="/auth/authority/create" />?${_csrf.parameterName}=${_csrf.token}&uri=" + uri, function(data) {
			$("#authorCreateAuthority").hide();
			$("#uriAuthorLocalId").val(data['id']);
			$("#authorAuthorityUsed").html("Created new authority entry <i>" + data['name'] + "</i>.");
			$("#authorAuthorityCreationFeedback").html('<div class="text-success" style="margin-top:10px;">Authority entry has been created!</div>');
			$("#uriLoadingFoundAuthor").popover('hide');
		});
	});
	
	$("#authorIconContainer").on('click', ".popover .foundAuthorities li a", function(event) {
		var authId = $(this).attr('data-authority-id');
		$("#uriAuthorLocalId").val(authId);
		$("#editorAuthorityUsed").html("Using stored authority entry <i>" + $(this).attr('data-authority-name') + "</i>.");
		$("#uriLoadingFoundEditor").popover('hide');
		event.preventDefault();
	});
	
	$("#editorIconContainer").on('click', ".popover #editorCreateAuthority", function() {
		var uri = $("#uriLoadingFoundEditor").attr('data-authority-uri');
		$.post("<c:url value="/auth/authority/create" />?${_csrf.parameterName}=${_csrf.token}&uri=" + uri, function(data) {
			$("#editorCreateAuthority").hide();
			$("#uriEditorLocalId").val(data['id']);
			$("#editorAuthorityUsed").html("Created new authority entry <i>" + data['name'] + "</i>.");
			$("#editorAuthorityCreationFeedback").html('<div class="text-success" style="margin-top:10px;">Authority entry has been created!</div>');
			$("#uriLoadingFoundEditor").popover('hide');
		});
	});
	
	$("#editorIconContainer").on('click', ".popover .foundAuthorities li a", function(event) {
		var authId = $(this).attr('data-authority-id');
		$("#uriEditorLocalId").val(authId);
		$("#editorAuthorityUsed").html("Using stored authority entry <i>" + $(this).attr('data-authority-name') + "</i>.");
		$("#uriLoadingFoundEditor").popover('hide');
		event.preventDefault();
	});
});

function savePersonDetails(personType){
	personType_lowerCase = personType.toLowerCase();
	var firstname = $("#firstName"+personType).val();
	var lastname = $("#lastName"+personType).val();
	var uri = $("#uri"+personType).val();
	var localAuthority = $("#uri"+personType+"LocalId").val();
	
	var personSpan = $("<span>");
	personSpan.attr("class", "label label-info "+personType_lowerCase +"-item");
	personSpan.attr("data-"+personType_lowerCase+"-firstname", firstname);
	personSpan.attr("data-"+personType_lowerCase+"-lastname", lastname);
	personSpan.attr("data-"+personType_lowerCase+"-uri", uri);
	personSpan.attr("data-"+personType_lowerCase+"-authority-id", localAuthority);
	
	var affiliationsList = [];
	var affSpan = $("<span>");
	$("#"+personType_lowerCase+"Affiliations").children().each(function(idx, elem) {
		var affSpan = $("<span>");
		var input = $(elem).find("input");
		affSpan.attr("data-affiliation-name", input.val());
		affiliationsList.push(input.val());
		personSpan.append(affSpan);
	});
	
	var affiliationString = "";
	if (affiliationsList) {
		affiliationString = " (" + affiliationsList.join(", ") + ")";
	}
	
	personSpan.append(lastname + ', ' + firstname + affiliationString + '&nbsp;&nbsp; ');
	var deleteIcon = $('<i class="fas fa-times remove-'+personType_lowerCase+'"></i>');
	deleteIcon.click(removePerson);
	personSpan.append(deleteIcon);
	$("#"+personType_lowerCase+"List").append(personSpan);
	$("#"+personType_lowerCase+"List").append("&nbsp;&nbsp; ");
	$("#"+personType_lowerCase+"Modal").modal('hide');
	resetPersonCreationModal(personType);
}
function constructPersonArray(arrayName){
		$('.'+arrayName+'-item').each(function(idx, person) {
		var personIdField = $("<input>");
		personIdField.attr("type", "hidden");
		personIdField.attr("id", arrayName+"s" + idx + ".id");
		personIdField.attr("name", arrayName+"s[" + idx + "].id");
		personIdField.attr("value", $(person).attr("data-"+arrayName+"-id"));
		$("#editForm").append(personIdField);
		
		var personFirstNameField = $("<input>");
		personFirstNameField.attr("type", "hidden");
		personFirstNameField.attr("id", arrayName+"s" + idx + ".firstName");
		personFirstNameField.attr("name", arrayName+"s[" + idx + "].firstName");
		personFirstNameField.attr("value", $(person).attr("data-"+arrayName+"-firstname"));
		$("#editForm").append(personFirstNameField);
		
		var personLastNameField = $("<input>");
		personLastNameField.attr("type", "hidden");
		personLastNameField.attr("id", arrayName+"s" + idx + ".lastName");
		personLastNameField.attr("name", arrayName+"s[" + idx + "].lastName");
		personLastNameField.attr("value", $(person).attr("data-"+arrayName+"-lastname"));
		$("#editForm").append(personLastNameField);
		
		var personUriField = $("<input>");
		personUriField.attr("type", "hidden");
		personUriField.attr("id", arrayName+"s" + idx + ".uri");
		personUriField.attr("name", arrayName+"s[" + idx + "].uri");
		personUriField.attr("value", $(person).attr("data-"+arrayName+"-uri"));
		$("#editForm").append(personUriField);
		
		var personAuthorityField = $("<input>");
		personAuthorityField.attr("type", "hidden");
		personAuthorityField.attr("id", arrayName+"s" + idx + ".localAuthorityId");
		personAuthorityField.attr("name", arrayName+"s[" + idx + "].localAuthorityId");
		personAuthorityField.attr("value", $(person).attr("data-"+arrayName+"-authority-id"));
		$("#editForm").append(personAuthorityField);
		
		$(person).children("span").each(function(idx2, affiliation) {
			var affiliationField = $("<input>");
			affiliationField.attr("type", "hidden");
			affiliationField.attr("id", arrayName+"s" + idx + ".affiliations" + idx2 + ".name");
			affiliationField.attr("name", arrayName+"s[" + idx + "].affiliations[" + idx2 + "].name");
			affiliationField.attr("value", $(affiliation).attr("data-affiliation-name"));
			$("#editForm").append(affiliationField);
			
			var affiliationIdField = $("<input>");
			affiliationIdField.attr("type", "hidden");
			affiliationIdField.attr("id", arrayName+"s" + idx + ".affiliations" + idx2 + ".id");
			affiliationIdField.attr("name", arrayName+"s[" + idx + "].affiliations[" + idx2 + "].id");
			affiliationIdField.attr("value", $(affiliation).attr("data-affiliation-id"));
			$("#editForm").append(affiliationIdField);
		});
	});
}

function resetPersonCreationModal(personType) {
	$("#firstName"+personType).val("");
	$("#lastName"+personType).val("");
	$("#"+personType+"AffiliationTemplate").find("input").val("");
	$("#uri"+personType).val("");
	resetPersonAuthorityCreation(personType);
}

function resetPersonAuthorityCreation(personType) {
	$("#uriLoadingFound"+personType).hide();
	$("#uriLoadingFailure"+personType).hide();
	$("#uriLoadingSpinner"+personType).hide();
	$("#uriLoadingFound"+personType).popover('hide');
	$("#uriLoadingFailure"+personType).popover('hide');
}

function getPersonAuthority(uri, personType) {
	$.get('<c:url value="/auth/authority/get?uri=" />' + uri + '&zoteroGroupId=' + ${zoteroGroupId}, function(data) {
		$("#uriLoadingFound"+personType).attr("data-authority-uri", data['uri']);
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
		$("#uriLoadingFound"+personType).attr("data-content", content);
		$("#uriLoadingFound"+personType).attr("data-authority-uri", uri);
		$("#uriLoadingFound"+personType).show();
		$("#uriLoadingFound"+personType).popover('show');
	})
	.fail(function() {
		$("#uriLoadingFailure"+personType).show();
	 })
    .always(function() {
    	$("#uriLoadingSpinner"+personType).hide();
	 });

}

let removePerson = function removePerson(e) {
	var deleteIcon = e.currentTarget;
	var person = $(deleteIcon).parent();
	person.remove();
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
	 <c:forEach items="${citation.editors}" var="editor" varStatus="status">
	 	  <strong>${editor.lastName}<c:if test="${not empty editor.firstName}">, ${editor.firstName}</c:if></strong><c:if test="${!status.last}">; </c:if>
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
<span id="editorList" style="font-size: 18px">
<c:forEach items="${citation.editors}" var="editor" varStatus="status">
<span class="label label-info editor-item" data-editor-id="${editor.id}" data-editor-firstname="${editor.firstName}" data-editor-lastname="${editor.lastName}" data-editor-uri="${editor.uri}" data-editor-authority-id="${editor.localAuthorityId}">
<c:forEach items="${editor.affiliations}" var="aff"> <span data-affiliation-name="${aff.name}" data-affiliation-id="${aff.id}"></span></c:forEach>
${editor.lastName}<c:if test="${not empty editor.firstName}">, ${editor.firstName}</c:if><c:forEach items="${editor.affiliations}" var="aff"> (${aff.name})</c:forEach>
&nbsp;&nbsp;
<i class="fas fa-times remove-editor"></i>
</span>
&nbsp;&nbsp;
</c:forEach>
</span>
<div class="pull-right"><a data-toggle="modal" data-target="#editorModal"><i class="fas fa-plus-circle"></i> Add Editor</a></div>
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
			    <div id="authorIconContainer" class="input-group-addon" style="min-width: 35px;">
			    	<i id="uriLoadingSpinnerAuthor" class="fas fa-spinner fa-spin text-info"></i>
			    	<i id="uriLoadingFoundAuthor" class="fas fa-info-circle text-success" data-toggle="popover" data-html="true" data-placement="right"></i>
			    	<i id="uriLoadingFailureAuthor" class="fas fa-exclamation-triangle text-danger" data-toggle="popover" data-html="true" data-placement="right" data-content="Could not find any data for this URI."></i>
			    </div>
			    <input type="hidden" id="uriAuthorLocalId" />
		    </div>
		    <div class="text-warning pull-right" id="authorAuthorityUsed"></div>
		  </div>
		  <div id="authorAffiliations">
		  <div id="authorAffiliationTemplate" class="form-group">
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

<!-- Editor Modal -->
<div class="modal fade" id="editorModal" tabindex="-1" role="dialog" aria-labelledby="editorLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="editorLabel">Enter Editor Information</h4>
      </div>
      <div class="modal-body">
          <div class="form-group">
		    <label for="firstNameEditor">First Name:</label>
		    <input type="text" class="form-control" id="firstNameEditor" placeholder="First Name">
		  </div>
		  <div class="form-group">
		    <label for="lastNameEditor">Last Name:</label>
		    <input type="text" class="form-control" id="lastNameEditor" placeholder="Last Name">
		  </div>
		  <div class="form-group">
		    <label for="uriEditor">URI:</label>
		    <div class="input-group">
			    <input type="text" class="form-control" id="uriEditor" placeholder="URI">
			    <div id="editorIconContainer" class="input-group-addon" style="min-width: 35px;">
			    	<i id="uriLoadingSpinnerEditor" class="fas fa-spinner fa-spin text-info"></i>
			    	<i id="uriLoadingFoundEditor" class="fas fa-info-circle text-success" data-toggle="popover" data-html="true" data-placement="right"></i>
			    	<i id="uriLoadingFailureEditor" class="fas fa-exclamation-triangle text-danger" data-toggle="popover" data-html="true" data-placement="right" data-content="Could not find any data for this URI."></i>
			    </div>
			    <input type="hidden" id="uriEditorLocalId" />
		    </div>
		    <div class="text-warning pull-right" id="editorAuthorityUsed"></div>
		  </div>
		  <div id="editorAffiliations">
		  <div id="editorAffiliationTemplate" class="form-group">
		    <label for="editorAffiliation">Affiliation:</label>
		    <input type="text" class="form-control" placeholder="Affiliation">
		  </div>
		  </div>
		  <div>
		  <div class="text-right"><a id="addEditorAffiliation"><i class="fas fa-plus-circle" title="Add another affiliation"></i> Add Affiliation</a></div>
      	  </div>
      </div>
      
      <div class="modal-footer">
        <button type="button" id="addEditorModalCancel" class="btn btn-default" data-dismiss="modal">Close</button>
        <button id="addEditorButton" type="button" class="btn btn-primary">Add Editor</button>
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
