<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cite" uri="https://diging.asu.edu/jps/tlds/citesphere" %>

<style>
.popover {
	min-width: 300px;
}
</style>
<script>
var creatorSubmitCount = 0;
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
	
	$("#uriLoadingSpinnerCreator").hide();
	$("#uriLoadingFailureCreator").hide();
	$("#uriLoadingFoundCreator").hide();
	
	$("#uriLoadingFoundCreator").popover();
	$("#uriLoadingFailureCreator").popover();
	
	$("#submitForm").click(function(e) {
		constructPersonArray("author", "author");
		creatorSubmitCount = 0;
		constructPersonArray("editor", "editor");
		creatorSubmitCount = 0;
		$(".creator").parent().closest("tr").each(function(idx, elem){
			if($(elem).css("display")!="none") {
				var ele = $(elem).children().first();
				if(!(ele.attr("id")=="editor" || ele.attr("id")=="author")) {
					console.log(ele.attr("id"));
					constructPersonArray("creator", ele.attr("id"));
				}
			}
		});
	});
	
	/* Handle Author events */
	$("#addAuthorButton").click(function() {
		savePersonDetails("Author", "Author");
	});
	
	$("#addAuthorModalCancel").click(function() {
		$("#authorModal").modal('hide');
		resetPersonCreationModal("Author");
	});
	
	$(".edit-author").click(function(){
		var authorItem = $(this).parent();
		editPerson('Author', authorItem[0]);
	});
	
	$(".edit-author").css('cursor', 'pointer');
	
	$(".remove-author").click(removePerson);
	$(".remove-author").css('cursor', 'pointer');
	
	$("#addAuthorAffiliation").click(function() {
		var affiliationCopy = $("#authorAffiliationTemplate").clone();
		affiliationCopy.removeAttr("id");
		affiliationCopy.addClass("aff-info");
		affiliationCopy.find("input").val("");
		affiliationCopy.show();
		$("#authorAffiliations").append(affiliationCopy);
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
		$("#authorAuthorityUsed").html("Using stored authority entry <i>" + $(this).attr('data-authority-name') + "</i>.");
		$("#uriLoadingFoundAuthor").popover('hide');
		event.preventDefault();
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
	
	/* Handle editor events */
	$(".edit-editor").click(function(){
		var editorItem = $(this).parent();
		editPerson('Editor', editorItem[0]);
	});
	
	$(".edit-editor").css('cursor', 'pointer');
	
	$("#addEditorButton").click(function() {
		savePersonDetails('Editor', 'Editor');
	});

	$("#addEditorModalCancel").click(function() {
		$("#editorModal").modal('hide');
		resetPersonCreationModal("Editor");
	});
	
	$(".remove-editor").click(removePerson);
	$(".remove-editor").css('cursor', 'pointer');
	
	$("#addEditorAffiliation").click(function() {
		var affiliationCopy = $("#editorAffiliationTemplate").clone();
		affiliationCopy.removeAttr("id");
		affiliationCopy.addClass("aff-info");
		affiliationCopy.find("input").val("");
		affiliationCopy.show();
		$("#editorAffiliations").append(affiliationCopy);
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
	
	/* Handle Other Creators events */
	$("#addCreatorButton").click(function(e) {
		var target = $(e.target);
		if(target.attr("data-creator-type")!=null) {			
			savePersonDetails(target.attr("data-creator-type"), "Creator");
		}
	});
	
	$(".creatorModalLink").click(function(e) {
		var target = $(e.target);
		var creatorType = target.attr("data-creator-type").charAt(0).toUpperCase() + target.attr("data-creator-type").slice(1);
		$("#creatorLabel").text("Enter "+creatorType+" Information");
		$("#addCreatorButton").text("Add "+creatorType);
		$("#addCreatorButton").attr("data-creator-type", creatorType);
	});
	
	$("#addCreatorModalCancel").click(function() {
		$("#creatorModal").modal('hide');
		resetPersonCreationModal("Creator");
	});
	
	$(".edit-creator").click(function(){
		var creatorItem = $(this).parent();
		editPerson('Creator', creatorItem[0]);
	});
	
	$(".edit-creator").css('cursor', 'pointer');
		
	$(".remove-creator").click(removePerson);
	$(".remove-creator").css('cursor', 'pointer');

	$("#addCreatorAffiliation").click(function() {
		var affiliationCopy = $("#creatorAffiliationTemplate").clone();
		affiliationCopy.removeAttr("id");
		affiliationCopy.addClass("aff-info");
		affiliationCopy.find("input").val("");
		affiliationCopy.show();
		$("#creatorAffiliations").append(affiliationCopy);
	});
	
	$("#uriCreator").change(function() {
		resetPersonAuthorityCreation("Creator");
		$("#uriLoadingSpinnerCreator").show();
		var uri = $("#uriCreator").val();
		clearTimeout(timer); 
	    timer = setTimeout(function() {
	    	getPersonAuthority(uri, "Creator");
	    }, 1000);
	});
	
	$("#creatorIconContainer").on('click', ".popover #creatorCreateAuthority", function() {
		var uri = $("#uriLoadingFoundCreator").attr('data-authority-uri');
		$.post("<c:url value="/auth/authority/create" />?${_csrf.parameterName}=${_csrf.token}&uri=" + uri, function(data) {
			$("#creatorCreateAuthority").hide();
			$("#uriCreatorLocalId").val(data['id']);
			$("#creatorAuthorityUsed").html("Created new authority entry <i>" + data['name'] + "</i>.");
			$("#creatorAuthorityCreationFeedback").html('<div class="text-success" style="margin-top:10px;">Authority entry has been created!</div>');
			$("#uriLoadingFoundCreator").popover('hide');
		});
	});
	
	$("#creatorIconContainer").on('click', ".popover .foundAuthorities li a", function(event) {
		var authId = $(this).attr('data-authority-id');
		$("#uriCreatorLocalId").val(authId);
		$("#creatorAuthorityUsed").html("Using stored authority entry <i>" + $(this).attr('data-authority-name') + "</i>.");
		$("#uriLoadingFoundCreator").popover('hide');
		event.preventDefault();
	});
});

/* Function to populate modal on edit */
function editPerson(modalName, item){
	var personItem = $(item);
	var modalNameLCase = modalName.toLowerCase();
	$("#firstName"+modalName).val(personItem.attr("data-"+modalNameLCase+"-firstname"));
	$("#lastName"+modalName).val(personItem.attr("data-"+modalNameLCase+"-lastname"));
	$("#uri"+modalName).val(personItem.attr("data-"+modalNameLCase+"-uri"));
	$("#id"+modalName).attr("data-"+modalNameLCase+"-id", personItem.attr("id"));
	
	personItem.children("span").each(function(idx, elem){
		var affInput = $("#"+modalNameLCase+"AffiliationTemplate").clone();
		affInput.removeAttr("id");
		affInput.addClass("aff-info");
		affInput.find("input").attr("data-affiliation-name", $(elem).data("affiliationName"));
		affInput.find("input").attr("data-affiliation-id", $(elem).data("affiliationId"));
		affInput.find("input").val($(elem).data("affiliationName"));
		$("#"+modalNameLCase+"Modal #"+modalNameLCase+"Affiliations").append(affInput);
	});
	
	if(personItem.children("span").length > 0) {
		$("#"+modalNameLCase+"AffiliationTemplate").hide();
	}
	
	$("#add"+modalName+"Button").text("Update "+modalName);
	
	$("#"+modalNameLCase+"Modal").modal('show');
}

/* Function to save information on closing modal */
function savePersonDetails(personType, modalName){
	var personTypeLCase = personType.toLowerCase();
	var modalNameLCase = modalName.toLowerCase();
	var personSpan;
	if($("#id"+modalName).attr("data-"+modalNameLCase+"-id") != null && $("#id"+modalName).attr("data-"+modalNameLCase+"-id").length > 0) {
		personSpan = $('#'+$("#id"+modalName).attr("data-"+modalNameLCase+"-id"));
	} else {
		var id = modalNameLCase+$(personTypeLCase+"List").length;
		personSpan = $('<span id='+id+'>');
	}
	
	if(personTypeLCase == "author") {
		personSpan.attr("class", "label label-primary "+personTypeLCase +"-item");
	} else {
		personSpan.attr("class", "label label-info "+personTypeLCase +"-item");
	}
	
	personSpan.html("");
	var firstname = $("#firstName"+modalName).val();
	var lastname = $("#lastName"+modalName).val();
	var uri = $("#uri"+modalName).val();
	var localAuthority = $("#"+modalName+"LocalId").val();
	
	personSpan.attr("data-"+modalNameLCase+"-firstname", firstname);
	personSpan.attr("data-"+modalNameLCase+"-lastname", lastname);
	personSpan.attr("data-"+modalNameLCase+"-uri", uri);
	personSpan.attr("data-"+modalNameLCase+"-authority-id", localAuthority);
	
	var affiliationsList = [];
	var affSpan = $("<span>");
	$("#"+modalNameLCase+"Affiliations").children().each(function(idx, elem) {
		var input = $(elem).find("input");
		if(input.val().length!=0){
			var affSpan = $("<span>");
			affSpan.attr("data-affiliation-name", input.val());
			affiliationsList.push(input.val());
			personSpan.append(affSpan);
		}
	});
	
	var affiliationString = "";
	if (affiliationsList.length != 0) {
		affiliationString = " (" + $.grep(affiliationsList, Boolean).join(", ") + ")";
	}
	
	personSpan.append(lastname + ', ' + firstname + affiliationString + '&nbsp;&nbsp; ');
	var editIcon = $('<i class="far fa-edit edit-'+modalNameLCase+'"></i>')
	var deleteIcon = $('<i class="fas fa-times remove-'+modalNameLCase+'"></i>');
	editIcon.click(function(){
		var personItem = $(this).parent();
		editPerson(modalName, personItem[0]);
	});
	deleteIcon.click(removePerson);
	personSpan.append(editIcon);
	personSpan.append(deleteIcon);
	$("#"+personTypeLCase+"List").append(personSpan);
	$("#"+personTypeLCase+"List").append("&nbsp;&nbsp; ");
	$("#"+modalNameLCase+"Modal").modal('hide');
	resetPersonCreationModal(modalName);
}


/* Function to append final information for form submission */
function constructPersonArray(arrayName, role){
		var creator;
		var roleLC = role.toLowerCase();
		if(arrayName == "creator"){
			creator = "otherCreator";
		} else {
			creator = arrayName;
		}
		$('.'+roleLC+'-item').each(function(idx, person) {
		var personIdField = $("<input>");
		personIdField.attr("type", "hidden");
		personIdField.attr("id", creator+"s" + creatorSubmitCount + ".id");
		personIdField.attr("name", creator+"s[" + creatorSubmitCount + "].id");
		personIdField.attr("value", $(person).attr("data-"+arrayName+"-id"));
		$("#editForm").append(personIdField);
		
		var personFirstNameField = $("<input>");
		personFirstNameField.attr("type", "hidden");
		personFirstNameField.attr("id", creator+"s" + creatorSubmitCount + ".firstName");
		personFirstNameField.attr("name", creator+"s[" + creatorSubmitCount + "].firstName");
		personFirstNameField.attr("value", $(person).attr("data-"+arrayName+"-firstname"));
		$("#editForm").append(personFirstNameField);
		
		var personLastNameField = $("<input>");
		personLastNameField.attr("type", "hidden");
		personLastNameField.attr("id", creator+"s" + creatorSubmitCount + ".lastName");
		personLastNameField.attr("name", creator+"s[" + creatorSubmitCount + "].lastName");
		personLastNameField.attr("value", $(person).attr("data-"+arrayName+"-lastname"));
		$("#editForm").append(personLastNameField);
		
		var personRoleField = $("<input>");
		personRoleField.attr("type", "hidden");
		personRoleField.attr("id", creator+"s" + creatorSubmitCount + ".role");
		personRoleField.attr("name", creator+"s[" + creatorSubmitCount + "].role");
		personRoleField.attr("value", role);
		$("#editForm").append(personRoleField);
		
		var personUriField = $("<input>");
		personUriField.attr("type", "hidden");
		personUriField.attr("id", creator+"s" + creatorSubmitCount + ".uri");
		personUriField.attr("name", creator+"s[" + creatorSubmitCount + "].uri");
		personUriField.attr("value", $(person).attr("data-"+arrayName+"-uri"));
		$("#editForm").append(personUriField);
		
		var personAuthorityField = $("<input>");
		personAuthorityField.attr("type", "hidden");
		personAuthorityField.attr("id", creator+"s" + creatorSubmitCount + ".localAuthorityId");
		personAuthorityField.attr("name", creator+"s[" + creatorSubmitCount + "].localAuthorityId");
		personAuthorityField.attr("value", $(person).attr("data-"+arrayName+"-authority-id"));
		$("#editForm").append(personAuthorityField);
		
		$(person).children("span").each(function(idx2, affiliation) {
			var affiliationField = $("<input>");
			affiliationField.attr("type", "hidden");
			affiliationField.attr("id", creator+"s" + creatorSubmitCount + ".affiliations" + idx2 + ".name");
			affiliationField.attr("name", creator+"s[" + creatorSubmitCount + "].affiliations[" + idx2 + "].name");
			affiliationField.attr("value", $(affiliation).attr("data-affiliation-name"));
			$("#editForm").append(affiliationField);
			
			var affiliationIdField = $("<input>");
			affiliationIdField.attr("type", "hidden");
			affiliationIdField.attr("id", creator+"s" + creatorSubmitCount + ".affiliations" + idx2 + ".id");
			affiliationIdField.attr("name", creator+"s[" + creatorSubmitCount + "].affiliations[" + idx2 + "].id");
			affiliationIdField.attr("value", $(affiliation).attr("data-affiliation-id"));
			$("#editForm").append(affiliationIdField);
		});
		creatorSubmitCount = creatorSubmitCount + 1;
	});
}

function resetPersonCreationModal(modalType) {
	var modalNameLCase = modalType.toLowerCase();
	$("#firstName"+modalType).val("");
	$("#lastName"+modalType).val("");
	$("#id"+modalType).val("");
	$("#id"+modalType).attr("data-"+modalType+"-id", "");
	var affTemplate = $("#"+modalNameLCase+"AffiliationTemplate");
	$("#"+modalNameLCase+"Affiliations").children().remove();
	$("#"+modalNameLCase+"Affiliations").append(affTemplate);
	$("#"+modalNameLCase+"AffiliationTemplate").find("input").val("");
	$("#"+modalNameLCase+"AffiliationTemplate").show();
	$(".aff-info").remove();
	$("#uri"+modalType).val("");
	$("#add"+modalType+"Button").text("Add "+modalType);
	resetPersonAuthorityCreation(modalType);
}

function resetPersonAuthorityCreation(personType) {
	$("#uriLoadingFound"+personType).hide();
	$("#uriLoadingFailure"+personType).hide();
	$("#uriLoadingSpinner"+personType).hide();
	$("#uriLoadingFound"+personType).popover('hide');
	$("#uriLoadingFailure"+personType).popover('hide');
	$("#"+personType.toLowerCase()+"AuthorityUsed").html("");
	$("#"+personType.toLowerCase()+"AuthorityCreationFeedback").html("");
}

function getPersonAuthority(uri, personType) {
	personType_lowerCase = personType.toLowerCase();
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
					'<span id="'+personType_lowerCase+'AuthorityCreationFeedback"><button id="'+personType_lowerCase+'CreateAuthority" type="submit" class="btn btn-link pull-right"><b>Yes, create a new entry!</b></button></span>';
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
<td class="creator" id="author">Authors</td>
<td>
<span id="authorList" style="font-size: 18px">
<c:forEach items="${citation.authors}" var="author" varStatus="status">
<span id="author${status.index}" class="label label-primary author-item" data-author-id="${author.id}" data-author-firstname="${author.firstName}" data-author-lastname="${author.lastName}" data-author-uri="${author.uri}" data-author-authority-id="${author.localAuthorityId}">
<c:forEach items="${author.affiliations}" var="aff"> <span data-affiliation-name="${aff.name}" data-affiliation-id="${aff.id}"></span></c:forEach>
${author.lastName}<c:if test="${not empty author.firstName}">, ${author.firstName}</c:if><c:forEach items="${author.affiliations}" var="aff"><c:if test="${not empty aff.name}"> (${aff.name})</c:if></c:forEach>
&nbsp;
<i class="far fa-edit edit-author"></i>
&nbsp;
<i class="fas fa-times remove-author"></i>
</span>
&nbsp;&nbsp;
</c:forEach>
</span>
<div class="pull-right"><a data-toggle="modal" data-target="#authorModal"><i class="fas fa-plus-circle"></i> Add Author</a></div>
</td>
</tr>
<tr>
<td class="creator" id="editor">Editors</td>
<td>
<span id="editorList" style="font-size: 18px">
<c:forEach items="${citation.editors}" var="editor" varStatus="status">
<span id="editor${status.index}" class="label label-info editor-item" data-editor-id="${editor.id}" data-editor-firstname="${editor.firstName}" data-editor-lastname="${editor.lastName}" data-editor-uri="${editor.uri}" data-editor-authority-id="${editor.localAuthorityId}">
<c:forEach items="${editor.affiliations}" var="aff"> <span data-affiliation-name="${aff.name}" data-affiliation-id="${aff.id}"></span></c:forEach>
${editor.lastName}<c:if test="${not empty editor.firstName}">, ${editor.firstName}</c:if><c:forEach items="${editor.affiliations}" var="aff"> (${aff.name})</c:forEach>
&nbsp;
<i class="far fa-edit edit-editor"></i>
&nbsp;
<i class="fas fa-times remove-editor"></i>
</span>
&nbsp;&nbsp;
</c:forEach>
</span>
<div class="pull-right"><a data-toggle="modal" data-target="#editorModal"><i class="fas fa-plus-circle"></i> Add Editor</a></div>
</td>
</tr>

<c:forEach items="${creatorMap}" var="curCreator">
	<c:if test="${ (curCreator.value ne 'author') and (curCreator.value ne 'editor')}">
		<tr style="display:none;">
			<td class="creator" id="${fn:substringAfter(curCreator.key, '_item_attribute_label_')}" >${curCreator.value}
			</td>
			
			<td>
				<c:set var="role" value="${fn:toLowerCase(fn:substringAfter(curCreator.key, '_item_attribute_label_'))}" />
				<span id="${role}List" style="font-size: 18px">
				<cite:creators citation="${citation}" role="${fn:substringAfter(curCreator.key, '_item_attribute_label_')}" var="creator">
				 	<span id="creator${fn:substringAfter(curCreator.key, '_item_attribute_label_')}${varStatus}" class="label label-info ${fn:substringAfter(curCreator.key, '_item_attribute_label_')}-item" data-creator-id="${creator.id}" data-creator-firstname="${creator.person.firstName}" data-creator-lastname="${creator.person.lastName}" data-creator-uri="${creator.person.uri}" data-creator-authority-id="${creator.person.localAuthorityId}">
				 	${creator.person.lastName}<c:if test="${not empty creator.person.firstName}">, ${creator.person.firstName}</c:if>
						<i class="far fa-edit edit-creator"></i>
						<i class="fas fa-times remove-creator"></i>
						</span>
						&nbsp;&nbsp;
				</cite:creators>
				</span>
				<div class="pull-right"><a class="creatorModalLink" data-toggle="modal" data-creator-type="${fn:substringAfter(curCreator.key, '_item_attribute_label_')}" data-target="#creatorModal"><i class="fas fa-plus-circle"></i> Add ${curCreator.value}</a></div>
			</td>
		</tr>
	</c:if>
</c:forEach>

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
      	  	<input type="hidden" class="form-control" id="idAuthor">
      	  </div>
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
		  <div class="text-right"><a id="addAuthorAffiliation"><i class="fas fa-plus-circle" title="Add another affiliation"></i> Add Affiliation</a></div>
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
      	  	<input type="hidden" class="form-control" id="idEditor">
      	  </div>
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

<!-- Other Creator Modal -->
<div class="modal fade" id="creatorModal" tabindex="-1" role="dialog" aria-labelledby="creatorLabel">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="creatorLabel">Enter Creator Information</h4>
      </div>
      <div class="modal-body">
      	  <div class="form-group">
      	  	<input type="hidden" class="form-control" id="idCreator">
      	  </div>
          <div class="form-group">
		    <label for="firstNameCreator">First Name:</label>
		    <input type="text" class="form-control" id="firstNameCreator" placeholder="First Name">
		  </div>
		  <div class="form-group">
		    <label for="lastNameCreator">Last Name:</label>
		    <input type="text" class="form-control" id="lastNameCreator" placeholder="Last Name">
		  </div>
		  <div class="form-group">
		    <label for="uriCreator">URI:</label>
		    <div class="input-group">
			    <input type="text" class="form-control" id="uriCreator" placeholder="URI">
			    <div id="creatorIconContainer" class="input-group-addon" style="min-width: 35px;">
			    	<i id="uriLoadingSpinnerCreator" class="fas fa-spinner fa-spin text-info"></i>
			    	<i id="uriLoadingFoundCreator" class="fas fa-info-circle text-success" data-toggle="popover" data-html="true" data-placement="right"></i>
			    	<i id="uriLoadingFailureCreator" class="fas fa-exclamation-triangle text-danger" data-toggle="popover" data-html="true" data-placement="right" data-content="Could not find any data for this URI."></i>
			    </div>
			    <input type="hidden" id="uriCreatorLocalId" />
		    </div>
		    <div class="text-warning pull-right" id="creatorAuthorityUsed"></div>
		  </div>
		  <div id="creatorAffiliations">
		  <div id="creatorAffiliationTemplate" class="form-group">
		    <label for="affiliationCreator">Affiliation:</label>
		    <input type="text" class="form-control" placeholder="Affiliation">
		  </div>
		  </div>
		  <div>
		  <div class="text-right"><a id="addCreatorAffiliation"><i class="fas fa-plus-circle" title="Add another affiliation"></i> Add Affiliation</a></div>
      	  </div>
      </div>
      
      <div class="modal-footer">
        <button type="button" class="btn btn-default" id="addCreatorModalCancel">Close</button>
        <button id="addCreatorButton" type="button" class="btn btn-primary">Add Creator</button>
      </div>
    </div>
  </div>
</div>
<script>
//# sourceURL=fields.js
$(document).ready(function() {
	<c:if test="${empty citation}">
		$("#items").val("${defaultItemType}");
	</c:if>
	loadFields();
	$('#items').on("change", function(e){
		loadFields();
	});
	
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
	$.ajax({
		url : '<c:url value="/auth/items/'+itemType+'/creators" />',
		type : 'GET',
		success: function(creators){
			$('.creator').each(function(idx, elem) {
				var tr = $(elem).parent().closest('tr');
				tr.hide();
			});
			for(i=0;i<creators.length;i++){
				if($('[id='+creators[i]).length > 0) {
					$('[id='+creators[i]).parent().closest('tr').show();
				} 
				else if(creators[i]!= 'editor' && creators[i]!= 'author'){
					var creatorRow = $("<tr>");
					creatorRow.css("display", "table-row");
					var creatorLabel = $("<td>");
					creatorLabel.attr("class", "creator");
					creatorLabel.css("text-transform", "capitalize");
					creatorLabel.attr("id", creators[i]);
					creatorLabel.append(creators[i]);
					creatorRow.append(creatorLabel);
					var creatorData = $("<td>");
					var creatorList = $("<span>");
					creatorList.attr("id",creators[i]+"List");
					creatorList.css("font-size", "18px");
					creatorData.append(creatorList);
					var addIconDiv = $("<div>");
					addIconDiv.attr("class","pull-right");
					var iconLink = $("<a>");
					
					iconLink.attr("data-toggle","modal");
					iconLink.attr("data-creator-type", creators[i]);
					iconLink.attr("data-target","#creatorModal");
					var iconImg = $("<i>");
					iconImg.attr("class", "fas fa-plus-circle");
					iconLink.append(iconImg);
					iconLink.append("Add "+creators[i]);
					addIconDiv.append(iconLink);
					creatorData.append(addIconDiv);
					creatorRow.append(creatorData);
					creatorRow.insertAfter($('.creator').last().parent());	

					$("#creatorLabel").css("text-transform", "capitalize");
					$("#creatorLabel").text("Enter "+creators[i]+" Information");
					$("#addCreatorButton").css("text-transform", "capitalize");
					$("#addCreatorButton").text("Add "+creators[i]);
					$("#addCreatorButton").attr("data-creator-type", creators[i]);
					iconLink.click(function(e) {
						creatorLinkHandler($(e.target));
					});
				}
			}
		},
		error: function(){
			$("#displayMessage").html("<i class='glyphicon glyphicon-remove-sign'></i>" +
			"Error loading the creators. Try again later.");
			$('#messageModal').modal('show');
			setTimeout(function() {
				$('#messageModal').modal('hide');
		  	}, 3000);
			
		}
	});
}
function creatorLinkHandler(target) {
	var creatorType = target.attr("data-creator-type").charAt(0).toUpperCase() + target.attr("data-creator-type").slice(1);
	$("#creatorLabel").text("Enter "+creatorType+" Information");
	$("#addCreatorButton").text("Add "+creatorType);
	$("#addCreatorButton").attr("data-creator-type", creatorType);
}
</script>