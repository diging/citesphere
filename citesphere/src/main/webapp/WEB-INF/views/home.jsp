<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<div class="jumbotron col-md-12">

<sec:authorize access="isAnonymous()">
<h1>Congratulation!</h1>
<p>
You did it. This basic webapp is all set up now. Try to login as "admin" with password "admin".
</p>
<form action="<c:url value="/signin/zotero" />" method="POST">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

	<button class="btn btn-primary btn-lg" type="submit">
		<i class="fab fa-google-plus-g" aria-hidden="true"></i> Login with Zotero
	</button>
</form>
</sec:authorize>
<sec:authorize access="isAuthenticated()">
<h1>Whoohoo!</h1>
<p>You are logged in, <sec:authentication property="principal.username" />.</p>
</sec:authorize>
</div>