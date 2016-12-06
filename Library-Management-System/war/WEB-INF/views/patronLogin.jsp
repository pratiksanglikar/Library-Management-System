<html>
<head>
<title>Home</title>
</head>


<body>
	<form action="/login" method="POST">
		<div align="center">
			<table>
				<tr>
					<td>User Name</td>
					<td><input type="text" name="email" /></td>
				</tr>
				<tr>
					<td>Password</td>
					<td><input type="password" name="password" /></td>
				</tr>
				<tr>
					<td></td>
					<td><button type="submit" value="Submit">Submit</button></td>
				</tr>
			</table>
			<div style="color: red">${error}</div>
		</div>
	</form>
</body>
</html>