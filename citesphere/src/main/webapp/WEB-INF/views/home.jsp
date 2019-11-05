<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>



<sec:authorize access="isAnonymous()">
<div class="jumbotron col-md-12">
<h1>Welcome to Citesphere!</h1>
<p>
If you try and take a cat apart to see how it works, the first thing you have on your hands is a nonworking cat.
</p>
</div>
</sec:authorize>
<sec:authorize access="isAuthenticated()">

<c:if test="${not isZoteroConnected}">

<div class="jumbotron col-md-12">
<center>
<h2>Please connect your Zotero account before continuing!</h2>
<form action="<c:url value="/signin/zotero" />" method="POST">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	<input type="hidden" name="all_groups" value="write" />
	<button class="btn btn-primary btn-lg"" type="submit">
		Connect Zotero
	</button>
</form>
</center>
</div>
</c:if>

<c:if test="${isZoteroConnected}">
<div class="jumbotron col-md-12"  style="margin-bottom: 20px;">
<h2>Welcome back, ${user.firstName}!</h2>
</div>
<div class="col-md-12">
<c:forEach items="${groups}" var="group">
<div class="panel panel-default">
  <div class="panel-body">
    <a href="<c:url value="/auth/group/${group.id}/items" />">${group.name} (${group.numItems})</a>
  </div>
</div>
</c:forEach>
</div>
</c:if>
</sec:authorize>