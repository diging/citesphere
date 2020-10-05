<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<script src="<c:url value="/resources/paginator/jquery.twbsPagination.min.js" />"></script>

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
	    <c:choose>
	    	<c:when test="${collectionId!=null}">
	    	window.location.href = "<c:url value="/auth/group/${zoteroGroupId}/collection/${collectionId}/items/" />?page=" + page; 
	        </c:when>    
	        <c:otherwise>
	        window.location.href = "<c:url value="/auth/group/${zoteroGroupId}/items" />?page=" + page;
	        </c:otherwise>
	    </c:choose>
	    }
	});
	
	$(".bib-entry").click(function() {
		var key = $(this).data("key");
		var index = $(this).data("index");
		window.location.href = "<c:url value="/auth/group/${zoteroGroupId}/items/" />" + key +"?index=" + index + "&page="+${currentPage} + "&sortBy=" + '${sort}' + "&collectionId=" + '${collectionId}';
	});
	
	$('.collapse').collapse();
	
	if(sessionStorage.getItem("collectionsHidden") == "true"){
		$("#collectionsList").hide()
	    $("#toggleCollection").attr('class', "fa fa-chevron-circle-right")
	    $("#toggleCollection").attr('title', "Show Collections")	    
	    $("#citationBlock").attr('class', 'col-md-12')
	}
	
	$("#toggleCollection").click(function(){
	    $("#collectionsList").toggle(); 
	    collectionsHidden = $("#toggleCollection").hasClass("fa-chevron-circle-left")
	    sessionStorage.setItem("collectionsHidden",collectionsHidden)
	        
	    $("#toggleCollection").attr('class', collectionsHidden ? "fa fa-chevron-circle-right" : "fa fa-chevron-circle-left")
	    $("#toggleCollection").attr('title', collectionsHidden ? "Show Collections":"Hide Collections")
	    
	    $("#citationBlock").attr('class', collectionsHidden ? 'col-md-12' : 'col-md-10')
	  })
	  
	var shownColumns = [<c:forEach items="${columns}" var="col">"${col}",</c:forEach>];
	
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
		window.location.href="<c:url value="/auth/group/${zoteroGroupId}/items/" />?columns=" + shownColumns; 
	});
	
	$("#findByItemKeyBtn").click(function() {
		var key = $("#findItemKey").val();
		if (key != undefined && key != '') {
			window.location.href="<c:url value="/auth/group/${zoteroGroupId}/items/" />" + key;
		}
	});
	
	$("#findItemKey").on('keypress',function(e) {
	    if(e.which == 13) {
	    	var key = $("#findItemKey").val();
	        if (key != undefined && key != '') {
	            window.location.href="<c:url value="/auth/group/${zoteroGroupId}/items/" />" + key;
	        }
	    }
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

<h2>
	<c:choose>
	    <c:when test="${collectionId!=null}">
	        Items in Collection <i>${collectionName}</i> in Group <i>${group.name}</i></br> 
	    </c:when>    
	    <c:otherwise>
	        Items in Group ${group.name}</br>
	    </c:otherwise>
	</c:choose>
	
	<small>${total} records </small>
</h2>

<div class="pull-right">
<div class="progress" style="width: 200px">
  <div id="syncProgress" class="progress-bar" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">  
  <span id="syncText">Up to date</span></div>
</div>
<div style="text-align: right;margin-top: -20px;margin-bottom: 20px;">
    <small><a href="."><i class="fas fa-redo-alt"></i> Reload page</a></small>
</div>

<c:url value="/auth/group/${group.groupId}/sync/info" var="syncUrl" />
<script>
//# sourceURL=poll.js
$.get("${syncUrl}", pollStatus);

function pollStatus(){
    $.get('${syncUrl}', function(data) {
        if(data['status'] == 'PREPARED' || data['status'] == 'STARTED') {
        	if (data['total'] == 0) {
        		$("#syncProgress").attr('style', "width:" + "100%");
                $("#syncProgress").attr('aria-valuenow', 100);
                $("#syncProgress").attr('aria-valuemax', 100);
                $("#syncProgress").addClass("progress-bar-striped active");
        	} else {
        		var percent = data['current']/data['total']*100;
                $("#syncProgress").attr('style', "width:" + percent + "%");
                $("#syncProgress").attr('aria-valuenow', data['current']);
                $("#syncProgress").attr('aria-valuemax', data['total']);
                $("#syncProgress").addClass("progress-bar-striped active");
                $("#syncText").text(Math.round(percent) + "% synced");
        	}
        	setTimeout(pollStatus,1000);
        } else {
        	$("#syncProgress").attr('style', "width: 100%");
        	$("#syncProgress").attr('aria-valuenow', 100);
            $("#syncProgress").attr('aria-valuemax', 100);
            $("#syncProgress").removeClass("progress-bar-striped active");
            $("#syncText").text("Up to date");
        }
    });
}
</script>
</div>

<div class="clearfix"></div>
<div class="form-group">
<div class="input-group">
    <input type="text" id="findItemKey" name="findItemKey" class="form-control" placeholder="Find by item key"/>
    <div class="input-group-addon" id="findByItemKeyBtn" style="cursor: pointer;"><i class="fas fa-search"></i></div>
</div>
</div>

<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">
  <div class="panel panel-default">
    <div class="panel-heading" role="tab" id="headingOne">
      <h4 class="panel-title">
        <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
          Group Info <i class="fas fa-arrows-alt-v pull-right"></i>
        </a>
      </h4>
    </div>
    <div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">
      <div class="panel-body">
      	${group.description}
        <div class="col-md-6">
        <b>Local Metadata Version:</b> ${group.metadataVersion}<br>
        <b>Local Library Version:</b> ${group.contentVersion}<br>
        <b>Created on:</b> <span class="date">${group.created}</span><br>
        <b>Last Modified on:</b> <span class="date">${group.lastModified}</span>
        </div>
        <div class="col-md-6">
        <b>Owner:</b> ${group.owner}<br>
        <br>
        <b>Last synced:</b> <span class="date">${group.updatedOn}</span>
        </div>
      </div>
    </div>
  </div>
</div>

<div class="col-md-12">
<ul id="pagination-top" class="pagination-sm"></ul>


<div class="pull-right" style="margin-top:25px;margin-left:12px;">
<c:choose>
    <c:when test="${collectionId!=null}">
        <a href="<c:url value="/auth/group/${zoteroGroupId}/collection/${collectionId}/items/sync?page=${currentPage}&sort=${sort}&columns=${columnString}" />"><big><i
		class="fas fa-sync" title="Sync Page"></i></big></a>
    </c:when>    
    <c:otherwise>
        <a href="<c:url value="/auth/group/${zoteroGroupId}/items/sync?page=${currentPage}&sort=${sort}&columns=${columnString}" />"><big><i
		class="fas fa-sync" title="Sync Page"></i></big></a>
    </c:otherwise>
</c:choose>
</div>

<div class="pull-right" style="margin-top: 20px;">
<a href="<c:url value="/auth/group/${zoteroGroupId}/items/create" />" class="btn btn-primary"><i class="fas fa-plus-circle"></i> Create Citation</a>
</div>

<div class="btn-group pull-right" style="margin-top: 20px; margin-right: 10px;">
  <button type="button" class="btn btn-primary dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
    Export <span class="caret"></span>
  </button>
  <ul class="dropdown-menu">
    <c:if test="${empty collectionId and total <= 300 }">
    <li><form method="POST" action="<c:url value="/auth/group/${zoteroGroupId}/export" />"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /><button class="btn btn-link" type="submit">CSV (up to 300 items)</button></form></li>
    </c:if>
    <c:if test="${empty collectionId}">
    <li><form method="POST" action="<c:url value="/auth/group/${zoteroGroupId}/job/export/" />"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /><button class="btn btn-link" type="submit">CSV  (for many items)</button></form></li>
    </c:if>
    <c:if test="${not empty collectionId and total <= 300 }">
    <li><form method="POST" action="<c:url value="/auth/group/${zoteroGroupId}/collection/${collectionId}/export" />"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /><button class="btn btn-link" type="submit">CSV (up to 300 items)</button></form></li>
    </c:if>
     <c:if test="${not empty collectionId}">
    <li><form method="POST" action="<c:url value="/auth/group/${zoteroGroupId}/collection/${collectionId}/job/export" />"><input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" /><button class="btn btn-link" type="submit">CSV (for many items)</button></form></li>
    </c:if>
    <c:if test="${total > 300}">
    <li><a class="btn btn-disabled" style="color: #999">CSV (up to 300 items)</a></li>
    </c:if>
  </ul>
</div>

<div>

<p class="lead" style="display:inline;padding-left:10px">Collections</p>

<big><i class="fa fa-chevron-circle-left" aria-hidden="true" id="toggleCollection" title="Hide Collections" style="color:#2e6da4;padding-left:5px;cursor:pointer"></i></big>

<div class="dropdown pull-right" style="padding-bottom: 10px;">
  <button class="btn btn-default dropdown-toggle" type="button" id="dropdownMenu1" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
    Columns
    <span class="caret"></span>
  </button>
  <ul id="addionalColumns" class="dropdown-menu" aria-labelledby="dropdownMenu1">
  <c:forEach items="${availableColumns}" var="column">
    <li><a href="#" data-column-name="${column}" data-is-shown="${fn:contains(columns, column) }">
    <spring:eval expression="@labelsResource.getProperty('_item_attribute_label_' + column)"  var="columnLabel" />
	<c:if test="${fn:contains(columns, column) }">
	<i class="fas fa-check"></i> 
	</c:if>
	${columnLabel}
    </a></li>
  </c:forEach>
  </ul>
</div>

</div>

</div>

<div class="col-md-2">
<c:set var = "columnString" value = ""/>
<c:forEach items="${columns}" var="column" varStatus="loop">
   <c:set var = "columnString" value = "${columnString}${column}"/>
   <c:if test="${!loop.last}">
   		<c:set var = "columnString" value = "${columnString},"/>
   </c:if>
</c:forEach>

<ul class="list-group" id="collectionsList">
<c:forEach items="${citationCollections}" var="collection" >
  <li class="list-group-item">
	  <span class="badge">${collection.numberOfItems}</span>
	  <a href="<c:url value="/auth/group/${zoteroGroupId}/collection/${collection.key}/items" />">${collection.name}</a>
  </li>
</c:forEach>
</ul>

</div>
<div class="col-md-10" id="citationBlock">



<table class="table table-striped table-bordered">
<tr>
	<th>Type</th>
	<th>Authors</th>
	<th>Title</th>
	<th>Date</th>
	<th>URL</th>
	<c:forEach items="${columns}" var="column">
	<th>
	<spring:eval expression="@labelsResource.getProperty('_item_attribute_label_' + column)"  var="columnLabel" />
	${columnLabel}
	</c:forEach>
	</th>
</tr>
<c:forEach items="${items}" var="entry" varStatus="loop">
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
	<td class="bib-entry" data-key="${entry.key}" data-index="${loop.index}">
	 <c:forEach items="${entry.authors}" var="author" varStatus="status">
 	  <strong>${author.lastName}<c:if test="${not empty author.firstName}">, ${author.firstName}</c:if></strong><c:if test="${!status.last}">; </c:if>
 	 </c:forEach>
 	</td>
 	<td class="bib-entry" data-key="${entry.key}" data-index="${loop.index}">
 		<em>${entry.title}</em>
 	</td>
 	<td class="bib-entry" data-key="${entry.key}" data-index="${loop.index}">
 		${entry.dateFreetext}
 	</td>
 	<td>
 		<c:if test="${not empty entry.url}">
 		<a href="${entry.url}" target="_blank" title="${entry.url}"><i class="fas fa-globe-americas"></i></a>
 		</c:if>
 	</td>
 	<c:forEach items="${columns}" var="column">
 	<td style="max-width:300px;">${entry[column]}</td>
 	</c:forEach>
</tr>
</c:forEach>
</table>
</div>