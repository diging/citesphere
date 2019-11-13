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

    <sec:authorize access="isAnonymous()">
        <form name='f' class="form-horizontal"
            action="<c:url value="/login/reset" />" method="POST">
            <h2 class="col-sm-12">Password Reset</h2>
            <p class="col-sm-12">Please enter the email address you used to register:</p>
            <input type="hidden" name="${_csrf.parameterName}"
                value="${_csrf.token}" />
            <div class="form-group">
                <div class="col-sm-12">
                    <input placeholder="Email Address" class="form-control input-sm"
                        type="text" id="email" name="email" />
                </div>
            </div>
            <div class="form-group">
                <div class="col-sm-12">
                    <button type="submit" class="btn btn-primary bg-dark-blue">Submit</button>
                </div>
            </div>
        </form>
    </sec:authorize>
    <sec:authorize access="isAuthenticated()">
       It looks like you're logged in. Please logout to reset your password.
    </sec:authorize>
</div>
<div class="col-sm"></div>
</div>