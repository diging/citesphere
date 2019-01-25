<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<h2>Managed Authority Entries</h2>

<table class="table table-striped table-bordered">
<tr>
<th width="20%">URI</th>
<th>Name</th>
</tr>
<c:forEach items="${authorities}" var="authority">
<tr>
<td>${authority.uri}</td>
<td>${authority.name}</td>
</tr>
</c:forEach>
</table>