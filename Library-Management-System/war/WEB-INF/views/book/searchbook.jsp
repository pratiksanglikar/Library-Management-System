<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="edu.cmpe275.team13.search.BookSearch"%>
<%@ page import="edu.cmpe275.team13.beans.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Library Management System: search book</title>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
</head>
<body>
<p style="float:right;">
	<a href="http://1-dot-cmpe-275-term-project-team-13.appspot.com/logout">Logout</a>
	</p>
	
	<%
		BookSearch bs = new BookSearch();
		String name = null;
		boolean isLibrarian = false;
		if (session.getAttribute("type").equals("librarian")) {
			Librarian user = (Librarian) session.getAttribute("user");
			name = user.getLibrarian_name();
			isLibrarian = true;
		} else {
			Patron user = (Patron) session.getAttribute("user");
			name = user.getPatron_name();
		}
	%>
	<h2>
		Welcome,
		<%=name%></h2>
	<h1>Search Book:</h1>
	<p>Enter one or more attributes to search a book: </p>
	<form action="/books/searchbook/" method="get">
		<table>
			<tr>
				<td><label>ISBN</label></td>
				<td><input name="isbn" value="${bs.isbn}"></td>
			</tr>
			<tr>
				<td><label>Author Name</label></td>
				<td><input name="author_name" value="${bs.author_name}"></td>
			</tr>
			<tr>
				<td><label>Title</label></td>
				<td><input name="title" value="${bs.title}"></td>
			</tr>
			<tr>
				<td><label>Publisher Name</label></td>
				<td><input name="publisher_name" value="${bs.publisher_name}"></td>
			</tr>
			<tr>
				<td><label>Year of Publication</label></td>
				<td><input type="text" name="year_of_publication"
					placeholder="yyyy-mm-dd" value="${bs.year_of_publication}"></td>
			</tr>
			<tr>
				<td><label>Book Status</label></td>
				<td><input name="book_status" value="true"></td>
			</tr>
			<!-- created by must be set by the librarian logged into the system -->
			<c:set var="bool_val"><%=isLibrarian %></c:set>
			<c:if test="${bool_val eq true}">
				<tr>
					<td><label>Created By</label></td>
					<td><input name="created_by" value="${bs.created_by}"></td>
				</tr>
				<tr>
					<td><label>Updated By</label></td>
					<td><input name="updated_by" value="${bs.updated_by}"></td>
				</tr>
			</c:if>
			<tr>
				<td><label>Keywords</label></td>
				<td><input name="keywords" value="${bs.keywords}"
					placeholder="Enter ; seperated keywords"></td>
			</tr>
			<tr>
				<td colspan="2"><input type="submit" value="Search Book" /></td>
			</tr>
		</table>
	</form>
	<%
		String string = "";
		if(session.getAttribute("type").equals("librarian")) {
			string += "<form action=\"/books\" method=\"GET\">	<input type=\"submit\" value=\"Add Book\"/></form>";
		}
	%>
	<%=string %>
</body>
</html>