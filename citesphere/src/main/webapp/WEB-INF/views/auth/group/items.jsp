<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script src="<c:url value="/resources/paginator/jquery.twbsPagination.min.js" />"></script>

<script>
$(function() {
	$('#pagination-top').twbsPagination({
	    totalPages: ${totalPages},
	    startPage: ${currentPage},
	    prev: "«",
	    next: "»",
	    visiblePages: 10,
	    initiateStartPageClick: false,
	    onPageClick: function (event, page) {
	    	window.location.href = "<c:url value="/auth/group/${zoteroGroupId}/items" />?page=" + page;
	    }
	});
	
	$(".bib-entry").click(function() {
		var key = $(this).data("key");
		window.location.href = "<c:url value="/auth/group/${zoteroGroupId}/items/" />" + key;
	});
	
});
</script>

<ol class="breadcrumb">
<li><a href="<c:url value="/" />">Home</a></li>
<c:forEach items="${breadCrumbs}" var="crumb">
	<c:choose>
	<c:when test="${crumb.type == 'GROUP'}">
		<c:url value="/auth/group/${crumb.objectId}/items" var="crumbUrl" />
	</c:when>
	<c:when test="${crumb.type == 'COLLECTION'}">
		<c:url value="/auth/group/${zoteroGroupId}/collection/${crumb.objectId}/items" var="crumbUrl" />
	</c:when>
	<c:otherwise>
		<c:url value="/auth/group/${zoteroGroupId}/items" var="crumbUrl" />
	</c:otherwise>
	</c:choose>
  <li><a href="${crumbUrl}">${crumb.label}</a></li>
</c:forEach>
</ol>

<h2>Items in Group ${zoteroGroupId}</h2>

<div class="col-md-12">
<ul id="pagination-top" class="pagination-sm"></ul>


<div class="pull-right" style="margin-top: 20px;">
<a href="<c:url value="/auth/group/${zoteroGroupId}/items/create" />" class="btn btn-primary"><i class="fas fa-plus-circle"></i> Create Citation</a>
</div>
</div>

<div class="col-md-2">
<p class="lead">Collections</p>
<ul class="list-group">
<c:forEach items="${citationCollections}" var="collection">
  <li class="list-group-item">
	  <span class="badge">${collection.numberOfItems}</span>
	  <a href="<c:url value="/auth/group/${zoteroGroupId}/collection/${collection.key}/items" />">${collection.name}</a>
  </li>
</c:forEach>
</ul>

</div>

<div class="col-md-10">
<table class="table table-striped table-bordered">
<tr>
	<th>Type</th>
	<th>Authors</th>
	<th>Title</th>
	<th>Date</th>
	<th>URL</th>
</tr>
<c:forEach items="${items}" var="entry">
<tr>
	<td>
	  <span class="text-warning">
	  	<spring:eval expression="@iconsResource.getProperty(entry.itemType + '_icon')"  var="iconClass" />
		<spring:eval expression="@iconsResource.getProperty(entry.itemType + '_label')"  var="iconLabel" />
		<c:if test="${empty iconClass}">
		<c:set var="iconClass" value="fas fa-file" />
		</c:if>
		<c:if test="${empty iconLabel}">
		<c:set var="iconLabel" value="${entry.itemType}" />
		</c:if>
		<i class="${iconClass}" title="${iconLabel}"></i> ${iconLabel}
	  </span>
	</td>
	<td class="bib-entry" data-key="${entry.key}">
	 <c:forEach items="${entry.authors}" var="author" varStatus="status">
 	  <strong>${author.lastName}<c:if test="${not empty author.firstName}">, ${author.firstName}</c:if></strong><c:if test="${!status.last}">; </c:if>
 	 </c:forEach>
 	</td>
 	<td class="bib-entry" data-key="${entry.key}">
 		<em>${entry.title}</em>
 	</td>
 	<td class="bib-entry" data-key="${entry.key}">
 		${entry.dateFreetext}
 	</td>
 	<td>
 		<c:if test="${not empty entry.url}">
 		<a href="${entry.url}" target="_blank" title="${entry.url}"><i class="fas fa-globe-americas"></i></a>
 		</c:if>
 	</td>
</tr>
</c:forEach>
</table>
</div>
