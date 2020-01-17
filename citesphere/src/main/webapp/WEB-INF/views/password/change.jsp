<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<div class="row">
<div class="col-sm"></div>
<div class="jumbotron col-sm">
<h3 class="col-sm">Enter your new Password:</h3>


<p class="col-sm">

<c:if test="${not empty param.error}">
<div class="alert alert-danger" role="alert">
  <c:if test="${param.error == 'noPassword'}">
  Please provide a password.
  </c:if>
  <c:if test="${param.error == 'notMatching'}">
  The passwords you entered do not match.
  </c:if>
</div>
</c:if>
<form class="form-horizontal" action="<c:url value="/password/reset" />" method="POST">
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                
<label>New Password:</label>
<input class="form-control" type="password" name="password">

<label style="margin-top: 20px;">Repeat new Password:</label>
<input class="form-control" type="password" name="passwordRepeat">

<input style="margin-top: 20px;" type="submit" class="btn btn-primary" value="Submit" />
</form>
</p>
</div>

<div class="col-sm"></div>
</div>