<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="cite" uri="https://diging.asu.edu/jps/tlds/citesphere" %>

<script type="text/javascript">
$(function() {
	$("#conflictFormSubmit").click(function() {
		constructPersonArray("author", "author", 0);
        constructPersonArray("editor", "editor", 0);
        var creatorSubmitCount = 0;
        $(".creator-row").each(function(idx, elem){
            var ele = $(elem).children().first();
            if(!(ele.attr("id")=="editor" || ele.attr("id")=="author")) {
                var roleCount = constructPersonArray("creator", ele.attr("id"), creatorSubmitCount);
                creatorSubmitCount = creatorSubmitCount + roleCount;
            }
        });
	});
});

function constructPersonArray(arrayName, role, iter){
    var creator, otherCreatorCount = 0;
    var roleLC = role.toLowerCase();
    if(arrayName == "creator"){
        creator = "otherCreator";
    } else {
        creator = arrayName;
    }
    $('.'+roleLC+'-item').each(function(idx, person) {
        var creatorSubmitCount = idx + iter;
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
        otherCreatorCount += 1;
    });
    return otherCreatorCount;
}
/* function constructPersonArray(arrayName){
	$('.'+arrayName+'-item').each(function(idx, person) {
		
		var personIdField = $("<input>");
		personIdField.attr("type", "hidden");
		personIdField.attr("id", arrayName+"s" + idx + ".id");
		personIdField.attr("name", arrayName+"s[" + idx + "].id");
		personIdField.attr("value", $(person).attr("data-"+arrayName+"-id"));
		$("#conflictForm").append(personIdField);
		
		var personFirstNameField = $("<input>");
		personFirstNameField.attr("type", "hidden");
		personFirstNameField.attr("id", arrayName+"s" + idx + ".firstName");
		personFirstNameField.attr("name", arrayName+"s[" + idx + "].firstName");
		personFirstNameField.attr("value", $(person).attr("data-"+arrayName+"-firstname"));
		$("#conflictForm").append(personFirstNameField);
		
		var personLastNameField = $("<input>");
		personLastNameField.attr("type", "hidden");
		personLastNameField.attr("id", arrayName+"s" + idx + ".lastName");
		personLastNameField.attr("name", arrayName+"s[" + idx + "].lastName");
		personLastNameField.attr("value", $(person).attr("data-"+arrayName+"-lastname"));
		$("#conflictForm").append(personLastNameField);
		
		var personUriField = $("<input>");
		personUriField.attr("type", "hidden");
		personUriField.attr("id", arrayName+"s" + idx + ".uri");
		personUriField.attr("name", arrayName+"s[" + idx + "].uri");
		personUriField.attr("value", $(person).attr("data-"+arrayName+"-uri"));
		$("#conflictForm").append(personUriField);
		
		var personAuthorityField = $("<input>");
		personAuthorityField.attr("type", "hidden");
		personAuthorityField.attr("id", arrayName+"s" + idx + ".localAuthorityId");
		personAuthorityField.attr("name", arrayName+"s[" + idx + "].localAuthorityId");
		personAuthorityField.attr("value", $(person).attr("data-"+arrayName+"-authority-id"));
		$("#conflictForm").append(personAuthorityField);
		
		$(person).children("span").each(function(idx2, affiliation) {
			var affiliationField = $("<input>");
			affiliationField.attr("type", "hidden");
			affiliationField.attr("id", arrayName+"s" + idx + ".affiliations" + idx2 + ".name");
			affiliationField.attr("name", arrayName+"s[" + idx + "].affiliations[" + idx2 + "].name");
			affiliationField.attr("value", $(affiliation).attr("data-affiliation-name"));
			$("#conflictForm").append(affiliationField);
			
			var affiliationIdField = $("<input>");
			affiliationIdField.attr("type", "hidden");
			affiliationIdField.attr("id", arrayName+"s" + idx + ".affiliations" + idx2 + ".id");
			affiliationIdField.attr("name", arrayName+"s[" + idx + "].affiliations[" + idx2 + "].id");
			affiliationIdField.attr("value", $(affiliation).attr("data-affiliation-id"));
			$("#conflictForm").append(affiliationIdField);
		});
	});
} */
//# sourceURL=somename.js
</script>
<ol class="breadcrumb">
  <li><a href="<c:url value="/" />">Home</a></li>
  <li><a href="<c:url value="/auth/group/${zoteroGroupId}/items" />">Group</a></li>
  <li class="active">${outdatedCitation.key}</li>
