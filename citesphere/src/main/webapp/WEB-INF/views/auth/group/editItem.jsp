<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

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

<c:url value="/auth/group/${zoteroGroupId}/items/${citation.key}/edit" var="editUrl" />
<form:form action="${editUrl}" modelAttribute="form" method="POST">
<table class="table table-striped">
<tr>
<td width="20%">Item Key</td>
<td>${citation.key}</td>
<form:hidden path="key" value="${citation.key}"/>
</tr>
<tr>
<td>Citation Type</td>
<td>
<c:set var="enumValues" value="<%=edu.asu.diging.citesphere.core.model.bib.ItemType.values()%>"/>
<form:select id="itemType" path="itemType" data-show-icon="true" class="form-control selectpicker">
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
<td>Title</td>
<td><form:input path="title" type="text" class="form-control" placeholder="Title" value="${citation.title}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'shortTitle') }">
<tr>
<td>Short Title</td>
<td><form:input path="shortTitle" type="text" class="form-control" placeholder="Short title" value="${citation.shortTitle}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'dateFreetext') }">
<tr>
<td>Date</td>
<td><form:input path="dateFreetext" type="text" class="form-control" placeholder="Date" value="${citation.dateFreetext}" /></td>
</tr>
</c:if>
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
<c:if test="${fn:contains(fields, 'publicationTitle') }">
<tr>
<td>Publication Title</td>
<td><form:input path="publicationTitle" type="text" class="form-control" placeholder="Publication Title" value="${citation.publicationTitle}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'journalAbbreviation') }">
<tr>
<td>Journal Abbreviation</td>
<td><form:input path="journalAbbreviation" type="text" class="form-control" placeholder="Journal Abbreviation" value="${citation.journalAbbreviation}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'volume') }">
<tr>
<td>Volume</td>
<td><form:input path="volume"  type="text" class="form-control" placeholder="Volume" value="${citation.volume}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'issue') }">
<tr>
<td>Issue</td>
<td><form:input path="issue" type="text" class="form-control" placeholder="Issue" value="${citation.issue}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'pages') }">
<tr>
<td>Pages</td>
<td><form:input path="pages" type="text" class="form-control" placeholder="Pages" value="${citation.pages}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'series') }">
<tr>
<td>Series</td>
<td><form:input path="series" type="text" class="form-control" placeholder="Series" value="${citation.series}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'seriesTitle') }">
<tr>
<td>Series Title</td>
<td><form:input path="seriesTitle" type="text" class="form-control" placeholder="Series Title" value="${citation.seriesTitle}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'seriesText') }">
<tr>
<td>Series Text</td>
<td><form:input path="seriesText" type="text" class="form-control" placeholder="Series Text" value="${citation.seriesText}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'url') }">
<tr>
<td>URL</td>
<td><form:input path="url" type="text" class="form-control" placeholder="Url" value="${citation.url}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'language') }">
<tr>
<td>Language</td>
<td><form:input path="language" type="text" class="form-control" placeholder="Language" value="${citation.language}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'doi') }">
<tr>
<td>DOI</td>
<td><form:input path="doi" type="text" class="form-control" placeholder="DOI" value="${citation.doi}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'issn') }">
<tr>
<td>ISSN</td>
<td><form:input path="issn" type="text" class="form-control" placeholder="ISSN" value="${citation.issn}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'archive') }">
<tr>
<td>Archive</td>
<td><form:input path="archive" type="text" class="form-control" placeholder="Archive" value="${citation.archive}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'archiveLocation') }">
<tr>
<td>Archive Location</td>
<td><form:input path="archiveLocation" type="text" class="form-control" placeholder="Archive Location" value="${citation.archiveLocation}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'libraryCatalog') }">
<tr>
<td>Library Catalog</td>
<td><form:input path="libraryCatalog" type="text" class="form-control" placeholder="Library Catalog" value="${citation.libraryCatalog}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'callNumber') }">
<tr>
<td>Call Number</td>
<td><form:input path="callNumber" type="text" class="form-control" placeholder="Call Number" value="${citation.callNumber}" /></td>
</tr>
</c:if>
<c:if test="${fn:contains(fields, 'rights') }">
<tr>
<td>Rights</td>
<td><form:input path="rights" type="text" class="form-control" placeholder="Rights" value="${citation.rights}" /></td>
</tr>
</c:if>
</table>

<button class="btn btn-primary" type="submit"><i class="far fa-save"></i> &nbsp;Save</button>
</form:form>
<script>
	$(document).ready(function() {
		$('#itemType').on("change", function(){
			$.ajax({
				url : '<c:url value="/auth/group/{zoteroGroupId}/items/{${citation.key}/edit" />',
				type : 'GET',
				success: console.log("success");
				error: console.log("error");
			});
		})
	});
</script>
