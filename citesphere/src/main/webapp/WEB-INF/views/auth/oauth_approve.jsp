<%@ page pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
    uri="http://www.springframework.org/security/tags"%>
<!-- Custom styles for this template -->
<link href="<c:url value="/resources/bootstrap-4.1.2/login.css" />"
    rel="stylesheet">

<div class="row">
<div class="col-sm"></div>
<div class="jumbotron col-sm">

    <b>Do you authorize ${clientId} access to your account on Citesphere (and therefore Zotero)?</b>
    
    <c:url value="/api/v1/oauth/authorize" var="confirmUrl" />
    <form id="confirmationForm" name="confirmationForm" action="${confirmUrl}" method="POST">
    <input name="user_oauth_approval" value="true" type="hidden"/>
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
    <input type="hidden" name="client_id" value="${authorizationRequest.clientId}" />
    <c:forEach items="${authorizationRequest.responseTypes}" var="responseType">
    <input type="hidden" name="response_type" value="${responseType}" />
    </c:forEach>
    
    <div style="margin-top: 15px; margin-bottom: 15px;">
    <c:forEach items="${scopes}" var="scope">
    <input type="radio" name="${scope.key}" value="true">  Approve
    &nbsp; &nbsp; &nbsp;
    <input type="radio" name="${scope.key}" value="false">  Deny
    </c:forEach>
    </div>
    
    <input class="form-control" name="authorize" value="Authorize" type="submit"/>
    </form>
    
</div>
<div class="col-sm"></div>
</div>