</ol>

<div class="alert alert-warning" role="alert">
<b>Citation has changed!</b> The citation stored in Citesphere is different than the one in Zotero. 
If you proceed, Citesphere will first update the locally stored citation with the updated data retrieved from 
Zotero and then apply the changes you made. You will be able to review and change your edits before data is sent to Zotero. 
Do you want to proceed?
</div>

<h2>
	 <c:forEach items="${outdatedCitation.authors}" var="author" varStatus="status">
	 	  <strong>${author.lastName}<c:if test="${not empty author.firstName}">, ${author.firstName}</c:if></strong><c:if test="${!status.last}">; </c:if>
	 </c:forEach>
	  <em>${outdatedCitation.title}</em>
	  <c:if test="${not empty outdatedCitation.dateFreetext}">
 	  (${outdatedCitation.dateFreetext})
 	  </c:if> 
</h2>

<!--  current citation fields -->
<div class="col-md-6">
<h4>Data retrieved from Zotero (Version ${currentCitation.version})</h4>
<table class="table table-striped">
<tr>
<td width="20%">Item Key</td>
<td>${currentCitation.key}</td>
</tr>
<tr>
<td>Citation Type</td>
<td>
<spring:eval expression="@iconsResource.getProperty(currentCitation.itemType + '_icon')"  var="iconClass" />
<spring:eval expression="@iconsResource.getProperty(currentCitation.itemType + '_label')"  var="iconLabel" />	
<c:if test="${empty iconClass}">
<c:set var="iconClass" value="fas fa-file" />
</c:if>
<c:if test="${empty iconLabel}">
<c:set var="iconLabel" value="${entry.itemType}" />
</c:if>
<i class="${iconClass}" title="${iconLabel}"></i> ${iconLabel}
</td>
</tr>
<c:if test="${fn:contains(currentCitationFields, 'title') }">
<tr>
<td>Title</td>
<td>${currentCitation.title}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'shortTitle') }">
<tr>
<td>Short Title</td>
<td>${currentCitation.shortTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'date') }">
<tr>
<td>Date</td>
<td>${currentCitation.dateFreetext}</td>
</tr>
</c:if>
<tr>
<td>Authors</td>
<td>
<c:forEach items="${currentCitation.authors}" var="author" varStatus="status">
 ${author.lastName}<c:if test="${not empty author.firstName}">, ${author.firstName}</c:if><c:forEach items="${author.affiliations}" var="aff"> (${aff.name})</c:forEach><c:if test="${!status.last}">; </c:if>
</c:forEach>
</td>
</tr>
<tr>
<td>Editors</td>
<td>
<c:forEach items="${currentCitation.editors}" var="editor" varStatus="status">
 ${editor.lastName}<c:if test="${not empty editor.firstName}">, ${editor.firstName}</c:if><c:if test="${!status.last}">; </c:if>
</c:forEach>
</td>
</tr>
<c:forEach items="${currentCitation.otherCreatorRoles}" var="role">
<tr>
<td>
<spring:eval expression="@labelsResource.getProperty('_item_attribute_label_${role}', '${role}')" />
</td>
<td>
<cite:creators citation="${currentCitation}" role="${role}" var="creator">
 ${creator.person.lastName}<c:if test="${not empty creator.person.firstName}">, ${creator.person.firstName}</c:if>
<c:if test="${not empty creator.person.affiliations}">
 (<c:forEach items="${creator.person.affiliations}" varStatus="affStatus" var="aff">${aff.name}<c:if test="${!affStatus.last}">, </c:if></c:forEach>)<c:if test="${!status.last}">; </c:if>
 </c:if>
 <c:if test="${not empty creator.person.uri}">
 <a href="${creator.person.uri}" target="_blank"><i class="fas fa-link"></i></a>
 </c:if>
  <c:if test="${not empty creator.person.localAuthorityId}">
 <a href="<c:url value="/auth/authority/${creator.person.localAuthorityId}" />"><i class="fas fa-anchor"></i></a>
 </c:if><c:if test="${!lastIteration}">; </c:if>
