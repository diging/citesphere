<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<script
	src="<c:url value="/resources/paginator/jquery.twbsPagination.min.js" />"></script>

<script>
//# sourceURL=page.js
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
	
	$('.collapse').collapse();
	
	var shownColumns = [<c:forEach items="${columns}" var="col">"${col}",</c:forEach>];
	var conceptSearchString = $("#search-input").val();
	
	$("#addionalColumns a").click(function(event) {
		var isShown = $(this).data("is-shown");
		var col = $(this).data("column-name");
		if (isShown) {
			shownColumns = shownColumns.filter(function(value, index, arr){
			    return value != col;
			});
		} else {
			shownColumns.push(col);
		}
		reloadWithParams(shownColumns, conceptSearchString);
	});
	
	
	$("#search-input").on('keyup', function (e) {
		if (e.keyCode == 13) {
			conceptSearchString = $("#search-input").val();
			reloadWithParams(shownColumns, conceptSearchString);
		}
	});
});

function reloadWithParams(shownColumns, conceptSearchString){
	var queryString = "";
	
	if(shownColumns.length != 0){
		queryString += "columns=" + shownColumns;
	}
	
	if(conceptSearchString !== ""){
		if(shownColumns.length != 0){
			queryString += "&";
		}
        queryString += "conceptTag=" + conceptSearchString;
    }
	
	if(shownColumns.length != 0 || conceptSearchString !== ""){
		var collectionId = "${collectionId}";
        if(collectionId === ""){
            window.location.href="<c:url value="/auth/group/${zoteroGroupId}/items/" />?" + queryString;
        }else{
            window.location.href="<c:url value="/auth/group/${zoteroGroupId}/collection/${collectionId}/items/" />?" + queryString;
        }
	}
}
</script>

<ol class="breadcrumb">
	<li><a href="<c:url value="/" />">Home</a></li>
	<c:forEach items="${breadCrumbs}" var="crumb">
		<c:choose>
			<c:when test="${crumb.type == 'GROUP'}">
				<c:url value="/auth/group/${crumb.objectId}/items" var="crumbUrl" />
			</c:when>
			<c:when test="${crumb.type == 'COLLECTION'}">
				<c:url
					value="/auth/group/${zoteroGroupId}/collection/${crumb.objectId}/items"
					var="crumbUrl" />
			</c:when>
			<c:otherwise>
				<c:url value="/auth/group/${zoteroGroupId}/items" var="crumbUrl" />
			</c:otherwise>
		</c:choose>
		<li><a href="${crumbUrl}">${crumb.label}</a></li>
	</c:forEach>
</ol>

<h2>Items in Group ${group.name}</h2>

<div class="panel-group" id="accordion" role="tablist"
	aria-multiselectable="true">
	<div class="panel panel-default">
		<div class="panel-heading" role="tab" id="headingOne">
			<h4 class="panel-title">
				<a role="button" data-toggle="collapse" data-parent="#accordion"
					href="#collapseOne" aria-expanded="true"
					aria-controls="collapseOne"> Group Info <i
					class="fas fa-arrows-alt-v pull-right"></i>
				</a>
			</h4>
		</div>
		<div id="collapseOne" class="panel-collapse collapse in"
			role="tabpanel" aria-labelledby="headingOne">
			<div class="panel-body">
				${group.description}
				<div class="col-md-6">
					<b>Local Version:</b> ${group.version}<br> <b>Created on:</b>
					<span class="date">${group.created}</span><br> <b>Last
						Modified on:</b> <span class="date">${group.lastModified}</span>
				</div>
				<div class="col-md-6">
					<b>Owner:</b> ${group.owner}<br> <br> <b>Last synced:</b>
					<span class="date">${group.updatedOn}</span>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="col-md-12">
	<ul id="pagination-top" class="pagination-sm"></ul>

	<div class="pull-right" style="margin-top: 20px;">
		<a href="<c:url value="/auth/group/${zoteroGroupId}/items/create" />"
			class="btn btn-primary"><i class="fas fa-plus-circle"></i> Create
			Citation</a>
	</div>

	<div class="search-main" style="width: 30%;">
		<div class="form-group has-feedback has-search" style="">
			<span class="glyphicon glyphicon-search form-control-feedback"></span>
			<input id="search-input" type="text" class="form-control" placeholder="Search Concepts" <c:if test="${not empty conceptTag}">value="${conceptTag}"</c:if> >
		</div>
	</div>

