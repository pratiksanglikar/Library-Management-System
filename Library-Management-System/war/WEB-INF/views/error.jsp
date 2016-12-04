<%@ page isErrorPage="true" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Group-13 - Error 404</title>
</head>
<body>
	<% String[] splits = pageContext.getErrorData().getRequestURI().split("/"); %>
	<h2>404</h2>
	<h2>Group - 13</h2>
	<h3>
		<%= "The " + splits[1] + " with ID " + splits[2] + " not found in the system!" %>
	</h3>
</body>
</html>