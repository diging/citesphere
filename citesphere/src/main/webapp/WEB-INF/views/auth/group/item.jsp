<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

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

<div style="margin-bottom: 20px;">
<a href="<c:url value="/auth/group/${zoteroGroupId}/items/${citation.key}/edit" />"><i class="far fa-edit" title="Edit"></i></a>
</div>

<table class="table table-striped">
<tr>
<td width="20%">Item Key</td>
<td>${citation.key}</td>
</tr>
<tr>
<td>Citation Type</td>
<td>
<spring:eval expression="@iconsResource.getProperty(citation.itemType + '_icon')"  var="iconClass" />
<spring:eval expression="@iconsResource.getProperty(citation.itemType + '_label')"  var="iconLabel" />	
<c:if test="${empty iconClass}">
<c:set var="iconClass" value="fas fa-file" />
</c:if>
<c:if test="${empty iconLabel}">
<c:set var="iconLabel" value="${entry.itemType}" />
</c:if>
<i class="${iconClass}" title="${iconLabel}"></i> ${iconLabel}
</td>
</tr>
<c:if test="${fn:contains(fields, 'title') }">
<tr>
<td>Title</td>
<td>${citation.title}</td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'shortTitle') }">
<tr>
<td>Short Title</td>
<td>${citation.shortTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'dateFreetext') }">
<tr>
<td>Date</td>
<td>${citation.dateFreetext}</td>
</tr>
</c:if>
<tr>
<td>Authors</td>
<td>
<c:forEach items="${citation.authors}" var="author" varStatus="status">
 ${author.lastName}<c:if test="${not empty author.firstName}">, ${author.firstName}</c:if>
 <c:if test="${not empty author.affiliations}">
 (<c:forEach items="${author.affiliations}" varStatus="affStatus" var="aff">${aff.name}<c:if test="${!affStatus.last}">, </c:if></c:forEach>)<c:if test="${!status.last}">; </c:if>
 </c:if>
 <c:if test="${not empty author.uri}">
 <a href="${author.uri}" target="_blank"><i class="fas fa-link"></i></a>
 </c:if>
 </c:forEach>
</td>
</tr>
<tr>
<td>Editors</td>
<td>
<c:forEach items="${citation.editors}" var="editor" varStatus="status">
 ${editor.lastName}<c:if test="${not empty editor.firstName}">, ${editor.firstName}</c:if>
 <c:if test="${not empty editor.affiliations}">
 (<c:forEach items="${editor.affiliations}" varStatus="affStatus" var="aff">${aff.name}<c:if test="${!affStatus.last}">, </c:if></c:forEach>)<c:if test="${!status.last}">; </c:if>
 </c:if>
</c:forEach>
</td>
</tr>
<c:if test="${fn:contains(fields, 'publicationTitle') }">
<tr>
<td>Publication Title</td>
<td>${citation.publicationTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'journalAbbreviation') }">
<tr>
<td>Journal Abbreviation</td>
<td>${citation.journalAbbreviation}</td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'volume') }">
<tr>
<td>Volume</td>
<td>${citation.volume}</td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'issue') }">
<tr>
<td>Issue</td>
<td>${citation.issue}</td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'pages') }">
<tr>
<td>Pages</td>
<td>${citation.pages}</td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'series') }">
<tr>
<td>Series</td>
<td>${citation.series}</td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'seriesTitle') }">
<tr>
<td>Series Title</td>
<td>${citation.seriesTitle}</td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'seriesText') }">
<tr>
<td>Series Text</td>
<td>${citation.seriesText}</td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'url') }">
<tr>
<td>URL</td>
<td><a href="${citation.url}">${citation.url}</a></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'language') }">
<tr>
<td>Language</td>
<td>${citation.language}</td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'doi') }">
<tr>
<td>DOI</td>
<td>${citation.doi}</td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'issn') }">
<tr>
<td>ISSN</td>
<td>${citation.issn}</td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'archive') }">
<tr>
<td>Archive</td>
<td>${citation.archive}</td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'archiveLocation') }">
<tr>
<td>Archive Location</td>
<td>${citation.archiveLocation}</td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'libraryCatalog') }">
<tr>
<td>Library Catalog</td>
<td>${citation.libraryCatalog}</td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'callNumber') }">
<tr>
<td>Call Number</td>
<td>${citation.callNumber}</td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'rights') }">
<tr>
<td>Rights</td>
<td>${citation.rights}</td>
</tr>
</c:if>
</table>