</cite:creators>
</td>
</tr>
</c:forEach>
<c:if test="${fn:contains(currentCitationFields, 'publicationTitle') }">
<tr>
<td>Publication Title</td>
<td>${currentCitation.publicationTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'journalAbbreviation') }">
<tr>
<td>Journal Abbreviation</td>
<td>${currentCitation.journalAbbreviation}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'volume') }">
<tr>
<td>Volume</td>
<td>${currentCitation.volume}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'issue') }">
<tr>
<td>Issue</td>
<td>${currentCitation.issue}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'pages') }">
<tr>
<td>Pages</td>
<td>${currentCitation.pages}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'series') }">
<tr>
<td>Series</td>
<td>${currentCitation.series}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'seriesTitle') }">
<tr>
<td>Series Title</td>
<td >${currentCitation.seriesTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'seriesText') }">
<tr>
<td>Series Text</td>
<td>${currentCitation.seriesText}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'url') }">
<tr>
<td>URL</td>
<td><a href="${currentCitation.url}">${currentCitation.url}</a></td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'language') }">
<tr>
<td>Language</td>
<td>${currentCitation.language}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'doi') }">
<tr>
<td>DOI</td>
<td>${currentCitation.doi}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'issn') }">
<tr>
<td>ISSN</td>
<td>${currentCitation.issn}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'archive') }">
<tr>
<td>Archive</td>
<td>${currentCitation.archive}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'archiveLocation') }">
<tr>
<td>Archive Location</td>
<td>${currentCitation.archiveLocation}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'libraryCatalog') }">
<tr>
<td>Library Catalog</td>
<td>${currentCitation.libraryCatalog}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'callNumber') }">
<tr>
<td>Call Number</td>
<td>${currentCitation.callNumber}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'rights') }">
<tr>
<td>Rights</td>
<td>${currentCitation.rights}</td>
</tr>
</c:if>
</table>
</div>

<!-- Form fields -->
<div class="col-md-6">
<h4>Your edits</h4>

<table class="table table-striped">
<tr>
<td width="20%">Item Key</td>
<td>${form.key}</td>
</tr>
<tr>
<td>Citation Type</td>
<td>
<spring:eval expression="@iconsResource.getProperty(form.itemType + '_icon')"  var="iconClass" />
<spring:eval expression="@iconsResource.getProperty(form.itemType + '_label')"  var="iconLabel" />	
<c:if test="${empty iconClass}">
<c:set var="iconClass" value="fas fa-file" />
</c:if>
<c:if test="${empty iconLabel}">
<c:set var="iconLabel" value="${entry.itemType}" />
</c:if>
<i class="${iconClass}" title="${iconLabel}"></i> ${iconLabel}
</td>
</tr>
<c:if test="${fn:contains(formFields, 'title') }">
<tr  <c:if test="${form.title != currentCitation.title}" >class="changed"</c:if>>
<td>Title</td>
<td>${form.title}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'shortTitle') }">
<tr  <c:if test="${form.shortTitle != currentCitation.shortTitle}" >class="changed"</c:if>>
<td>Short Title</td>
<td>${form.shortTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'date') }">
<tr  <c:if test="${form.dateFreetext != currentCitation.dateFreetext}" >class="changed"</c:if>>
<td>Date</td>
<td>${form.dateFreetext}</td>
</tr>
</c:if>

<tr <c:if test = "${authorsChanged}">class="changed"</c:if>>
<td>Authors</td>
<td>
<span id="authorList">
<c:forEach items="${form.authors}" var="author" varStatus="status">
<span id="author${status.index}" class="author-item" data-author-id="${author.id}" data-author-firstname="${author.firstName}" data-author-lastname="${author.lastName}" 
data-author-uri="${author.uri}" data-author-authority-id="${author.localAuthorityId}">
 <c:forEach items="${author.affiliations}" var="aff"> <span data-affiliation-name="${aff.name}" data-affiliation-id="${aff.id}"></span> </c:forEach>
 ${author.lastName}<c:if test="${not empty author.firstName}">, ${author.firstName}</c:if><c:if test="${!status.last}">; </c:if>
 </span>