</div>



<div class="col-md-2">
	<p class="lead">Collections</p>
	<ul class="list-group">
		<c:forEach items="${citationCollections}" var="collection">
			<li class="list-group-item"><span class="badge">${collection.numberOfItems}</span>
				<a
				href="<c:url value="/auth/group/${zoteroGroupId}/collection/${collection.key}/items" />">${collection.name}</a>
			</li>
		</c:forEach>
	</ul>

</div>
<div class="col-md-10">
	<div class="dropdown pull-right" style="padding-bottom: 10px;">
		<button class="btn btn-default dropdown-toggle" type="button"
			id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true"
			aria-expanded="true">
			Columns <span class="caret"></span>
		</button>
		<ul id="addionalColumns" class="dropdown-menu"
			aria-labelledby="dropdownMenu1">
			<c:forEach items="${availableColumns}" var="column">
				<li><a href="#" data-column-name="${column}"
					data-is-shown="${fn:contains(columns, column) }"> <spring:eval
							expression="@labelsResource.getProperty('_item_attribute_label_' + column)"
							var="columnLabel" /> <c:if
							test="${fn:contains(columns, column) }">
							<i class="fas fa-check"></i>
						</c:if> ${columnLabel}
				</a></li>
			</c:forEach>
		</ul>
	</div>

	<table class="table table-striped table-bordered">
		<tr>
			<th>Type</th>
			<th>Authors</th>
			<th>Title</th>
			<th>Date</th>
			<th>Concept Tags</th>
			<th>URL</th>
			<c:forEach items="${columns}" var="column">
				<th><spring:eval
						expression="@labelsResource.getProperty('_item_attribute_label_' + column)"
						var="columnLabel" /> ${columnLabel}
			</c:forEach>
			</th>
		</tr>
		<c:forEach items="${items}" var="entry">
			<tr>
				<td><span class="text-warning"> <spring:eval
							expression="@iconsResource.getProperty(entry.itemType + '_icon')"
							var="iconClass" /> <spring:eval
							expression="@iconsResource.getProperty(entry.itemType + '_label')"
							var="iconLabel" /> <c:if test="${empty iconClass}">
							<c:set var="iconClass" value="fas fa-file" />
						</c:if> <c:if test="${empty iconLabel}">
							<c:set var="iconLabel" value="${entry.itemType}" />
						</c:if> <i class="${iconClass}" title="${iconLabel}"></i> ${iconLabel}
				</span></td>
				<td class="bib-entry" data-key="${entry.key}"><c:forEach
						items="${entry.authors}" var="author" varStatus="status">
						<strong>${author.lastName}<c:if
								test="${not empty author.firstName}">, ${author.firstName}</c:if></strong>
						<c:if test="${!status.last}">; </c:if>
					</c:forEach></td>
				<td class="bib-entry" data-key="${entry.key}"><em>${entry.title}</em>
				</td>
				<td class="bib-entry" data-key="${entry.key}">
					${entry.dateFreetext}</td>
				<td class="bib-entry" data-key="${entry.key}"><c:forEach
						items="${entry.conceptTags}" var="tag" varStatus="status">
						<strong>${tag.conceptName}</strong>
						<c:if test="${!status.last}">; </c:if>
					</c:forEach></td>
				<td><c:if test="${not empty entry.url}">
						<a href="${entry.url}" target="_blank" title="${entry.url}"><i
							class="fas fa-globe-americas"></i></a>
					</c:if></td>
				<c:forEach items="${columns}" var="column">
					<td style="max-width: 300px;">${entry[column]}</td>
				</c:forEach>
			</tr>
		</c:forEach>
	</table>
</div>
