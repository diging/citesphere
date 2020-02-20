<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="cite"
	uri="https://diging.asu.edu/jps/tlds/citesphere"%>

<ol class="breadcrumb">
	<li><a href="<c:url value="/" />">Home</a></li>
	<li><a href="<c:url value="/auth/group/${zoteroGroupId}/items" />">Group</a></li>
	<li class="active">${citation.key}</li>
</ol>

<h2>
	<c:forEach items="${citation.authors}" var="author" varStatus="status">
		<strong>${author.lastName}<c:if
				test="${not empty author.firstName}">, ${author.firstName}</c:if></strong>
		<c:if test="${!status.last}">; </c:if>
	</c:forEach>
	<em>${citation.title}</em>
	<c:if test="${not empty citation.dateFreetext}">
 	  (${citation.dateFreetext})
 	  </c:if>

</h2>

<div style="margin-bottom: 20px;">
	<a
		href="<c:url value="/auth/group/${zoteroGroupId}/items/${citation.key}/edit" />"><i
		class="far fa-edit" title="Edit"></i></a> &nbsp;&nbsp; 
		<a
		href="<c:url value="/auth/group/${zoteroGroupId}/items/${citation.key}/sync" />"><i
		class="fas fa-sync" title="Sync Citation"></i></a>
		 &nbsp;&nbsp; 
		<a
		href="<c:url value="/auth/group/${zoteroGroupId}/items/${citation.key}/delete" />"><i
		class="fas fa-trash" title="Delete"></i></a>
</div>

<table class="table table-striped">
	<tr>
		<td width="20%">Item Key</td>
		<td>${citation.key}</td>
	</tr>
	<tr>
		<td>Citation Type</td>
		<td><spring:eval
				expression="@iconsResource.getProperty(citation.itemType + '_icon')"
				var="iconClass" /> <spring:eval
				expression="@iconsResource.getProperty(citation.itemType + '_label')"
				var="iconLabel" /> <c:if test="${empty iconClass}">
				<c:set var="iconClass" value="fas fa-file" />
			</c:if> <c:if test="${empty iconLabel}">
				<c:set var="iconLabel" value="${entry.itemType}" />
			</c:if> <i class="${iconClass}" title="${iconLabel}"></i> ${iconLabel}</td>
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
	<c:if test="${fn:contains(fields, 'date') }">
		<tr>
			<td>Date</td>
			<td>${citation.dateFreetext}</td>
		</tr>
	</c:if>
	<tr>
		<td>Authors</td>
		<td><c:forEach items="${citation.authors}" var="author"
				varStatus="status">
 ${author.lastName}<c:if test="${not empty author.firstName}">, ${author.firstName}</c:if>
				<c:if test="${not empty author.affiliations}">
 (<c:forEach items="${author.affiliations}" varStatus="affStatus"
						var="aff">${aff.name}<c:if test="${!affStatus.last}">, </c:if>
					</c:forEach>)<c:if test="${!status.last}">; </c:if>
				</c:if>
				<c:if test="${not empty author.uri}">
					<a href="${author.uri}" target="_blank"><i class="fas fa-link"></i></a>
				</c:if>
				<c:if test="${not empty author.localAuthorityId}">
					<a
						href="<c:url value="/auth/authority/${author.localAuthorityId}" />"><i
						class="fas fa-anchor"></i></a>
				</c:if>
			</c:forEach></td>
	</tr>
	<tr>
		<td>Editors</td>
		<td><c:forEach items="${citation.editors}" var="editor"
				varStatus="status">
 ${editor.lastName}<c:if test="${not empty editor.firstName}">, ${editor.firstName}</c:if>
				<c:if test="${not empty editor.affiliations}">
 (<c:forEach items="${editor.affiliations}" varStatus="affStatus"
						var="aff">${aff.name}<c:if test="${!affStatus.last}">, </c:if>
					</c:forEach>)<c:if test="${!status.last}">; </c:if>
				</c:if>
				<c:if test="${not empty editor.uri}">
					<a href="${editor.uri}" target="_blank"><i class="fas fa-link"></i></a>
				</c:if>
				<c:if test="${not empty editor.localAuthorityId}">
					<a
						href="<c:url value="/auth/authority/${editor.localAuthorityId}" />"><i
						class="fas fa-anchor"></i></a>
				</c:if>
			</c:forEach></td>
	</tr>
	<c:forEach items="${citation.otherCreatorRoles}" var="role">
		<c:if test="${role != 'editor'}">
		<tr>
			<td><spring:eval
					expression="@labelsResource.getProperty('_item_attribute_label_${role}', '${role}')" />
			</td>
			<td><cite:creators citation="${citation}" role="${role}"
					var="creator">
 ${creator.person.lastName}<c:if
						test="${not empty creator.person.firstName}">, ${creator.person.firstName}</c:if>
					<c:if test="${not empty creator.person.affiliations}">
 (<c:forEach items="${creator.person.affiliations}"
							varStatus="affStatus" var="aff">${aff.name}<c:if
								test="${!affStatus.last}">, </c:if>
						</c:forEach>)<c:if test="${!status.last}">; </c:if>
					</c:if>
					<c:if test="${not empty creator.person.uri}">
						<a href="${creator.person.uri}" target="_blank"><i
							class="fas fa-link"></i></a>
					</c:if>
					<c:if test="${not empty creator.person.localAuthorityId}">
						<a
							href="<c:url value="/auth/authority/${creator.person.localAuthorityId}" />"><i
							class="fas fa-anchor"></i></a>
					</c:if>
					<c:if test="${!lastIteration}">; </c:if>
				</cite:creators></td>
		</tr>
		</c:if>
	</c:forEach>
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
		<tr>
			<td>Concepts</td>
			<td><c:forEach items="${citation.conceptTags}" var="tag">
					<span class="badge">${tag.conceptName} | ${tag.typeName}</span>
				</c:forEach></td>
		</tr>
	</c:if>
	<tr>
		<td>References</td>
		<td><c:forEach items="${citation.references}" var="ref">
				<p class="parse-ref-info" data-authors="${ref.authorString}" data-title="${ref.title}"
                        data-year="${ref.year}"
                        data-identifier="${ref.identifier}"
                        data-identifier-type="${ref.identifierType}"
                        data-first-page="${ref.firstPage}"
                        data-last-page="${ref.endPage}"
                        data-volume="${ref.volume}"
                        data-source="${ref.source}"
                        data-publication-type="${ref.publicationType}"
                        data-reference-id="${ref.referenceId}"
                        data-reference-label="${ref.referenceLabel}"
                        data-citation-id="${ref.citationId}"
                        data-ref-raw="${fn:escapeXml(ref.referenceStringRaw)}"
                        >
					<span class="fas fa-info-circle"></span>
					${ref.referenceString}
				</p>
			</c:forEach></td>
	</tr>