</c:forEach>
</span>
</td>
</tr>

<tr <c:if test = "${editorsChanged}">class="changed"</c:if>>
<td>Editors</td>
<td>
<span id="editorList">
<c:forEach items="${form.editors}" var="editor" varStatus="status">
 <!-- ${editor.lastName}<c:if test="${not empty editor.firstName}">, ${editor.firstName}</c:if><c:if test="${!status.last}">; </c:if>  -->
 <span id="editor${status.index}" class="editor-item" data-editor-id="${editor.id}" data-editor-firstname="${editor.firstName}" data-editor-lastname="${editor.lastName}" 
data-editor-uri="${editor.uri}" data-editor-authority-id="${editor.localAuthorityId}">
 <c:forEach items="${editor.affiliations}" var="aff"> <span data-affiliation-name="${aff.name}" data-affiliation-id="${aff.id}"></span> </c:forEach>
 ${editor.lastName}<c:if test="${not empty editor.firstName}">, ${editor.firstName}</c:if><c:if test="${!status.last}">; </c:if>
 </span>
</c:forEach>
</td>
</tr>
<%-- <c:forEach items="${currentCitation.otherCreatorRoles}" var="curCreator">
    <c:if test="${ (curCreator.value ne 'author') and (curCreator.value ne 'editor')}">
        <tr style="">
            <td class="creator" id="${fn:substringAfter(curCreator.key, '_item_attribute_label_')}" >${curCreator.value}
            </td>
            
            <td>
                <c:set var="role" value="${fn:toLowerCase(fn:substringAfter(curCreator.key, '_item_attribute_label_'))}" />
                <span id="${role}List" style="font-size: 18px">
                <cite:creators citation="${currentCitation}" role="${fn:substringAfter(curCreator.key, '_item_attribute_label_')}" var="creator">
                    <span id="${role}${varStatus}" class="label label-info ${role}-item" data-creator-id="${creator.id}" data-creator-type="${fn:substringAfter(curCreator.key, '_item_attribute_label_')}" data-creator-firstname="${creator.person.firstName}" data-creator-lastname="${creator.person.lastName}" data-creator-uri="${creator.person.uri}" data-creator-authority-id="${creator.person.localAuthorityId}">
                        <c:forEach items="${creator.person.affiliations}" var="aff"> <span data-affiliation-name="${aff.name}" data-affiliation-id="${aff.id}"></span></c:forEach>
                            ${creator.person.lastName}<c:if test="${not empty creator.person.firstName}">, ${creator.person.firstName}</c:if>
                            <c:forEach items="${creator.person.affiliations}" var="aff"> (${aff.name})</c:forEach>
                    </span>      
                </cite:creators>
                </span>
                
            </td>
        </tr>
    </c:if>
