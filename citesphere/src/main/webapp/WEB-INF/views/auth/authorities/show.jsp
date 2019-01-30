<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<c:if test="${empty entry}"><h1>Not Found</h1></c:if>

<h2>${entry.name}<br><small>${entry.uri}</small></h2>	

<p>
${entry.description}
</p>

<p>
Created on: <span class="date">${entry.createdOn}</span>
</p>