</table>

<div id="parsed-ref-modal" class="modal fade" tabindex="-1" role="dialog">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title">Parse Reference</h4>
      </div>
      <div class="modal-body">
        <p id="ref-unparsed"></p>
        <table class="table table-striped">
        <tr>
            <td width="30%">Author string:</td>
            <td id="parsed-ref-authors"></td>
        </tr>
        <tr>
            <td>Title:</td>
            <td id="parsed-ref-title"></td>
        </tr>
        <tr>
            <td>Year:</td>
            <td id="parsed-ref-year"></td>
        </tr>
        <tr>
            <td>Identifier:</td>
            <td id="parsed-ref-ident"></td>
        </tr>
        <tr>
            <td>First page:</td>
            <td id="parsed-ref-first-page"></td>
        </tr>
        <tr>
            <td>Last page:</td>
            <td id="parsed-ref-last-page"></td>
        </tr>
        <tr>
            <td>Volume:</td>
            <td id="parsed-ref-volume"></td>
        </tr>
        <tr>
            <td>Source:</td>
            <td id="parsed-ref-source"></td>
        </tr>
        <tr>
            <td>Publication type:</td>
            <td id="parsed-ref-publication-type"></td>
        </tr>
        <tr>
	        <td colspan="2">
		        
            </td>
        </tr>
        <tr>
            <td>Reference ID:</td>
            <td id="parsed-ref-reference-id"></td>
        </tr>
        <tr>
            <td>Reference label:</td>
            <td id="parsed-ref-reference-label"></td>
        </tr>
        <tr>
            <td>Citation ID:</td>
            <td id="parsed-ref-citation-id"></td>
        </tr>
        <tr>
            <td>Reference as found:</td>
            <td id="parsed-ref-ref-raw"></td>
        </tr>
        </table>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<script>
//# sourceURL=modal.js
	$(function() {
		$(".parse-ref-info").click(function(e) {
			var clicked = e.target;
			$("#ref-unparsed").text($(clicked).text());
			$("#parsed-ref-authors").text($(clicked).attr('data-authors'));
			$("#parsed-ref-title").text($(clicked).attr('data-title'));
			$("#parsed-ref-year").text($(clicked).attr('data-year'));
			var identifier = $(clicked).attr('data-identifier');
			var identType = $(clicked).attr('data-identifier-type');
			if (identType != undefined && identType != "") {
				identifier = identifier + " (" + identType + ")";
			}
			$("#parsed-ref-ident").text(identifier);
			$("#parsed-ref-first-page").text($(clicked).attr('data-first-page'));
			$("#parsed-ref-last-page").text($(clicked).attr('data-last-page'));
			$("#parsed-ref-volume").text($(clicked).attr('data-volume'));
			$("#parsed-ref-source").text($(clicked).attr('data-source'));
			$("#parsed-ref-publication-type").text($(clicked).attr('data-publication-type'));
			$("#parsed-ref-reference-id").text($(clicked).attr('data-reference-id'));
			$("#parsed-ref-reference-label").text($(clicked).attr('data-reference-label'));
			$("#parsed-ref-citation-id").text($(clicked).attr('data-citation-id'));
			$("#parsed-ref-ref-raw").text($(clicked).attr('data-ref-raw'));
            
			$("#parsed-ref-modal").modal();
		});
	})
</script>