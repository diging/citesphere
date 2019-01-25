<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script>
//@ sourceURL=submit.js
$(function() {
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
		
		var authorSpan = $("<span>");
		authorSpan.attr("class", "label label-primary author-item");
		authorSpan.attr("data-author-firstname", firstname);
		authorSpan.attr("data-author-lastname", lastname);
		
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
		$("#firstNameAuthor").val("");
		$("#lastNameAuthor").val("");
		$("#affiliationTemplate").find("input").val("");
	});
	
	$(".remove-author").click(removeAuthor);
	$(".remove-author").css('cursor', 'pointer');
	
	$("#addAffiliation").click(function() {
		var affiliationCopy = $("#affiliationTemplate").clone();
		affiliationCopy.removeAttr("id");
		affiliationCopy.find("input").val("");
		$("#affiliations").append(affiliationCopy);
	});
});

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


<div id="displayMessage"></div>

<c:if test="${not empty citation.key}" >
<c:url value="/auth/group/${zoteroGroupId}/items/${citation.key}/edit" var="processingUrl" />
</c:if>
<c:if test="${empty citation.key}" >
<c:url value="/auth/group/${zoteroGroupId}/items/create" var="processingUrl" />
</c:if>

<form:form action="${processingUrl}" modelAttribute="form" method="POST" id="editForm">
<table class="table table-striped">
<c:if test="${not empty citation.key}" >
<tr>
<td width="20%">Item Key</td>
<td>${citation.key}</td>
<form:hidden path="key" value="${citation.key}"/>
</tr>
</c:if>
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
<c:if test="${fn:contains(fields, 'title') }">
<tr>
<td width="20%">Title</td>
<td>
<form:input path="title" type="text" class="form-control" placeholder="Title" value="${not empty form.title ? form.title : citation.title}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'shortTitle') }">
<tr>
<td width="20%">Short Title</td>
<td><form:input path="shortTitle" type="text" class="form-control" placeholder="Short title" value="${citation.shortTitle}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'dateFreetext') }">
<tr>
<td width="20%">Date</td>
<td><form:input path="dateFreetext" type="text" class="form-control" placeholder="Date" value="${not empty form.dateFreetext ? form.dateFreetext : citation.dateFreetext}" /></td>
</tr>
</c:if>
<tr>
<td width="20%">Authors</td>
<td>
<span id="authorList" style="font-size: 18px">
<c:forEach items="${citation.authors}" var="author" varStatus="status">
<span class="label label-primary author-item" data-author-id="${author.id}" data-author-firstname="${author.firstName}" data-author-lastname="${author.lastName}">
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
<td width="20%">Editors</td>
<td>
<c:forEach items="${citation.editors}" var="editor" varStatus="status">
 ${editor.lastName}<c:if test="${not empty editor.firstName}">, ${editor.firstName}</c:if><c:if test="${!status.last}">; </c:if>
