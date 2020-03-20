<%@ page language="java" pageEncoding="UTF-8"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->
    <meta name="description" content="">
    <meta name="author" content="">
    
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.5.0/css/all.css" integrity="sha384-B4dIYHKNBt8Bc12p+WXckhzcICo0wtJAoU8YZTY5qE0Id1GSseTk6S+L3BlXeVIU" crossorigin="anonymous">
	<link href="https://fonts.googleapis.com/css?family=Fira+Sans" rel="stylesheet">
	
    <title>Citesphere</title>

    <!-- Bootstrap core CSS -->
    <link href="<c:url value="/resources/bootstrap/css/bootstrap.min.css" />" rel="stylesheet">

    <!-- IE10 viewport hack for Surface/desktop Windows 8 bug -->
    <link href="<c:url value="/resources/bootstrap/assets/css/ie10-viewport-bug-workaround.css" />" rel="stylesheet">

    <!-- Custom styles for this template -->
    <link href="<c:url value="/resources/bootstrap/grid.css" />" rel="stylesheet">

    <!-- Just for debugging purposes. Don't actually copy these 2 lines! -->
    <!--[if lt IE 9]><script src="<c:url value="/resources/bootstrap/assets/js/ie8-responsive-file-warning.js" />"></script><![endif]-->
    <script src="<c:url value="/resources/bootstrap/assets/js/ie-emulation-modes-warning.js" />"></script>

    <!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
      <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
 	<script src="<c:url value="/resources/bootstrap/js/bootstrap.min.js" />"></script>
	<script src="<c:url value="/resources/bootstrap/js/main.js" />"></script>
	
	<script src="<c:url value="/resources/dateFormatting/dateFormat.min.js" />"></script>
	<script src="<c:url value="/resources/dateFormatting/jquery-dateformat.min.js" />"></script>
	
  </head>

  <body>
    <div class="container" style="padding-bottom: 150px;">
    <c:if test="${not empty param.error}">
	    <div class="alert alert-danger" role="alert" style="margin: 10px;">
	    		<spring:eval var="alertMsg" expression="@messageSource.getMessage('alert.login.${param.error}', null, null)"/>
	    		<p>${alertMsg}</p>
		</div>
    </c:if>
		
      <div class="page-header">
      <nav>
          <ul class="nav nav-pills pull-right">
          	<li role="presentation">
          		<a href="<c:url value="/" />" >Home</a>
          	</li>
          	
          	<sec:authorize access="hasRole('ADMIN')">
          	<li role="presentation">
          		<a href="<c:url value="/admin/user/list" />" >Users</a>
          	</li>
          	<li role="presentation" class="dropdown">
          	<a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
			      Apps <span class="caret"></span>
			    </a>
	          	<ul class="dropdown-menu">
	          	  <li role="presentation">
		          	<a href="<c:url value="/admin/apps" />" >See all Apps</a>
		          </li>
			      <li role="presentation">
		          	<a href="<c:url value="/admin/apps/add" />" >Add</a>
		          </li>
			    </ul>
		    </li>
          	</sec:authorize>
          	
          	<sec:authorize access="hasAnyRole('USER', 'ADMIN')">
          	<li role="presentation">
                <a href="<c:url value="/auth/exports" />" >Exports</a>
            </li>
          	<li role="presentation" class="dropdown">
			    <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
			      Citation Concepts <span class="caret"></span>
			    </a>
			    <ul class="dropdown-menu">
			      <li role="presentation">
		          	<a href="<c:url value="/auth/concepts/list" />" >Concepts</a>
		          </li>
		          <li role="presentation">
		          	<a href="<c:url value="/auth/concepts/types/list" />" >Concept Types</a>
		          </li>
			    </ul>
			</li>
			<li role="presentation" class="dropdown">
                <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
                  Import <span class="caret"></span>
                </a>
                <ul class="dropdown-menu">
					<li role="presentation">
		          		<a href="<c:url value="/auth/import/upload" />" >Import from File</a>
		          	</li>
		          	<li role="separator" class="divider"></li>
		          	<li role="presentation">
                        <a href="<c:url value="/auth/import/jobs" />" >See all Imports</a>
                    </li>
	          	</ul>
	        </li>
          	<li role="presentation">
          		<a href="<c:url value="/auth/authority/list" />" >Managed Authority Entries</a>
          	</li>
          	<li role="presentation" class="dropdown">
			    <a class="dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">
			      Profile <span class="caret"></span>
			    </a>
			    <ul class="dropdown-menu">
				    <li role="presentation">
	          			<a href="<c:url value="/auth/tokens" />" >Authorized OAuth Apps</a>
	          		</li>
          		</ul>
			</li>
          	</sec:authorize>
          	<sec:authorize access="isAuthenticated()">
          	<li role="presentation">
         	 	<form action="<c:url value="/logout" />" method="POST">
         	 	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  				<button class="btn-link" type="submit" title="Logout"><i class="fas fa-sign-out-alt"></i> Logout</button>
         	 	</form>
         	 </li>
          </sec:authorize>
          </ul>
         
        </nav>
        
        <h1><a class="appName" href="<c:url value="/" />">Citesphere</a></h1>  
      </div>
      
      
	  <c:if test="${show_alert}" >
	  <div class="alert alert-${alert_type}" role="alert">${alert_msg}</div>
	  </c:if>
      <tiles:insertAttribute name="content" />

    </div> <!-- /container -->
    
    <footer class="footer">
      <div class="container">
      
        <div class="row">
        <div class="col-md-12">
		<hr style="margin-bottom: 25px;">
		<p class="text-muted pull-left">
		<c:set var="PR" value="${pullrequest}" />
            Version: ${buildNumber}<c:if test="${not empty PR}">, Pull Request: ${pullrequest}</c:if> 
        </p>
		
	    <p class="text-muted">
	    
	         
	   	<sec:authorize access="isAnonymous()">
	   	
		<form name='f' class="form-inline pull-right" action="<c:url value="/login/authenticate" />" method="POST">
			Login:
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
  			<input placeholder="Username" class="form-control input-sm" type="text" id="username" name="username"/>        
		    <input placeholder="Password" class="form-control input-sm" type="password" id="password" name="password"/>    
		    <button type="submit" class="btn btn-default btn-sm">Log in</button>
		    <a href="<c:url value="/register" />" class="btn btn-primary btn-sm">Sign Up</a><br>
		    <a href="<c:url value="/login/reset" />"><small>Forgot Password?</small></a>
		</form>
		
		
		</sec:authorize>
        </p>
        </div>
        </div>
      </div>
    </footer>
    

     </body>
</html>
