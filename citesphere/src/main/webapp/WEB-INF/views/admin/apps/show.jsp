<%@ page pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<h2>Apps</h2>

<table class="table table-striped">
<tr>
<th>Client Id</th>
<th>Name</th>
<th>Description</th>
</tr>

<c:forEach items="${clientList}" var="client">


<tr> 
<td>
    ${client.clientId}
</td>
<td>${client.name}</td>
<td>${client.description}</td>
</tr>
</c:forEach>

</table>