</c:forEach>
</td>
</tr>
<c:if test="${fn:contains(fields, 'publicationTitle') }">
<tr>
<td width="20%">Publication Title</td>
<td><form:input path="publicationTitle" type="text" class="form-control" placeholder="Publication Title" value="${not empty form.publicationTitle ? form.publicationTitle : citation.publicationTitle}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'journalAbbreviation') }">
<tr>
<td width="20%">Journal Abbreviation</td>
<td><form:input path="journalAbbreviation" type="text" class="form-control" placeholder="Journal Abbreviation" value="${not empty form.journalAbbreviation ? form.journalAbbreviation : citation.journalAbbreviation}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'volume') }">
<tr>
<td width="20%">Volume</td>
<td><form:input path="volume"  type="text" class="form-control" placeholder="Volume" value="${not empty form.volume ? form.volume : citation.volume}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'issue') }">
<tr>
<td width="20%">Issue</td>
<td><form:input path="issue" type="text" class="form-control" placeholder="Issue" value="${not empty form.issue ? form.issue : citation.issue}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'pages') }">
<tr>
<td width="20%">Pages</td>
<td><form:input path="pages" type="text" class="form-control" placeholder="Pages" value="${not empty form.pages ? form.pages : citation.pages}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'series') }">
<tr>
<td width="20%">Series</td>
<td><form:input path="series" type="text" class="form-control" placeholder="Series" value="${not empty form.series ? form.series : citation.series}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'seriesTitle') }">
<tr>
<td width="20%">Series Title</td>
<td><form:input path="seriesTitle" type="text" class="form-control" placeholder="Series Title" value="${not empty form.seriesTitle ? form.seriesTitle : citation.seriesTitle}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'seriesText') }">
<tr>
<td width="20%">Series Text</td>
<td><form:input path="seriesText" type="text" class="form-control" placeholder="Series Text" value="${not empty form.seriesText ? form.seriesText : citation.seriesText}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'url') }">
<tr>
<td width="20%">URL</td>
<td><form:input path="url" type="text" class="form-control" placeholder="Url" value="${not empty form.url ? form.url : citation.url}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'language') }">
<tr>
<td width="20%">Language</td>
<td><form:input path="language" type="text" class="form-control" placeholder="Language" value="${not empty form.language ? form.language : citation.language}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'doi') }">
<tr>
<td width="20%">DOI</td>
<td><form:input path="doi" type="text" class="form-control" placeholder="DOI" value="${not empty form.doi ? form.doi : citation.doi}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'issn') }">
<tr>
<td width="20%">ISSN</td>
<td><form:input path="issn" type="text" class="form-control" placeholder="ISSN" value="${not empty form.issn ? form.issn : citation.issn}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'archive') }">
<tr>
<td width="20%">Archive</td>
<td><form:input path="archive" type="text" class="form-control" placeholder="Archive" value="${not empty form.archive ? form.archive : citation.archive}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'archiveLocation') }">
<tr>
<td width="20%">Archive Location</td>
<td><form:input path="archiveLocation" type="text" class="form-control" placeholder="Archive Location" value="${not empty form.archiveLocation ? form.archiveLocation : citation.archiveLocation}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'libraryCatalog') }">
<tr>
<td width="20%">Library Catalog</td>
<td><form:input path="libraryCatalog" type="text" class="form-control" placeholder="Library Catalog" value="${not empty form.libraryCatalog ? form.libraryCatalog : citation.libraryCatalog}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'callNumber') }">
<tr>
<td width="20%">Call Number</td>
<td><form:input path="callNumber" type="text" class="form-control" placeholder="Call Number" value="${not empty form.callNumber ? form.callNumber : citation.callNumber}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'rights') }">
<tr>
<td width="20%">Rights</td>
<td><form:input path="rights" type="text" class="form-control" placeholder="Rights" value="${not empty form.rights ? form.rights : citation.rights}" /></td>
</tr>
</c:if>
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
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button id="addAuthorButton" type="button" class="btn btn-primary">Add Author</button>
      </div>
    </div>
  </div>
</div>
<script>
$(document).ready(function() {
	$('#items').on("change", function(e){
		var itemType = $('#items option:selected').val()
		$('#displayMessage').replaceWith("<div id='displayMessage' class='alert-info row'>"+
			"<span class='glyphicon glyphicon-refresh spinning'></span>"+
				"Loading fields... </div>");
		$('#displayMessage').show();
		$.ajax({
			url : '<c:url value="/auth/items/'+itemType+'/fields" />',
			type : 'GET',
			success: function(changedFields){
				$('form input').parent().closest('tr').hide();
				var i = 0;
				for(;i<changedFields.length;i++){
					$('form input#'+changedFields[i]).parent().closest('tr').show();
				}
				$('#displayMessage').hide();
			},
			error: function(){
				$('#displayMessage').replaceWith("<div id='errorDiv' class='alert-danger row'>" +
					"<i class='glyphicon glyphicon-remove-sign'></i>" +
						"Error loading the form fields</div>");
				$('#displayMessage').delay(2000).fadeOut();
			}
		});
	})
});
</script>