</c:forEach> --%>
<c:if test="${fn:contains(formFields, 'publicationTitle') }">
<tr  <c:if test="${form.publicationTitle != currentCitation.publicationTitle}" >class="changed"</c:if>>
<td>Publication Title</td>
<td>${form.publicationTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'journalAbbreviation') }">
<tr  <c:if test="${form.journalAbbreviation != currentCitation.journalAbbreviation}" >class="changed"</c:if>>
<td>Journal Abbreviation</td>
<td>${form.journalAbbreviation}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'volume') }">
<tr  <c:if test="${form.volume != currentCitation.volume}" >class="changed"</c:if>>
<td>Volume</td>
<td>${form.volume}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'issue') }">
<tr  <c:if test="${form.issue != currentCitation.issue}" >class="changed"</c:if>>
<td>Issue</td>
<td>${form.issue}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'pages') }">
<tr  <c:if test="${form.pages != currentCitation.pages}" >class="changed"</c:if>>
<td>Pages</td>
<td>${form.pages}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'series') }">
<tr  <c:if test="${form.series != currentCitation.series}" >class="changed"</c:if>>
<td>Series</td>
<td>${form.series}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'seriesTitle') }">
<tr <c:if test="${form.seriesTitle != currentCitation.seriesTitle}" >class="changed"</c:if>>
<td>Series Title</td>
<td >${form.seriesTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'seriesText') }">
<tr  <c:if test="${form.seriesText != currentCitation.seriesText}" >class="changed"</c:if>>
<td>Series Text</td>
<td>${form.seriesText}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'url') }">
<tr  <c:if test="${form.url != currentCitation.url}" >class="changed"</c:if>>
<td>URL</td>
<td><a href="${form.url}">${form.url}</a></td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'language') }">
<tr  <c:if test="${form.language != currentCitation.language}" >class="changed"</c:if>>
<td>Language</td>
<td>${form.language}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'doi') }">
<tr  <c:if test="${form.doi != currentCitation.doi}" >class="changed"</c:if>>
<td>DOI</td>
<td>${form.doi}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'issn') }">
<tr  <c:if test="${form.issn != currentCitation.issn}" >class="changed"</c:if>>
<td>ISSN</td>
<td>${form.issn}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'archive') }">
<tr  <c:if test="${form.archive != currentCitation.archive}" >class="changed"</c:if>>
<td>Archive</td>
<td>${form.archive}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'archiveLocation') }">
<tr  <c:if test="${form.archiveLocation != currentCitation.archiveLocation}" >class="changed"</c:if>>
<td>Archive Location</td>
<td>${form.archiveLocation}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'libraryCatalog') }">
<tr  <c:if test="${form.libraryCatalog != currentCitation.libraryCatalog}" >class="changed"</c:if>>
<td>Library Catalog</td>
<td>${form.libraryCatalog}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'callNumber') }">
<tr  <c:if test="${form.callNumber != currentCitation.callNumber}" >class="changed"</c:if>>
<td>Call Number</td>
<td>${form.callNumber}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'rights') }">
<tr  <c:if test="${form.rights != currentCitation.rights}" >class="changed"</c:if>>
<td>Rights</td>
<td>${form.rights}</td>
</tr>
</c:if>
</table>
</div>

<div class="col-md-12">
<c:url value="/auth/group/${zoteroGroupId}/items/${outdatedCitation.key}/conflict/resolve" var="editUrl" />
<form:form action="${editUrl}" modelAttribute="form" method="POST" id="conflictForm">
<form:hidden path="key" value="${form.key}" />
<form:hidden path="itemType" value="${form.itemType}" />
<form:hidden path="title" value="${form.title}" />
<form:hidden path="shortTitle" value="${form.shortTitle}" />
<form:hidden path="dateFreetext" value="${form.dateFreetext}" />
<form:hidden path="publicationTitle" value="${form.publicationTitle}" />
<form:hidden path="journalAbbreviation" value="${form.journalAbbreviation}" />
<form:hidden path="volume" value="${form.volume}" />
<form:hidden path="issue" value="${form.issue}" />
<form:hidden path="pages" value="${form.pages}" />
<form:hidden path="series" value="${form.series}" />
<form:hidden path="seriesTitle" value="${form.seriesTitle}" />
<form:hidden path="seriesText" value="${form.seriesText}" />
<form:hidden path="url" value="${form.url}" />
<form:hidden path="language" value="${form.language}" />
<form:hidden path="doi" value="${form.doi}" />
<form:hidden path="issn" value="${form.issn}" />
<form:hidden path="archive" value="${form.archive}" />
<form:hidden path="archiveLocation" value="${form.archiveLocation}" />
<form:hidden path="libraryCatalog" value="${form.libraryCatalog}" />
<form:hidden path="callNumber" value="${form.callNumber}" />
<form:hidden path="rights" value="${form.rights}" />

<button id="conflictFormSubmit" type="submit" class="btn btn-primary">Sounds good, proceed!</button>
<a href="<c:url value="/auth/group/${zoteroGroupId}/items/${outdatedCitation.key}" />" class="btn btn-default">No, cancel.</a>
</form:form>
</div>