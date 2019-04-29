<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

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

<!--  currently stored citation  -->
<!--  <div class="col-md-4">
<h4>Currently stored data (Version ${outdatedCitation.version})</h4>
<table class="table table-striped">
<tr>
<td width="20%">Item Key</td>
<td>${outdatedCitation.key}</td>
</tr>
<tr>
<td>Citation Type</td>
<td>
<spring:eval expression="@iconsResource.getProperty(outdatedCitation.itemType + '_icon')"  var="iconClass" />
<spring:eval expression="@iconsResource.getProperty(outdatedCitation.itemType + '_label')"  var="iconLabel" />	
<c:if test="${empty iconClass}">
<c:set var="iconClass" value="fas fa-file" />
</c:if>
<c:if test="${empty iconLabel}">
<c:set var="iconLabel" value="${entry.itemType}" />
</c:if>
<i class="${iconClass}" title="${iconLabel}"></i> ${iconLabel}
</td>
</tr>
<c:if test="${fn:contains(outdatedCitationFields, 'title') }">
<tr>
<td>Title</td>
<td>${outdatedCitation.title}</td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'shortTitle') }">
<tr>
<td>Short Title</td>
<td>${outdatedCitation.shortTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'date') }">
<tr>
<td>Date</td>
<td>${outdatedCitation.dateFreetext}</td>
</tr>
</c:if>
<tr>
<td>Authors</td>
<td>
<c:forEach items="${outdatedCitation.authors}" var="author" varStatus="status">
 ${author.lastName}<c:if test="${not empty author.firstName}">, ${author.firstName}</c:if><c:forEach items="${author.affiliations}" var="aff"> (${aff.name})</c:forEach><c:if test="${!status.last}">; </c:if>
</c:forEach>
</td>
</tr>
<tr>
<td>Editors</td>
<td>
<c:forEach items="${outdatedCitation.editors}" var="editor" varStatus="status">
 ${editor.lastName}<c:if test="${not empty editor.firstName}">, ${editor.firstName}</c:if><c:if test="${!status.last}">; </c:if>
</c:forEach>
</td>
</tr>
<c:if test="${fn:contains(outdatedCitationFields, 'publicationTitle') }">
<tr>
<td>Publication Title</td>
<td>${outdatedCitation.publicationTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'journalAbbreviation') }">
<tr>
<td>Journal Abbreviation</td>
<td>${outdatedCitation.journalAbbreviation}</td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'volume') }">
<tr>
<td>Volume</td>
<td>${outdatedCitation.volume}</td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'issue') }">
<tr>
<td>Issue</td>
<td>${outdatedCitation.issue}</td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'pages') }">
<tr>
<td>Pages</td>
<td>${outdatedCitation.pages}</td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'series') }">
<tr>
<td>Series</td>
<td>${outdatedCitation.series}</td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'seriesTitle') }">
<tr>
<td>Series Title</td>
<td>${outdatedCitation.seriesTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'seriesText') }">
<tr>
<td>Series Text</td>
<td>${outdatedCitation.seriesText}</td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'url') }">
<tr>
<td>URL</td>
<td><a href="${outdatedCitation.url}">${outdatedCitation.url}</a></td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'language') }">
<tr>
<td>Language</td>
<td>${outdatedCitation.language}</td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'doi') }">
<tr>
<td>DOI</td>
<td>${outdatedCitation.doi}</td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'issn') }">
<tr>
<td>ISSN</td>
<td>${outdatedCitation.issn}</td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'archive') }">
<tr>
<td>Archive</td>
<td>${outdatedCitation.archive}</td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'archiveLocation') }">
<tr>
<td>Archive Location</td>
<td>${outdatedCitation.archiveLocation}</td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'libraryCatalog') }">
<tr>
<td>Library Catalog</td>
<td>${outdatedCitation.libraryCatalog}</td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'callNumber') }">
<tr>
<td>Call Number</td>
<td>${outdatedCitation.callNumber}</td>
</tr>
</c:if>
<c:if test="${fn:contains(outdatedCitationFields, 'rights') }">
<tr>
<td>Rights</td>
<td>${outdatedCitation.rights}</td>
</tr>
</c:if>
</table>
</div> -->

