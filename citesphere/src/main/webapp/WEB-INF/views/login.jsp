<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<div class="row">
	<div class="col-md-3">&nbsp;</div>
	<div class="col-md-6">
		<c:if test="${param.error != null}">
			<div class="alert alert-danger" role="alert">Invalid Username
				or Password.</div>
		</c:if>

		<div class="jumbotron col-md-12">
			<sec:authorize access="isAnonymous()">
				<h2>Login to Citesphere</h2>

				<form name='f' class="" action="<c:url value="/login/authenticate" />" method="POST">
					<input type="hidden" name="${_csrf.parameterName}"
						value="${_csrf.token}" />
					<div class="row">
						<div class="form-group">
							<label class="control-label col-sm-12" for="username">Username:</label>
							<div class="col-sm-12">
								<input placeholder="Username" class="form-control input-sm"
									type="text" id="username" name="username" />
							</div>
						</div>
					</div>
					<div class="row">
						<div class="form-group">
							<label class="control-label col-sm-12" for="password">Password:</label>
							<div class="col-sm-12">
								<input placeholder="Password" class="form-control input-sm"
									type="password" id="password" name="password" />
							</div>
						</div>
					</div>
					<div class="row">
						<div class="form-group">
							<div class="col-sm-12">
								<button type="submit" class="btn btn-primary bg-dark-blue">Submit</button>
							</div>
						</div>
					</div>
				</form>

			</sec:authorize>

			<sec:authorize access="isAuthenticated()">
				<h1>You are logged in to Citesphere!</h1>
			</sec:authorize>
		</div>
	</div>
	<div class="col-md-3"></div>
</div>
