<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="edu.cmpe275.team13.beans.Book"%>
<%@ page import="java.util.List"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Search Results!</title>
</head>
<body>
	<%
		List<Book> books = (List<Book>) request.getAttribute("books");
	%>

	<c:forEach items="${books}" var="book">
		<h2>"${book.title}"</h2>
		<img alt="Title" src="${book.image}">
		<h4>"${book.isbn}"</h4>
		<h4>"${book.author_name}"</h4>
		<h4>"${book.call_number}"</h4>
		<h4>"${book.publisher_name}"</h4>
		<h4>"${book.year_of_publication}"</h4>
		<h4>"${book.location_in_library}"</h4>
		<h4>"${book.book_status}"</h4>
		<h4>"${book.keywords}"</h4>
	</c:forEach>
</body>
</html>