<!--  current citation fields -->
<div class="col-md-4">
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
<tr  <c:if test="${currentCitation.title != outdatedCitation.title}" >class="changed"</c:if>>
<td>Title</td>
<td>${currentCitation.title}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'shortTitle') }">
<tr  <c:if test="${currentCitation.shortTitle != outdatedCitation.shortTitle}" >class="changed"</c:if>>
<td>Short Title</td>
<td>${currentCitation.shortTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'date') }">
<tr  <c:if test="${currentCitation.dateFreetext != outdatedCitation.dateFreetext}" >class="changed"</c:if>>
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
<c:if test="${fn:contains(currentCitationFields, 'publicationTitle') }">
<tr  <c:if test="${currentCitation.publicationTitle != outdatedCitation.publicationTitle}" >class="changed"</c:if>>
<td>Publication Title</td>
<td>${currentCitation.publicationTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'journalAbbreviation') }">
<tr  <c:if test="${currentCitation.journalAbbreviation != outdatedCitation.journalAbbreviation}" >class="changed"</c:if>>
<td>Journal Abbreviation</td>
<td>${currentCitation.journalAbbreviation}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'volume') }">
<tr  <c:if test="${currentCitation.volume != outdatedCitation.volume}" >class="changed"</c:if>>
<td>Volume</td>
<td>${currentCitation.volume}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'issue') }">
<tr  <c:if test="${currentCitation.issue != outdatedCitation.issue}" >class="changed"</c:if>>
<td>Issue</td>
<td>${currentCitation.issue}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'pages') }">
<tr  <c:if test="${currentCitation.pages != outdatedCitation.pages}" >class="changed"</c:if>>
<td>Pages</td>
<td>${currentCitation.pages}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'series') }">
<tr  <c:if test="${currentCitation.series != outdatedCitation.series}" >class="changed"</c:if>>
<td>Series</td>
<td>${currentCitation.series}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'seriesTitle') }">
<tr <c:if test="${currentCitation.seriesTitle != outdatedCitation.seriesTitle}" >class="changed"</c:if>>
<td>Series Title</td>
<td >${currentCitation.seriesTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'seriesText') }">
<tr  <c:if test="${currentCitation.seriesText != outdatedCitation.seriesText}" >class="changed"</c:if>>
<td>Series Text</td>
<td>${currentCitation.seriesText}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'url') }">
<tr  <c:if test="${currentCitation.url != outdatedCitation.url}" >class="changed"</c:if>>
<td>URL</td>
<td><a href="${currentCitation.url}">${currentCitation.url}</a></td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'language') }">
<tr  <c:if test="${currentCitation.language != outdatedCitation.language}" >class="changed"</c:if>>
<td>Language</td>
<td>${currentCitation.language}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'doi') }">
<tr  <c:if test="${currentCitation.doi != outdatedCitation.doi}" >class="changed"</c:if>>
<td>DOI</td>
<td>${currentCitation.doi}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'issn') }">
<tr  <c:if test="${currentCitation.issn != outdatedCitation.issn}" >class="changed"</c:if>>
<td>ISSN</td>
<td>${currentCitation.issn}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'archive') }">
<tr  <c:if test="${currentCitation.archive != outdatedCitation.archive}" >class="changed"</c:if>>
<td>Archive</td>
<td>${currentCitation.archive}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'archiveLocation') }">
<tr  <c:if test="${currentCitation.archiveLocation != outdatedCitation.archiveLocation}" >class="changed"</c:if>>
<td>Archive Location</td>
<td>${currentCitation.archiveLocation}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'libraryCatalog') }">
<tr  <c:if test="${currentCitation.libraryCatalog != outdatedCitation.libraryCatalog}" >class="changed"</c:if>>
<td>Library Catalog</td>
<td>${currentCitation.libraryCatalog}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'callNumber') }">
<tr  <c:if test="${currentCitation.callNumber != outdatedCitation.callNumber}" >class="changed"</c:if>>
<td>Call Number</td>
<td>${currentCitation.callNumber}</td>
</tr>
</c:if>
<c:if test="${fn:contains(currentCitationFields, 'rights') }">
<tr  <c:if test="${currentCitation.rights != outdatedCitation.rights}" >class="changed"</c:if>>
<td>Rights</td>
<td>${currentCitation.rights}</td>
</tr>
</c:if>
</table>
</div>

<!-- Form fields -->
<div class="col-md-4">
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
<tr  <c:if test="${form.title != outdatedCitation.title}" >class="changed"</c:if>>
<td>Title</td>
<td>${form.title}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'shortTitle') }">
<tr  <c:if test="${form.shortTitle != outdatedCitation.shortTitle}" >class="changed"</c:if>>
<td>Short Title</td>
<td>${form.shortTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'date') }">
<tr  <c:if test="${form.dateFreetext != outdatedCitation.dateFreetext}" >class="changed"</c:if>>
<td>Date</td>
<td>${form.dateFreetext}</td>
</tr>
</c:if>

<tr <c:if test = "${authorsChanged}">class="changed"</c:if>>
<td>Authors</td>
<td>
<c:forEach items="${form.authors}" var="author" varStatus="status">
 ${author.lastName}<c:if test="${not empty author.firstName}">, ${author.firstName}</c:if><c:forEach items="${author.affiliations}" var="aff"> (${aff.name})</c:forEach><c:if test="${!status.last}">; </c:if>
