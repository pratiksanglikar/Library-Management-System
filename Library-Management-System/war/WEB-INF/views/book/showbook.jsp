<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="edu.cmpe275.team13.beans.Book" %>
<%@ page import = "java.sql.Timestamp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Book</title>
</head>
<body>
<%Timestamp date = (Timestamp) request.getAttribute("date"); %>
<p style="float:right;">
	<a href="http://1-dot-cmpe-275-term-project-team-13.appspot.com/logout">Logout</a>
	<%= date.toLocaleString() %>
	</p>
	
	<% Book book = (Book) request.getAttribute("book"); %>
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
</body>
</html>