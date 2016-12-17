<html>
<head>
<title>Home</title>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
</head>
<body>
	<form action="/signUp" method="POST">
		<div align="center">
			<table>
				<tr>
					<td>User Name</td>
					<td><input type="email" name="email" /></td>
				</tr>
				<tr>
					<td>Password</td>
					<td><input type="password" name="password" /></td>
				</tr>
				<tr>
					<td>Name</td>
					<td><input type="text" name="name" /></td>
				</tr>
				<tr>
					<td>Student ID</td>
					<td><input type="text" name="studentid" /></td>
				</tr>


				<tr>
					<td></td>
					<td><button type="submit" value="Submit">Submit</button></td>
				</tr>
			</table>
			<div style="color: red">${error}</div>
		</div>
	</form>
	<form action="/login" method="GET"><input type="submit" value="Login"></form>
</body>
</html>