<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="jumbotron col-md-12">

<sec:authorize access="isAnonymous()">
<h1>Welcome to Citesphere!</h1>
<p>
If you try and take a cat apart to see how it works, the first thing you have on your hands is a nonworking cat.
</p>

</sec:authorize>
<sec:authorize access="isAuthenticated()">
<c:if test="${not isZoteroConnected}">
<center>
<h2>Please connect your Zotero account before continuing!</h2>
<form action="<c:url value="/signin/zotero" />" method="POST">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	<button class="btn btn-primary btn-lg"" type="submit">
		Connect Zotero
	</button>
</form>
</center>
</c:if>
<c:if test="${isZoteroConnected}">
<h2>Welcome back, ${user.firstName}!</h2>
</c:if>
</sec:authorize>
</div>