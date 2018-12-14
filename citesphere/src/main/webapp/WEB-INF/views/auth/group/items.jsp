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

<h2>Items in Group ${zoteroGroupId}</h2>

<ul id="pagination-top" class="pagination-sm"></ul>

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

