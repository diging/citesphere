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
<tr>
<td>Title</td>
<td>${citation.title}</td>
</tr>
<tr>
<td>Short Title</td>
<td>${citation.shortTitle}</td>
</tr>
<tr>
<td>Date</td>
<td>${citation.dateFreetext}</td>
</tr>
<tr>
<td>Authors</td>
<td>
<c:forEach items="${citation.authors}" var="author" varStatus="status">
 ${author.lastName}<c:if test="${not empty author.firstName}">, ${author.firstName}</c:if><c:forEach items="${author.affiliations}" var="aff"> (${aff.name})</c:forEach><c:if test="${!status.last}">; </c:if>
</c:forEach>
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
<tr>
<td>Publication Title</td>
<td>${citation.publicationTitle}</td>
</tr>
<tr>
<td>Journal Abbreviation</td>
<td>${citation.journalAbbreviation}</td>
</tr>
<tr>
<td>Volume</td>
<td>${citation.volume}</td>
</tr>
<tr>
<td>Issue</td>
<td>${citation.issue}</td>
</tr>
<tr>
<td>Pages</td>
<td>${citation.pages}</td>
</tr>
<tr>
<td>Series</td>
<td>${citation.series}</td>
</tr>
<tr>
<td>Series Title</td>
<td>${citation.seriesTitle}</td>
</tr>
<tr>
<td>Series Text</td>
<td>${citation.seriesText}</td>
</tr>
<tr>
<td>URL</td>
<td><a href="${citation.url}">${citation.url}</a></td>
</tr>
<tr>
<td>Language</td>
<td>${citation.language}</td>
</tr>
<tr>
<td>DOI</td>
<td>${citation.doi}</td>
</tr>
<tr>
<td>ISSN</td>
<td>${citation.issn}</td>
</tr>
<tr>
<td>Archive</td>
<td>${citation.archive}</td>
</tr>
<tr>
<td>Archive Location</td>
<td>${citation.archiveLocation}</td>
</tr>
<tr>
<td>Library Catalog</td>
<td>${citation.libraryCatalog}</td>
</tr>
<tr>
<td>Call Number</td>
<td>${citation.callNumber}</td>
</tr>
<tr>
<td>Rights</td>
<td>${citation.rights}</td>
</tr>
</table>