</c:forEach>
</td>
</tr>

<tr <c:if test = "${editorsChanged}">class="changed"</c:if>>
<td>Editors</td>
<td>
<c:forEach items="${form.editors}" var="editor" varStatus="status">
 ${editor.lastName}<c:if test="${not empty editor.firstName}">, ${editor.firstName}</c:if><c:if test="${!status.last}">; </c:if>
</c:forEach>
</td>
</tr>
<c:if test="${fn:contains(formFields, 'publicationTitle') }">
<tr  <c:if test="${form.publicationTitle != outdatedCitation.publicationTitle}" >class="changed"</c:if>>
<td>Publication Title</td>
<td>${form.publicationTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'journalAbbreviation') }">
<tr  <c:if test="${form.journalAbbreviation != outdatedCitation.journalAbbreviation}" >class="changed"</c:if>>
<td>Journal Abbreviation</td>
<td>${form.journalAbbreviation}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'volume') }">
<tr  <c:if test="${form.volume != outdatedCitation.volume}" >class="changed"</c:if>>
<td>Volume</td>
<td>${form.volume}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'issue') }">
<tr  <c:if test="${form.issue != outdatedCitation.issue}" >class="changed"</c:if>>
<td>Issue</td>
<td>${form.issue}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'pages') }">
<tr  <c:if test="${form.pages != outdatedCitation.pages}" >class="changed"</c:if>>
<td>Pages</td>
<td>${form.pages}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'series') }">
<tr  <c:if test="${form.series != outdatedCitation.series}" >class="changed"</c:if>>
<td>Series</td>
<td>${form.series}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'seriesTitle') }">
<tr <c:if test="${form.seriesTitle != outdatedCitation.seriesTitle}" >class="changed"</c:if>>
<td>Series Title</td>
<td >${form.seriesTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'seriesText') }">
<tr  <c:if test="${form.seriesText != outdatedCitation.seriesText}" >class="changed"</c:if>>
<td>Series Text</td>
<td>${form.seriesText}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'url') }">
<tr  <c:if test="${form.url != outdatedCitation.url}" >class="changed"</c:if>>
<td>URL</td>
<td><a href="${form.url}">${form.url}</a></td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'language') }">
<tr  <c:if test="${form.language != outdatedCitation.language}" >class="changed"</c:if>>
<td>Language</td>
<td>${form.language}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'doi') }">
<tr  <c:if test="${form.doi != outdatedCitation.doi}" >class="changed"</c:if>>
<td>DOI</td>
<td>${form.doi}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'issn') }">
<tr  <c:if test="${form.issn != outdatedCitation.issn}" >class="changed"</c:if>>
<td>ISSN</td>
<td>${form.issn}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'archive') }">
<tr  <c:if test="${form.archive != outdatedCitation.archive}" >class="changed"</c:if>>
<td>Archive</td>
<td>${form.archive}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'archiveLocation') }">
<tr  <c:if test="${form.archiveLocation != outdatedCitation.archiveLocation}" >class="changed"</c:if>>
<td>Archive Location</td>
<td>${form.archiveLocation}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'libraryCatalog') }">
<tr  <c:if test="${form.libraryCatalog != outdatedCitation.libraryCatalog}" >class="changed"</c:if>>
<td>Library Catalog</td>
<td>${form.libraryCatalog}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'callNumber') }">
<tr  <c:if test="${form.callNumber != outdatedCitation.callNumber}" >class="changed"</c:if>>
<td>Call Number</td>
<td>${form.callNumber}</td>
</tr>
</c:if>
<c:if test="${fn:contains(formFields, 'rights') }">
<tr  <c:if test="${form.rights != outdatedCitation.rights}" >class="changed"</c:if>>
<td>Rights</td>
<td>${form.rights}</td>
</tr>
</c:if>
</table>
</div>

<div class="col-md-12">
<c:url value="/auth/group/${zoteroGroupId}/items/${outdatedCitation.key}/conflict/resolve" var="editUrl" />
<form:form action="${editUrl}" modelAttribute="form" method="POST">
<form:hidden path="key" value="${form.key}" />
<form:hidden path="itemType" value="${form.itemType}" />
<form:hidden path="title" value="${form.title}" />
<form:hidden path="shortTitle" value="${form.shortTitle}" />
<form:hidden path="dateFreetext" value="${form.dateFreetext}" />
<form:hidden path="authors" value="${form.authors}" />
<form:hidden path="editors" value="${form.editors}" />
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

<button type="submit" class="btn btn-primary">Sounds good, proceed!</button>
<a href="<c:url value="/auth/group/${zoteroGroupId}/items/${outdatedCitation.key}" />" class="btn btn-default">No, cancel.</a>
</form:form>
</div>